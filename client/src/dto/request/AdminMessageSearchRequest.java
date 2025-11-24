package dto.request;

import dto.type.DtoType;

public class AdminMessageSearchRequest extends DTO {
    private final String nickname;
    private final String roomName;

    public AdminMessageSearchRequest(String nickname, String roomName) {
        super(DtoType.ADMIN_MESSAGE_SEARCH);
        this.nickname = nickname == null ? "" : nickname;
        this.roomName = roomName == null ? "" : roomName;
    }

    @Override
    public String toString() {
        return super.toString() + nickname + "|" + roomName;
    }
}
