package view.panel;

import app.Application;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import view.frame.ChatFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoomListPanel extends JPanel {

    private final DefaultListModel<ChatRoom> roomModel = new DefaultListModel<>();
    private final JList<ChatRoom> roomList = new JList<>(roomModel);

    public ChatRoomListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("채팅방");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        roomList.setBorder(BorderFactory.createEmptyBorder());
        roomList.setBackground(Color.WHITE);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new ChatRoomRenderer());
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = roomList.locationToIndex(e.getPoint());
                if (index >= 0 && e.getClickCount() == 2) {
                    ChatRoom room = roomModel.getElementAt(index);
                    enterRoom(room.getName());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(roomList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        add(scroll, BorderLayout.CENTER);
    }

    public void paintChatRoomList() {
        roomModel.clear();
        for (ChatRoom chatRoom : Application.chatRooms) {
            if (!Application.LOBBY_CHAT_NAME.equals(chatRoom.getName())) {
                roomModel.addElement(chatRoom);
            }
        }
    }

    public void addChatRoom(String chatRoomName) {
        if (Application.LOBBY_CHAT_NAME.equals(chatRoomName)) {
            return;
        }
        boolean exists = false;
        for (int i = 0; i < roomModel.size(); i++) {
            if (roomModel.get(i).getName().equals(chatRoomName)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            roomModel.addElement(new ChatRoom(chatRoomName));
        }
    }

    private void enterRoom(String chatRoomName) {
        if (Application.me == null || Application.me.getId() == null) {
            JOptionPane.showMessageDialog(null, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Application.chatPanelMap.containsKey(chatRoomName)) {
            JOptionPane.showMessageDialog(null, "이미 열려있는 채팅방입니다.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ChatFrame chatFrame = new ChatFrame(chatRoomName);
        Application.chatPanelMap.put(chatRoomName, chatFrame.getChatPanel());
        Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.getChatRoomUserListPanel());
        Application.sender.sendMessage(new EnterChatRequest(chatRoomName, Application.me.getId()));
    }

    private static class ChatRoomRenderer extends JPanel implements ListCellRenderer<ChatRoom> {
        private final JLabel name = new JLabel();

        ChatRoomRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            name.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            add(name, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ChatRoom> list, ChatRoom value, int index, boolean isSelected, boolean cellHasFocus) {
            name.setText(value != null ? value.getName() : "");
            if (isSelected) {
                setBackground(new Color(246, 248, 252));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
