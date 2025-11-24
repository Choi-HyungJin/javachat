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

    public LobbyFrame() {
        super("Chat Chat");

        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(new BorderLayout());
        setSize(520, 500);

        friendListPanel = new FriendListPanel();
        chatRoomListPanel = new ChatRoomListPanel();

        JPanel chatTab = new JPanel(new BorderLayout());
        JButton createChatBtn = new JButton("채팅방 만들기");
        createChatBtn.addActionListener(e -> createChatFrame.setVisible(true));
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(createChatBtn);
        chatTab.add(topBar, BorderLayout.NORTH);
        chatTab.add(chatRoomListPanel, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("친구", friendListPanel);
        tabs.addTab("채팅방", chatTab);

        add(tabs, BorderLayout.CENTER);
        addWindowListener(this);

        setVisible(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("window opened");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("window closing - 프로그램 종료 처리");

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
                System.out.println("[종료] 소켓 연결 종료");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("[종료] 프로그램 종료");
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("window closed");
    }

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("window iconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("window deiconified");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("window activated");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("window deactivated");
    }
}
