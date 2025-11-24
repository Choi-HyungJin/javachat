package dto.response;

public class ProfileUpdateResponse {
    private final boolean success;
    private final String message;
    private final String nickname;

    public ProfileUpdateResponse(String payload) {
        String[] parts = payload.split(",", 3);
        this.success = Boolean.parseBoolean(parts[0]);
        this.message = parts.length > 1 ? parts[1] : "";
        this.nickname = parts.length > 2 ? parts[2] : "";
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getNickname() {
        return nickname;
    }
}
