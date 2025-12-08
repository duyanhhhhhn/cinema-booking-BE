package CinemaBooking.Group2.models;

import java.time.LocalDateTime;

public class Otp {

    public enum OtpPurpose {
        REGISTER,
        FORGOT_PASSWORD,
        TWO_FACTOR
    }

    private int id;
    private Integer userId; 
    private String email;
    private String code;
    private OtpPurpose purpose;
    private LocalDateTime expiresAt;
    private int used;
    private LocalDateTime createdAt;

    public Otp() {}

    /**
     * Constructor đầy đủ (mapping từ DB)
     */
    public Otp(int id, Integer userId, String email, String code, OtpPurpose purpose, 
               LocalDateTime expiresAt, int used, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.code = code;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    /**
     * ⭐ Constructor dùng để tạo OTP mới (khi gửi email)
     * 👉 Không có id, userId, createdAt, used = 0
     */
    public Otp(String email, String code, OtpPurpose purpose, LocalDateTime expiresAt) {
        this.email = email;
        this.code = code;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.used = 0;        // 0 = false (chưa sử dụng)
        this.userId = null;   // User chưa tồn tại
        this.createdAt = null; // DB sẽ tự thêm
    }

    // ===== Getter / Setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public OtpPurpose getPurpose() { return purpose; }
    public void setPurpose(OtpPurpose purpose) { this.purpose = purpose; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getUsed() { return used; }
    public void setUsed(int used) { this.used = used; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
