package dto.response;

import domain.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListResponse {
    private final List<User> users = new ArrayList<>();

    public AdminUserListResponse(String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        String[] entries = message.split("\\|");
        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            String[] parts = entry.split(",");
            String id = parts.length > 0 ? parts[0] : "";
            String nickname = parts.length > 1 ? parts[1] : "";
            String role = parts.length > 2 ? parts[2] : "USER";
            boolean online = parts.length > 3 && "1".equals(parts[3]);
            boolean banned = parts.length > 4 && "1".equals(parts[4]);
            users.add(new User(id, nickname, role, online, banned));
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
