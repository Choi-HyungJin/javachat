package thread;

import app.ServerApplication;
import domain.ChatRoom;
import domain.User;
import dto.request.*;
import dto.response.*;
import dto.type.DtoType;
import dto.type.MessageType;
import exception.ChatRoomExistException;
import exception.ChatRoomNotFoundException;
import exception.UserNotFoundException;
import service.ChatService;
import service.FriendOperationResult;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

public class ServerThread extends Thread {

    Socket socket;

    ChatService chatService;

    public ServerThread(Socket socket, ChatService chatService) {
        this.socket = socket;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        super.run();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String str = reader.readLine();
                if (str == null) {
                    System.out.println("socket error (can't get socket input stream) - client socket closed");
                    try {
                        socket.close();
                        System.out.println("socket closed.");
                        ServerApplication.sockets.remove(socket);
                        return;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                if (str.startsWith("POST /address")) {
                    handleHttpAddressRequest(reader);
                    continue;
                }

                String[] token = str.split(":");
                DtoType type = DtoType.valueOf(token[0]);
                String message = token.length > 1 ? token[1] : "";

                processReceiveMessage(type, message);
                Thread.sleep(300);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void processReceiveMessage(DtoType type, String message)
            throws UserNotFoundException, ChatRoomNotFoundException, ChatRoomExistException, IOException {
        switch (type) {

        case LOGIN:
            LoginRequest loginReq = new LoginRequest(message);

            boolean isValid = chatService.isValidLogin(loginReq.getId(), loginReq.getPw());
            if (!isValid) {
                sendResponse("LOGIN_FAIL");
                return;
            }

            User user = chatService.getUserByLogin(loginReq.getId(), loginReq.getPw());
            if (user == null) {
                sendResponse("LOGIN_FAIL");
                return;
            }

            user.setSocket(socket);
            chatService.removeUser(user.getId());
            chatService.addUser(user);

            List<ChatRoom> dbChatRooms = chatService.getAllChatRooms();
            sendMessage(new InitDataResponse(dbChatRooms, chatService.getUsers()));

            sendMessage(new FriendListResponse(chatService.getFriends(user.getId())));

            UserListResponse lobbyUserList = new UserListResponse("Lobby", chatService.getUsers());
            broadcastToAll(lobbyUserList);

            System.out.println("[LOGIN] 사용자 로그인 완료: " + user.getId() + " (" + user.getNickName() + ")");
            break;

        case LOGOUT:
            LogoutRequest logoutReq = new LogoutRequest(message);
            System.out.println("[LOGOUT] 로그아웃 요청 - 사용자 " + logoutReq.getUserId());

            chatService.removeUser(logoutReq.getUserId());

            UserListResponse updatedUserList = new UserListResponse("Lobby", chatService.getUsers());
            broadcastToAll(updatedUserList);

            System.out.println("[LOGOUT] 로그아웃 완료");
            break;

        case ID_CHECK:
            boolean isDuplicate = chatService.isUserIdDuplicate(message.trim());
            sendDtoResponse(isDuplicate ? DtoType.ID_DUPLICATE : DtoType.ID_OK);
            break;

        case NICKNAME_CHECK:
            boolean isDuplicateNickname = chatService.isNicknameDuplicate(message.trim());
            sendDtoResponse(isDuplicateNickname ? DtoType.NICKNAME_DUPLICATE : DtoType.NICKNAME_OK);
            break;

        case SIGNUP:
            JoinRequest joinReq = new JoinRequest(message);
            if (!isValidPassword(joinReq.getPassword())) {
                sendDtoResponse(DtoType.SIGNUP_INVALID_PASSWORD);
                break;
            }

            boolean success = chatService.signupUser(joinReq);
            if (success) {
                try {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    int length = dis.readInt();
                    byte[] imageBytes = new byte[length];
                    dis.readFully(imageBytes);

                    File dir = new File("profile_images");
                    if (!dir.exists())
                        dir.mkdirs();

                    String fileName = UUID.randomUUID() + ".jpg";
                    File file = new File(dir, fileName);

                    BufferedImage originalImage = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
                    Image scaledImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                    resizedImage.getGraphics().drawImage(scaledImage, 0, 0, null);
                    ImageIO.write(resizedImage, "jpg", file);

                    String imagePath = "profile_images/" + fileName;
                    chatService.updateUserProfileImage(joinReq.getUserId(), imagePath);

                    System.out.println("[SIGNUP] 프로필 이미지 리사이징 완료: " + imagePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("이미지 처리 실패: " + ex.getMessage());
                }
                sendDtoResponse(DtoType.SIGNUP_SUCCESS);
            } else {
                sendDtoResponse(DtoType.SIGNUP_FAIL);
            }
            break;

        case CREATE_CHAT:
            CreateChatRoomRequest createReq = new CreateChatRoomRequest(message);
            System.out.println("[CREATE_CHAT] 채팅방 생성 요청 - 이름: " + createReq.getName() + ", 생성자 " + createReq.getUserId());

            ChatRoom newRoom = chatService.createChatRoom(createReq.getName(), createReq.getUserId());
            if (newRoom != null) {
                chatService.enterChatRoom(newRoom.getName(), createReq.getUserId());
                CreateChatRoomResponse createRes = new CreateChatRoomResponse(newRoom);
                broadcastToAll(createRes);

                List<User> roomUsers = chatService.getChatRoomUsers(newRoom.getName());
                UserListResponse userListRes = new UserListResponse(newRoom.getName(), roomUsers);
                sendMessage(userListRes);

                System.out.println("[CREATE_CHAT] 채팅방 생성 완료: " + newRoom.getName());
            }
            break;

        case ENTER_CHAT:
            EnterChatRequest enterReq = new EnterChatRequest(message);
            System.out.println("[ENTER_CHAT] 입장 요청 - 사용자 " + enterReq.getUserId() + ", 방 " + enterReq.getChatRoomName());

            User requestUser = chatService.getUser(enterReq.getUserId());
            if (requestUser == null) {
                System.out.println("[ENTER_CHAT] 로그인하지 않은 사용자: " + enterReq.getUserId());
                sendResponse("ENTER_FAIL:로그인이 필요합니다");
                break;
            }

            boolean isNewEntry = chatService.enterChatRoom(enterReq.getChatRoomName(), enterReq.getUserId());

            if (isNewEntry) {
                User enteredUser = chatService.getUser(enterReq.getUserId());
                MessageResponse enterMsg = new MessageResponse(
                        MessageType.ENTER,
                        enterReq.getChatRoomName(),
                        enteredUser.getNickName(),
                        enteredUser.getNickName() + "님이 입장하셨습니다."
                );
                broadcastToRoom(enterReq.getChatRoomName(), enterMsg);
            }

            List<User> users = chatService.getChatRoomUsers(enterReq.getChatRoomName());
            UserListResponse enterUserList = new UserListResponse(enterReq.getChatRoomName(), users);
            broadcastToRoom(enterReq.getChatRoomName(), enterUserList);

            System.out.println("[ENTER_CHAT] 입장 완료 - 현재 인원: " + users.size() + " (신규: " + isNewEntry + ")");
            break;

        case EXIT_CHAT:
            ExitChatRequest exitReq = new ExitChatRequest(message);
            System.out.println("[EXIT_CHAT] 퇴장 요청 - 사용자 " + exitReq.getUserId() + ", 방 " + exitReq.getChatRoomName());

            User exitedUser = chatService.exitChatRoom(exitReq.getChatRoomName(), exitReq.getUserId());

            if (exitedUser != null) {
                MessageResponse exitMsg = new MessageResponse(
                        MessageType.EXIT,
                        exitReq.getChatRoomName(),
                        exitedUser.getNickName(),
                        exitedUser.getNickName() + "님이 퇴장하셨습니다."
                );
                broadcastToRoom(exitReq.getChatRoomName(), exitMsg);
            }

            ChatRoom exitRoom = chatService.getChatRoom(exitReq.getChatRoomName());
            if (exitRoom != null && exitRoom.ieExistUser()) {
                List<User> remainingUsers = chatService.getChatRoomUsers(exitReq.getChatRoomName());
                UserListResponse exitUserList = new UserListResponse(exitReq.getChatRoomName(), remainingUsers);
                broadcastToRoom(exitReq.getChatRoomName(), exitUserList);
            }

            System.out.println("[EXIT_CHAT] 퇴장 완료");
            break;

        case MESSAGE:
            MessageResponse chatMsg = new MessageResponse(message);
            System.out.println("[MESSAGE] 메시지 수신 - 방 " + chatMsg.getChatRoomName() + ", 발신자 " + chatMsg.getUserName() + ", 내용: " + chatMsg.getMessage());

            if (chatMsg.getMessageType() == MessageType.CHAT && !"Lobby".equals(chatMsg.getChatRoomName())) {
                chatService.saveChatMessage(chatMsg.getChatRoomName(), chatMsg.getUserName(), chatMsg.getMessage());
            }

            if ("Lobby".equals(chatMsg.getChatRoomName())) {
                broadcastToAll(chatMsg);
            } else {
                broadcastToRoom(chatMsg.getChatRoomName(), chatMsg);
            }

            System.out.println("[MESSAGE] 메시지 전송 완료");
            break;

        case FRIEND_ADD:
            FriendAddRequest addReq = new FriendAddRequest(message);
            FriendOperationResult addResult = chatService.addFriendByNickname(addReq.getUserId(), addReq.getFriendNickname());
            sendMessage(new FriendOperationResponse(DtoType.FRIEND_ADD_RESULT, addResult.isSuccess(), addResult.getMessage()));
            if (addResult.isSuccess()) {
                sendMessage(new FriendListResponse(chatService.getFriends(addReq.getUserId())));
                String friendId = chatService.findUserIdByNickname(addReq.getFriendNickname());
                if (friendId != null) {
                    User friendUser = chatService.getUser(friendId);
                    if (friendUser != null) {
                        sendMessageToUser(friendUser, new FriendListResponse(chatService.getFriends(friendId)));
                    }
                }
            }
            break;

        case FRIEND_REMOVE:
            FriendRemoveRequest removeReq = new FriendRemoveRequest(message);
            FriendOperationResult removeResult = chatService.removeFriend(removeReq.getUserId(), removeReq.getFriendId());
            sendMessage(new FriendOperationResponse(DtoType.FRIEND_REMOVE_RESULT, removeResult.isSuccess(), removeResult.getMessage()));
            if (removeResult.isSuccess()) {
                sendMessage(new FriendListResponse(chatService.getFriends(removeReq.getUserId())));
                User friendUser = chatService.getUser(removeReq.getFriendId());
                if (friendUser != null) {
                    sendMessageToUser(friendUser, new FriendListResponse(chatService.getFriends(removeReq.getFriendId())));
                }
            }
            break;

        case FRIEND_CHAT_START:
            FriendChatStartRequest chatStartReq = new FriendChatStartRequest(message);
            String roomName = buildDirectChatRoomName(chatStartReq.getUserId(), chatStartReq.getFriendId());
            boolean createdNew = false;

            ChatRoom directRoom = chatService.getChatRoom(roomName);
            if (directRoom == null) {
                directRoom = chatService.createChatRoom(roomName, chatStartReq.getUserId());
                createdNew = directRoom != null;
            }

            if (directRoom != null) {
                chatService.enterChatRoom(roomName, chatStartReq.getUserId());

                if (createdNew) {
                    CreateChatRoomResponse createRes = new CreateChatRoomResponse(directRoom);
                    broadcastToAll(createRes);
                }

                String inviterNickname = chatService.findNicknameByUserId(chatStartReq.getUserId());
                User friendUser = chatService.getUser(chatStartReq.getFriendId());
                if (friendUser != null && inviterNickname != null) {
                    FriendChatInviteResponse inviteRes = new FriendChatInviteResponse(roomName, chatStartReq.getUserId(), inviterNickname);
                    sendMessageToUser(friendUser, inviteRes);
                }

                List<User> roomUsers = chatService.getChatRoomUsers(roomName);
                UserListResponse directUserList = new UserListResponse(roomName, roomUsers);
                broadcastToRoom(roomName, directUserList);
            } else {
                sendResponse("FRIEND_CHAT_FAIL");
            }
            break;

        case USER_LIST:
        case CHAT_ROOM_LIST:
            System.out.println("아직 구현되지 않은 기능: " + type);
            break;

        default:
            System.out.println("알 수 없는 메시지 타입: " + type);
            break;
        }
    }

    private void sendResponse(String message) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(message);
        writer.flush();
    }

    private void sendDtoResponse(DtoType type) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(type.toString());
        writer.flush();
    }

    private void sendMessage(DTO dto) {
        try {
            PrintWriter sender = new PrintWriter(socket.getOutputStream());
            sender.println(dto);
            sender.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToUser(User user, DTO dto) {
        try {
            Socket userSocket = user.getSocket();
            if (userSocket != null && !userSocket.isClosed()) {
                PrintWriter sender = new PrintWriter(userSocket.getOutputStream());
                sender.println(dto);
                sender.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastToRoom(String roomName, DTO dto) {
        try {
            ChatRoom room = chatService.getChatRoom(roomName);
            if (room == null) {
                System.out.println("[ERROR] 채팅방을 찾을 수 없음: " + roomName);
                return;
            }

            List<User> roomUsers = room.getUsers();
            for (User user : roomUsers) {
                Socket userSocket = user.getSocket();
                if (userSocket != null && !userSocket.isClosed()) {
                    PrintWriter sender = new PrintWriter(userSocket.getOutputStream());
                    sender.println(dto);
                    sender.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastToAll(DTO dto) {
        try {
            for (Socket s : ServerApplication.sockets) {
                if (s != null && !s.isClosed()) {
                    PrintWriter sender = new PrintWriter(s.getOutputStream());
                    sender.println(dto);
                    sender.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        return hasLetter && hasDigit && hasSpecial;
    }

    private void handleHttpAddressRequest(BufferedReader reader) {
        try {
            StringBuilder body = new StringBuilder();
            String line;
            int contentLength = 0;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(16).trim());
                }
            }

            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                body.append(buffer);
            }

            String jsonBody = body.toString();
            System.out.println("[HTTP] Address request: " + jsonBody);

            String postal = extractJsonValue(jsonBody, "postal");
            String address = extractJsonValue(jsonBody, "address");

            if (postal != null && address != null) {
                String result = postal + "|" + address;
                sendDtoMessage("ADDRESS_RESULT:" + result);
                System.out.println("[HTTP] Address sent to client: " + result);
            }

            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println("HTTP/1.1 200 OK");
            writer.println("Access-Control-Allow-Origin: *");
            writer.println("Content-Type: application/json");
            writer.println("Content-Length: 15");
            writer.println();
            writer.println("{\"ok\":true}");
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;

        startIndex = json.indexOf("\"", startIndex + searchKey.length()) + 1;
        int endIndex = json.indexOf("\"", startIndex);

        if (startIndex > 0 && endIndex > startIndex) {
            return json.substring(startIndex, endIndex);
        }
        return null;
    }

    private void sendDtoMessage(String message) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        writer.println(message);
        writer.flush();
    }

    private String buildDirectChatRoomName(String userId, String friendId) {
        if (userId.compareTo(friendId) < 0) {
            return "DM-" + userId + "-" + friendId;
        }
        return "DM-" + friendId + "-" + userId;
    }
}
