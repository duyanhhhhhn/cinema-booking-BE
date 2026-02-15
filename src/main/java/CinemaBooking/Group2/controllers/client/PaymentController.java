package CinemaBooking.Group2.controllers.client;

import java.util.HashMap;
import java.util.Map;

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
                    response.sendRedirect(frontendUrl + "?status=success&method=vnpay&txnRef=" + (txnRef != null ? txnRef : ""));
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
                    response.sendRedirect(frontendUrl + "?status=failed&method=vnpay&txnRef=" + (txnRef != null ? txnRef : ""));
                } catch (java.io.IOException e) {
                    // ignore
                }
                return ResponseEntity.ok().build();
            }
        }
    }

    @GetMapping("/momo/return")
    public ResponseEntity<?> momoReturn(HttpServletRequest request, HttpServletResponse response) {
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
        
        // If successful, also verify through PaymentService
        String verificationResult = null;
        if (isSuccess) {
            try {
                verificationResult = paymentService.handleIpn(params);
                isSuccess = "OK".equalsIgnoreCase(verificationResult);
            } catch (Exception e) {
                isSuccess = false;
            }
        }

        if (isSuccess) {
            if (wantJson) {
                int bookingId = parseBookingIdFromOrderId(orderId);
                if (bookingId <= 0) return ResponseEntity.badRequest().body(Map.of("error", "Invalid orderId"));

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
                    String redirectUrl = frontendUrl + "?status=success&method=momo&orderId=" + (orderId != null ? orderId : "");
                    response.sendRedirect(redirectUrl);
                    return ResponseEntity.status(302).build(); // Return proper redirect status
                } catch (java.io.IOException e) {
                    // If redirect fails, return error response
                    return ResponseEntity.status(500).body(Map.of("error", "Redirect failed", "redirectUrl", frontendUrl));
                }
            }
        } else {
            // Payment failed or cancelled
            if (wantJson) {
                Map<String, Object> result = new HashMap<>();
                result.put("status", "failed");
                result.put("method", "momo");
                result.put("orderId", orderId != null ? orderId : "");
                result.put("resultCode", resultCode != null ? resultCode : "unknown");
                // Add specific message for common result codes
                if ("1006".equals(resultCode)) {
                    result.put("message", "Transaction cancelled by user");
                } else if ("1001".equals(resultCode)) {
                    result.put("message", "Transaction failed");
                }
                return ResponseEntity.ok(result);
            } else {
                try {
                    String redirectUrl = frontendUrl + "?status=failed&method=momo&orderId=" + (orderId != null ? orderId : "") 
                        + "&resultCode=" + (resultCode != null ? resultCode : "unknown");
                    response.sendRedirect(redirectUrl);
                    return ResponseEntity.status(302).build(); // Return proper redirect status
                } catch (java.io.IOException e) {
                    // If redirect fails, return error response
                    return ResponseEntity.status(500).body(Map.of("error", "Redirect failed", "redirectUrl", frontendUrl));
                }
            }
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
