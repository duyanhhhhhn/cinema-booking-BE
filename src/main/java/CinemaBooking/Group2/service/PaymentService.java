package CinemaBooking.Group2.service;

import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.repositories.BookingRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;
import CinemaBooking.Group2.repositories.MovieRepository;
import CinemaBooking.Group2.repositories.RoomRepository;
import CinemaBooking.Group2.repositories.CinemaRepository;
import CinemaBooking.Group2.repositories.UserRepository;
import CinemaBooking.Group2.dtos.booking.BookingEmailData;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.Showtime;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Room;
import CinemaBooking.Group2.models.Cinema;
import CinemaBooking.Group2.models.User;
import CinemaBooking.Group2.repositories.SeatHoldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    
    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.payUrl:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl:http://localhost:8000/payment/return}")
    private String vnpReturnUrl;

    // ================ MOMO CONFIG ================
    @Value("${momo.partner-code:}")
    private String momoPartnerCode;

    @Value("${momo.access-key:}")
    private String momoAccessKey;

    @Value("${momo.secret-key:}")
    private String momoSecretKey;

    @Value("${momo.endpoint:}")
    private String momoEndpoint;

    @Value("${momo.return-url:}")
    private String momoReturnUrl;

    @Value("${momo.ipn-url:}")
    private String momoIpnUrl;

    // simple RestTemplate for calling momo API
    private org.springframework.web.client.RestTemplate rest = new org.springframework.web.client.RestTemplate();

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CinemaBooking.Group2.repositories.concessions.ComboRepository comboRepository;

    @Autowired
    private SeatHoldRepository seatHoldRepository;

    @Autowired
    private CinemaBooking.Group2.repositories.SeatRepository seatRepository;

    // =========================
    // PUBLIC API - Compatibility methods
    // =========================

    /**
     * Method tương thích với BookingService - không cần HttpServletRequest
     */
    public String createPaymentUrl(int bookingId, String provider) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found: " + bookingId);
        
        validateBookingHold(booking);

        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    // New overload: allow optional channel/bankCode for providers (e.g. ATM via Momo)
    public String createPaymentUrl(int bookingId, String provider, String channel) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found: " + bookingId);
        
        validateBookingHold(booking);

        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking, channel);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    /**
     * Method tương thích với BookingService - nhận Booking object
     */
    public String createPaymentUrlForBooking(Booking booking, String provider) throws Exception {
        if (booking == null) throw new RuntimeException("Booking is null");
        
        validateBookingHold(booking);

        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking, null);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    // New overload to allow bankCode/channel selection (e.g. ATM)
    public String createPaymentUrlForBooking(Booking booking, String provider, String channel) throws Exception {
        if (booking == null) throw new RuntimeException("Booking is null");
        
        validateBookingHold(booking);

        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking, channel);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    private void validateBookingHold(Booking booking) {
        // Find seats associated with this booking
        List<BookingSeat> seats = bookingRepository.findBookingSeats(booking.getId());
        boolean hasExpired = false;
        
        LocalDateTime now = LocalDateTime.now();

        for (BookingSeat seat : seats) {
            // Check hold expiration for each seat
            LocalDateTime expiresAt = seatHoldRepository.getHoldExpiration(booking.getShowtimeId(), seat.getSeatId());
            
            // If expiry is null (no hold) or in the past (expired)
            if (expiresAt == null || expiresAt.isBefore(now)) {
                // Delete the seat hold record
                seatHoldRepository.deleteSeatHold(booking.getShowtimeId(), seat.getSeatId());
                hasExpired = true;
                logger.info("Seat hold expired for seat {} in booking {}", seat.getSeatId(), booking.getId());
            }
        }
        
        if (hasExpired) {
            // Optional: You could update booking status to FAILED/CANCELLED here if desired
            // bookingRepository.updateBookingPaymentStatus(booking.getId(), Booking.PaymentStatus.CANCELLED.name(), null, null);
            throw new RuntimeException("Thời gian giữ ghế đã hết. Vui lòng đặt lại vé.");
        }
    }

    /**
     * Retry payment using booking code.
     */
    public String retryPayment(String bookingCode, String provider) throws Exception {
        return retryPayment(bookingCode, provider, null);
    }

    public String retryPayment(String bookingCode, String provider, String bankCode) throws Exception {
        Booking booking = bookingRepository.findByBookingCode(bookingCode);
        if (booking == null) {
            throw new RuntimeException("Booking not found with code: " + bookingCode);
        }
        
        if (booking.getPaymentStatus() == Booking.PaymentStatus.PAID) {
            throw new RuntimeException("Booking is already paid.");
        }

        return createPaymentUrlForBooking(booking, provider, bankCode);
    }

    /**
     * Tạo URL thanh toán cho 1 booking (VNPAY) với HttpServletRequest.
     */
    public String createVnPayPaymentUrl(int bookingId, HttpServletRequest request) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found: " + bookingId);

        validateBookingHold(booking);

        return buildVnPayUrl(booking, request);
    }

    /**
     * Callback/IPN handler
     */
    public String handleVnPayIpn(Map<String, String> params) {
        try {
            return verifyAndUpdateVnPay(params);
        } catch (Exception ex) {
            logger.error("VNPAY IPN ERROR: {}", ex.getMessage(), ex);
            return "ERROR";
        }
    }

    /**
     * MoMo IPN handler – verify signature, check resultCode, update booking status.
     */
    public String handleMomoIpn(Map<String, String> params) {
        try {
            return verifyAndUpdateMomo(params);
        } catch (Exception ex) {
            logger.error("MOMO IPN ERROR: {}", ex.getMessage(), ex);
            return "ERROR";
        }
    }

    /**
     * Generic IPN handler – auto-detect provider from payload keys.
     */
    public String handleIpn(Map<String, String> params) {
        try {
            if (params.keySet().stream().anyMatch(k -> k.startsWith("vnp_"))) {
                return handleVnPayIpn(params);
            } else if (params.containsKey("partnerCode") || params.containsKey("orderId")) {
                // MoMo IPN / return callback contains partnerCode and orderId
                return handleMomoIpn(params);
            } else {
                logger.warn("Unknown IPN payload: {}", params);
                return "UNKNOWN_PROVIDER";
            }
        } catch (Exception ex) {
            logger.error("Error handling IPN: {}", ex.getMessage(), ex);
            return "ERROR";
        }
    }

    // =========================
    // MOMO CORE
    // =========================

    /**
     * Verify MoMo callback signature + update booking status.
     * MoMo IPN/return sends these params (among others):
     *   partnerCode, orderId, requestId, amount, orderInfo, orderType,
     *   transId, resultCode, message, payType, responseTime, extraData, signature
     *
     * rawSignature for verification (alphabetical order):
     *   accessKey=...&amount=...&extraData=...&message=...&orderId=...&orderInfo=...
     *   &orderType=...&partnerCode=...&payType=...&requestId=...&responseTime=...
     *   &resultCode=...&transId=...
     */
    private String verifyAndUpdateMomo(Map<String, String> params) {
        logger.info("MOMO IPN/return params: {}", params);

        String signature = params.get("signature");
        String orderId = params.get("orderId");
        String resultCode = params.get("resultCode");

        if (orderId == null || orderId.isBlank()) return "MISSING_ORDERID";

        // 1) Verify signature – build rawSignature in alphabetical order (same as MoMo docs)
        if (signature != null && !signature.isBlank()) {
            String accessKey   = momoAccessKey != null ? momoAccessKey : "";
            String amount      = params.getOrDefault("amount", "");
            String extraData   = params.getOrDefault("extraData", "");
            String message     = params.getOrDefault("message", "");
            String orderInfo   = params.getOrDefault("orderInfo", "");
            String orderType   = params.getOrDefault("orderType", "");
            String partnerCode = params.getOrDefault("partnerCode", "");
            String payType     = params.getOrDefault("payType", "");
            String requestId   = params.getOrDefault("requestId", "");
            String responseTime = params.getOrDefault("responseTime", "");
            String transId     = params.getOrDefault("transId", "");

            String rawSignature = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&message=" + message +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&orderType=" + orderType +
                    "&partnerCode=" + partnerCode +
                    "&payType=" + payType +
                    "&requestId=" + requestId +
                    "&responseTime=" + responseTime +
                    "&resultCode=" + resultCode +
                    "&transId=" + transId;

            String calculated = hmacSHA256(momoSecretKey, rawSignature);
            logger.info("MOMO IPN rawSignature={}", rawSignature);
            logger.info("MOMO IPN calculated={}, received={}", calculated, signature);

            if (!calculated.equalsIgnoreCase(signature)) {
                logger.warn("MOMO INVALID_SIGNATURE for orderId={}", orderId);
                return "INVALID_SIGNATURE";
            }
        } else {
            logger.warn("MOMO IPN missing signature for orderId={}, skipping verification", orderId);
        }

        // 2) Parse bookingId from orderId (format: "BK167-1772524061622")
        int bookingId = parseMomoOrderId(orderId);
        if (bookingId <= 0) {
            logger.warn("MOMO IPN cannot parse bookingId from orderId={}", orderId);
            return "INVALID_ORDERID";
        }

        // 3) Update booking status: resultCode "0" = success in MoMo
        if ("0".equals(resultCode)) {
            LocalDateTime now = LocalDateTime.now();
            bookingRepository.updateBookingPaymentStatus(
                    bookingId,
                    Booking.PaymentStatus.PAID.name(),
                    now,
                    Booking.PaymentMethod.MOMO.name()
            );
            logger.info("MOMO Payment SUCCESS for booking {} (orderId={})", bookingId, orderId);

            // ✅ Send detailed booking confirmation email after successful payment
            try {
                BookingEmailData emailData = buildBookingEmailData(bookingId);
                if (emailData != null && emailData.getCustomerEmail() != null && !emailData.getCustomerEmail().isBlank()) {
                    emailService.sendBookingConfirmation(emailData.getCustomerEmail(), emailData);
                    logger.info("Sent detailed booking confirmation email for booking {} to {}", bookingId, emailData.getCustomerEmail());
                } else {
                    logger.warn("Cannot send email: missing email data or customer email for booking {}", bookingId);
                }
            } catch (Exception ex) {
                logger.error("Failed to send booking confirmation email after MoMo success for booking {}: {}", bookingId, ex.getMessage(), ex);
            }

            return "OK";
        } else {
            bookingRepository.updateBookingPaymentStatus(
                    bookingId,
                    Booking.PaymentStatus.FAILED.name(),
                    null,
                    Booking.PaymentMethod.MOMO.name()
            );
            logger.warn("MOMO Payment FAILED for booking {} (orderId={}), resultCode={}", bookingId, orderId, resultCode);
            return "FAILED";
        }
    }

    /**
     * Parse bookingId from MoMo orderId.
     * Format: "BK167-1772524061622" → 167
     */
    private static int parseMomoOrderId(String orderId) {
        try {
            if (orderId == null) return -1;
            // Remove "BK" prefix
            String rest = orderId;
            if (rest.startsWith("BK")) rest = rest.substring(2);
            // Split by "-" and take the first part (bookingId)
            int idx = rest.indexOf('-');
            if (idx > 0) rest = rest.substring(0, idx);
            return Integer.parseInt(rest);
        } catch (Exception e) {
            return -1;
        }
    }

    // =========================
    // VNPAY CORE
    // =========================

    private String buildVnPayUrl(Booking booking, HttpServletRequest request) throws Exception {
        // 1) Validate config
        logger.info("VNPAY Config => tmnCode={}, payUrl={}, returnUrl={}", vnpTmnCode, vnpPayUrl, vnpReturnUrl);

        // 2) Timezone VN (GMT+7)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        String vnpCreateDate = formatter.format(cal.getTime());
        cal.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cal.getTime());

        // 3) Amount: nhân 100 (bắt buộc)
        // booking.getTotalPrice() là BigDecimal → setScale(0) để không có dấu phẩy
        String amountStr = booking.getTotalPrice().setScale(0).toPlainString();
        long amountVal = Long.parseLong(amountStr) * 100;

        // 4) TxnRef: nên unique để tránh trùng khi user pay lại
        String vnpTxnRef = generateTxnRef(booking.getId());

        // 5) IP thật (không hardcode 127.0.0.1)
        String ipAddr = request != null ? getClientIp(request) : "127.0.0.1";

        // 6) Params
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amountVal));
        vnpParams.put("vnp_CurrCode", "VND");

        // Optional: BankCode (nếu FE cho chọn)
        String bankCode = request != null ? request.getParameter("bankCode") : null;
        if (bankCode != null && !bankCode.isBlank()) {
            vnpParams.put("vnp_BankCode", bankCode.trim());
        }

        vnpParams.put("vnp_TxnRef", vnpTxnRef);

        // OrderInfo: nên không dấu nếu muốn chắc (nhưng VNPAY vẫn support encode)
        vnpParams.put("vnp_OrderInfo", "Thanh toan booking " + booking.getId());
        vnpParams.put("vnp_OrderType", "other");

        // Locale
        String locale = request != null ? request.getParameter("locale") : null;
        vnpParams.put("vnp_Locale", (locale != null && !locale.isBlank()) ? locale.trim() : "vn");

        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", ipAddr);

        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // 7) Build hashData & query (giống project cũ)
        String queryUrl = buildQueryUrl(vnpParams);
        String hashData = buildHashData(vnpParams);

        // 8) Sign
        String secureHash = hmacSHA512(vnpHashSecret, hashData);

        // 9) Final URL
        String paymentUrl = vnpPayUrl + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;

        logger.info("VNPAY TxnRef={}, Amount={}, hashData={}", vnpTxnRef, amountVal, hashData);
        logger.info("VNPAY paymentUrl={}", paymentUrl);

        return paymentUrl;
    }

    /**
     * Verify signature + update booking status
     */
    private String verifyAndUpdateVnPay(Map<String, String> params) throws Exception {
        String secureHash = params.get("vnp_SecureHash");
        if (secureHash == null || secureHash.isBlank()) return "MISSING_SIGNATURE";

        // 1) Lọc chỉ vnp_*
        Map<String, String> filtered = params.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getKey().startsWith("vnp_"))
                .filter(e -> !"vnp_SecureHash".equals(e.getKey()))
                .filter(e -> !"vnp_SecureHashType".equals(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 2) Rebuild hashData giống lúc tạo URL (key raw + value encoded)
        String hashData = buildHashData(filtered);
        String calculated = hmacSHA512(vnpHashSecret, hashData);

        if (!calculated.equalsIgnoreCase(secureHash)) {
            logger.warn("VNPAY INVALID_SIGNATURE: calculated={}, received={}, hashData={}", calculated, secureHash, hashData);
            return "INVALID_SIGNATURE";
        }

        // 3) Business logic
        String responseCode = params.get("vnp_ResponseCode"); // "00" = success
        String txnRef = params.get("vnp_TxnRef");

        if (txnRef == null || txnRef.isBlank()) return "MISSING_TXNREF";

        // Nếu bạn dùng txnRef unique dạng "bookingId_timestamp"
        int bookingId = parseBookingIdFromTxnRef(txnRef);

        if ("00".equals(responseCode)) {
            LocalDateTime now = LocalDateTime.now();
            bookingRepository.updateBookingPaymentStatus(
                    bookingId,
                    Booking.PaymentStatus.PAID.name(),
                    now,
                    Booking.PaymentMethod.VNPAY.name()
            );
            logger.info("VNPAY Payment SUCCESS for booking {}", bookingId);

            // ✅ Send detailed booking confirmation email after successful payment
            try {
                BookingEmailData emailData = buildBookingEmailData(bookingId);
                if (emailData != null && emailData.getCustomerEmail() != null && !emailData.getCustomerEmail().isBlank()) {
                    emailService.sendBookingConfirmation(emailData.getCustomerEmail(), emailData);
                    logger.info("Sent detailed booking confirmation email for booking {} to {}", bookingId, emailData.getCustomerEmail());
                } else {
                    logger.warn("Cannot send email: missing email data or customer email for booking {}", bookingId);
                }
            } catch (Exception ex) {
                logger.error("Failed to send booking confirmation email after VNPAY success for booking {}: {}", bookingId, ex.getMessage(), ex);
            }

            return "OK";
        } else {
            bookingRepository.updateBookingPaymentStatus(
                    bookingId,
                    Booking.PaymentStatus.FAILED.name(),
                    null,
                    Booking.PaymentMethod.VNPAY.name()
            );
            logger.warn("VNPAY Payment FAILED for booking {}, responseCode={}", bookingId, responseCode);
            return "FAILED";
        }
    }

    // =========================
    // BUILDERS (HASH / QUERY)
    // =========================

    /**
     * Query URL: encode cả key + value
     */
    private String buildQueryUrl(Map<String, String> params) throws Exception {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String key = itr.next();
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {
                // Query: key encode + value encode
                query.append(URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) query.append('&');
            }
        }
        return query.toString();
    }

    /**
     * HashData: key RAW + value ENCODED (giống project cũ bạn đưa)
     */
    private String buildHashData(Map<String, String> params) throws Exception {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String key = itr.next();
            String value = params.get(key);

            if (value != null && !value.isEmpty()) {
                // Hash: key raw + value encode
                hashData.append(key);
                hashData.append('=');
                hashData.append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) hashData.append('&');
            }
        }
        return hashData.toString();
    }

    // =========================
    // UTILS
    // =========================

    private static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) throw new NullPointerException("key/data null");
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);

            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Build MOMO payment URL by calling MOMO create API and returning payUrl.
     */
    private String buildMomoUrl(Booking booking) throws Exception {
        return buildMomoUrl(booking, null);
    }

    // bankCode: optional bank channel code to request ATM/card payment via Momo.
    private String buildMomoUrl(Booking booking, String bankCode) throws Exception {
        if (momoEndpoint == null || momoEndpoint.isBlank()) {
            throw new RuntimeException("Momo endpoint not configured");
        }

        String orderId = "BK" + booking.getId() + "-" + System.currentTimeMillis();
        String requestId = orderId;
        // momo expects amount as integer (VND)
        String amount = booking.getTotalPrice().setScale(0).toPlainString();
        // Use booking ID in orderInfo (not bookingCode) to ensure consistency with orderId
        String orderInfo = "Booking BK" + String.format("%08d", booking.getId());

        // Build raw signature following MoMo docs (parameter order matters)
        String extraData = "";
        String ipnUrl = momoIpnUrl != null ? momoIpnUrl : "";
        String redirectUrl = (momoReturnUrl != null && !momoReturnUrl.isBlank()) ? momoReturnUrl : ipnUrl;
        // If bankCode provided, request ATM/card payment via MoMo API (requestType = payWithATM)
        String requestType = (bankCode != null && !bankCode.isBlank()) ? "payWithATM" : "captureWallet";

        // MoMo API v2 requires rawSignature in ALPHABETICAL order of parameter names
        String rawSignature = "accessKey=" + momoAccessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoPartnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
        String signature = hmacSHA256(momoSecretKey, rawSignature);
        // Debug logs to troubleshoot signature issues
        logger.info("MOMO rawSignature={}", rawSignature);
        logger.info("MOMO signature={}", signature);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", momoPartnerCode);
        body.put("accessKey", momoAccessKey);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", redirectUrl);
        body.put("ipnUrl", ipnUrl);
        body.put("extraData", extraData);
        body.put("requestType", requestType);
        if (bankCode != null && !bankCode.isBlank()) body.put("bankCode", bankCode);
        body.put("lang", "vi");
        body.put("signature", signature);

        // Log the exact request body sent to MoMo for debugging
        logger.info("MOMO request body: {}", body);

        // Set JSON headers explicitly
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<Map<String, Object>> req = new org.springframework.http.HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = rest.postForObject(momoEndpoint, req, Map.class);
            if (resp != null) logger.info("Momo create payment response: {}", resp);
            if (resp != null && resp.get("payUrl") != null) {
                return resp.get("payUrl").toString();
            }
            String respStr = resp != null ? resp.toString() : "null";
            throw new RuntimeException("Failed to create Momo payment, response: " + respStr);
        } catch (Exception ex) {
            logger.error("Momo create payment failed: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create Momo payment: " + ex.getMessage());
        }
    }

    private static String hmacSHA256(String key, String data) {
        try {
            if (key == null) key = "";
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Lấy IP thật (hỗ trợ proxy)
     */
    private static String getClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";

        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            // XFF có thể dạng: client, proxy1, proxy2
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) return ip.trim();

        return request.getRemoteAddr();
    }

    /**
     * TxnRef unique: bookingId + "_" + epochMillis + random 3 digits
     */
    private static String generateTxnRef(int bookingId) {
        int rnd = new Random().nextInt(900) + 100;
        return bookingId + "_" + System.currentTimeMillis() + "_" + rnd;
    }

    /**
     * Parse bookingId từ txnRef unique.
     * Ví dụ: "123_1700000000000_456" → bookingId=123
     */
    private static int parseBookingIdFromTxnRef(String txnRef) {
        try {
            String[] parts = txnRef.split("_");
            return Integer.parseInt(parts[0]);
        } catch (Exception ex) {
            // fallback: nếu bạn dùng txnRef = bookingId
            return Integer.parseInt(txnRef);
        }
    }

    /**
     * Parse bookingId từ orderId unique.
     * Ví dụ: "BK123_1700000000000" → bookingId=123
     */
    private static int parseBookingIdFromOrderId(String orderId) {
        try {
            if (orderId != null && orderId.startsWith("BK")) {
                String[] parts = orderId.split("_");
                return Integer.parseInt(parts[0].substring(2)); // bỏ "BK" và chuyển sang int
            }
        } catch (Exception ex) {
            // fallback: nếu có lỗi thì coi như bookingId = 0 (không cập nhật gì)
        }
        return 0;
    }

    /**
     * Build BookingEmailData với đầy đủ thông tin chi tiết để gửi email
     */
    private BookingEmailData buildBookingEmailData(int bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId);
            if (booking == null) {
                logger.warn("Cannot build email data: booking {} not found", bookingId);
                return null;
            }

            BookingEmailData emailData = new BookingEmailData();
            emailData.setBookingCode(booking.getBookingCode());
            emailData.setTotalPrice(booking.getTotalPrice());
            emailData.setCreatedAt(booking.getCreatedAt());

            // Get user info
            if (booking.getUserId() != null) {
                try {
                    User user = userRepository.findById(booking.getUserId());
                    if (user != null) {
                        emailData.setCustomerName(user.getFullName() != null ? user.getFullName() : "Quý khách");
                        emailData.setCustomerPhone(user.getPhone() != null ? user.getPhone() : "N/A");
                        emailData.setCustomerEmail(user.getEmail());
                    }
                } catch (Exception ex) {
                    logger.warn("Failed to get user info for booking {}: {}", bookingId, ex.getMessage());
                    emailData.setCustomerName("Quý khách");
                    emailData.setCustomerPhone("N/A");
                }
            } else {
                emailData.setCustomerName("Quý khách");
                emailData.setCustomerPhone("N/A");
            }

            // Get showtime and movie info
            try {
                Showtime showtime = showtimeRepository.findById(booking.getShowtimeId());
                if (showtime != null) {
                    emailData.setShowtime(showtime.getStartTime());
                    
                    // Get movie info
                    try {
                        Movie movie = movieRepository.getMovieDetailById(showtime.getMovieId());
                        if (movie != null) {
                            emailData.setMovieTitle(movie.getTitle());
                        } else {
                            emailData.setMovieTitle("N/A");
                        }
                    } catch (Exception ex) {
                        logger.warn("Failed to get movie info: {}", ex.getMessage());
                        emailData.setMovieTitle("N/A");
                    }

                    // Get room and cinema info
                    try {
                        Room room = roomRepository.findById(showtime.getRoomId());
                        if (room != null) {
                            emailData.setRoomName(room.getName());
                            
                            // Get cinema info
                            try {
                                Cinema cinema = cinemaRepository.findById(room.getCinemaId());
                                if (cinema != null) {
                                    emailData.setCinemaName(cinema.getName());
                                    emailData.setCinemaAddress(cinema.getAddress() != null ? cinema.getAddress() : "N/A");
                                } else {
                                    emailData.setCinemaName("N/A");
                                    emailData.setCinemaAddress("N/A");
                                }
                            } catch (Exception ex) {
                                logger.warn("Failed to get cinema info: {}", ex.getMessage());
                                emailData.setCinemaName("N/A");
                                emailData.setCinemaAddress("N/A");
                            }
                        } else {
                            emailData.setRoomName("N/A");
                            emailData.setCinemaName("N/A");
                            emailData.setCinemaAddress("N/A");
                        }
                    } catch (Exception ex) {
                        logger.warn("Failed to get room info: {}", ex.getMessage());
                        emailData.setRoomName("N/A");
                        emailData.setCinemaName("N/A");
                        emailData.setCinemaAddress("N/A");
                    }
                } else {
                    emailData.setShowtime(null);
                    emailData.setMovieTitle("N/A");
                    emailData.setRoomName("N/A");
                    emailData.setCinemaName("N/A");
                    emailData.setCinemaAddress("N/A");
                }
            } catch (Exception ex) {
                logger.warn("Failed to get showtime info: {}", ex.getMessage());
                emailData.setShowtime(null);
                emailData.setMovieTitle("N/A");
                emailData.setRoomName("N/A");
                emailData.setCinemaName("N/A");
                emailData.setCinemaAddress("N/A");
            }

            // Get seats info - Dùng 1 query JOIN duy nhất lấy seat_code trực tiếp,
            // tránh vấn đề BeanPropertyRowMapper không map được enum SeatType trong SeatRepository.findById()
            try {
                List<String> seatCodeList = bookingRepository.findSeatCodesByBookingId(bookingId);
                emailData.setTicketCount(seatCodeList.size());
                
                if (!seatCodeList.isEmpty()) {
                    emailData.setSeatCodes(String.join(", ", seatCodeList));
                } else {
                    emailData.setSeatCodes("N/A");
                }
                logger.info("Email data for booking {}: ticketCount={}, seatCodes={}", 
                    bookingId, seatCodeList.size(), emailData.getSeatCodes());
            } catch (Exception ex) {
                logger.error("Failed to get seats info for booking {}: {}", bookingId, ex.getMessage(), ex);
                emailData.setTicketCount(0);
                emailData.setSeatCodes("N/A");
            }

            // Get combo/concession info - Dùng 1 query JOIN duy nhất lấy tên combo/product trực tiếp,
            // tránh vấn đề BeanPropertyRowMapper không map được NULL vào int primitive (comboId, productId)
            try {
                List<java.util.Map<String, Object>> concessionRows = bookingRepository.findConcessionDetailsByBookingId(bookingId);
                if (concessionRows != null && !concessionRows.isEmpty()) {
                    StringBuilder comboStr = new StringBuilder();
                    for (int i = 0; i < concessionRows.size(); i++) {
                        java.util.Map<String, Object> row = concessionRows.get(i);
                        if (i > 0) comboStr.append(", ");
                        
                        String itemName = row.get("item_name") != null ? row.get("item_name").toString() : "Sản phẩm";
                        int qty = 0;
                        Object qtyObj = row.get("quantity");
                        if (qtyObj != null) qty = Integer.parseInt(qtyObj.toString());
                        
                        comboStr.append(itemName).append(" x").append(qty);
                    }
                    emailData.setComboDetails(comboStr.toString());
                } else {
                    emailData.setComboDetails("Không có");
                }
                logger.info("Email data for booking {}: comboDetails={}", bookingId, emailData.getComboDetails());
            } catch (Exception ex) {
                logger.error("Failed to get combo info for booking {}: {}", bookingId, ex.getMessage(), ex);
                emailData.setComboDetails("Không có");
            }

            return emailData;
        } catch (Exception ex) {
            logger.error("Failed to build booking email data for booking {}: {}", bookingId, ex.getMessage(), ex);
            return null;
        }
    }
}
