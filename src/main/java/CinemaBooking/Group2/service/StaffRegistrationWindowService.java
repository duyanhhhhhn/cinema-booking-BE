package CinemaBooking.Group2.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.staff.StaffRegistrationWindowResponseDTO;

@Service
public class StaffRegistrationWindowService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final AtomicBoolean forceOpen = new AtomicBoolean(false);

    public boolean isForceOpen() {
        return forceOpen.get();
    }

    public void setForceOpen(boolean enabled) {
        forceOpen.set(enabled);
    }

    public boolean isWeekendOpenToday() {
        DayOfWeek dayOfWeek = LocalDate.now(ZONE_VN).getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    public boolean canStaffRegisterToday() {
        return isForceOpen() || isWeekendOpenToday();
    }

    public StaffRegistrationWindowResponseDTO toResponse() {
        StaffRegistrationWindowResponseDTO dto = new StaffRegistrationWindowResponseDTO();
        dto.setForceOpen(isForceOpen());
        dto.setWeekendOpenToday(isWeekendOpenToday());
        dto.setStaffCanRegisterNow(canStaffRegisterToday());
        return dto;
    }
}
