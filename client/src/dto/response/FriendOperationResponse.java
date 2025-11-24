package dto.response;

public class FriendOperationResponse {
    private final boolean success;
    private final String message;

    public FriendOperationResponse(String payload) {
        String[] values = payload.split(",", 2);
        this.success = Boolean.parseBoolean(values[0]);
        this.message = values.length > 1 ? values[1] : "";
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
