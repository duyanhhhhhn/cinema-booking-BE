package CinemaBooking.Group2.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;


@Service
public class ResendClient {

    private static final Logger logger = LoggerFactory.getLogger(ResendClient.class);

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.email.from:}")
    private String fromEmail;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("resend.api.key is not set. ResendClient will not send emails until configured.");
        }
        if (fromEmail == null || fromEmail.isBlank()) {
            logger.warn("resend.email.from is not set. Email 'from' will be empty until configured.");
        }
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.error("Cannot send email: resend.api.key is not configured.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            String json = buildJsonPayload(fromEmail, to, subject, htmlContent);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            String body = response.body();
            if (status >= 200 && status < 300) {
                logger.info("Email sent successfully, status={}, response={}", status, body);
            } else {
                logger.error("Failed to send email, status={}, response={}", status, body);
            }

        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.error("Error sending email via Resend: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error("Unexpected error sending email via Resend: {}", ex.getMessage(), ex);
        }
    }

    private String buildJsonPayload(String from, String to, String subject, String html) {
        return String.format(
                "{\"from\":\"%s\",\"to\":[\"%s\"],\"subject\":\"%s\",\"html\":\"%s\"}",
                escapeJson(from), escapeJson(to), escapeJson(subject), escapeJson(html)
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> {
                    if (c < 0x20 || c > 0x7E) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}