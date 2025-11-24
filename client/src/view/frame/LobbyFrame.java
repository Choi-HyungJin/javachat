package view.frame;

import app.Application;
import view.panel.ChatPanel;
import view.panel.ChatRoomListPanel;
import view.panel.FriendListPanel;
import view.panel.MenuPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LobbyFrame extends JFrame implements WindowListener {

    public static ChatPanel chatPanel;

    public static ChatRoomListPanel chatRoomListPanel;

    public static MenuPanel menuPanel;

    public static FriendListPanel friendListPanel;

    public static CreateChatFrame createChatFrame;

    public LobbyFrame() {
        super("Chat Chat");

        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(null);
        setSize(830, 550);

        chatPanel = new ChatPanel(this, Application.LOBBY_CHAT_NAME);
        friendListPanel = new FriendListPanel(this);
        chatRoomListPanel = new ChatRoomListPanel(this);
        menuPanel = new MenuPanel(this, Application.LOBBY_CHAT_NAME);
        menuPanel.setCreateChatBtnVisible(true);
        menuPanel.setCloseBtnVisible(true);

        this.addWindowListener(this);

        setVisible(false);
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
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
