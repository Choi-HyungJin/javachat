package dto.response;

import domain.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class AdminChatRoomListResponse {
    private final List<ChatRoom> chatRooms = new ArrayList<>();

    public AdminChatRoomListResponse(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String[] entries = message.split("\\|");
        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] parts = entry.split(",", 2);
            String name = parts.length > 0 ? parts[0] : "";
            int count = 0;
            try {
                count = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            } catch (NumberFormatException ignored) { }
            ChatRoom room = new ChatRoom(name, null, count);
            chatRooms.add(room);
        }
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }
}
