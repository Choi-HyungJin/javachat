package view.frame;

import util.PostcodeHttpServer;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

/**
 * Daum 우편번호 서비스를 사용한 우편번호 검색 다이얼로그
 * 프로젝트의 postcode_browser.html 파일을 브라우저로 열어 주소 검색
 */
public class PostalCodeSearchDialog extends JDialog {
    
    private JLabel statusLabel;
    private String selectedPostalCode;
    private String selectedAddress;
    
    public PostalCodeSearchDialog(JFrame parent) {
        super(parent, "우편번호 검색", true);
        setLayout(new BorderLayout(10, 10));
        setSize(450, 200);
        setLocationRelativeTo(parent);
        
        // 중앙 패널 (안내 및 버튼)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 안내 메시지
        JLabel infoLabel = new JLabel("<html><body style='width: 350px; text-align: center;'>" +
                "<h2>Daum 우편번호 검색</h2>" +
                "<p>아래 버튼을 클릭하면 브라우저에서 우편번호 검색 페이지가 열립니다.</p>" +
                "<p style='color: #d00; font-weight: bold;'>주소를 선택하면 자동으로 입력됩니다.</p>" +
                "</body></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(infoLabel, BorderLayout.NORTH);
        
        // 검색 버튼
        JButton searchBtn = new JButton("우편번호 검색 시작");
        searchBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(200, 40));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(searchBtn);
        centerPanel.add(btnPanel, BorderLayout.CENTER);
        
        // 상태 표시 레이블
        statusLabel = new JLabel("대기 중...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.GRAY);
        centerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 하단 패널 (취소 버튼)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton cancelBtn = new JButton("취소");
        bottomPanel.add(cancelBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // 이벤트 리스너
        searchBtn.addActionListener(e -> {
            searchBtn.setEnabled(false);
            statusLabel.setText("브라우저에서 주소를 선택하세요...");
            statusLabel.setForeground(new Color(0, 128, 0));
            openDaumPostcode();
        });
        
        cancelBtn.addActionListener(e -> {
            selectedPostalCode = null;
            selectedAddress = null;
            PostcodeHttpServer.setAddressCallback(null);
            dispose();
        });
        
        // 창 닫을 때 정리
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                PostcodeHttpServer.setAddressCallback(null);
            }
        });
    }
    
    /**
     * Daum 우편번호 서비스 브라우저에서 열기
     */
    private void openDaumPostcode() {
        try {
            // HTTP 서버 시작
            PostcodeHttpServer.start();
            
            // HTTP 서버 콜백 설정 (주소 데이터 수신)
            PostcodeHttpServer.setAddressCallback((postal, address) -> {
                SwingUtilities.invokeLater(() -> {
                    selectedPostalCode = postal;
                    selectedAddress = address;
                    
                    statusLabel.setText("주소 선택 완료!");
                    statusLabel.setForeground(new Color(0, 128, 0));
                    
                    System.out.println("[PostalCodeSearchDialog] Address received: " + postal + " | " + address);
                    
                    // 다이얼로그 닫기
                    dispose();
                });
            });
            
            // HTTP 서버 URL로 브라우저 열기
            String url = PostcodeHttpServer.getUrl();
            Desktop.getDesktop().browse(new URI(url));
            
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("오류 발생");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "우편번호 검색을 열 수 없습니다: " + e.getMessage(), 
                "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 다이얼로그 표시 및 결과 반환
     * @return [우편번호, 주소] 배열, 취소 시 null
     */
    public String[] showDialog() {
        setVisible(true);
        if (selectedPostalCode != null && selectedAddress != null) {
            return new String[]{selectedPostalCode, selectedAddress};
        }
        return null;
    }
}
