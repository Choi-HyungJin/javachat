package dto.request;

import dto.type.DtoType;

public class FriendRemoveRequest extends DTO {
    private final String userId;
    private final String friendId;

    public FriendRemoveRequest(String userId, String friendId) {
        super(DtoType.FRIEND_REMOVE);
        this.userId = userId;
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + friendId;
    }
}
