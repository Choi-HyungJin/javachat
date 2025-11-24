package dto.request;

import dto.type.DtoType;

public class AdminMessageDeleteRequest extends DTO {
    private final long messageId;

    public AdminMessageDeleteRequest(long messageId) {
        super(DtoType.ADMIN_MESSAGE_DELETE);
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return super.toString() + messageId;
    }
}
