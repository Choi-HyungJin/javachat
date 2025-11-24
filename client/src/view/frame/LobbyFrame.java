package view.frame;

import app.Application;
import network.MessageSender;
import view.panel.ChatPanel;
import view.panel.ChatRoomListPanel;
import view.panel.ChatRoomUserListPanel;
import view.panel.MenuPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LobbyFrame extends JFrame implements WindowListener {

    public static ChatPanel chatPanel;

    public static ChatRoomListPanel chatRoomListPanel;

    public static MenuPanel menuPanel;

    public static ChatRoomUserListPanel chatRoomUserListPanel;

    public static CreateChatFrame createChatFrame;

    public LobbyFrame() {
        super("Chat Chat");

        new LoginFrame(this);
        createChatFrame = new CreateChatFrame();

        setLayout(null);
        setSize(830, 550);

        chatPanel = new ChatPanel(this, Application.LOBBY_CHAT_NAME);
        chatRoomUserListPanel = new ChatRoomUserListPanel(this);
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

    public ChatRoomUserListPanel getChatRoomUserListPanel() {
        return chatRoomUserListPanel;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("window opened");
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("window closing - 프로그램 종료 처리");
        
        // 서버에 로그아웃 요청 전송
        if (Application.me != null) {
            Application.sender.sendMessage(new dto.request.LogoutRequest(Application.me.getId()));
            
            try {
                // 서버가 응답을 처리할 시간을 줌
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        // 소켓 연결 종료
        try {
            if (Application.socket != null && !Application.socket.isClosed()) {
                Application.socket.close();
                System.out.println("[종료] 소켓 연결 종료");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // 프로그램 종료
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
