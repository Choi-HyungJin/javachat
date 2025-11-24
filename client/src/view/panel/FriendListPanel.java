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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FriendListPanel extends JPanel {

    private static final Color BACKGROUND = new Color(245, 242, 235);
    private static final Color CARD_BG = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 20);
    private static final Font BODY_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);

    private final DefaultListModel<User> fullModel = new DefaultListModel<>();
    private final DefaultListModel<User> filteredModel = new DefaultListModel<>();
    private final JList<User> friendList = new JList<>(filteredModel);
    private final JTextField searchField = new JTextField();

    private final JLabel myAvatarLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel myNameLabel = new JLabel("내 프로필");
    private final JLabel myIdLabel = new JLabel("");
    private final JLabel friendCountLabel = new JLabel("친구 0");
    private final JLabel nicknameLabel = new JLabel("선택된 친구 -");
    private final JLabel idLabel = new JLabel("아이디 -");
    private final JButton profileBtn = new JButton("프로필 보기");
    private final JButton chatBtn = new JButton("1:1 채팅");
    private final JButton removeBtn = new JButton("친구 삭제");

    public FriendListPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 16));

        JLabel title = new JLabel("친구");
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(36, 36, 36));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton searchBtn = createGhostButton("\u2315", "검색");
        JButton refreshBtn = createGhostButton("\u21BA", "새로고침");
        JButton addBtn = createGhostButton("+", "친구 추가");
        searchBtn.addActionListener(e -> searchField.requestFocusInWindow());
        refreshBtn.addActionListener(e -> applyFilter());
        addBtn.addActionListener(e -> onAddFriend());
        actions.add(searchBtn);
        actions.add(refreshBtn);
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        body.add(buildProfileCard());
        body.add(Box.createVerticalStrut(10));
        body.add(buildSearchRow());
        body.add(Box.createVerticalStrut(8));
        body.add(buildListContainer());
        return body;
    }

    private JComponent buildProfileCard() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        myAvatarLabel.setPreferredSize(new Dimension(48, 48));
        myAvatarLabel.setOpaque(true);
        myAvatarLabel.setBackground(new Color(230, 228, 223));
        myAvatarLabel.setForeground(new Color(80, 80, 80));
        myAvatarLabel.setFont(new Font("Arial", Font.BOLD, 16));
        myAvatarLabel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        myNameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        myIdLabel.setFont(BODY_FONT);
        myNameLabel.setForeground(new Color(46, 46, 46));
        myIdLabel.setForeground(new Color(90, 90, 90));
        info.add(myNameLabel);
        info.add(Box.createVerticalStrut(2));
        info.add(myIdLabel);

        JButton multiProfileBtn = new JButton("멀티프로필 +");
        multiProfileBtn.setFocusPainted(false);
        multiProfileBtn.setBackground(new Color(247, 247, 247));
        multiProfileBtn.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        multiProfileBtn.addActionListener(e -> openMyProfileDialog());

        card.add(myAvatarLabel, BorderLayout.WEST);
        card.add(info, BorderLayout.CENTER);
        card.add(multiProfileBtn, BorderLayout.EAST);

        updateMyProfileCard();
        return card;
    }

    private JComponent buildSearchRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JLabel icon = new JLabel("\uD83D\uDD0E");
        icon.setForeground(new Color(110, 110, 110));
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchField.setOpaque(false);
        searchField.setFont(BODY_FONT);
        searchField.setToolTipText("친구 검색");
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::applyFilter));

        searchWrapper.add(icon, BorderLayout.WEST);
        searchWrapper.add(searchField, BorderLayout.CENTER);

        JButton addFriendBtn = new JButton("친구 추가");
        addFriendBtn.setFocusPainted(false);
        addFriendBtn.setBackground(new Color(250, 244, 230));
        addFriendBtn.setForeground(new Color(90, 70, 20));
        addFriendBtn.setBorder(BorderFactory.createLineBorder(new Color(232, 220, 200)));
        addFriendBtn.addActionListener(e -> onAddFriend());

        row.add(searchWrapper, BorderLayout.CENTER);
        row.add(addFriendBtn, BorderLayout.EAST);
        return row;
    }

    private JComponent buildListContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        friendCountLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        friendCountLabel.setForeground(new Color(90, 90, 90));
        friendCountLabel.setBorder(BorderFactory.createEmptyBorder(6, 2, 6, 2));
        container.add(friendCountLabel, BorderLayout.NORTH);

        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setCellRenderer(new FriendRenderer());
        friendList.setBackground(Color.WHITE);
        friendList.setBorder(BorderFactory.createEmptyBorder());
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
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder()
        ));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildFooter() {
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BACKGROUND);
        south.setBorder(BorderFactory.createEmptyBorder(10, 12, 16, 12));

        JPanel detailPanel = new JPanel(new GridLayout(2, 1));
        detailPanel.setOpaque(false);
        nicknameLabel.setFont(BODY_FONT);
        idLabel.setFont(BODY_FONT);
        nicknameLabel.setForeground(new Color(70, 70, 70));
        idLabel.setForeground(new Color(90, 90, 90));
        detailPanel.add(nicknameLabel);
        detailPanel.add(idLabel);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        for (JButton btn : new JButton[]{profileBtn, chatBtn, removeBtn}) {
            btn.setFocusPainted(false);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        }

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

        south.add(detailPanel, BorderLayout.CENTER);
        south.add(actions, BorderLayout.EAST);
        return south;
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
        friendCountLabel.setText("친구 " + filteredModel.size());
        updateMyProfileCard();
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        filteredModel.clear();
        for (int i = 0; i < fullModel.size(); i++) {
            User u = fullModel.get(i);
            String nick = u.getNickName() == null ? "" : u.getNickName();
            String id = u.getId() == null ? "" : u.getId();
            if (keyword.isEmpty() || nick.toLowerCase().contains(keyword) || id.toLowerCase().contains(keyword)) {
                filteredModel.addElement(u);
            }
        }
        friendCountLabel.setText("친구 " + filteredModel.size());
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
        String nickname = JOptionPane.showInputDialog(this, "추가할 친구의 닉네임을 입력하세요");
        if (nickname == null) return;
        nickname = nickname.trim();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요", "알림", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "삭제할 친구를 선택하세요", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, selected.getNickName() + "님을 삭제하시겠습니까?", "친구 삭제", JOptionPane.YES_NO_OPTION);
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
            Application.chatFrameMap.put(roomName, chatFrame);
            Application.chatPanelMap.put(roomName, chatFrame.getChatPanel());
            Application.chatRoomUserListPanelMap.put(roomName, chatFrame.getChatRoomUserListPanel());
        }

        Application.sender.sendMessage(new FriendChatStartRequest(Application.me.getId(), friend.getId()));
    }

    private void updateDetail(User user) {
        if (user == null) {
            nicknameLabel.setText("선택된 친구 -");
            idLabel.setText("아이디 -");
        } else {
            nicknameLabel.setText("선택된 친구: " + user.getNickName());
            idLabel.setText("아이디: " + user.getId());
        }
    }

    private void updateMyProfileCard() {
        if (Application.me != null) {
            String displayName = Application.me.getNickName() != null && !Application.me.getNickName().isEmpty()
                    ? Application.me.getNickName()
                    : Application.me.getId();
            myNameLabel.setText(displayName);
            myIdLabel.setText(Application.me.getId() != null ? Application.me.getId() : "");
            String initial = displayName == null || displayName.isEmpty() ? "?" : displayName.substring(0, 1);
            myAvatarLabel.setText(initial);
        } else {
            myNameLabel.setText("내 프로필");
            myIdLabel.setText("");
            myAvatarLabel.setText("?");
        }
    }

    private void openFriendProfileDialog(User friend) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "친구 프로필", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JLabel avatar = new JLabel(friend.getNickName().isEmpty() ? "?" : friend.getNickName().substring(0, 1), SwingConstants.CENTER);
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
        JButton deleteButton = new JButton("친구 삭제");
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
        String currentNick = Application.me.getNickName() == null ? "" : Application.me.getNickName();
        JTextField nicknameField = new JTextField(currentNick, 15);
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JLabel("아이디: " + Application.me.getId()));
        panel.add(nicknameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "내 프로필 수정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newNick = nicknameField.getText().trim();
            if (newNick.isEmpty()) {
                JOptionPane.showMessageDialog(this, "닉네임을 입력하세요", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newNick.equals(Application.me.getNickName())) {
                Application.me.setNickName(newNick);
                Application.sender.sendMessage(new ProfileUpdateRequest(Application.me.getId(), newNick));
            }
            updateMyProfileCard();
        }
    }

    private String buildDirectChatRoomName(String userId, String friendId) {
        if (userId.compareTo(friendId) < 0) {
            return "DM-" + userId + "-" + friendId;
        }
        return "DM-" + friendId + "-" + userId;
    }

    private JButton createGhostButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        btn.setToolTipText(tooltip);
        btn.setForeground(new Color(80, 80, 80));
        return btn;
    }

    private static class FriendRenderer extends JPanel implements ListCellRenderer<User> {
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel name = new JLabel();
        private final JLabel status = new JLabel("최근 메시지가 없습니다.");

        FriendRenderer() {
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setBackground(Color.WHITE);
            avatar.setPreferredSize(new Dimension(44, 44));
            avatar.setOpaque(true);
            avatar.setBackground(new Color(230, 228, 223));
            avatar.setForeground(new Color(80, 80, 80));
            avatar.setFont(new Font("Arial", Font.BOLD, 14));

            name.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            name.setForeground(new Color(34, 34, 34));
            status.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
            status.setForeground(new Color(120, 120, 120));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(name);
            text.add(Box.createVerticalStrut(2));
            text.add(status);

            add(avatar, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null && value.getNickName() != null && !value.getNickName().isEmpty()) {
                avatar.setText(value.getNickName().substring(0, 1));
            } else {
                avatar.setText("?");
            }
            name.setText(value != null ? value.getNickName() : "");
            status.setText(value != null ? "아이디: " + value.getId() : "");

            if (isSelected) {
                setBackground(new Color(249, 247, 240));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable callback;
        SimpleDocumentListener(Runnable callback) {
            this.callback = callback;
        }
        @Override public void insertUpdate(DocumentEvent e) { callback.run(); }
        @Override public void removeUpdate(DocumentEvent e) { callback.run(); }
        @Override public void changedUpdate(DocumentEvent e) { callback.run(); }
    }
}
