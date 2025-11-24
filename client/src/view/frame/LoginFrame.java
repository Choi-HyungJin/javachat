package view.frame;

import app.Application;
import domain.User;
import dto.request.LoginRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame implements ActionListener {

    private final LobbyFrame lobbyFrame;
    private final JTextField idField = new JTextField();
    private final JPasswordField pwField = new JPasswordField();
    private final JButton loginButton = new JButton("로그인");
    private final JButton joinButton = new JButton("회원가입");

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;
        setTitle("Chat - Login");
        setSize(360, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color kakaoYellow = new Color(255, 235, 0);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(kakaoYellow);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        center.add(buildLogo());
        center.add(Box.createVerticalStrut(20));
        center.add(buildInputPanel());
        center.add(Box.createVerticalStrut(14));
        center.add(buildLoginButtons());
        center.add(Box.createVerticalStrut(30));
        center.add(buildAutoLogin());

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }

    private JPanel buildLogo() {
        JPanel bubble = new JPanel(new BorderLayout());
        bubble.setPreferredSize(new Dimension(140, 70));
        bubble.setMaximumSize(new Dimension(140, 70));
        bubble.setBackground(new Color(46, 46, 46));
        bubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JLabel talk = new JLabel("TALK", SwingConstants.CENTER);
        talk.setForeground(new Color(255, 235, 0));
        talk.setFont(new Font("Arial Black", Font.BOLD, 22));
        bubble.add(talk, BorderLayout.CENTER);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.add(bubble);
        return wrapper;
    }

    private JPanel buildInputPanel() {
        JPanel inputs = new JPanel();
        inputs.setOpaque(false);
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));

        idField.setPreferredSize(new Dimension(260, 40));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        styleField(idField);
        idField.setToolTipText("아이디를 입력하세요");

        pwField.setPreferredSize(new Dimension(260, 40));
        pwField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        styleField(pwField);
        pwField.setToolTipText("비밀번호를 입력하세요");

        inputs.add(idField);
        inputs.add(Box.createVerticalStrut(8));
        inputs.add(pwField);
        return inputs;
    }

    private JPanel buildLoginButtons() {
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        stylePrimaryButton(loginButton);
        loginButton.addActionListener(this);
        styleSecondaryButton(joinButton);
        joinButton.addActionListener(this);

        buttons.add(loginButton);
        buttons.add(Box.createVerticalStrut(10));

        JLabel divider = new JLabel("또는", SwingConstants.CENTER);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        divider.setForeground(new Color(90, 90, 90));
        buttons.add(divider);
        buttons.add(Box.createVerticalStrut(10));

        buttons.add(joinButton);
        return buttons;
    }

    private JPanel buildAutoLogin() {
        JPanel auto = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        auto.setOpaque(false);
        JCheckBox check = new JCheckBox("자동 로그인");
        check.setOpaque(false);
        check.setSelected(false);
        check.setForeground(Color.DARK_GRAY);
        auto.add(check);
        return auto;
    }

    private void styleField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(260, 42));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Application.me = new User(id, "");
            Application.sender.sendMessage(new LoginRequest(id, pw));

            this.dispose();
            lobbyFrame.setVisible(true);
        }

        if (e.getSource() == joinButton) {
            new JoinFrame();
        }
    }
}
