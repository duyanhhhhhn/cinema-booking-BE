package CinemaBooking.Group2.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import CinemaBooking.Group2.models.Booking;
import CinemaBooking.Group2.models.BookingSeat;
import CinemaBooking.Group2.models.Enum.PaymentMethod;
import CinemaBooking.Group2.models.Enum.PaymentStatus;
import CinemaBooking.Group2.models.Payment;
import CinemaBooking.Group2.repositories.BookingRepository;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${vnpay.tmnCode:}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret:}")
    private String vnpHashSecret;

    @Value("${vnpay.payUrl:}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl:}")
    private String vnpReturnUrl;

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

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    private RestTemplate rest = new RestTemplate();

    public String createPaymentUrl(int bookingId, String provider) throws Exception {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new RuntimeException("Booking not found");

        if ("VNPAY".equalsIgnoreCase(provider)) {
            return buildVnPayUrl(booking);
        } else if ("MOMO".equalsIgnoreCase(provider)) {
            return buildMomoUrl(booking);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    private String buildVnPayUrl(Booking booking) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        String amount = booking.getTotalPrice().setScale(0).toPlainString();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", amount);
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(booking.getId()));
        params.put("vnp_OrderInfo", "Booking " + booking.getBookingCode());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl != null ? vnpReturnUrl.replaceAll("\"", "") : "");
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        String signData = params.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()))
            .collect(Collectors.joining("&"));

        String signature = hmacSHA512(vnpHashSecret, signData);
        params.put("vnp_SecureHash", signature);

        // build URL manually
        StringBuilder sb = new StringBuilder();
        sb.append(vnpPayUrl);
        if (!params.isEmpty()) {
            sb.append("?");
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> e : params.entrySet()) {
                sj.add(urlEncode(e.getKey()) + "=" + urlEncode(e.getValue()));
            }
            sb.append(sj.toString());
        }
        String url = sb.toString();

        logger.info("Generated VNPay URL: {}", url);
        return url;
    }

    private String buildMomoUrl(Booking booking) throws Exception {
        String orderId = "BK" + booking.getId() + "-" + System.currentTimeMillis();
        String requestId = orderId;
        String amount = booking.getTotalPrice().setScale(0).toPlainString();
        String orderInfo = "Booking " + booking.getBookingCode();

        String rawSignature = momoPartnerCode + momoAccessKey + requestId + amount + orderId + orderInfo + momoReturnUrl + "";
        String signature = hmacSHA256(momoSecretKey, rawSignature);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", momoPartnerCode);
        body.put("accessKey", momoAccessKey);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("returnUrl", momoReturnUrl);
        body.put("notifyUrl", "");
        body.put("requestType", "captureWallet");
        body.put("signature", signature);

        org.springframework.http.HttpEntity<Map<String, Object>> req = new org.springframework.http.HttpEntity<>(body);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = rest.postForObject(momoEndpoint, req, Map.class);
            if (resp != null && resp.get("payUrl") != null) {
                String payUrl = resp.get("payUrl").toString();
                logger.info("Momo payUrl: {}", payUrl);
                return payUrl;
            }
        } catch (Exception ex) {
            logger.error("Momo create payment failed: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create Momo payment: " + ex.getMessage());
        }
        throw new RuntimeException("Failed to create Momo payment");
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (Exception e) {
            return "";
        }
    }

    private static String hmacSHA512(String key, String data) throws Exception {
        if (key == null) key = "";
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secret_key);
        byte[] mac_data = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(2 * mac_data.length);
        for (byte b : mac_data) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private static String hmacSHA256(String key, String data) throws Exception {
        if (key == null) key = "";
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] mac_data = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder(2 * mac_data.length);
        for (byte b : mac_data) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public String handleIpn(Map<String, String> params) {
        try {
            if (params.keySet().stream().anyMatch(k -> k.startsWith("vnp_"))) {
                return handleVnPayIpn(params);
            } else if (params.get("partnerCode") != null || params.get("orderId") != null) {
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

    private String handleVnPayIpn(Map<String, String> params) throws Exception {
        String secureHash = params.get("vnp_SecureHash");
        Map<String, String> filtered = params.entrySet().stream()
            .filter(e -> e.getKey().startsWith("vnp_") && !"vnp_SecureHash".equals(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String signData = filtered.keySet().stream().sorted()
            .map(k -> urlEncode(k) + "=" + urlEncode(filtered.get(k)))
            .collect(Collectors.joining("&"));
        String calculated = hmacSHA512(vnpHashSecret, signData);
        if (!calculated.equalsIgnoreCase(secureHash)) {
            logger.warn("VNPay signature mismatch");
            return "INVALID_SIGNATURE";
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        int bookingId = Integer.parseInt(txnRef);
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return "BOOKING_NOT_FOUND";

        if ("00".equals(responseCode)) {
            LocalDateTime now = LocalDateTime.now();
            bookingRepository.updateBookingPaymentStatus(bookingId, Booking.PaymentStatus.PAID.name(), now, Booking.PaymentMethod.VNPAY.name());

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(booking.getTotalPrice());
            payment.setMethod(PaymentMethod.VNPAY);
            payment.setProviderPaymentId(params.get("vnp_TransactionNo"));
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(now);
            bookingRepository.createPayment(payment);

            // update in-memory booking so email contains paidAt and status
            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            booking.setPaidAt(now);
            booking.setPaymentMethod(Booking.PaymentMethod.VNPAY);

            List<BookingSeat> seats = bookingRepository.findBookingSeats(bookingId);
            String userEmail = bookingRepository.getUserEmail(booking.getUserId());
            if (userEmail != null) emailService.sendTickets(userEmail, booking, seats);

            return "OK";
        } else {
            bookingRepository.updateBookingPaymentStatus(bookingId, Booking.PaymentStatus.FAILED.name(), null, Booking.PaymentMethod.VNPAY.name());
            return "FAILED";
        }
    }

    private String handleMomoIpn(Map<String, String> params) throws Exception {
        String orderId = params.get("orderId");
        if (orderId == null) return "NO_ORDER";

        int bookingId = parseBookingIdFromOrderId(orderId);
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) return "BOOKING_NOT_FOUND";

        String resultCode = params.getOrDefault("resultCode", params.getOrDefault("errorCode", ""));
        if ("0".equals(resultCode)) {
            LocalDateTime now = LocalDateTime.now();
            bookingRepository.updateBookingPaymentStatus(bookingId, Booking.PaymentStatus.PAID.name(), now, Booking.PaymentMethod.MOMO.name());

            Payment payment = new Payment();
            payment.setBookingId(bookingId);
            payment.setAmount(booking.getTotalPrice());
            payment.setMethod(PaymentMethod.MOMO);
            payment.setProviderPaymentId(params.get("transId"));
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(now);
            bookingRepository.createPayment(payment);

            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
            booking.setPaidAt(now);
            booking.setPaymentMethod(Booking.PaymentMethod.MOMO);

            List<BookingSeat> seats = bookingRepository.findBookingSeats(bookingId);
            String userEmail = bookingRepository.getUserEmail(booking.getUserId());
            if (userEmail != null) emailService.sendTickets(userEmail, booking, seats);

            return "OK";
        } else {
            bookingRepository.updateBookingPaymentStatus(bookingId, Booking.PaymentStatus.FAILED.name(), null, Booking.PaymentMethod.MOMO.name());
            return "FAILED";
        }
    }

    private int parseBookingIdFromOrderId(String orderId) {
        try {
            if (orderId.startsWith("BK")) {
                String rest = orderId.substring(2);
                int idx = rest.indexOf('-');
                if (idx > 0) rest = rest.substring(0, idx);
                return Integer.parseInt(rest);
            }
        } catch (Exception e) {
            // ignore
        }
        return -1;
    }
}