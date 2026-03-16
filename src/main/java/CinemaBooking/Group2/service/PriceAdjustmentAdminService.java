package CinemaBooking.Group2.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentFilterDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentUpsertDto;
import CinemaBooking.Group2.repositories.PriceAdjustmentAdminRepository;

@Service
public class PriceAdjustmentAdminService {

    @Autowired
    private PriceAdjustmentAdminRepository priceAdjustmentAdminRepository;

    public List<AdminPriceAdjustmentDto> getAll(AdminPriceAdjustmentFilterDto filter) {
        try {
            return priceAdjustmentAdminRepository.findAll(filter);
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO FETCH PRICE ADJUSTMENTS.", e);
        }
    }

    public AdminPriceAdjustmentDto getById(Integer id) {
        try {
            AdminPriceAdjustmentDto dto = priceAdjustmentAdminRepository.findById(id);
            if (dto == null) {
                throw new RuntimeException("PRICE ADJUSTMENT NOT FOUND: " + id);
            }
            return dto;
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage() != null && e.getMessage().contains("NOT FOUND")) {
                throw e;
            }
            throw new RuntimeException("FAILED TO FETCH PRICE ADJUSTMENT DETAIL.", e);
        }
    }

    public AdminPriceAdjustmentDto create(AdminPriceAdjustmentUpsertDto dto) {
        try {
            normalizeAndValidate(dto, null);

            Integer id = priceAdjustmentAdminRepository.create(dto);
            if (id == null) {
                throw new RuntimeException("FAILED TO CREATE PRICE ADJUSTMENT.");
            }

            return getById(id);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("FAILED TO CREATE PRICE ADJUSTMENT.", e);
        }
    }

    public AdminPriceAdjustmentDto update(Integer id, AdminPriceAdjustmentUpsertDto dto) {
        try {
            if (!priceAdjustmentAdminRepository.existsById(id)) {
                throw new RuntimeException("PRICE ADJUSTMENT NOT FOUND: " + id);
            }

            normalizeAndValidate(dto, id);

            boolean ok = priceAdjustmentAdminRepository.update(id, dto);
            if (!ok) {
                throw new RuntimeException("FAILED TO UPDATE PRICE ADJUSTMENT.");
            }

            return getById(id);
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("FAILED TO UPDATE PRICE ADJUSTMENT.", e);
        }
    }

    public void toggleActive(Integer id) {
        try {
            AdminPriceAdjustmentDto current = priceAdjustmentAdminRepository.findById(id);
            if (current == null) {
                throw new RuntimeException("PRICE ADJUSTMENT NOT FOUND: " + id);
            }

            boolean nextActive = !Boolean.TRUE.equals(current.getIsActive());

            if (nextActive) {
                validateOverlapWhenActivate(id, current);
            }

            boolean ok = priceAdjustmentAdminRepository.updateActive(id, nextActive);
            if (!ok) {
                throw new RuntimeException("FAILED TO TOGGLE PRICE ADJUSTMENT STATUS.");
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("FAILED TO TOGGLE PRICE ADJUSTMENT STATUS.", e);
        }
    }

    public void delete(Integer id) {
        try {
            if (!priceAdjustmentAdminRepository.existsById(id)) {
                throw new RuntimeException("PRICE ADJUSTMENT NOT FOUND: " + id);
            }

            boolean ok = priceAdjustmentAdminRepository.delete(id);
            if (!ok) {
                throw new RuntimeException("FAILED TO DELETE PRICE ADJUSTMENT.");
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("FAILED TO DELETE PRICE ADJUSTMENT.", e);
        }
    }

    private void normalizeAndValidate(AdminPriceAdjustmentUpsertDto dto, Integer excludeId) {
        if (dto == null) {
            throw new RuntimeException("INVALID REQUEST.");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("NAME MUST NOT BE EMPTY.");
        }
        dto.setName(dto.getName().trim());

        if (dto.getAdjustmentType() == null || dto.getAdjustmentType().trim().isEmpty()) {
            throw new RuntimeException("ADJUSTMENT TYPE MUST NOT BE EMPTY.");
        }

        String adjustmentType = dto.getAdjustmentType().trim().toUpperCase(Locale.ROOT);
        if (!"AMOUNT".equals(adjustmentType) && !"PERCENT".equals(adjustmentType)) {
            throw new RuntimeException("ADJUSTMENT TYPE MUST BE AMOUNT OR PERCENT.");
        }
        dto.setAdjustmentType(adjustmentType);

        if (dto.getValue() == null) {
            throw new RuntimeException("VALUE MUST NOT BE EMPTY.");
        }

        if ("PERCENT".equals(adjustmentType) && dto.getValue().doubleValue() <= -100d) {
            throw new RuntimeException("PERCENT VALUE MUST BE GREATER THAN -100.");
        }

        if (dto.getIsActive() == null) {
            dto.setIsActive(true);
        }

        normalizeApplyOnDays(dto);

        boolean hasDays = dto.getApplyOnDays() != null && !dto.getApplyOnDays().trim().isEmpty();
        boolean hasStart = dto.getStartDate() != null;
        boolean hasEnd = dto.getEndDate() != null;
        boolean hasRange = hasStart || hasEnd;

        if (hasDays && hasRange) {
            throw new RuntimeException("ONLY ONE APPLY MODE IS ALLOWED: DAYS OR DATE RANGE.");
        }

        if (!hasDays && !hasRange) {
            throw new RuntimeException("YOU MUST CHOOSE APPLY ON DAYS OR DATE RANGE.");
        }

        if (hasDays) {
            dto.setStartDate(null);
            dto.setEndDate(null);

            if (Boolean.TRUE.equals(dto.getIsActive())
                    && priceAdjustmentAdminRepository.existsActiveOverlapDays(excludeId, dto.getAdjustmentType(), dto.getApplyOnDays())) {
                throw new RuntimeException("ACTIVE RULE OVERLAPS ON DAYS WITH THE SAME ADJUSTMENT TYPE.");
            }
        }

        if (hasRange) {
            if (!hasStart || !hasEnd) {
                throw new RuntimeException("START DATE AND END DATE ARE REQUIRED.");
            }

            if (dto.getStartDate().isAfter(dto.getEndDate())) {
                throw new RuntimeException("START DATE MUST BE BEFORE OR EQUAL TO END DATE.");
            }

            dto.setApplyOnDays(null);

            if (Boolean.TRUE.equals(dto.getIsActive())
                    && priceAdjustmentAdminRepository.existsActiveOverlapDateRange(
                            excludeId,
                            dto.getAdjustmentType(),
                            dto.getStartDate(),
                            dto.getEndDate())) {
                throw new RuntimeException("ACTIVE RULE OVERLAPS ON DATE RANGE WITH THE SAME ADJUSTMENT TYPE.");
            }
        }
    }

    private void validateOverlapWhenActivate(Integer id, AdminPriceAdjustmentDto current) {
        boolean hasDays = current.getApplyOnDays() != null && !current.getApplyOnDays().trim().isEmpty();

        if (hasDays) {
            if (priceAdjustmentAdminRepository.existsActiveOverlapDays(
                    id,
                    current.getAdjustmentType(),
                    current.getApplyOnDays())) {
                throw new RuntimeException("CANNOT ACTIVATE RULE. DAYS OVERLAP WITH ANOTHER ACTIVE RULE OF THE SAME TYPE.");
            }
        } else {
            if (current.getStartDate() != null
                    && current.getEndDate() != null
                    && priceAdjustmentAdminRepository.existsActiveOverlapDateRange(
                            id,
                            current.getAdjustmentType(),
                            current.getStartDate(),
                            current.getEndDate())) {
                throw new RuntimeException("CANNOT ACTIVATE RULE. DATE RANGE OVERLAPS WITH ANOTHER ACTIVE RULE OF THE SAME TYPE.");
            }
        }
    }

    private void normalizeApplyOnDays(AdminPriceAdjustmentUpsertDto dto) {
        if (dto.getApplyOnDays() == null || dto.getApplyOnDays().trim().isEmpty()) {
            dto.setApplyOnDays(null);
            return;
        }

        String[] arr = dto.getApplyOnDays().split(",");
        Set<String> normalized = new LinkedHashSet<>();

        for (String item : arr) {
            String day = item == null ? "" : item.trim();
            if (day.isEmpty()) {
                continue;
            }

            String formatted = formatDay(day);
            if (formatted == null) {
                throw new RuntimeException("INVALID DAY: " + day + ". ONLY Mon,Tue,Wed,Thu,Fri,Sat,Sun ARE ALLOWED.");
            }

            normalized.add(formatted);
        }

        if (normalized.isEmpty()) {
            dto.setApplyOnDays(null);
            return;
        }

        List<String> values = new ArrayList<>(normalized);
        dto.setApplyOnDays(String.join(",", values));
    }

    private String formatDay(String day) {
        String d = day.trim().toLowerCase(Locale.ROOT);
        return switch (d) {
            case "mon" -> "Mon";
            case "tue" -> "Tue";
            case "wed" -> "Wed";
            case "thu" -> "Thu";
            case "fri" -> "Fri";
            case "sat" -> "Sat";
            case "sun" -> "Sun";
            default -> null;
        };
    }
}