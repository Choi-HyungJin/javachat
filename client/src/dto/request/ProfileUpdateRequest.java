package dto.request;

import dto.type.DtoType;

public class ProfileUpdateRequest extends DTO {
    private final String userId;
    private final String nickname;

    public ProfileUpdateRequest(String userId, String nickname) {
        super(DtoType.PROFILE_UPDATE);
        this.userId = userId;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + nickname;
    }
}
