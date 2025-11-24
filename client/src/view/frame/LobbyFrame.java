package view.frame;

import app.Application;
import view.panel.ChatRoomListPanel;
import view.panel.FriendListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LobbyFrame extends JFrame implements WindowListener {

    public static ChatRoomListPanel chatRoomListPanel;
    public static FriendListPanel friendListPanel;
    public static CreateChatFrame createChatFrame;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCards = new JPanel(cardLayout);

    public LobbyFrame() {
        super("Chat");

        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(new BorderLayout());
        setSize(1000, 720);
        setLocationRelativeTo(null);

        friendListPanel = new FriendListPanel();
        chatRoomListPanel = new ChatRoomListPanel();

        contentCards.add(friendListPanel, "friends");
        contentCards.add(chatRoomListPanel, "chats");

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
        addWindowListener(this);
        setVisible(false);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(70, 720));
        side.setBackground(new Color(245, 245, 247));
        side.setLayout(new GridLayout(6, 1, 0, 12));
        side.setBorder(BorderFactory.createEmptyBorder(24, 8, 24, 8));

        side.add(createNavButton("😊", "친구", "friends"));
        side.add(createNavButton("💬", "채팅", "chats"));
        side.add(new JLabel()); // spacer
        side.add(new JLabel());
        side.add(createSettingsButton());
        side.add(new JLabel());
        return side;
    }

    private JButton createNavButton(String icon, String tooltip, String card) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 247));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.addActionListener(e -> {
            cardLayout.show(contentCards, card);
            contentCards.revalidate();
        });
        return btn;
    }

    private JButton createSettingsButton() {
        JButton btn = new JButton("⚙");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btn.setToolTipText("설정");
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 245, 247));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Chat");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton createChatBtn = new JButton("채팅방 만들기");
        createChatBtn.setFocusPainted(false);
        createChatBtn.setBackground(new Color(245, 245, 247));
        createChatBtn.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225)));
        createChatBtn.addActionListener(e -> createChatFrame.setVisible(true));
        actions.add(createChatBtn);
        header.add(actions, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        cardWrapper.add(contentCards, BorderLayout.CENTER);
        main.add(cardWrapper, BorderLayout.CENTER);

        return main;
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        if (Application.me != null) {
            Application.sender.sendMessage(new dto.request.LogoutRequest(Application.me.getId()));
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        try {
            if (Application.socket != null && !Application.socket.isClosed()) {
                Application.socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) { }
    @Override
    public void windowIconified(WindowEvent e) { }
    @Override
    public void windowDeiconified(WindowEvent e) { }
    @Override
    public void windowActivated(WindowEvent e) { }
    @Override
    public void windowDeactivated(WindowEvent e) { }
}
