package dto.response;

public class AdminActionResultResponse {
    private final boolean success;
    private final String message;

    public AdminActionResultResponse(String payload) {
        String[] parts = payload.split("\\|", 2);
        this.success = parts.length > 0 && "OK".equalsIgnoreCase(parts[0]);
        this.message = parts.length > 1 ? parts[1] : "";
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
