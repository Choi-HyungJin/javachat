package view.panel;

import app.Application;
import domain.ChatRoom;
import domain.User;
import dto.request.FriendAddRequest;
import dto.request.FriendChatStartRequest;
import dto.request.FriendRemoveRequest;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FriendListPanel extends JPanel {

    private final DefaultListModel<User> friendModel = new DefaultListModel<>();
    private final JList<User> friendList = new JList<>(friendModel);
    private final JLabel nicknameLabel = new JLabel("닉네임: -");
    private final JLabel idLabel = new JLabel("아이디: -");
    private final JButton removeBtn = new JButton("친구삭제");
    private final JButton chatBtn = new JButton("1:1채팅");

    public FriendListPanel(JFrame frame) {
        setLayout(null);

        JLabel title = new JLabel("친구 목록");
        title.setBounds(10, 5, 150, 25);
        add(title);

        JButton addFriendBtn = new JButton("친구추가");
        addFriendBtn.setBounds(260, 5, 120, 25);
        addFriendBtn.addActionListener(e -> onAddFriend());
        add(addFriendBtn);

        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setCellRenderer(new FriendCellRenderer());
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = friendList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    User selected = friendModel.get(index);
                    updateDetail(selected);
                    if (e.getClickCount() == 2) {
                        startDirectChat(selected);
                    }
                }
            }
        });

        JScrollPane scrPane = new JScrollPane(friendList);
        scrPane.setBounds(0, 35, 400, 90);
        add(scrPane);

        nicknameLabel.setBounds(10, 130, 200, 20);
        idLabel.setBounds(10, 150, 200, 20);
        add(nicknameLabel);
        add(idLabel);

        removeBtn.setBounds(220, 130, 150, 25);
        chatBtn.setBounds(220, 160, 150, 25);
        removeBtn.addActionListener(e -> onRemoveFriend());
        chatBtn.addActionListener(e -> {
            User selected = friendList.getSelectedValue();
            if (selected != null) {
                startDirectChat(selected);
            }
        });
        add(removeBtn);
        add(chatBtn);

        frame.add(this);
        setBounds(410, 10, 400, 200);
    }

    public void setFriends(List<User> friends) {
        friendModel.clear();
        if (friends == null) {
            return;
        }
        for (User user : friends) {
            friendModel.addElement(user);
        }
        if (!friends.isEmpty()) {
            friendList.setSelectedIndex(0);
            updateDetail(friends.get(0));
        } else {
            updateDetail(null);
        }
    }

    private void updateDetail(User user) {
        if (user == null) {
            nicknameLabel.setText("닉네임: -");
            idLabel.setText("아이디: -");
            return;
        }
        nicknameLabel.setText("닉네임: " + user.getNickName());
        idLabel.setText("아이디: " + user.getId());
    }

    private void onAddFriend() {
        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nickname = JOptionPane.showInputDialog(this, "추가할 친구의 닉네임을 입력하세요.");
        if (nickname == null) {
            return;
        }
        nickname = nickname.trim();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Application.sender.sendMessage(new FriendAddRequest(Application.me.getId(), nickname));
    }

    private void onRemoveFriend() {
        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User selected = friendList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "삭제할 친구를 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, selected.getNickName() + "님을 삭제할까요?", "친구삭제", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Application.sender.sendMessage(new FriendRemoveRequest(Application.me.getId(), selected.getId()));
        }
    }

    private void startDirectChat(User friend) {
        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomName = buildDirectChatRoomName(Application.me.getId(), friend.getId());

        if (Application.chatRooms.stream().noneMatch(r -> r.getName().equals(roomName))) {
            Application.chatRooms.add(new ChatRoom(roomName));
            if (LobbyFrame.chatRoomListPanel != null) {
                LobbyFrame.chatRoomListPanel.addChatRoomLabel(roomName);
            }
        }

        if (!Application.chatPanelMap.containsKey(roomName)) {
            ChatFrame chatFrame = new ChatFrame(roomName);
            Application.chatPanelMap.put(roomName, chatFrame.getChatPanel());
            Application.chatRoomUserListPanelMap.put(roomName, chatFrame.getChatRoomUserListPanel());
        }

        Application.sender.sendMessage(new FriendChatStartRequest(Application.me.getId(), friend.getId()));
    }

    private String buildDirectChatRoomName(String userId, String friendId) {
        if (userId.compareTo(friendId) < 0) {
            return "DM-" + userId + "-" + friendId;
        }
        return "DM-" + friendId + "-" + userId;
    }

    private static class FriendCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof User) {
                User user = (User) value;
                setText(user.getNickName());
            }
            return this;
        }
    }
}
