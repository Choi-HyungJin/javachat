package dto.request;

import dto.type.DtoType;

public class LogoutRequest extends DTO {
    
    String userId;
    
    public LogoutRequest(String userId) {
        super(DtoType.LOGOUT);
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return DtoType.LOGOUT + ":" + userId;
    }
}
