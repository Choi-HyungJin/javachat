package domain;

public class ChatRoom {

    String name;
    String creatorId;
    int memberCount;

    public ChatRoom(String name) {
        this.name = name;
    }

    public ChatRoom(String name, String creatorId) {
        this.name = name;
        this.creatorId = creatorId;
    }

    public ChatRoom(String name, String creatorId, int memberCount) {
        this.name = name;
        this.creatorId = creatorId;
        this.memberCount = memberCount;
    }

    public String getName() {
        return name;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}
