package view.panel;

import app.Application;
import domain.ChatRoom;
import domain.User;
import dto.request.FriendAddRequest;
import dto.request.FriendChatStartRequest;
import dto.request.FriendRemoveRequest;
import dto.request.ProfileUpdateRequest;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;

import javax.swing.*;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class FriendListPanel extends JPanel {

    private final DefaultListModel<User> fullModel = new DefaultListModel<>();
    private final DefaultListModel<User> filteredModel = new DefaultListModel<>();
    private final JList<User> friendList = new JList<>(filteredModel);
    private final JTextField searchField = new JTextField();

    private final JLabel nicknameLabel = new JLabel("닉네임: -");
    private final JLabel idLabel = new JLabel("아이디: -");
    private final JButton profileBtn = new JButton("프로필 보기");
    private final JButton chatBtn = new JButton("1:1 채팅");
    private final JButton removeBtn = new JButton("친구삭제");

    public FriendListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout(5, 5));
        JButton myProfileBtn = new JButton("내 프로필");
        myProfileBtn.addActionListener(e -> openMyProfileDialog());
        JButton addFriendBtn = new JButton("친구추가");
        addFriendBtn.addActionListener(e -> onAddFriend());

        searchField.setToolTipText("친구 검색");
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::applyFilter));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnGroup.add(myProfileBtn);
        btnGroup.add(addFriendBtn);

        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(btnGroup, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setCellRenderer(new FriendRenderer());
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = friendList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    User selected = friendList.getModel().getElementAt(index);
                    updateDetail(selected);
                    if (e.getClickCount() == 2) {
                        startDirectChat(selected);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(friendList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new GridLayout(2, 1));
        detailPanel.add(nicknameLabel);
        detailPanel.add(idLabel);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        profileBtn.addActionListener(e -> {
            User selected = friendList.getSelectedValue();
            if (selected != null) {
                openFriendProfileDialog(selected);
            }
        });
        chatBtn.addActionListener(e -> {
            User selected = friendList.getSelectedValue();
            if (selected != null) {
                startDirectChat(selected);
            }
        });
        removeBtn.addActionListener(e -> onRemoveFriend());
        actions.add(profileBtn);
        actions.add(chatBtn);
        actions.add(removeBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(detailPanel, BorderLayout.CENTER);
        south.add(actions, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    public void setFriends(List<User> friends) {
        fullModel.clear();
        filteredModel.clear();
        if (friends != null) {
            for (User u : friends) {
                fullModel.addElement(u);
            }
        }
        applyFilter();
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        filteredModel.clear();
        for (int i = 0; i < fullModel.size(); i++) {
            User u = fullModel.get(i);
            if (keyword.isEmpty() || u.getNickName().toLowerCase().contains(keyword) || u.getId().toLowerCase().contains(keyword)) {
                filteredModel.addElement(u);
            }
        }
        if (!filteredModel.isEmpty()) {
            friendList.setSelectedIndex(0);
            updateDetail(filteredModel.get(0));
        } else {
            updateDetail(null);
        }
    }

    private void onAddFriend() {
        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nickname = JOptionPane.showInputDialog(this, "추가할 친구의 닉네임을 입력하세요.");
        if (nickname == null) return;
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

        boolean exists = Application.chatRooms.stream().anyMatch(r -> r.getName().equals(roomName));
        if (!exists) {
            Application.chatRooms.add(new ChatRoom(roomName));
            if (LobbyFrame.chatRoomListPanel != null) {
                LobbyFrame.chatRoomListPanel.addChatRoom(roomName);
            }
        }

        if (!Application.chatPanelMap.containsKey(roomName)) {
            ChatFrame chatFrame = new ChatFrame(roomName);
            Application.chatPanelMap.put(roomName, chatFrame.getChatPanel());
            Application.chatRoomUserListPanelMap.put(roomName, chatFrame.getChatRoomUserListPanel());
        }

        Application.sender.sendMessage(new FriendChatStartRequest(Application.me.getId(), friend.getId()));
    }

    private void updateDetail(User user) {
        if (user == null) {
            nicknameLabel.setText("닉네임: -");
            idLabel.setText("아이디: -");
        } else {
            nicknameLabel.setText("닉네임: " + user.getNickName());
            idLabel.setText("아이디: " + user.getId());
        }
    }

    private void openFriendProfileDialog(User friend) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "친구 프로필", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JLabel avatar = new JLabel(friend.getNickName().substring(0, 1), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(180, 200, 230));
        avatar.setForeground(Color.DARK_GRAY);
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 28f));
        avatar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.add(new JLabel("닉네임: " + friend.getNickName()));
        info.add(new JLabel("아이디: " + friend.getId()));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton chatButton = new JButton("1:1 채팅");
        JButton deleteButton = new JButton("친구삭제");
        chatButton.addActionListener(e -> {
            dialog.dispose();
            startDirectChat(friend);
        });
        deleteButton.addActionListener(e -> {
            dialog.dispose();
            friendList.setSelectedValue(friend, true);
            onRemoveFriend();
        });
        actions.add(chatButton);
        actions.add(deleteButton);

        dialog.add(avatar, BorderLayout.WEST);
        dialog.add(info, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openMyProfileDialog() {
        if (Application.me == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField nicknameField = new JTextField(Application.me.getNickName(), 15);
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JLabel("아이디: " + Application.me.getId()));
        panel.add(nicknameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "내 프로필", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newNick = nicknameField.getText().trim();
            if (newNick.isEmpty()) {
                JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newNick.equals(Application.me.getNickName())) {
                Application.sender.sendMessage(new ProfileUpdateRequest(Application.me.getId(), newNick));
            }
        }
    }

    private String buildDirectChatRoomName(String userId, String friendId) {
        if (userId.compareTo(friendId) < 0) {
            return "DM-" + userId + "-" + friendId;
        }
        return "DM-" + friendId + "-" + userId;
    }

    private static class FriendRenderer extends JPanel implements ListCellRenderer<User> {
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel name = new JLabel();

        FriendRenderer() {
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            avatar.setPreferredSize(new Dimension(36, 36));
            avatar.setOpaque(true);
            avatar.setBackground(new Color(200, 210, 230));
            avatar.setForeground(Color.DARK_GRAY);
            avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 14f));
            add(avatar, BorderLayout.WEST);
            add(name, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null && value.getNickName() != null && !value.getNickName().isEmpty()) {
                avatar.setText(value.getNickName().substring(0, 1));
            } else {
                avatar.setText("?");
            }
            name.setText(value != null ? value.getNickName() : "");

            if (isSelected) {
                setBackground(new Color(235, 239, 250));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    // Simple document listener utility
    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable callback;
        SimpleDocumentListener(Runnable callback) {
            this.callback = callback;
        }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
    }
}
