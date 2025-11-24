package dto.response;

import domain.ChatRoom;
import domain.User;

import java.util.*;

public class InitResponse {

    private List<ChatRoom> chatRooms = new ArrayList<>();

    private List<User> users = new ArrayList<>();

    public InitResponse(String message) {
        System.out.println(message);
        String[] data = message.split("\\+", -1);
        if (data.length > 0 && !data[0].isEmpty()) {
            String[] chatRoomNames = data[0].split("\\|");
            for (String name : chatRoomNames) {
                if (!name.isEmpty()) {
                    chatRooms.add(new ChatRoom(name));
                }
            }
        }

        if (data.length > 1 && !data[1].isEmpty()) {
            String[] userValues = data[1].split("\\|");
            for (String userValue : userValues) {
                if (userValue.isEmpty()) {
                    continue;
                }
                String[] elem = userValue.split(",");
                String id = elem.length > 0 ? elem[0] : "";
                String nickname = elem.length > 1 ? elem[1] : "";
                String role = elem.length > 2 ? elem[2] : "USER";
                boolean online = elem.length > 3 && "1".equals(elem[3]);
                boolean banned = elem.length > 4 && "1".equals(elem[4]);
                users.add(new User(id, nickname, role, online, banned));
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }
}
