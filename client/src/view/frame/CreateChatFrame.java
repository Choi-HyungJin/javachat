package view.frame;

import app.Application;
import dto.request.CreateChatRoomRequest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateChatFrame extends JFrame {

    CreateChatFrame frame;

    JLabel chatNameLabel = new JLabel("채팅방 이름");

    JTextField chatNameTextF = new JTextField();

    JButton okBtn = new JButton("확인");

    JButton cancelBtn = new JButton("취소");

    public CreateChatFrame() {
        setLayout(null);

        frame = this;

        chatNameLabel.setBounds(100, 50, 100, 50);
        add(chatNameLabel);

        chatNameTextF.setBounds(200, 50, 300, 50);
        add(chatNameTextF);

        okBtn.setBounds(100, 220, 150, 50);
        okBtn.addActionListener(new OkBtnActionListener());
        add(okBtn);

        cancelBtn.setBounds(260, 220, 150, 50);
        cancelBtn.addActionListener(new CancelBtnActionListener());
        add(cancelBtn);

        setSize(600, 400);
        setVisible(false);
    }

    class OkBtnActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String chatRoomName = chatNameTextF.getText().trim();
            
            // 로그인 확인
            if (Application.me == null || Application.me.getId() == null) {
                JOptionPane.showMessageDialog(null,
                        "로그인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (chatRoomName.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "채팅방 이름을 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 로비 이름 사용 금지
            if ("Lobby".equalsIgnoreCase(chatRoomName)) {
                JOptionPane.showMessageDialog(null,
                        "사용할 수 없는 채팅방 이름입니다.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Application.sender.sendMessage(new CreateChatRoomRequest(chatRoomName, Application.me.getId()));
            frame.dispose();

            ChatFrame chatFrame = new ChatFrame(chatRoomName);
            Application.chatFrameMap.put(chatRoomName, chatFrame);

            Application.chatPanelMap.put(chatRoomName, chatFrame.chatPanel);
            Application.chatRoomUserListPanelMap.put(chatRoomName, chatFrame.chatRoomUserListPanel);
        }
    }

    class CancelBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }
}


