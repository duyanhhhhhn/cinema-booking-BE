package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CinemaBooking.Group2.dtos.booking.BookingCalculateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCalculateResponse;
import CinemaBooking.Group2.dtos.booking.BookingCreateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCreateResponse;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingConcession;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.PriceAdjustment;
import CinemaBooking.Group2.models.Seat;
import CinemaBooking.Group2.models.Showtime;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;
import CinemaBooking.Group2.repositories.BookingRepository;
import CinemaBooking.Group2.repositories.ComboRepository;
import CinemaBooking.Group2.repositories.SeatRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;
import CinemaBooking.Group2.repositories.VoucherRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ComboRepository comboRepository;
    
    @Autowired
    private VoucherRepository voucherRepository;

    public BookingCalculateResponse calculateBooking(BookingCalculateRequest request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId());
        if (showtime == null) {
            throw new RuntimeException("Showtime not found");
        }

        BookingCalculateResponse response = new BookingCalculateResponse();
        BookingCalculateResponse.PriceBreakdown breakdown = new BookingCalculateResponse.PriceBreakdown();
        
        // 1. Tính giá vé cơ bản và phụ thu ghế VIP
        BigDecimal basePrice = BigDecimal.ZERO;
        BigDecimal seatExtraPrice = BigDecimal.ZERO;
        List<BookingCalculateResponse.PriceBreakdown.SeatDetail> seatDetails = new ArrayList<>();
        
        for (Integer seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId);
            if (seat == null) {
                throw new RuntimeException("Seat not found: " + seatId);
            }
            
            // Kiểm tra ghế đã được đặt chưa
            if (bookingRepository.isSeatBooked(seatId, request.getShowtimeId())) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatCode());
            }
            
            BigDecimal seatBasePrice = showtime.getBasePrice();
            BigDecimal extraPrice = seat.getExtraPrice() != null ? seat.getExtraPrice() : BigDecimal.ZERO;
            
            basePrice = basePrice.add(seatBasePrice);
            seatExtraPrice = seatExtraPrice.add(extraPrice);
            
            // Add to breakdown
            BookingCalculateResponse.PriceBreakdown.SeatDetail seatDetail = 
            new BookingCalculateResponse.PriceBreakdown.SeatDetail();
            seatDetail.setSeatId(seatId);
            seatDetail.setSeatCode(seat.getSeatCode());
            seatDetail.setSeatType(seat.getSeatType().name());
            seatDetail.setBasePrice(seatBasePrice);
            seatDetail.setExtraPrice(extraPrice);
            seatDetail.setTotalPrice(seatBasePrice.add(extraPrice));
            seatDetails.add(seatDetail);
        }
        
        breakdown.setSeats(seatDetails);
        
        // 2. Tính giá combo
        BigDecimal comboPrice = BigDecimal.ZERO;
        List<BookingCalculateResponse.PriceBreakdown.ComboDetail> comboDetails = new ArrayList<>();
        
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (BookingCalculateRequest.ComboItem comboItem : request.getCombos()) {
                Combo combo = comboRepository.findById(comboItem.getComboId());
                if (combo == null) {
                    throw new RuntimeException("Combo not found: " + comboItem.getComboId());
                }
                
                BigDecimal itemTotal = combo.getPrice()
                    .multiply(new BigDecimal(comboItem.getQuantity()));
                comboPrice = comboPrice.add(itemTotal);
                
                // Add to breakdown
                BookingCalculateResponse.PriceBreakdown.ComboDetail comboDetail = 
                    new BookingCalculateResponse.PriceBreakdown.ComboDetail();
                comboDetail.setComboId(combo.getId());
                comboDetail.setComboName(combo.getName());
                comboDetail.setQuantity(comboItem.getQuantity());
                comboDetail.setUnitPrice(combo.getPrice());
                comboDetail.setTotalPrice(itemTotal);
                comboDetails.add(comboDetail);
            }
        }
        
        breakdown.setCombos(comboDetails);
        
        // 3. Tính phụ thu lễ (nếu có)
        BigDecimal holidaySurcharge = BigDecimal.ZERO;
        List<PriceAdjustment> activeAdjustments = bookingRepository.findActivePriceAdjustments();
        
        LocalDate showtimeDate = showtime.getStartTime().toLocalDate();
        DayOfWeek dayOfWeek = showtimeDate.getDayOfWeek();
        String dayName = dayOfWeek.name();
        
        for (PriceAdjustment adjustment : activeAdjustments) {
            String applyOnDays = adjustment.getApplyOnDays();
            if (applyOnDays != null && applyOnDays.contains(dayName)) {
                BigDecimal subtotalBeforeHoliday = basePrice.add(seatExtraPrice).add(comboPrice);
                
                if (adjustment.getAdjustmentType() == PriceAdjustment.AdjustmentType.PERCENT) {
                    holidaySurcharge = subtotalBeforeHoliday
                        .multiply(adjustment.getValue())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                } else {
                    holidaySurcharge = adjustment.getValue();
                }
                
                // Add to breakdown
                BookingCalculateResponse.PriceBreakdown.HolidayDetail holidayDetail = 
                    new BookingCalculateResponse.PriceBreakdown.HolidayDetail();
                holidayDetail.setHolidayName(adjustment.getName());
                holidayDetail.setAdjustmentType(adjustment.getAdjustmentType().name());
                holidayDetail.setValue(adjustment.getValue());
                holidayDetail.setSurchargeAmount(holidaySurcharge);
                breakdown.setHoliday(holidayDetail);
                
                break; // Chỉ áp dụng 1 price adjustment
            }
        }
        
        // 4. Tính subtotal
        BigDecimal subtotal = basePrice
            .add(seatExtraPrice)
            .add(comboPrice)
            .add(holidaySurcharge);
        
        // 5. Áp dụng voucher
        BigDecimal discountAmount = BigDecimal.ZERO;
        String voucherCode = null;
        
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            Voucher voucher = voucherRepository.findByCode(request.getVoucherCode());
            
            if (voucher != null) {
                // Kiểm tra voucher còn hợp lệ không
                LocalDate now = LocalDate.now();
                if (voucher.getStartAt().isAfter(now) || voucher.getEndAt().isBefore(now)) {
                    throw new RuntimeException("Voucher has expired or not yet valid");
                }
                
                if (voucher.getUsedCount() >= voucher.getUsageLimit()) {
                    throw new RuntimeException("Voucher usage limit reached");
                }
                
                // Kiểm tra min order amount
                if (voucher.getMinOrderAmount() != null && 
                    subtotal.compareTo(voucher.getMinOrderAmount()) < 0) {
                    throw new RuntimeException("Order amount does not meet minimum requirement for voucher");
                }
                
                // Tính discount
                if (voucher.getDiscountType() == DiscountType.PERCENT) {
                    discountAmount = subtotal
                        .multiply(voucher.getDiscountValue())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                } else {
                    discountAmount = voucher.getDiscountValue();
                }
                
                // Discount không vượt quá subtotal
                if (discountAmount.compareTo(subtotal) > 0) {
                    discountAmount = subtotal;
                }
                
                voucherCode = voucher.getCode();
            } else {
                throw new RuntimeException("Voucher not found");
            }
        }
        
        // 6. Tính tổng tiền cuối cùng
        BigDecimal totalPrice = subtotal.subtract(discountAmount);
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            totalPrice = BigDecimal.ZERO;
        }
        
        // Set response
        response.setBasePrice(basePrice);
        response.setSeatExtraPrice(seatExtraPrice);
        response.setComboPrice(comboPrice);
        response.setHolidaySurcharge(holidaySurcharge);
        response.setSubtotal(subtotal);
        response.setDiscountAmount(discountAmount);
        response.setTotalPrice(totalPrice);
        response.setVoucherCode(voucherCode);
        response.setBreakdown(breakdown);
        
        return response;
    }

    /**
     * Tạo booking mới với trạng thái PENDING
     */
    @Transactional
    public BookingCreateResponse createBooking(BookingCreateRequest request) {
        // Validate showtime
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId());
        if (showtime == null) {
            throw new RuntimeException("Showtime not found");
        }

        // Validate seats availability
        for (Integer seatId : request.getSeatIds()) {
            if (bookingRepository.isSeatBooked(seatId, request.getShowtimeId())) {
                Seat seat = seatRepository.findById(seatId);
                throw new RuntimeException("Seat already booked: " + 
                    (seat != null ? seat.getSeatCode() : seatId));
            }
        }

        // Calculate total price (reuse calculate logic)
        BookingCalculateRequest calculateRequest = new BookingCalculateRequest();
        calculateRequest.setShowtimeId(request.getShowtimeId());
        calculateRequest.setSeatIds(request.getSeatIds());
        
        // Convert combos from CreateRequest to CalculateRequest
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            List<BookingCalculateRequest.ComboItem> calculateCombos = new ArrayList<>();
            for (BookingCreateRequest.ComboItem createCombo : request.getCombos()) {
                BookingCalculateRequest.ComboItem calcCombo = new BookingCalculateRequest.ComboItem();
                calcCombo.setComboId(createCombo.getComboId());
                calcCombo.setQuantity(createCombo.getQuantity());
                calculateCombos.add(calcCombo);
            }
            calculateRequest.setCombos(calculateCombos);
        }
        
        calculateRequest.setVoucherCode(request.getVoucherCode());
        
        BookingCalculateResponse calculation = calculateBooking(calculateRequest);

        // Create booking
        Booking booking = new Booking();
        booking.setBookingCode(bookingRepository.generateBookingCode());
        booking.setUserId(request.getUserId());
        booking.setShowtimeId(request.getShowtimeId());
        booking.setTotalPrice(calculation.getTotalPrice());
        booking.setDiscountAmount(calculation.getDiscountAmount());
        booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        
        // Set payment method if provided
        if (request.getPaymentMethod() != null && !request.getPaymentMethod().isEmpty()) {
            try {
                booking.setPaymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid payment method: " + request.getPaymentMethod());
            }
        }
        
        // Set voucher if used
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            Voucher voucher = voucherRepository.findByCode(request.getVoucherCode());
            if (voucher != null) {
                booking.setVoucherId(voucher.getId());
            }
        }
        
        // Save booking
        int bookingId = bookingRepository.createBooking(booking);
        booking.setId(bookingId);

        // Save booking seats
        for (BookingCalculateResponse.PriceBreakdown.SeatDetail seatDetail : 
             calculation.getBreakdown().getSeats()) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBookingId(bookingId);
            bookingSeat.setSeatId(seatDetail.getSeatId());
            bookingSeat.setSeatPrice(seatDetail.getTotalPrice());
            bookingSeat.setTicketCode(generateTicketCode(booking.getBookingCode(), seatDetail.getSeatCode()));
            bookingRepository.createBookingSeat(bookingSeat);
        }

        // Save booking concessions (combos)
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (BookingCreateRequest.ComboItem comboItem : request.getCombos()) {
                Combo combo = comboRepository.findById(comboItem.getComboId());
                if (combo != null) {
                    BookingConcession concession = new BookingConcession();
                    concession.setBookingId(bookingId);
                    concession.setComboId(combo.getId());
                    concession.setQuantity(comboItem.getQuantity());
                    concession.setPrice(combo.getPrice().multiply(new BigDecimal(comboItem.getQuantity())));
                    bookingRepository.createBookingConcession(concession);
                }
            }
        }

        // Build response
        BookingCreateResponse response = new BookingCreateResponse();
        response.setBookingId(bookingId);
        response.setBookingCode(booking.getBookingCode());
        response.setUserId(booking.getUserId());
        response.setShowtimeId(booking.getShowtimeId());
        response.setTotalPrice(booking.getTotalPrice());
        response.setDiscountAmount(booking.getDiscountAmount());
        response.setPaymentStatus(booking.getPaymentStatus().name());
        response.setPaymentMethod(booking.getPaymentMethod() != null ? 
            booking.getPaymentMethod().name() : null);
        response.setCreatedAt(booking.getCreatedAt());
        response.setMessage("Booking created successfully with PENDING status");

        return response;
    }

    private String generateTicketCode(String bookingCode, String seatCode) {
        return bookingCode + "-" + seatCode;
    }
}
