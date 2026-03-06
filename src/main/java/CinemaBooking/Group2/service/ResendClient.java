package CinemaBooking.Group2.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;


@Service
public class ResendClient {

    private static final Logger logger = LoggerFactory.getLogger(ResendClient.class);

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.email.from:}")
    private String fromEmail;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("resend.api.key is not set. ResendClient will not send emails until configured.");
        } else {
            logger.info("ResendClient initialized with API key (length={})", apiKey.trim().length());
        }
        if (fromEmail == null || fromEmail.isBlank()) {
            logger.warn("resend.email.from is not set. Email 'from' will be empty until configured.");
        } else {
            logger.info("ResendClient from email: {}", fromEmail);
        }
    }

    // Clean API key to remove control characters that are invalid in HTTP headers
    private String getCleanApiKey() {
        if (apiKey == null) return "";
        // remove CR/LF and trim surrounding whitespace
        return apiKey.replace("\r", "").replace("\n", "").trim();
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        String cleanKey = getCleanApiKey();
        if (cleanKey.isEmpty()) {
            logger.error("Cannot send email: resend.api.key is not configured or contains invalid characters.");
            return;
        }

        if (to == null || to.isBlank()) {
            logger.error("Cannot send email: recipient address is null or blank.");
            return;
        }

        logger.info("Sending email to={}, subject={}, htmlLength={}", to, subject, 
                     htmlContent != null ? htmlContent.length() : 0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            String json = buildJsonPayload(fromEmail, to, subject, htmlContent);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + cleanKey)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();
            if (status >= 200 && status < 300) {
                logger.info("Email sent successfully to={}, status={}, response={}", to, status, body);
            } else {
                logger.error("Failed to send email to={}, status={}, response={}", to, status, body);
            }

        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.error("Error sending email via Resend to={}: {}", to, ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error sending email via Resend to={}: {}", to, ex.getMessage(), ex);
        }
    }

    private String buildJsonPayload(String from, String to, String subject, String html) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", from);
            payload.put("to", List.of(to));
            payload.put("subject", subject);
            payload.put("html", html);
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            logger.error("Failed to build JSON payload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to build email JSON payload", e);
        }
    }
}