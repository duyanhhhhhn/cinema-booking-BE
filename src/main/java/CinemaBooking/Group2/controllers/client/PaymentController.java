package CinemaBooking.Group2.controllers.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import CinemaBooking.Group2.repositories.BookingRepository;
import CinemaBooking.Group2.dtos.booking.BookingDetailResponse;
import CinemaBooking.Group2.dtos.ApiResponse;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "Payment gateway integration (VNPay/Momo)")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingRepository bookingRepository;

    public static class CreateUrlRequest {
        public int bookingId;
        public String provider; // VNPAY or MOMO
        public int getBookingId() { return bookingId; }
        public void setBookingId(int bookingId) { this.bookingId = bookingId; }
        public String getProvider() { return provider; }
        public void setProvider(String provider) { this.provider = provider; }
    }

    @PostMapping("/create-url")
    @Operation(summary = "Create payment URL for VNPay or Momo")
    public ResponseEntity<?> createUrl(@RequestBody CreateUrlRequest req) {
        try {
            String url = paymentService.createPaymentUrl(req.getBookingId(), req.getProvider());
            Map<String, Object> resp = new HashMap<>();
            resp.put("url", url);
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
    
    @GetMapping("/vnpay/return")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request, HttpServletResponse response) {
        // Build flat map of all parameters returned by VNPay
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> { if (v != null && v.length > 0) params.put(k, v[0]); });

        // Let PaymentService verify signature and update booking (server-to-server verification)
        String result = paymentService.handleIpn(params); // expects "OK" on success

        // Frontend URL (React chạy port 3000)
        String frontendUrl = "http://localhost:3000/payment-result";

        String txnRef = request.getParameter("vnp_TxnRef");

        // If caller asked for JSON response, return booking details JSON on success
        String responseType = request.getParameter("responseType");
        boolean wantJson = "json".equalsIgnoreCase(responseType) || request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json");

        // Resolve bookingCode from txnRef for redirect
        int vnpBookingId = parseBookingId(txnRef);
        String vnpBookingCode = null;
        if (vnpBookingId > 0) {
            var vnpBooking = bookingRepository.findById(vnpBookingId);
            if (vnpBooking != null) {
                vnpBookingCode = vnpBooking.getBookingCode();
                logger.info("VNPay return: bookingId={}, bookingCode={}", vnpBookingId, vnpBookingCode);
            } else {
                logger.warn("VNPay return: booking not found for id={}", vnpBookingId);
            }
        }

        if ("OK".equalsIgnoreCase(result)) {
            if (wantJson) {
                int bookingId = parseBookingId(txnRef);
                if (bookingId <= 0) return ResponseEntity.badRequest().body(Map.of("error", "Invalid txnRef"));

                var booking = bookingRepository.findById(bookingId);
                if (booking == null) return ResponseEntity.status(404).body(Map.of("error", "Booking not found"));

                String bookingCode = booking.getBookingCode();
                ApiResponse<BookingDetailResponse> dtoResp = bookingRepository.getBookingByCodeAdmin(bookingCode);
                if (dtoResp == null || dtoResp.getData() == null) return ResponseEntity.status(404).body(Map.of("error", "Booking detail not found"));
                BookingDetailResponse dto = dtoResp.getData();

                Map<String, Object> out = new HashMap<>();
                out.put("movieName", dto.getMovieTitle());
                out.put("posterUrl", dto.getPosterUrl());
                out.put("format", dto.getFormat());
                out.put("durationMin", dto.getDurationMinutes());
                out.put("cinemaName", dto.getCinemaName());
                out.put("cinemaAddress", dto.getCinemaAddress());
                out.put("showDate", dto.getStartTime());
                out.put("screenName", dto.getRoomName());
                out.put("showTime", dto.getStartTime());
                out.put("seatList", dto.getSeatCodes());
                out.put("totalPrice", dto.getTotalPrice());
                out.put("bookingCode", dto.getBookingCode());
                out.put("qrData", dto.getQrData());

                return ResponseEntity.ok(out);
            } else {
                try {
                    StringBuilder redirectUrl = new StringBuilder(frontendUrl);
                    redirectUrl.append("?status=success&method=vnpay");
                    redirectUrl.append("&txnRef=").append(txnRef != null ? txnRef : "");
                    if (vnpBookingCode != null) {
                        redirectUrl.append("&bookingCode=").append(urlEncode(vnpBookingCode));
                    }
                    response.sendRedirect(redirectUrl.toString());
                } catch (java.io.IOException e) {
                    // ignore
                }
                return ResponseEntity.ok().build();
            }
        } else {
            if (wantJson) {
                return ResponseEntity.ok(Map.of("status", "failed", "method", "vnpay", "txnRef", txnRef));
            } else {
                try {
                    StringBuilder redirectUrl = new StringBuilder(frontendUrl);
                    redirectUrl.append("?status=failed&method=vnpay");
                    redirectUrl.append("&txnRef=").append(txnRef != null ? txnRef : "");
                    if (vnpBookingCode != null) {
                        redirectUrl.append("&bookingCode=").append(urlEncode(vnpBookingCode));
                    }
                    response.sendRedirect(redirectUrl.toString());
                } catch (java.io.IOException e) {
                    // ignore
                }
                return ResponseEntity.ok().build();
            }
        }
    }

    @GetMapping("/momo/return")
    public void momoReturn(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        // Build flat map of all parameters returned by MoMo
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> { if (v != null && v.length > 0) params.put(k, v[0]); });

        String orderId = request.getParameter("orderId");
        String resultCode = request.getParameter("resultCode");
        
        // Frontend URL
        String frontendUrl = "http://localhost:3000/payment-result";

        // Check if caller wants JSON response
        String responseType = request.getParameter("responseType");
        boolean wantJson = "json".equalsIgnoreCase(responseType) || 
            (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        // First check the resultCode from MoMo directly (0 = success, others = failed/cancelled)
        boolean isSuccess = "0".equals(resultCode);
        
        // If successful, also verify through PaymentService and update DB
        if (isSuccess) {
            try {
                String verificationResult = paymentService.handleIpn(params);
                isSuccess = "OK".equalsIgnoreCase(verificationResult);
            } catch (Exception e) {
                isSuccess = false;
            }
        }

        // Resolve booking info for redirect / JSON response
        int bookingId = parseBookingIdFromOrderId(orderId);
        logger.info("MoMo return: orderId={}, resultCode={}, isSuccess={}, parsed bookingId={}", orderId, resultCode, isSuccess, bookingId);
        String bookingCode = null;
        BookingDetailResponse dto = null;
        if (bookingId > 0) {
            var booking = bookingRepository.findById(bookingId);
            if (booking != null) {
                bookingCode = booking.getBookingCode();
                logger.info("MoMo return: found booking id={}, bookingCode={}", bookingId, bookingCode);
                
                // Fallback: if bookingCode is null, try to find it another way (shouldn't happen with new mapping)
                if (bookingCode == null || bookingCode.trim().isEmpty()) {
                    logger.warn("MoMo return: bookingCode was null for booking id={}, attempting fallback", bookingId);
                    // Try to get from all bookings - this shouldn't happen but safety check
                    bookingCode = null; // Will be handled below
                }
                
                // Load full booking details if we have bookingCode
                if (bookingCode != null && !bookingCode.trim().isEmpty()) {
                    try {
                        ApiResponse<BookingDetailResponse> dtoResp = bookingRepository.getBookingByCodeAdmin(bookingCode);
                        if (dtoResp != null) dto = dtoResp.getData();
                    } catch (Exception e) {
                        logger.warn("Failed to load booking detail for {}: {}", bookingCode, e.getMessage());
                    }
                }
            } else {
                logger.warn("MoMo return: booking not found for id={}", bookingId);
            }
        } else {
            logger.warn("MoMo return: could not parse bookingId from orderId={}", orderId);
        }

        if (isSuccess) {
            if (wantJson) {
                if (dto == null) {
                    response.setStatus(404);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Booking detail not found\"}");
                    return;
                }

                Map<String, Object> out = new HashMap<>();
                out.put("status", "success");
                out.put("method", "momo");
                out.put("bookingCode", dto.getBookingCode());
                out.put("movieName", dto.getMovieTitle());
                out.put("posterUrl", dto.getPosterUrl());
                out.put("format", dto.getFormat());
                out.put("durationMin", dto.getDurationMinutes());
                out.put("cinemaName", dto.getCinemaName());
                out.put("cinemaAddress", dto.getCinemaAddress());
                out.put("showDate", dto.getStartTime());
                out.put("screenName", dto.getRoomName());
                out.put("showTime", dto.getStartTime());
                out.put("seatList", dto.getSeatCodes());
                out.put("totalPrice", dto.getTotalPrice());
                out.put("qrData", dto.getQrData());
                
                response.setContentType("application/json");
                response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(out));
            } else {
                // Build redirect URL - ALWAYS include bookingCode when available
                StringBuilder redirectUrl = new StringBuilder(frontendUrl);
                redirectUrl.append("?status=success&method=momo");
                redirectUrl.append("&orderId=").append(orderId != null ? orderId : "");
                
                // Always append bookingCode if we have it
                if (bookingCode != null && !bookingCode.trim().isEmpty()) {
                    redirectUrl.append("&bookingCode=").append(urlEncode(bookingCode));
                } else if (dto != null && dto.getBookingCode() != null) {
                    // Fallback: get from dto if available
                    redirectUrl.append("&bookingCode=").append(urlEncode(dto.getBookingCode()));
                }
                
                // Append additional booking details if available
                if (dto != null) {
                    redirectUrl.append("&movieName=").append(urlEncode(dto.getMovieTitle()));
                    redirectUrl.append("&cinemaName=").append(urlEncode(dto.getCinemaName()));
                    redirectUrl.append("&screenName=").append(urlEncode(dto.getRoomName()));
                    redirectUrl.append("&seatList=").append(urlEncode(dto.getSeatCodes()));
                    if (dto.getTotalPrice() != null) redirectUrl.append("&totalPrice=").append(dto.getTotalPrice().toPlainString());
                    if (dto.getStartTime() != null) redirectUrl.append("&showTime=").append(urlEncode(dto.getStartTime().toString()));
                }
                
                logger.info("MoMo return: redirecting to {}", redirectUrl.toString());
                response.sendRedirect(redirectUrl.toString());
            }
        } else {
            // Payment failed or cancelled
            if (wantJson) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "failed");
                result.put("method", "momo");
                result.put("orderId", orderId != null ? orderId : "");
                result.put("resultCode", resultCode != null ? resultCode : "unknown");
                if (bookingCode != null && !bookingCode.trim().isEmpty()) {
                    result.put("bookingCode", bookingCode);
                } else if (dto != null && dto.getBookingCode() != null) {
                    result.put("bookingCode", dto.getBookingCode());
                }
                if ("1006".equals(resultCode)) {
                    result.put("message", "Transaction cancelled by user");
                } else if ("1001".equals(resultCode)) {
                    result.put("message", "Transaction failed");
                }
                
                response.setContentType("application/json");
                response.getWriter().write(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result));
            } else {
                StringBuilder redirectUrl = new StringBuilder(frontendUrl);
                redirectUrl.append("?status=failed&method=momo");
                redirectUrl.append("&orderId=").append(orderId != null ? orderId : "");
                redirectUrl.append("&resultCode=").append(resultCode != null ? resultCode : "unknown");
                
                // Always append bookingCode if available for failed payments too
                if (bookingCode != null && !bookingCode.trim().isEmpty()) {
                    redirectUrl.append("&bookingCode=").append(urlEncode(bookingCode));
                } else if (dto != null && dto.getBookingCode() != null) {
                    redirectUrl.append("&bookingCode=").append(urlEncode(dto.getBookingCode()));
                }
                
                logger.info("MoMo return (failed): redirecting to {}", redirectUrl.toString());
                response.sendRedirect(redirectUrl.toString());
            }
        }
    }

    /** URL-encode helper for redirect params */
    private static String urlEncode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return value;
        }
    }

    @GetMapping(value = "/ipn", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "IPN endpoint for payment providers (GET)")
    public ResponseEntity<String> ipnGet(@RequestParam MultiValueMap<String, String> queryParams) {
        try {
            Map<String, String> flat = new HashMap<>();
            queryParams.forEach((k,v) -> {
                if (v != null && !v.isEmpty()) flat.put(k, v.get(0));
            });
            String result = paymentService.handleIpn(flat);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.ok("ERROR");
        }
    }

    @PostMapping(value = "/ipn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "IPN endpoint for payment providers (POST JSON)")
    public ResponseEntity<String> ipnPost(@RequestBody Map<String, Object> body) {
        try {
            Map<String, String> flat = new HashMap<>();
            body.forEach((k,v) -> { if (v != null) flat.put(k, String.valueOf(v)); });
            String result = paymentService.handleIpn(flat);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            return ResponseEntity.ok("ERROR");
        }
    }

    /**
     * Dedicated MoMo IPN endpoint (POST JSON).
     * MoMo sends IPN as POST with JSON body containing: partnerCode, orderId, requestId,
     * amount, orderInfo, orderType, transId, resultCode, message, payType, responseTime,
     * extraData, signature.
     * Must return HTTP 204 to acknowledge receipt.
     */
    @PostMapping(value = "/momo/ipn", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "MoMo IPN callback endpoint")
    public ResponseEntity<Void> momoIpn(@RequestBody Map<String, Object> body) {
        try {
            Map<String, String> flat = new HashMap<>();
            body.forEach((k, v) -> { if (v != null) flat.put(k, String.valueOf(v)); });
            String result = paymentService.handleMomoIpn(flat);
            logger.info("MoMo IPN processed: result={}, orderId={}", result, flat.get("orderId"));
        } catch (Exception ex) {
            logger.error("MoMo IPN processing error: {}", ex.getMessage(), ex);
        }
        // MoMo expects HTTP 204 No Content to confirm IPN received
        return ResponseEntity.noContent().build();
    }

    // Helper parsing functions
    private int parseBookingId(String txnRef) {
        try {
            if (txnRef == null) return -1;
            if (txnRef.contains("_")) {
                return Integer.parseInt(txnRef.split("_")[0]);
            }
            return Integer.parseInt(txnRef);
        } catch (Exception e) {
            return -1;
        }
    }

    private int parseBookingIdFromOrderId(String orderId) {
        try {
            if (orderId == null) return -1;
            if (orderId.startsWith("BK")) {
                String rest = orderId.substring(2);
                int idx = rest.indexOf('-');
                if (idx > 0) rest = rest.substring(0, idx);
                return Integer.parseInt(rest);
            }
            return Integer.parseInt(orderId);
        } catch (Exception e) {
            return -1;
        }
    }
}
