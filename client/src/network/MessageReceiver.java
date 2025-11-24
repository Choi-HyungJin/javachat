package network;

import app.Application;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import dto.response.*;
import dto.type.DtoType;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;
import view.panel.ChatPanel;
import view.panel.ChatRoomUserListPanel;
import dto.response.ProfileUpdateResponse;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageReceiver extends Thread {

    Socket socket;

    // 중복확인 응답 콜백
    private static volatile DuplicateCheckCallback duplicateCheckCallback;

    public interface DuplicateCheckCallback {
        void onResult(DtoType type, String message);
    }

    public static void setDuplicateCheckCallback(DuplicateCheckCallback callback) {
        duplicateCheckCallback = callback;
    }

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String str = reader.readLine();
                if (str == null) {
                    try {
                        socket.close();
                        System.out.println(Application.me != null ? Application.me.getNickName() + "'s socket is closed." : "Socket is closed.");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("disconnect");
                    System.exit(1);
                }
                System.out.println(str);
                String[] token = str.split(":");

                try {
                    DtoType type = DtoType.valueOf(token[0]);
                    String message = token.length > 1 ? token[1] : "";
                    processReceivedMessage(type, message);
                } catch (IllegalArgumentException e) {
                    System.out.println("알 수 없는 DtoType: " + token[0]);
                }

                Thread.sleep(300);
            }
        }
        catch (Exception e) {
            try {
                System.out.println("socket error (can't get socket input stream)");
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private void processReceivedMessage(DtoType type, String message) {
        System.out.println("[" + type + "] 메시지 수신: " + message);

        switch (type) {
            case LOGIN_FAIL:
                if (Application.loginFrame != null) {
                    SwingUtilities.invokeLater(() -> Application.loginFrame.handleLoginFailure("??? ?? ????? ???? ????."));
                }
                break;

            case LOGIN_BANNED:
                if (Application.loginFrame != null) {
                    SwingUtilities.invokeLater(() -> Application.loginFrame.handleLoginFailure("??? ???? ???? ? ????."));
                }
                break;

            case LOGIN:
                InitResponse initRes = new InitResponse(message);
                Application.chatRooms = initRes.getChatRooms();
                Application.users = initRes.getUsers();

                for (domain.User u : Application.users) {
                    if (u.getId().equals(Application.me.getId())) {
                        Application.me.setNickName(u.getNickName());
                        Application.me.setRole(u.getRole());
                        Application.me.setOnline(u.isOnline());
                        Application.me.setBanned(u.isBanned());
                        System.out.println("??? ??: " + Application.me.getId() + " (" + Application.me.getNickName() + ")");
                        break;
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (Application.isAdmin()) {
                        if (Application.adminFrame == null) {
                            Application.adminFrame = new view.frame.AdminFrame();
                        }
                        Application.adminFrame.setVisible(true);
                        Application.sender.sendMessage(new dto.request.AdminInitRequest());
                        if (Application.lobbyFrame != null) {
                            Application.lobbyFrame.setVisible(false);
                        }
                    } else if (Application.lobbyFrame != null) {
                        Application.lobbyFrame.setVisible(true);
                    }
                    if (Application.loginFrame != null) {
                        Application.loginFrame.dispose();
                    }
                    if (LobbyFrame.chatRoomListPanel != null) {
                        LobbyFrame.chatRoomListPanel.paintChatRoomList();
                    }
                });
                break;
                    }
                }

                if (LobbyFrame.chatRoomListPanel != null) {
                    LobbyFrame.chatRoomListPanel.paintChatRoomList();
                }
                break;

            case MESSAGE:
                MessageResponse messageRes = new MessageResponse(message);
                ChatPanel chatPanel = Application.chatPanelMap.get(messageRes.getChatRoomName());
                if (chatPanel != null) {
                    chatPanel.addMessage(messageRes.getMessageType(), messageRes.getUserName(), messageRes.getMessage());
                    System.out.println("[MESSAGE] 메시지 수신 - 방 " + messageRes.getChatRoomName() + ", 발신자 " + messageRes.getUserName());
                } else {
                    System.out.println("[WARNING] 채팅 패널을 찾을 수 없음: " + messageRes.getChatRoomName());
                }
                break;

            case CREATE_CHAT:
                CreateChatRoomResponse createChatRoomResponse = new CreateChatRoomResponse(message);
                String chatRoomName = createChatRoomResponse.getName();

                boolean exists = Application.chatRooms.stream()
                    .anyMatch(room -> room.getName().equals(chatRoomName));

                if (!exists) {
                    ChatRoom newChatRoom = new ChatRoom(chatRoomName);
                    Application.chatRooms.add(newChatRoom);
                    System.out.println("[CREATE_CHAT] 새 채팅방 추가: " + chatRoomName);
                    if (LobbyFrame.chatRoomListPanel != null) {
                        LobbyFrame.chatRoomListPanel.addChatRoom(chatRoomName);
                    }
                }
                break;

            case ADMIN_USER_LIST:
                AdminUserListResponse adminUsers = new AdminUserListResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateUsers(adminUsers.getUsers());
                }
                break;

            case ADMIN_CHATROOM_LIST:
                AdminChatRoomListResponse adminRooms = new AdminChatRoomListResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateChatRooms(adminRooms.getChatRooms());
                }
                break;

            case ADMIN_MESSAGE_RESULT:
                AdminMessageSearchResponse adminMessages = new AdminMessageSearchResponse(message);
                if (Application.adminFrame != null) {
                    Application.adminFrame.updateMessages(adminMessages.getMessages());
                }
                break;

            case ADMIN_ACTION_RESULT:
                AdminActionResultResponse adminAction = new AdminActionResultResponse(message);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, adminAction.getMessage(),
                                adminAction.isSuccess() ? "??" : "??",
                                adminAction.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
                break;

            case FORCE_LOGOUT:
                SwingUtilities.invokeLater(() -> {
                    String notice = (message == null || message.isEmpty()) ? "???? ?? ?????????." : message;
                    JOptionPane.showMessageDialog(null, notice, "??", JOptionPane.WARNING_MESSAGE);
                    try { socket.close(); } catch (Exception ignored) { }
                    System.exit(0);
                });
                break;

            case FORCE_EXIT:
                String[] exitParts = message.split("\|", 2);
                String forcedRoom = exitParts.length > 0 ? exitParts[0] : "";
                String reason = exitParts.length > 1 ? exitParts[1] : "????? ???????.";
                closeChatRoom(forcedRoom, reason);
                break;

            case USER_LIST:
                UserListResponse userListRes = new UserListResponse(message);

                if ("Lobby".equals(userListRes.getChatRoomName())) {
                    Application.users = userListRes.getUsers();
                    System.out.println("[USER_LIST] 로비 사용자 목록 업데이트 (" + Application.users.size() + ")");
                } else {
                    ChatRoomUserListPanel userListPanel = Application.chatRoomUserListPanelMap.get(userListRes.getChatRoomName());
                    if (userListPanel != null) {
                        userListPanel.paintChatUsers(userListRes.getUsers());
                        System.out.println("[USER_LIST] 사용자 목록 업데이트: " + userListRes.getChatRoomName() + " (" + userListRes.getUsers().size() + ")");
                    } else {
                        System.out.println("[WARNING] 사용자 목록 패널을 찾을 수 없음: " + userListRes.getChatRoomName());
                    }
                }
                break;

            case CHAT_ROOM_LIST:
                ChatRoomListResponse chatRoomListRes = new ChatRoomListResponse(message);
                Application.chatRooms = chatRoomListRes.getChatRooms();
                if (LobbyFrame.chatRoomListPanel != null) {
                    LobbyFrame.chatRoomListPanel.paintChatRoomList();
                }
                break;

            case CHAT_HISTORY:
                ChatHistoryResponse historyRes = new ChatHistoryResponse(message);
                ChatPanel historyPanel = Application.chatPanelMap.get(historyRes.getChatRoomName());
                if (historyPanel != null) {
                    for (ChatHistoryResponse.HistoryEntry entry : historyRes.getEntries()) {
                        historyPanel.addHistoryMessage(entry.nickname, entry.content, entry.time);
                    }
                    System.out.println("[CHAT_HISTORY] 이전 대화 로드 완료: " + historyRes.getChatRoomName() + " (" + historyRes.getEntries().size() + ")");
                } else {
                    System.out.println("[WARNING] 히스토리 패널을 찾을 수 없음: " + historyRes.getChatRoomName());
                }
                break;

            case FRIEND_LIST:
                FriendListResponse friendListResponse = new FriendListResponse(message);
                Application.friends = friendListResponse.getFriends();
                if (LobbyFrame.friendListPanel != null) {
                    LobbyFrame.friendListPanel.setFriends(Application.friends);
                    System.out.println("[FRIEND_LIST] 친구 목록 업데이트 (" + Application.friends.size() + ")");
                }
                break;

            case FRIEND_ADD_RESULT:
            case FRIEND_REMOVE_RESULT:
                FriendOperationResponse opRes = new FriendOperationResponse(message);
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, opRes.getMessage(), opRes.isSuccess() ? "알림" : "오류",
                                opRes.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE)
                );
                break;

            case PROFILE_UPDATE_RESULT:
                ProfileUpdateResponse profileRes = new ProfileUpdateResponse(message);
                if (profileRes.isSuccess() && Application.me != null) {
                    Application.me.setNickName(profileRes.getNickname());
                }
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, profileRes.getMessage(),
                                profileRes.isSuccess() ? "알림" : "오류",
                                profileRes.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
                break;

            case FRIEND_CHAT_INVITE:
                FriendChatInviteResponse inviteRes = new FriendChatInviteResponse(message);
                if (Application.me != null) {
                    if (Application.chatRooms.stream().noneMatch(r -> r.getName().equals(inviteRes.getRoomName()))) {
                        Application.chatRooms.add(new ChatRoom(inviteRes.getRoomName()));
                        if (LobbyFrame.chatRoomListPanel != null) {
                            LobbyFrame.chatRoomListPanel.addChatRoom(inviteRes.getRoomName());
                        }
                    }
                    if (!Application.chatPanelMap.containsKey(inviteRes.getRoomName())) {
                                                ChatFrame chatFrame = new ChatFrame(inviteRes.getRoomName());
                        Application.chatFrameMap.put(inviteRes.getRoomName(), chatFrame);
                        Application.chatPanelMap.put(inviteRes.getRoomName(), chatFrame.getChatPanel());
                        Application.chatRoomUserListPanelMap.put(inviteRes.getRoomName(), chatFrame.getChatRoomUserListPanel());
                    }
                    Application.sender.sendMessage(new EnterChatRequest(inviteRes.getRoomName(), Application.me.getId()));
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(null,
                                    inviteRes.getInviterNickname() + "님이 1:1 채팅을 시작했습니다.",
                                    "채팅 초대", JOptionPane.INFORMATION_MESSAGE));
                }
                break;

            case ID_OK:
            case ID_DUPLICATE:
            case NICKNAME_OK:
            case NICKNAME_DUPLICATE:
            case SIGNUP_SUCCESS:
            case SIGNUP_FAIL:
            case SIGNUP_INVALID_PASSWORD:
            case ADDRESS_RESULT:
                if (duplicateCheckCallback != null) {
                    duplicateCheckCallback.onResult(type, message);
                }
                break;

            default:
                System.out.println("[WARNING] 처리할 수 없는 메시지 타입 " + type);
                break;
        }
    }

    private void closeChatRoom(String roomName, String reason) {
        ChatFrame frame = Application.chatFrameMap.remove(roomName);
        if (frame != null) {
            frame.dispose();
        }
        Application.chatPanelMap.remove(roomName);
        Application.chatRoomUserListPanelMap.remove(roomName);
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, reason, "??", JOptionPane.WARNING_MESSAGE);
            if (LobbyFrame.chatRoomListPanel != null) {
                LobbyFrame.chatRoomListPanel.paintChatRoomList();
            }
        });
    }

}