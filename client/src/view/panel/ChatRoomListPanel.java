package view.panel;

import app.Application;
import domain.ChatRoom;
import dto.request.EnterChatRequest;
import view.frame.ChatFrame;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatRoomListPanel extends JPanel {

    private static final Color BACKGROUND = new Color(245, 242, 235);
    private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 20);
    private static final Font BODY_FONT = new Font("Malgun Gothic", Font.PLAIN, 13);

    private final DefaultListModel<ChatRoom> roomModel = new DefaultListModel<>();
    private final JList<ChatRoom> roomList = new JList<>(roomModel);

    private final JToggleButton allFilter = new JToggleButton("전체");
    private final JToggleButton unreadFilter = new JToggleButton("읽지않음 3");
    private final JToggleButton directFilter = new JToggleButton("지연건설-개인");
    private final JToggleButton groupFilter = new JToggleButton("지연건설-단체");

    public ChatRoomListPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildListArea(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel chatTitle = new JLabel("채팅");
        chatTitle.setFont(TITLE_FONT);
        JLabel divider = new JLabel("｜");
        divider.setForeground(new Color(120, 120, 120));
        JLabel openChat = new JLabel("오픈채팅");
        openChat.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
        openChat.setForeground(new Color(140, 140, 140));
        titlePanel.add(chatTitle);
        titlePanel.add(divider);
        titlePanel.add(openChat);

        JPanel icons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        icons.setOpaque(false);
        JButton newChatBtn = createGhostButton("+", "새 채팅 만들기");
        newChatBtn.addActionListener(e -> {
            if (LobbyFrame.createChatFrame != null) {
                LobbyFrame.createChatFrame.setVisible(true);
            }
        });
        icons.add(newChatBtn);
        icons.add(createGhostButton("\uD83D\uDD0E", "검색"));
        icons.add(createGhostButton("\uD83D\uDCE9", "메일"));
        icons.add(createGhostButton("\u2630", "메뉴"));

        top.add(titlePanel, BorderLayout.WEST);
        top.add(icons, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        filters.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        for (JToggleButton btn : new JToggleButton[]{allFilter, unreadFilter, directFilter, groupFilter}) {
            styleFilterButton(btn);
            group.add(btn);
            filters.add(btn);
        }
        allFilter.setSelected(true);

        header.add(top);
        header.add(filters);
        return header;
    }

    private JComponent buildListArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

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
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 232, 232)),
                BorderFactory.createEmptyBorder()
        ));
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        area.add(scroll, BorderLayout.CENTER);
        return area;
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
        Application.chatFrameMap.put(chatRoomName, chatFrame);
        Application.chatPanelMap.put(chatRoomName, chatFrame.getChatPanel());
        Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.getChatRoomUserListPanel());
        Application.sender.sendMessage(new EnterChatRequest(chatRoomName, Application.me.getId()));
    }

    private void styleFilterButton(JToggleButton btn) {
        btn.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        btn.setPreferredSize(new Dimension(120, 32));
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

    private static class ChatRoomRenderer extends JPanel implements ListCellRenderer<ChatRoom> {
        private final JLabel avatar = new JLabel("", SwingConstants.CENTER);
        private final JLabel title = new JLabel();
        private final JLabel lastMessage = new JLabel("새 메시지가 없습니다.");
        private final JLabel timeLabel = new JLabel("");
        private final JLabel badge = new JLabel("", SwingConstants.CENTER);

        ChatRoomRenderer() {
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setBackground(Color.WHITE);

            avatar.setPreferredSize(new Dimension(46, 46));
            avatar.setOpaque(true);
            avatar.setBackground(new Color(230, 228, 223));
            avatar.setForeground(new Color(80, 80, 80));
            avatar.setFont(new Font("Arial", Font.BOLD, 14));

            title.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            title.setForeground(new Color(30, 30, 30));
            lastMessage.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
            lastMessage.setForeground(new Color(120, 120, 120));

            timeLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 11));
            timeLabel.setForeground(new Color(140, 140, 140));
            badge.setPreferredSize(new Dimension(22, 22));
            badge.setFont(new Font("Malgun Gothic", Font.BOLD, 11));
            badge.setOpaque(true);
            badge.setBackground(new Color(234, 83, 60));
            badge.setForeground(Color.WHITE);
            badge.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

            JPanel textBox = new JPanel();
            textBox.setOpaque(false);
            textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
            textBox.add(title);
            textBox.add(Box.createVerticalStrut(3));
            textBox.add(lastMessage);

            JPanel right = new JPanel();
            right.setOpaque(false);
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.add(timeLabel);
            right.add(Box.createVerticalStrut(8));
            right.add(badge);

            add(avatar, BorderLayout.WEST);
            add(textBox, BorderLayout.CENTER);
            add(right, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ChatRoom> list, ChatRoom value, int index, boolean isSelected, boolean cellHasFocus) {
            String name = value != null ? value.getName() : "";
            title.setText(name);
            lastMessage.setText("새 메시지가 없습니다.");
            avatar.setText(name.isEmpty() ? "?" : name.substring(0, 1));

            if (index % 2 == 0) {
                timeLabel.setText("오전 10:" + String.format("%02d", index));
                badge.setText(String.valueOf((index % 3) + 1));
                badge.setVisible(true);
            } else {
                timeLabel.setText("");
                badge.setVisible(false);
            }

            if (isSelected) {
                setBackground(new Color(245, 243, 236));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
}
