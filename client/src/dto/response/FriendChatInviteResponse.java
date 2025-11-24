package dto.response;

public class FriendChatInviteResponse {
    private final String roomName;
    private final String inviterId;
    private final String inviterNickname;

    public FriendChatInviteResponse(String payload) {
        String[] values = payload.split(",", 3);
        this.roomName = values.length > 0 ? values[0] : "";
        this.inviterId = values.length > 1 ? values[1] : "";
        this.inviterNickname = values.length > 2 ? values[2] : "";
    }

    public String getRoomName() {
        return roomName;
    }

    public String getInviterId() {
        return inviterId;
    }

    public String getInviterNickname() {
        return inviterNickname;
    }
}
