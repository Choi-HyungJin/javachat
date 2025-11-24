package view.frame;

import app.Application;
import domain.User;
import dto.request.LoginRequest;
import network.MessageSender;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class LoginFrame extends JFrame implements ActionListener {
    LobbyFrame lobbyFrame;
    JLabel titleLabel = new JLabel("채팅 로그인");
    JLabel infoLabel = new JLabel("로그인 후 채팅방에 접속할 수 있습니다.");
    JLabel idLabel = new JLabel("아이디 ");
    JLabel pwLabel = new JLabel("비밀번호");
    JTextField idTextF = new JTextField(20);
    JPasswordField pwTextF = new JPasswordField(20);
    JButton loginBtn = new JButton("로그인");
    JButton joinBtn = new JButton("회원가입");

    // 추가: 도시 선택 콤보박스
    JLabel cityLabel = new JLabel("도시");
    String[] cities = {
    		"Seoul",       // 서울특별시
    	    "Busan",       // 부산광역시
    	    "Daegu",       // 대구광역시
    	    "Incheon",     // 인천광역시
    	    "Gwangju",     // 광주광역시
    	    "Daejeon",     // 대전광역시
    	    "Ulsan",       // 울산광역시
    	    "Sejong",      // 세종특별자치시
    	    "Gyeonggi-do", // 경기도
    	    "Gangwon-do",  // 강원도
    	    "Chungcheongbuk-do", // 충청북도
    	    "Chungcheongnam-do", // 충청남도
    	    "Jeollabuk-do",      // 전라북도
    	    "Jeollanam-do",      // 전라남도
    	    "Gyeongsangbuk-do",  // 경상북도
    	    "Gyeongsangnam-do",  // 경상남도
    	    "Jeju-do"            // 제주특별자치도
};
    JComboBox<String> cityComboBox = new JComboBox<>(cities);

    // 날짜 & 날씨 라벨
    private final JLabel dateBadgeLabel = new JLabel();
    private final JLabel weatherImageLabel = new JLabel();
    private LocalDate lastDate = LocalDate.now();

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
        setLayout(null);

        // 타이틀
        titleLabel.setBounds(100, 10, 400, 30);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel);
        
        // 안내 문구
        infoLabel.setBounds(100, 35, 400, 20);
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        add(infoLabel);

        // 아이디 & 비밀번호
        idLabel.setBounds(100, 70, 100, 50);
        add(idLabel);
        idTextF.setBounds(200, 70, 300, 50);
        add(idTextF);

        pwLabel.setBounds(100, 130, 100, 50);
        add(pwLabel);
        pwTextF.setBounds(200, 130, 300, 50);
        add(pwTextF);

        // 도시 선택 콤보박스
        cityLabel.setBounds(100, 190, 100, 50);
        cityComboBox.setBounds(200, 190, 300, 50);
        add(cityLabel);
        add(cityComboBox);
        
        // 로그인 & 회원가입 버튼
        loginBtn.setBounds(150, 260, 140, 50);
        loginBtn.addActionListener(this);
        add(loginBtn);

        joinBtn.setBounds(310, 260, 140, 50);
        joinBtn.addActionListener(this);
        add(joinBtn);

        // 날짜 배지
        dateBadgeLabel.setBounds(520, 10, 64, 64);
        add(dateBadgeLabel);
        dateBadgeLabel.setIcon(new ImageIcon(CalendarBadge.createBadge(LocalDate.now(), 64, 64)));

        // 날씨 이미지
        weatherImageLabel.setBounds(520, 80, 64, 64);
        add(weatherImageLabel);

        // 날씨 API 호출
        String apiKey = "1e9136088e03c34b29269b0e130d17a1";
        updateWeatherIcon(apiKey);

        // 도시 변경 시 날씨 갱신
        cityComboBox.addActionListener(e -> updateWeatherIcon(apiKey));

        // 자동 갱신 (1분마다 날짜 & 날씨)
        new javax.swing.Timer(60_000, e -> {
            LocalDate today = LocalDate.now();
            if (!today.equals(lastDate)) {
                lastDate = today;
                dateBadgeLabel.setIcon(new ImageIcon(CalendarBadge.createBadge(today, 64, 64)));
            }
            updateWeatherIcon(apiKey);
        }).start();

        setSize(600, 400);
        setVisible(true);
    }

    private void updateWeatherIcon(String apiKey) {
        String city = (String) cityComboBox.getSelectedItem();
        
        // 백그라운드에서 날씨 정보 조회
        new Thread(() -> {
            String[] weatherInfo = WeatherFetcher.getWeatherInfo(city, apiKey);
            String weather = weatherInfo[0];
            String temp = weatherInfo[1];
            
            // UI 업데이트는 EDT에서
            SwingUtilities.invokeLater(() -> {
                weatherImageLabel.setIcon(getWeatherIcon(weather));
                weatherImageLabel.setToolTipText(weather + " - " + temp + "°C");
            });
        }).start();
    }

    private ImageIcon getWeatherIcon(String weather) {
        switch (weather) {
            case "Clear":
                return new ImageIcon(new ImageIcon("image/sun.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            case "Clouds":
                return new ImageIcon(new ImageIcon("image/cloud.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            case "Rain":
                return new ImageIcon(new ImageIcon("image/rain.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            default:
                return new ImageIcon(new ImageIcon("image/default.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == loginBtn) {
    	    String id = idTextF.getText();
    	    String pw = new String(pwTextF.getPassword());

    	    if (id.trim().isEmpty()) {
    	        JOptionPane.showMessageDialog(null, "아이디를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }
    	    if (pw.trim().isEmpty()) {
    	        JOptionPane.showMessageDialog(null, "비밀번호를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
    	        return;
    	    }

    	    User user = new User(id, "");  // 닉네임은 서버에서 받아옴
    	    Application.me = user;
    	    Application.sender.sendMessage(new LoginRequest(id, pw));
    	    
    	    System.out.println("[로그인] 요청 전송: " + id);
    	    
    	    // 로그인 성공 시 MessageReceiver가 LobbyFrame을 표시
    	    this.dispose();
    	    lobbyFrame.setVisible(true);
    	}
        if (e.getSource() == joinBtn) {
            new JoinFrame();
        }
    }

    // 날짜 배지 생성 클래스
    private static final class CalendarBadge {
        static BufferedImage createBadge(LocalDate date, int width, int height) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250),
                        0, height, new Color(230, 234, 240));
                g.setPaint(gp);
                g.fill(new RoundRectangle2D.Float(0, 0, width, height, 22, 22));

                int headerH = Math.max(22, height / 5);
                g.setColor(new Color(220, 65, 65));
                g.fill(new RoundRectangle2D.Float(0, 0, width, headerH, 22, 22));
                g.fillRect(0, headerH - 10, width, 10);

                Locale ko = Locale.KOREAN;
                String monthText = date.getMonth().getDisplayName(TextStyle.SHORT, ko);
                String dowText = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, ko);
                String yearText = String.valueOf(date.getYear());
                String dayText = String.valueOf(date.getDayOfMonth());

                g.setColor(Color.WHITE);
                Font headerFont = fontOrFallback("Malgun Gothic", Font.BOLD, Math.max(12, headerH - 8));
                g.setFont(headerFont);
                drawCentered(g, monthText, new Rectangle(0, 0, width, headerH));

                Font dayFont = fontOrFallback("Malgun Gothic", Font.BOLD, Math.max(28, height / 2));
                g.setFont(dayFont);
                Rectangle dayArea = new Rectangle(0, headerH, width, height - headerH - (height / 6));
                g.setColor(new Color(0, 0, 0, 40));
                drawCentered(g, dayText, new Rectangle(dayArea.x, dayArea.y + 2, dayArea.width, dayArea.height));
                g.setColor(new Color(40, 40, 40));
                drawCentered(g, dayText, dayArea);

                Font subFont = fontOrFallback("Malgun Gothic", Font.PLAIN, Math.max(12, height / 8));
                g.setFont(subFont);
                g.setColor(new Color(90, 90, 90));
                String sub = dowText + " · " + yearText;
                drawCentered(g, sub, new Rectangle(0, height - (height / 6), width, (height / 6)));
            } finally {
                g.dispose();
            }
            return img;
        }

        private static Font fontOrFallback(String name, int style, int size) {
            try {
                return new Font(name, style, size);
            } catch (Exception e) {
                return new Font(Font.SANS_SERIF, style, size);
            }
        }

        private static void drawCentered(Graphics2D g, String text, Rectangle area) {
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent() - fm.getDescent();
            int x = area.x + (area.width - textW) / 2;
            int y = area.y + (area.height + textH) / 2;
            g.drawString(text, x, y);
        }
    }
}