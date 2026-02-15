package CinemaBooking.Group2.service;

import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.repositories.BookingRepository;
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

    // =========================
    // PUBLIC API - Compatibility methods
    // =========================

    /**
     * Method tương thích với BookingService - không cần HttpServletRequest
     */
    public String createPaymentUrl(int bookingId, String provider) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found: " + bookingId);
        
        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    /**
     * Method tương thích với BookingService - nhận Booking object
     */
    public String createPaymentUrlForBooking(Booking booking, String provider) throws Exception {
        if (booking == null) throw new RuntimeException("Booking is null");
        
        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking, null);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    /**
     * Tạo URL thanh toán cho 1 booking (VNPAY) với HttpServletRequest.
     */
    public String createVnPayPaymentUrl(int bookingId, HttpServletRequest request) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found: " + bookingId);

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
     * Generic IPN handler
     */
    public String handleIpn(Map<String, String> params) {
        try {
            if (params.keySet().stream().anyMatch(k -> k.startsWith("vnp_"))) {
                return handleVnPayIpn(params);
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
        if (momoEndpoint == null || momoEndpoint.isBlank()) {
            throw new RuntimeException("Momo endpoint not configured");
        }

        String orderId = "BK" + booking.getId() + "-" + System.currentTimeMillis();
        String requestId = orderId;
        // momo expects amount as integer (VND)
        String amount = booking.getTotalPrice().setScale(0).toPlainString();
        String orderInfo = "Booking " + booking.getBookingCode();

        // Build raw signature following MoMo docs (parameter order matters)
        String extraData = "";
        String notifyUrl = momoIpnUrl != null ? momoIpnUrl : "";
        String redirectUrl = (momoReturnUrl != null && !momoReturnUrl.isBlank()) ? momoReturnUrl : notifyUrl;
        String requestType = "captureWallet";

        // Build rawSignature using the exact values that will be sent in the request body
        String rawSignature = "accessKey=" + momoAccessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + notifyUrl +
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
        body.put("returnUrl", momoReturnUrl);
        body.put("redirectUrl", redirectUrl);
        body.put("ipnUrl", notifyUrl);
        body.put("notifyUrl", notifyUrl);
        body.put("extraData", extraData == null ? "" : extraData);
        body.put("requestType", "captureWallet");
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
}
