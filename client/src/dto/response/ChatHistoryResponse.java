package dto.response;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryResponse {
    private String chatRoomName;
    private List<HistoryEntry> entries = new ArrayList<>();

    public static class HistoryEntry {
        public final String nickname;
        public final String time;
        public final String content;
        public HistoryEntry(String nickname, String time, String content) {
            this.nickname = nickname;
            this.time = time;
            this.content = content;
        }
    }

    public ChatHistoryResponse(String raw) {
        // 형식: roomName|nick,time,content|nick,time,content
        String[] parts = raw.split("\\|");
        if (parts.length > 0) {
            chatRoomName = parts[0];
        }
        for (int i = 1; i < parts.length; i++) {
            String seg = parts[i];
            String[] fields = seg.split(",", 3);
            if (fields.length == 3) {
                entries.add(new HistoryEntry(fields[0], fields[1], fields[2]));
            }
        }
    }

    public String getChatRoomName() { return chatRoomName; }
    public List<HistoryEntry> getEntries() { return entries; }
}