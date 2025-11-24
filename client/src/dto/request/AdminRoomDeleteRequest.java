package dto.request;

import dto.type.DtoType;

public class AdminRoomDeleteRequest extends DTO {
    private final String roomName;

    public AdminRoomDeleteRequest(String roomName) {
        super(DtoType.ADMIN_ROOM_DELETE);
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return super.toString() + roomName;
    }
}
