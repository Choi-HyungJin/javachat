package dto.request;

import dto.type.DtoType;

public class AdminBanRequest extends DTO {
    private final String userId;
    private final boolean banned;

    public AdminBanRequest(String userId, boolean banned) {
        super(DtoType.ADMIN_BAN);
        this.userId = userId;
        this.banned = banned;
    }

    @Override
    public String toString() {
        return super.toString() + userId + "," + (banned ? "1" : "0");
    }
}
