package CinemaBooking.Group2.controllers.admin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentFilterDto;
import CinemaBooking.Group2.dtos.price_adjustment.admin.AdminPriceAdjustmentUpsertDto;
import CinemaBooking.Group2.service.PriceAdjustmentAdminService;

@RestController
@RequestMapping("/api/admin/price-adjustments")
public class PriceAdjustmentAdminController {

    @Autowired
    private PriceAdjustmentAdminService priceAdjustmentAdminService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminPriceAdjustmentDto>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive
    ) {
        AdminPriceAdjustmentFilterDto filter = new AdminPriceAdjustmentFilterDto();
        filter.setKeyword(keyword);
        filter.setIsActive(isActive);

        List<AdminPriceAdjustmentDto> data = priceAdjustmentAdminService.getAll(filter);
        return ResponseEntity.ok(new ApiResponse<>("OK", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminPriceAdjustmentDto>> getById(@PathVariable Integer id) {
        AdminPriceAdjustmentDto data = priceAdjustmentAdminService.getById(id);
        return ResponseEntity.ok(new ApiResponse<>("OK", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminPriceAdjustmentDto>> create(
            @RequestParam String name,
            @RequestParam String adjustmentType,
            @RequestParam BigDecimal value,
            @RequestParam(required = false) String applyOnDays,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive
    ) {
        AdminPriceAdjustmentUpsertDto dto = new AdminPriceAdjustmentUpsertDto();
        dto.setName(name);
        dto.setAdjustmentType(adjustmentType);
        dto.setValue(value);
        dto.setApplyOnDays(applyOnDays);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setIsActive(isActive);

        AdminPriceAdjustmentDto data = priceAdjustmentAdminService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Created price adjustment successfully", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminPriceAdjustmentDto>> update(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String adjustmentType,
            @RequestParam BigDecimal value,
            @RequestParam(required = false) String applyOnDays,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "true") Boolean isActive
    ) {
        AdminPriceAdjustmentUpsertDto dto = new AdminPriceAdjustmentUpsertDto();
        dto.setName(name);
        dto.setAdjustmentType(adjustmentType);
        dto.setValue(value);
        dto.setApplyOnDays(applyOnDays);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setIsActive(isActive);

        AdminPriceAdjustmentDto data = priceAdjustmentAdminService.update(id, dto);

        return ResponseEntity.ok(new ApiResponse<>("Updated price adjustment successfully", data));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Object>> toggleActive(@PathVariable Integer id) {
        priceAdjustmentAdminService.toggleActive(id);
        return ResponseEntity.ok(new ApiResponse<>("Toggled price adjustment status successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) {
        priceAdjustmentAdminService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("Deleted price adjustment successfully", null));
    }
}