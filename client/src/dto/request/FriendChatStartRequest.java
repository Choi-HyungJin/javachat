package dto.request;

import dto.type.DtoType;

public class FriendChatStartRequest extends DTO {
    private final String userId;
    private final String friendId;

    public FriendChatStartRequest(String userId, String friendId) {
        super(DtoType.FRIEND_CHAT_START);
        this.userId = userId;
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + friendId;
    }
}
