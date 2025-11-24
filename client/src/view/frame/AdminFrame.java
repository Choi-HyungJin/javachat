package view.frame;

import app.Application;
import domain.ChatRoom;
import domain.User;
import dto.request.AdminBanRequest;
import dto.request.AdminForceExitRequest;
import dto.request.AdminForceLogoutRequest;
import dto.request.AdminInitRequest;
import dto.request.AdminMessageDeleteRequest;
import dto.request.AdminMessageSearchRequest;
import dto.request.AdminRoomDeleteRequest;
import dto.response.AdminMessageSearchResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.SwingUtilities;

public class AdminFrame extends JFrame implements ActionListener {

    private final DefaultTableModel userModel = new DefaultTableModel(new Object[]{"ID", "Nickname", "Role", "Online", "Banned"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex >= 3) return Boolean.class;
            return String.class;
        }
    };
    private final JTable userTable = new JTable(userModel);
    private final JTextField forceRoomField = new JTextField(12);

    private final DefaultTableModel roomModel = new DefaultTableModel(new Object[]{"Room", "Members"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable roomTable = new JTable(roomModel);

    private final DefaultTableModel messageModel = new DefaultTableModel(new Object[]{"ID", "Room", "Nickname", "Content", "Sent At"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Long.class : String.class;
        }
    };
    private final JTable messageTable = new JTable(messageModel);
    private final JTextField searchNicknameField = new JTextField(12);
    private final JTextField searchRoomField = new JTextField(12);

    public AdminFrame() {
        super("Admin Dashboard");
        setSize(920, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", buildUserPanel());
        tabs.addTab("Rooms", buildRoomPanel());
        tabs.addTab("Messages", buildMessagePanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(userTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Force Logout");
        JButton banBtn = new JButton("Toggle Ban");
        JButton forceExitBtn = new JButton("Force Exit");
        controls.add(refreshBtn);
        controls.add(logoutBtn);
        controls.add(banBtn);
        controls.add(new JLabel("Room"));
        controls.add(forceRoomField);
        controls.add(forceExitBtn);

        refreshBtn.setActionCommand("refresh_users");
        logoutBtn.setActionCommand("force_logout");
        banBtn.setActionCommand("toggle_ban");
        forceExitBtn.setActionCommand("force_exit");

        refreshBtn.addActionListener(this);
        logoutBtn.addActionListener(this);
        banBtn.addActionListener(this);
        forceExitBtn.addActionListener(this);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(roomTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteBtn = new JButton("Delete Room");
        refreshBtn.setActionCommand("refresh_rooms");
        deleteBtn.setActionCommand("delete_room");
        refreshBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        controls.add(refreshBtn);
        controls.add(deleteBtn);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(messageTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filters.add(new JLabel("Nickname"));
        filters.add(searchNicknameField);
        filters.add(new JLabel("Room"));
        filters.add(searchRoomField);
        JButton searchBtn = new JButton("Search");
        JButton deleteBtn = new JButton("Delete");
        searchBtn.setActionCommand("search_messages");
        deleteBtn.setActionCommand("delete_message");
        searchBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        filters.add(searchBtn);
        filters.add(deleteBtn);

        panel.add(filters, BorderLayout.SOUTH);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "refresh_users":
            case "refresh_rooms":
                Application.sender.sendMessage(new AdminInitRequest());
                break;
            case "force_logout":
                String userId = getSelectedUserId();
                if (userId != null) {
                    Application.sender.sendMessage(new AdminForceLogoutRequest(userId));
                }
                break;
            case "toggle_ban":
                int row = userTable.getSelectedRow();
                if (row >= 0) {
                    String targetId = userModel.getValueAt(row, 0).toString();
                    boolean banned = Boolean.TRUE.equals(userModel.getValueAt(row, 4));
                    Application.sender.sendMessage(new AdminBanRequest(targetId, !banned));
                } else {
                    showWarning("Select a user first.");
                }
                break;
            case "force_exit":
                String selectedUser = getSelectedUserId();
                if (selectedUser == null) break;
                String roomName = forceRoomField.getText().trim();
                if (roomName.isEmpty()) {
                    showWarning("Enter a room name.");
                    break;
                }
                Application.sender.sendMessage(new AdminForceExitRequest(selectedUser, roomName));
                break;
            case "delete_room":
                int roomRow = roomTable.getSelectedRow();
                if (roomRow >= 0) {
                    String room = roomModel.getValueAt(roomRow, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete room \"" + room + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Application.sender.sendMessage(new AdminRoomDeleteRequest(room));
                    }
                } else {
                    showWarning("Select a room first.");
                }
                break;
            case "search_messages":
                Application.sender.sendMessage(new AdminMessageSearchRequest(
                        searchNicknameField.getText().trim(),
                        searchRoomField.getText().trim()
                ));
                break;
            case "delete_message":
                int msgRow = messageTable.getSelectedRow();
                if (msgRow >= 0) {
                    Object idObj = messageModel.getValueAt(msgRow, 0);
                    long msgId = (idObj instanceof Number) ? ((Number) idObj).longValue() : Long.parseLong(idObj.toString());
                    int confirm = JOptionPane.showConfirmDialog(this, "Delete message " + msgId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Application.sender.sendMessage(new AdminMessageDeleteRequest(msgId));
                    }
                } else {
                    showWarning("Select a message first.");
                }
                break;
            default:
                break;
        }
    }

    private String getSelectedUserId() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            showWarning("Select a user first.");
            return null;
        }
        return userModel.getValueAt(row, 0).toString();
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    public void updateUsers(List<User> users) {
        SwingUtilities.invokeLater(() -> {
            userModel.setRowCount(0);
            for (User u : users) {
                userModel.addRow(new Object[]{u.getId(), u.getNickName(), u.getRole(), u.isOnline(), u.isBanned()});
            }
        });
    }

    public void updateChatRooms(List<ChatRoom> rooms) {
        SwingUtilities.invokeLater(() -> {
            roomModel.setRowCount(0);
            for (ChatRoom r : rooms) {
                roomModel.addRow(new Object[]{r.getName(), r.getMemberCount()});
            }
        });
    }

    public void updateMessages(List<AdminMessageSearchResponse.AdminMessage> messages) {
        SwingUtilities.invokeLater(() -> {
            messageModel.setRowCount(0);
            for (AdminMessageSearchResponse.AdminMessage m : messages) {
                messageModel.addRow(new Object[]{m.id, m.roomName, m.nickname, m.content, m.sentAt});
            }
        });
    }
}
