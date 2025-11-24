package view.panel;

import app.Application;
import dto.request.ExitChatRequest;
import dto.request.LogoutRequest;
import view.frame.LobbyFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {

    String chatRoomName;

    JButton createChatBtn = new JButton("새로운 채팅방 생성");

    JButton exitBtn = new JButton("나가기");
    
    JButton backBtn = new JButton("뒤로가기");
    
    JButton closeBtn = new JButton("종료");

    public MenuPanel(JFrame frame, String chatRoomName) {
        setLayout(null);

        this.chatRoomName = chatRoomName;

        createChatBtn.setBounds(15, 10, 170, 35);
        createChatBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chat room create btn clicked");
                LobbyFrame.createChatFrame.setVisible(true);
            }
        });
        add(createChatBtn);
        createChatBtn.setVisible(false); // 로비 채팅방, 생성된 채팅방에 따라 버튼 show, hide

        closeBtn.setBounds(200, 10, 185, 35);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("close btn clicked - 클라이언트 종료");
                
                // 서버에 로그아웃 요청 전송
                if (Application.me != null) {
                    Application.sender.sendMessage(new LogoutRequest(Application.me.getId()));
                    
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
        });
        add(closeBtn);
        closeBtn.setVisible(false);

        backBtn.setBounds(15, 10, 180, 35);
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("back btn clicked - 뒤로가기 (채팅방에서 나가지 않음)");
                
                // 채팅 화면만 닫기 (서버에 EXIT 전송 안 함)
                Application.chatPanelMap.remove(chatRoomName);
                Application.chatRoomUserListPanelMap.remove(chatRoomName);
                
                frame.dispose();
            }
        });
        add(backBtn);
        backBtn.setVisible(false);

        exitBtn.setBounds(205, 10, 180, 35);
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chat room exit btn clicked - 채팅방 나가기");
                System.out.println("chat room = [" + chatRoomName + "] user exit.");

                // 서버에 채팅방 나가기 요청 전송
                Application.sender.sendMessage(new ExitChatRequest(chatRoomName, Application.me.getId()));
                
                // 채팅 화면 닫기
                Application.chatPanelMap.remove(chatRoomName);
                Application.chatRoomUserListPanelMap.remove(chatRoomName);
                
                frame.dispose();
            }
        });
        add(exitBtn);
        exitBtn.setVisible(false);

        frame.add(this);

        setBounds(410, 460, 400, 50);
        setVisible(true);
    }

    public void setCreateChatBtnVisible(boolean bool) {
        createChatBtn.setVisible(bool);
    }
    
    public void setCloseBtnVisible(boolean bool) {
        closeBtn.setVisible(bool);
    }
    
    public void setBackBtnVisible(boolean bool) {
        backBtn.setVisible(bool);
    }

    public void setExitBtnVisible(boolean bool) {
        exitBtn.setVisible(bool);
    }
}
