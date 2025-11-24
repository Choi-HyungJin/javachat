package dto.request;

import dto.type.DtoType;

public class FriendAddRequest extends DTO {
    private final String userId;
    private final String friendNickname;

    public FriendAddRequest(String userId, String friendNickname) {
        super(DtoType.FRIEND_ADD);
        this.userId = userId;
        this.friendNickname = friendNickname;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + friendNickname;
    }
}
