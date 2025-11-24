package app;

import domain.ChatRoom;
import domain.User;
import network.MessageReceiver;
import network.MessageSender;
import util.PostcodeHttpServer;
import view.frame.LobbyFrame;
import view.panel.ChatPanel;
import view.panel.ChatRoomUserListPanel;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    public static Socket socket;

    public static MessageSender sender;

    public static MessageReceiver receiver;

    public static LobbyFrame lobbyFrame;

    public static User me;

    public static List<User> users = new ArrayList<>(); // 현재 접속 중인 모든 사용자 리스트
    public static List<User> friends = new ArrayList<>(); // 친구 목록
    public static List<ChatRoom> chatRooms = new ArrayList<>(); // 채팅방 목록

    public static Map<String, ChatPanel> chatPanelMap = new HashMap<>();
    public static Map<String, ChatRoomUserListPanel> chatRoomUserListPanelMap = new HashMap<>();

    public static final String LOBBY_CHAT_NAME = "Lobby";

    public Application() {
        try {
            PostcodeHttpServer.start();

            socket = new Socket("192.168.0.22", 9000);
            System.out.println("connect success to chat server");

            sender = new MessageSender(socket);
            receiver = new MessageReceiver(socket);
            receiver.start();

            lobbyFrame = new LobbyFrame();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
