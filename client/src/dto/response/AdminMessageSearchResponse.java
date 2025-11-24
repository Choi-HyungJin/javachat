package dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminMessageSearchResponse {
    public static class AdminMessage {
        public final long id;
        public final String roomName;
        public final String nickname;
        public final String content;
        public final String sentAt;

        public AdminMessage(long id, String roomName, String nickname, String content, String sentAt) {
            this.id = id;
            this.roomName = roomName;
            this.nickname = nickname;
            this.content = content;
            this.sentAt = sentAt;
        }
    }

    private final List<AdminMessage> messages = new ArrayList<>();

    public AdminMessageSearchResponse(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String[] entries = message.split("\\|");
        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] parts = entry.split(",", 5);
            try {
                long id = Long.parseLong(parts[0]);
                String roomName = parts.length > 1 ? parts[1] : "";
                String nickname = parts.length > 2 ? parts[2] : "";
                String content = parts.length > 3 ? parts[3] : "";
                String sentAt = parts.length > 4 ? parts[4] : "";
                messages.add(new AdminMessage(id, roomName, nickname, content, sentAt));
            } catch (NumberFormatException ignored) { }
        }
    }

    public List<AdminMessage> getMessages() {
        return messages;
    }
}
