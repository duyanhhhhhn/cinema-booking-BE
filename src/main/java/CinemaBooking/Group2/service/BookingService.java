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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.booking.BookingCalculateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCalculateResponse;
import CinemaBooking.Group2.dtos.booking.BookingCreateRequest;
import CinemaBooking.Group2.dtos.booking.BookingCreateResponse;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.dtos.booking.BookingEmailData;
import CinemaBooking.Group2.dtos.booking.BookingHistoryResponse;
import CinemaBooking.Group2.dtos.booking.WalkInBookingRequest;
import CinemaBooking.Group2.dtos.booking.WalkInBookingResponse;
import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingConcession;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.Combo;
import CinemaBooking.Group2.models.Product;
import CinemaBooking.Group2.models.PriceAdjustment;
import CinemaBooking.Group2.models.Seat;
import CinemaBooking.Group2.models.Showtime;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.models.Voucher;
import CinemaBooking.Group2.models.Enum.DiscountType;
import CinemaBooking.Group2.repositories.BookingRepository;
import CinemaBooking.Group2.repositories.CinemaRepository;
import CinemaBooking.Group2.repositories.ComboRepository;
import CinemaBooking.Group2.repositories.ProductRepository;
import CinemaBooking.Group2.repositories.MovieRepository;
import CinemaBooking.Group2.repositories.RoomRepository;
import CinemaBooking.Group2.repositories.SeatRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.repositories.VoucherRepository;
import CinemaBooking.Group2.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

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

    // Added repositories to fetch Movie/Room/Cinema details instead of relying on showtime.getMovie()/getRoom()
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    public BookingCalculateResponse calculateBooking(BookingCalculateRequest request) {
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId());
        if (showtime == null) {
            throw new RuntimeException("Showtime not found");
        }

        BookingCalculateResponse response = new BookingCalculateResponse();
        BookingCalculateResponse.PriceBreakdown breakdown = new BookingCalculateResponse.PriceBreakdown();
        
        // 1. Tính giá vé và phụ thu ghế VIP
        BigDecimal totalSeatsPrice = BigDecimal.ZERO; // Tổng giá tất cả ghế (bao gồm cả base và extra)
        BigDecimal basePrice = BigDecimal.ZERO;        // Tổng giá vé cơ bản
        BigDecimal seatExtraPrice = BigDecimal.ZERO;   // Tổng phụ thu ghế VIP
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
            BigDecimal seatTotalPrice = seatBasePrice.add(extraPrice);
            
            // Cộng dồn vào tổng
            basePrice = basePrice.add(seatBasePrice);
            seatExtraPrice = seatExtraPrice.add(extraPrice);
            totalSeatsPrice = totalSeatsPrice.add(seatTotalPrice);
            
            // Add to breakdown
            BookingCalculateResponse.PriceBreakdown.SeatDetail seatDetail = 
                new BookingCalculateResponse.PriceBreakdown.SeatDetail();
            seatDetail.setSeatId(seatId);
            seatDetail.setSeatCode(seat.getSeatCode());
            seatDetail.setSeatType(seat.getSeatType().name());
            seatDetail.setBasePrice(seatBasePrice);
            seatDetail.setExtraPrice(extraPrice);
            seatDetail.setTotalPrice(seatTotalPrice);
            seatDetails.add(seatDetail);
        }
        
        breakdown.setSeats(seatDetails);
        
        // 2. Tính giá combo
        BigDecimal comboPrice = BigDecimal.ZERO;
        List<BookingCalculateResponse.PriceBreakdown.ComboDetail> comboDetails = new ArrayList<>();
        
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (BookingCalculateRequest.ComboItem comboItem : request.getCombos()) {
                // Skip invalid combo items (e.g. frontend sends empty combo objects)
                if (comboItem.getComboId() == null || comboItem.getComboId() <= 0) {
                    continue;
                }
                Combo combo = comboRepository.findById(comboItem.getComboId());
                if (combo == null) {
                    throw new RuntimeException("Combo not found: " + comboItem.getComboId());
                }
                if (combo.getPrice() == null) {
                    throw new RuntimeException("Combo price is not set for combo: " + comboItem.getComboId());
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
        
        // 2b. Tính giá sản phẩm lẻ (products)
        BigDecimal productPrice = BigDecimal.ZERO;
        List<BookingCalculateResponse.PriceBreakdown.ProductDetail> productDetails = new ArrayList<>();
        
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (BookingCalculateRequest.ProductItem productItem : request.getProducts()) {
                Product product = productRepository.findById(productItem.getProductId());
                if (product == null) {
                    throw new RuntimeException("Product not found: " + productItem.getProductId());
                }
                if (product.getPrice() == null) {
                    throw new RuntimeException("Product price is not set for product: " + productItem.getProductId());
                }
                
                BigDecimal itemTotal = product.getPrice()
                    .multiply(new BigDecimal(productItem.getQuantity()));
                productPrice = productPrice.add(itemTotal);
                
                // Add to breakdown
                BookingCalculateResponse.PriceBreakdown.ProductDetail productDetail = 
                    new BookingCalculateResponse.PriceBreakdown.ProductDetail();
                productDetail.setProductId(product.getId());
                productDetail.setProductName(product.getName());
                productDetail.setQuantity(productItem.getQuantity());
                productDetail.setUnitPrice(product.getPrice());
                productDetail.setTotalPrice(itemTotal);
                productDetails.add(productDetail);
            }
        }
        
        breakdown.setProducts(productDetails);
        
        // 3. Tính phụ thu lễ (nếu có) - áp dụng trên tổng giá ghế + combo + product
        BigDecimal holidaySurcharge = BigDecimal.ZERO;
        List<PriceAdjustment> activeAdjustments = bookingRepository.findActivePriceAdjustments();
        
        LocalDate showtimeDate = showtime.getStartTime().toLocalDate();
        DayOfWeek dayOfWeek = showtimeDate.getDayOfWeek();
        String dayName = dayOfWeek.name();
        
        for (PriceAdjustment adjustment : activeAdjustments) {
            String applyOnDays = adjustment.getApplyOnDays();
            if (applyOnDays != null && applyOnDays.contains(dayName)) {
                BigDecimal subtotalBeforeHoliday = totalSeatsPrice.add(comboPrice).add(productPrice);
                
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
        
        // 4. Tính subtotal - sử dụng totalSeatsPrice thay vì basePrice + seatExtraPrice để tránh sai lệch
        BigDecimal subtotal = totalSeatsPrice
            .add(comboPrice)
            .add(productPrice)
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
        
        // Set response - đảm bảo tính nhất quán
        response.setBasePrice(basePrice);
        response.setSeatExtraPrice(seatExtraPrice);
        response.setComboPrice(comboPrice);
        response.setProductPrice(productPrice);
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
        if (request.getCombos() != null && request.getCombos().isEmpty() == false) {
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
                if (combo != null && combo.getPrice() != null) {
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

        // If payment method is an online provider, generate payment URL and include in response
        if (booking.getPaymentMethod() != null && booking.getPaymentMethod() != Booking.PaymentMethod.CASH) {
            String provider = booking.getPaymentMethod().name();
            String channel = request.getBankCode();
            logger.info("Generating payment URL for booking {} with provider {} channel={}", bookingId, provider, channel);
            // Use the in-memory booking to generate payment URL to avoid re-loading possibly incomplete DB record
            try {
                String url = paymentService.createPaymentUrlForBooking(booking, provider, channel);
                if (url == null || url.isBlank()) {
                    throw new RuntimeException("Payment provider returned empty URL");
                }
                response.setPaymentUrl(url);
            } catch (Exception ex) {
                logger.error("Failed to create payment URL for booking {}: {}", bookingId, ex.getMessage(), ex);
                // Throw RuntimeException so @Transactional rolls back the booking
                throw new RuntimeException("Failed to create payment URL: " + ex.getMessage());
            }
        }

        // ❌ REMOVED: Don't send email here - booking is still PENDING
        // Email will be sent only after successful payment in PaymentService
        // This prevents sending incomplete emails before payment is confirmed

         return response;
     }

    private String generateTicketCode(String bookingCode, String seatCode) {
        return bookingCode + "-" + seatCode;
    }

    /**
     * Get booking history for a specific user (no filters)
     */
    public PageResponse<BookingHistoryResponse> getMyBookingHistory(
            int userId, 
            String startDate, String endDate, 
            Booking.PaymentStatus status, String movieTitle, 
            int page, int perPage
    ) {
        if (page < 1) page = 1;
        if (perPage < 1) perPage = 10;
        if (startDate != null && !startDate.isBlank() && endDate != null && !endDate.isBlank()) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (start.isAfter(end)) {
                throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc!");
            }
        }
        
        return bookingRepository.searchBookings(
                userId, startDate, endDate, status, movieTitle, page, perPage
        );
    }
    public BookingDetailResponse  getMyBookingByCode(int userId, String code) {
        
        var resp = bookingRepository.getMyBookingByCode(userId, code);
      if (resp == null || resp.getData() == null) {
          String msg = (resp != null && resp.getMessage() != null) ? resp.getMessage() : "Booking not found";
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
      }
        return resp.getData();
    }
    
    public BookingDetailResponse getBookingByCodeAdmin(String code) {
        var resp = bookingRepository.getBookingByCodeAdmin(code);
        if (resp == null || resp.getData() == null) {
            String msg = (resp != null && resp.getMessage() != null) ? resp.getMessage() : "Booking not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
        return resp.getData();
    }
    
    /**
     * Tạo booking cho khách hàng vãng lai (walk-in) - chỉ hỗ trợ CASH và MOMO
     * Phương thức này dành cho nhân viên tại quầy tạo booking cho khách hàng không có tài khoản
     */
    @Transactional
    public WalkInBookingResponse createWalkInBooking(WalkInBookingRequest request) {
        // Validate payment method - chỉ cho phép CASH hoặc MOMO
        if (!request.getPaymentMethod().equals("CASH") && !request.getPaymentMethod().equals("MOMO")) {
            throw new RuntimeException("Walk-in customers can only use CASH or MOMO payment methods");
        }
        
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

        // Calculate total price using existing calculate logic
        BookingCalculateRequest calculateRequest = new BookingCalculateRequest();
        calculateRequest.setShowtimeId(request.getShowtimeId());
        calculateRequest.setSeatIds(request.getSeatIds());
        
        // Convert combos if provided
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            List<BookingCalculateRequest.ComboItem> calculateCombos = new ArrayList<>();
            for (WalkInBookingRequest.ComboItem walkInCombo : request.getCombos()) {
                // Skip invalid combo items (e.g. frontend sends empty combo objects)
                if (walkInCombo.getComboId() == null || walkInCombo.getComboId() <= 0) {
                    continue;
                }
                BookingCalculateRequest.ComboItem calcCombo = new BookingCalculateRequest.ComboItem();
                calcCombo.setComboId(walkInCombo.getComboId());
                calcCombo.setQuantity(walkInCombo.getQuantity());
                calculateCombos.add(calcCombo);
            }
            if (!calculateCombos.isEmpty()) {
                calculateRequest.setCombos(calculateCombos);
            }
        }
        
        // Convert products if provided
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            List<BookingCalculateRequest.ProductItem> calculateProducts = new ArrayList<>();
            for (WalkInBookingRequest.ProductItem walkInProduct : request.getProducts()) {
                // Skip invalid product items
                if (walkInProduct.getProductId() == null || walkInProduct.getProductId() <= 0) {
                    continue;
                }
                BookingCalculateRequest.ProductItem calcProduct = new BookingCalculateRequest.ProductItem();
                calcProduct.setProductId(walkInProduct.getProductId());
                calcProduct.setQuantity(walkInProduct.getQuantity());
                calculateProducts.add(calcProduct);
            }
            calculateRequest.setProducts(calculateProducts);
        }
        
        calculateRequest.setVoucherCode(request.getVoucherCode());
        
        BookingCalculateResponse calculation = calculateBooking(calculateRequest);

        // Create booking for walk-in customer (no userId - set to null or special value)
        Booking booking = new Booking();
        booking.setBookingCode(bookingRepository.generateBookingCode());
        booking.setUserId(null); // Walk-in customers don't have user accounts
        booking.setCreatedByStaffId(request.getStaffId()); // Track which staff created the booking
        booking.setShowtimeId(request.getShowtimeId());
        booking.setTotalPrice(calculation.getTotalPrice());
        booking.setDiscountAmount(calculation.getDiscountAmount());
        
        // Set payment method and status based on payment type
        booking.setPaymentMethod(Booking.PaymentMethod.valueOf(request.getPaymentMethod()));
        
        if (request.getPaymentMethod().equals("CASH")) {
            // For cash payments, mark as PAID immediately since payment is received at counter
            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            booking.setPaidAt(java.time.LocalDateTime.now());
        } else {
            // For MoMo, start with PENDING status
            booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
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
        List<WalkInBookingResponse.SeatInfo> seatInfos = new ArrayList<>();
        for (BookingCalculateResponse.PriceBreakdown.SeatDetail seatDetail : 
             calculation.getBreakdown().getSeats()) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBookingId(bookingId);
            bookingSeat.setSeatId(seatDetail.getSeatId());
            bookingSeat.setSeatPrice(seatDetail.getTotalPrice());
            bookingSeat.setTicketCode(generateTicketCode(booking.getBookingCode(), seatDetail.getSeatCode()));
            bookingRepository.createBookingSeat(bookingSeat);
            
            // Add to response
            seatInfos.add(new WalkInBookingResponse.SeatInfo(
                seatDetail.getSeatId(),
                seatDetail.getSeatCode(),
                seatDetail.getSeatType(),
                seatDetail.getTotalPrice()
            ));
        }

        // Save booking concessions (combos) if any
        List<WalkInBookingResponse.ComboInfo> comboInfos = new ArrayList<>();
        if (request.getCombos() != null && !request.getCombos().isEmpty()) {
            for (WalkInBookingRequest.ComboItem comboItem : request.getCombos()) {
                // Skip invalid combo items
                if (comboItem.getComboId() == null || comboItem.getComboId() <= 0) {
                    continue;
                }
                Combo combo = comboRepository.findById(comboItem.getComboId());
                if (combo != null && combo.getPrice() != null) {
                    BookingConcession concession = new BookingConcession();
                    concession.setBookingId(bookingId);
                    concession.setComboId(combo.getId());
                    concession.setQuantity(comboItem.getQuantity());
                    concession.setPrice(combo.getPrice().multiply(new BigDecimal(comboItem.getQuantity())));
                    bookingRepository.createBookingConcession(concession);
                    
                    // Add to response
                    comboInfos.add(new WalkInBookingResponse.ComboInfo(
                        combo.getId(),
                        combo.getName(),
                        comboItem.getQuantity(),
                        combo.getPrice()
                    ));
                }
            }
        }

        // Save booking concessions (products) if any
        List<WalkInBookingResponse.ProductInfo> productInfos = new ArrayList<>();
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (WalkInBookingRequest.ProductItem productItem : request.getProducts()) {
                Product product = productRepository.findById(productItem.getProductId());
                if (product != null && product.getPrice() != null) {
                    BookingConcession concession = new BookingConcession();
                    concession.setBookingId(bookingId);
                    concession.setProductId(product.getId());
                    concession.setQuantity(productItem.getQuantity());
                    concession.setPrice(product.getPrice().multiply(new BigDecimal(productItem.getQuantity())));
                    bookingRepository.createBookingConcession(concession);
                    
                    // Add to response
                    productInfos.add(new WalkInBookingResponse.ProductInfo(
                        product.getId(),
                        product.getName(),
                        productItem.getQuantity(),
                        product.getPrice()
                    ));
                }
            }
        }

        // Build response without customer information
        WalkInBookingResponse response = new WalkInBookingResponse();
        response.setBookingId(bookingId);
        response.setBookingCode(booking.getBookingCode());
        response.setShowtimeId(booking.getShowtimeId());
        
        // Get showtime details for response
        // Showtime model only stores movieId and roomId. Fetch details via repositories.
        String movieTitle = "Unknown Movie";
        try {
            var movie = movieRepository.getMovieDetailById(showtime.getMovieId());
            if (movie != null && movie.getTitle() != null) movieTitle = movie.getTitle();
        } catch (Exception ex) {
            // ignore and use fallback
        }
        response.setMovieTitle(movieTitle);
        
        String roomName = "Unknown Room";
        String cinemaName = "Unknown Cinema";
        try {
            var room = roomRepository.findById(showtime.getRoomId());
            if (room != null) {
                if (room.getName() != null) roomName = room.getName();
                try {
                    var cinema = cinemaRepository.findById(room.getCinemaId());
                    if (cinema != null && cinema.getName() != null) cinemaName = cinema.getName();
                } catch (Exception ex) {
                    // ignore
                }
            }
        } catch (Exception ex) {
            // ignore
        }
        response.setCinemaName(cinemaName);
        response.setRoomName(roomName);
        response.setShowtime(showtime.getStartTime());
        
        response.setSeats(seatInfos);
        response.setCombos(comboInfos);
        response.setProducts(productInfos);
        response.setVoucherCode(request.getVoucherCode());
        response.setDiscountAmount(booking.getDiscountAmount());
        response.setTotalPrice(booking.getTotalPrice());
        response.setPaymentMethod(request.getPaymentMethod());
        response.setPaymentStatus(booking.getPaymentStatus().name());
        response.setCreatedAt(booking.getCreatedAt());
        response.setCreatedByStaffId(request.getStaffId());
        response.setCreatedByStaffName("Staff ID: " + request.getStaffId());

        // Nếu thanh toán MOMO, generate payment URL để hiển thị QR
        if ("MOMO".equalsIgnoreCase(request.getPaymentMethod())) {
            logger.info("Generating MOMO payment URL for walk-in booking {}", bookingId);
            try {
                String paymentUrl = paymentService.createPaymentUrlForBooking(booking, "MOMO");
                if (paymentUrl == null || paymentUrl.isBlank()) {
                    throw new RuntimeException("MOMO payment provider returned empty URL");
                }
                response.setPaymentUrl(paymentUrl);
                logger.info("MOMO payment URL generated for walk-in booking {}: {}", bookingId, paymentUrl);
            } catch (Exception ex) {
                logger.error("Failed to create MOMO payment URL for walk-in booking {}: {}", bookingId, ex.getMessage(), ex);
                // Rollback transaction vì không tạo được URL thanh toán
                throw new RuntimeException("Failed to create MOMO payment URL: " + ex.getMessage());
            }
        }

        logger.info("Walk-in booking created successfully: {} by staff: {}", 
            booking.getBookingCode(), request.getStaffId());

        return response;
    }
}
