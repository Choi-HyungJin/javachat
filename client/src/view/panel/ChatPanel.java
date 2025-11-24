package view.panel;

import app.Application;
import dto.request.MessageRequest;
import dto.type.MessageType;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPanel extends JPanel implements ActionListener {

    String chatRoomName;

    JTextPane chatTextPane = new JTextPane();  // JTextArea 대신 JTextPane 사용
    StyledDocument doc;

    JTextField msgTextF = new JTextField(50);
    JButton sendBtn = new JButton("전송");
    
    // 카카오톡 스타일 색상
    private static final Color BACKGROUND_COLOR = new Color(178, 199, 217);  // 연한 파란색 배경
    private static final Color MY_MESSAGE_COLOR = new Color(255, 235, 51);   // 노란색 (내 메시지)
    private static final Color OTHER_MESSAGE_COLOR = Color.WHITE;            // 흰색 (상대방 메시지)
    private static final Color SYSTEM_MESSAGE_COLOR = new Color(100, 100, 100);  // 회색 (시스템 메시지)
    
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ChatPanel(JFrame frame, String chatRoomName) {
        setLayout(null);
        this.chatRoomName = chatRoomName;

        // 배경색 설정
        setBackground(BACKGROUND_COLOR);

        // 채팅 메시지 영역 (스크롤)
        chatTextPane.setEditable(false);
        chatTextPane.setBackground(BACKGROUND_COLOR);
        chatTextPane.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        doc = chatTextPane.getStyledDocument();
        
        JScrollPane scrPane = new JScrollPane(chatTextPane);
        scrPane.setBounds(10, 10, 380, 430);
        scrPane.setBorder(BorderFactory.createEmptyBorder());
        scrPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrPane);

        // 메시지 입력 필드
        msgTextF.setBounds(10, 450, 280, 40);
        msgTextF.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        msgTextF.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(msgTextF);

        // 전송 버튼
        sendBtn.setBounds(300, 450, 90, 40);
        sendBtn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        sendBtn.setBackground(new Color(255, 235, 51));
        sendBtn.setForeground(new Color(60, 30, 30));
        sendBtn.setBorderPainted(false);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(this);
        add(sendBtn);

        frame.add(this);
        setBounds(10, 10, 400, 500);
    }

    public void addMessage(MessageType messageType, String userName, String message) {
        try {
            String timeStamp = timeFormat.format(new Date());
            
            switch (messageType) {
                case ENTER:
                case EXIT:
                    // 시스템 메시지 (중앙 정렬)
                    addSystemMessage(message);
                    break;
                    
                case CHAT:
                    // 내가 보낸 메시지인지 확인
                    boolean isMyMessage = userName.equals(Application.me.getNickName());
                    addChatMessage(userName, message, timeStamp, isMyMessage);
                    break;
            }
            
            // 자동 스크롤 (최신 메시지로)
            chatTextPane.setCaretPosition(doc.getLength());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 기존 저장된 기록(과거 시간 포함)을 추가하기 위한 메서드
    public void addHistoryMessage(String userName, String message, String time) {
        try {
            boolean isMyMessage = Application.me != null && userName.equals(Application.me.getNickName());
            addChatMessage(userName, message, time, isMyMessage);
            chatTextPane.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addSystemMessage(String message) throws BadLocationException {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setForeground(attrs, SYSTEM_MESSAGE_COLOR);
        StyleConstants.setFontSize(attrs, 11);
        StyleConstants.setItalic(attrs, true);
        
        int start = doc.getLength();
        doc.insertString(doc.getLength(), "\n" + message + "\n", attrs);
        doc.setParagraphAttributes(start, doc.getLength() - start, attrs, false);
    }
    
    private void addChatMessage(String userName, String message, String time, boolean isMyMessage) throws BadLocationException {
        SimpleAttributeSet nameAttrs = new SimpleAttributeSet();
        SimpleAttributeSet msgAttrs = new SimpleAttributeSet();
        SimpleAttributeSet timeAttrs = new SimpleAttributeSet();
        
        StyleConstants.setFontSize(nameAttrs, 11);
        StyleConstants.setFontSize(msgAttrs, 13);
        StyleConstants.setFontSize(timeAttrs, 9);
        StyleConstants.setForeground(timeAttrs, new Color(120, 120, 120));
        
        doc.insertString(doc.getLength(), "\n", null);
        
        if (isMyMessage) {
            // 내 메시지 (오른쪽 정렬)
            StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setBackground(msgAttrs, MY_MESSAGE_COLOR);
            
            int start = doc.getLength();
            String fullMsg = time + "  " + message + " ";
            doc.insertString(doc.getLength(), fullMsg + "\n", msgAttrs);
            doc.setParagraphAttributes(start, fullMsg.length(), msgAttrs, false);
            
        } else {
            // 상대방 메시지 (왼쪽 정렬)
            StyleConstants.setAlignment(msgAttrs, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(nameAttrs, new Color(60, 60, 60));
            StyleConstants.setBackground(msgAttrs, OTHER_MESSAGE_COLOR);
            
            int start = doc.getLength();
            doc.insertString(doc.getLength(), userName + "\n", nameAttrs);
            
            String fullMsg = message + "  " + time;
            doc.insertString(doc.getLength(), fullMsg + "\n", msgAttrs);
            doc.setParagraphAttributes(start, doc.getLength() - start, msgAttrs, false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = msgTextF.getText().trim();

        if (!message.isEmpty()) {
            String nickname = Application.me.getNickName();
            if (nickname == null || nickname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "닉네임 정보가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Application.sender.sendMessage(new MessageRequest(MessageType.CHAT, chatRoomName, nickname, message));
            System.out.println("[전송] 방: " + chatRoomName + ", 내용: " + message);
        }
        msgTextF.setText("");
        msgTextF.requestFocus();
    }
}
