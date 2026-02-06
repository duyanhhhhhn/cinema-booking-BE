package CinemaBooking.Group2.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "message", "code", "statusCode", "error" })
public class ErrorResponse {
    private String message;
    private String code;
    private int statusCode;
    private boolean error;

    public ErrorResponse() {
        this.error = true;
    }

    public ErrorResponse(String message, String code, int statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
        this.error = true;
    }

    // Static factory methods for common errors
    public static ErrorResponse badRequest(String message) {
        return new ErrorResponse(message, "BAD_REQUEST", 400);
    }

    public static ErrorResponse unauthorized(String message) {
        return new ErrorResponse(message, "UNAUTHORIZED", 401);
    }

    public static ErrorResponse forbidden(String message) {
        return new ErrorResponse(message, "FORBIDDEN", 403);
    }

    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(message, "NOT_FOUND", 404);
    }

    public static ErrorResponse conflict(String message) {
        return new ErrorResponse(message, "CONFLICT", 409);
    }

    public static ErrorResponse internalError(String message) {
        return new ErrorResponse(message, "INTERNAL_SERVER_ERROR", 500);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
