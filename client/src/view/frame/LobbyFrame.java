package view.frame;

import app.Application;
import dto.request.LogoutRequest;
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
    private final Color BACKGROUND = new Color(245, 242, 235);

    private JButton friendNavBtn;
    private JButton chatNavBtn;

    public LobbyFrame() {
        super("Chat");

        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(new BorderLayout());
        setSize(460, 780);
        setLocationRelativeTo(null);

        friendListPanel = new FriendListPanel();
        chatRoomListPanel = new ChatRoomListPanel();

        contentCards.add(friendListPanel, "friends");
        contentCards.add(chatRoomListPanel, "chats");

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
        addWindowListener(this);
        updateNavSelection("friends");
        setVisible(false);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(72, 720));
        side.setBackground(BACKGROUND);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(18, 10, 18, 10));

        friendNavBtn = createNavButton("\uD83D\uDC64", "친구", "friends");
        chatNavBtn = createNavButton("\uD83D\uDCAC", "채팅", "chats");

        side.add(friendNavBtn);
        side.add(Box.createVerticalStrut(12));
        side.add(chatNavBtn);
        side.add(Box.createVerticalStrut(18));
        side.add(createDotDivider());
        side.add(Box.createVerticalGlue());
        side.add(createGhostButton("\u263A", "이모티콘"));
        side.add(Box.createVerticalStrut(12));
        side.add(createGhostButton("\u2699", "설정"));
        return side;
    }

    private JButton createNavButton(String icon, String tooltip, String card) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBackground(BACKGROUND);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        btn.addActionListener(e -> {
            cardLayout.show(contentCards, card);
            contentCards.revalidate();
            updateNavSelection(card);
        });
        return btn;
    }

    private JPanel createDotDivider() {
        JPanel dots = new JPanel();
        dots.setOpaque(false);
        dots.setLayout(new BoxLayout(dots, BoxLayout.Y_AXIS));
        for (int i = 0; i < 3; i++) {
            JLabel dot = new JLabel("\u2022");
            dot.setForeground(new Color(160, 160, 160));
            dot.setAlignmentX(Component.CENTER_ALIGNMENT);
            dots.add(dot);
            dots.add(Box.createVerticalStrut(6));
        }
        return dots;
    }

    private JButton createGhostButton(String icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setForeground(new Color(100, 100, 100));
        return btn;
    }

    private void updateNavSelection(String card) {
        Color active = Color.WHITE;
        Color inactive = BACKGROUND;
        if (friendNavBtn != null) {
            friendNavBtn.setBackground("friends".equals(card) ? active : inactive);
        }
        if (chatNavBtn != null) {
            chatNavBtn.setBackground("chats".equals(card) ? active : inactive);
        }
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 12));

        JPanel surface = new JPanel(new BorderLayout());
        surface.setBackground(Color.WHITE);
        surface.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        surface.add(contentCards, BorderLayout.CENTER);

        wrapper.add(surface, BorderLayout.CENTER);
        main.add(wrapper, BorderLayout.CENTER);
        return main;
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        if (Application.me != null) {
            Application.sender.sendMessage(new LogoutRequest(Application.me.getId()));
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
