package dto.response;

import domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserListResponse {

    String chatRoomName;

    List<User> users;

    public UserListResponse(String message) {
        users = new ArrayList<>();

        String[] userValues = message.split("\\|");
        chatRoomName = userValues[0];

        for (int i = 1; i<userValues.length; i++) {
            String[] value = userValues[i].split(",");
            String id = value.length > 0 ? value[0] : "";
            String nickname = value.length > 1 ? value[1] : "";
            String role = value.length > 2 ? value[2] : "USER";
            boolean online = value.length > 3 && "1".equals(value[3]);
            boolean banned = value.length > 4 && "1".equals(value[4]);
            users.add(new User(id, nickname, role, online, banned));
        }

    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public List<User> getUsers() {
        return users;
    }
}
