package dto.request;

import dto.type.DtoType;

public class AdminForceLogoutRequest extends DTO {
    private final String userId;

    public AdminForceLogoutRequest(String userId) {
        super(DtoType.ADMIN_FORCE_LOGOUT);
        this.userId = userId;
    }

    @Override
    public String toString() {
        return super.toString() + userId;
    }
}
