package dto.request;

import dto.type.DtoType;

public class AdminForceExitRequest extends DTO {
    private final String userId;
    private final String roomName;

    public AdminForceExitRequest(String userId, String roomName) {
        super(DtoType.ADMIN_FORCE_EXIT);
        this.userId = userId;
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + roomName;
    }
}
