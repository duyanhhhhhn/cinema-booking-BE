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

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "Payment gateway integration (VNPay/Momo)")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

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
}
