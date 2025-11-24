package view.frame;

import app.Application;
import dto.request.JoinRequest;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class JoinFrame extends JFrame implements ActionListener {
    
    // 필드 컴포넌트
    private JLabel idLabel = new JLabel("아이디*");
    private JTextField idField = new JTextField(20);
    private JButton idCheckBtn = new JButton("중복확인");
    private boolean idChecked = false;
    
    private JLabel pwLabel = new JLabel("비밀번호*");
    private JPasswordField pwField = new JPasswordField(20);
    
    private JLabel pwConfirmLabel = new JLabel("비밀번호 확인*");
    private JPasswordField pwConfirmField = new JPasswordField(20);
    
    private JLabel nicknameLabel = new JLabel("닉네임*");
    private JTextField nicknameField = new JTextField(20);
    private JButton nicknameCheckBtn = new JButton("중복확인");
    private boolean nicknameChecked = false;
    
    private JLabel nameLabel = new JLabel("이름*");
    private JTextField nameField = new JTextField(20);
    
    private JLabel emailLabel = new JLabel("이메일*");
    private JTextField emailIdField = new JTextField(15);
    private JLabel emailAtLabel = new JLabel("@");
    private JComboBox<String> emailDomainCombo = new JComboBox<>(new String[]{"naver.com", "daum.net", "gmail.com"});
    
    private JLabel phoneLabel = new JLabel("전화번호*");
    private JTextField phoneField = new JTextField(20);
    
    private JLabel postalCodeLabel = new JLabel("우편번호");
    private JTextField postalCodeField = new JTextField(15);
    private JButton postalCodeSearchBtn = new JButton("검색");
    
    private JLabel addressLabel = new JLabel("주소");
    private JTextField addressField = new JTextField(30);
    
    private JLabel detailAddressLabel = new JLabel("상세주소");
    private JTextField detailAddressField = new JTextField(30);
    
    private JLabel genderLabel = new JLabel("성별*");
    private JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"선택", "남성", "여성"});
    
    private JLabel birthDateLabel = new JLabel("생년월일*");
    private JDatePickerImpl birthDatePicker;
    
    private JLabel profileImageLabel = new JLabel("프로필 이미지");
    private JButton profileImageBtn = new JButton("선택");
    private JLabel profileImagePreview = new JLabel();
    private File selectedImageFile = null;
    
    private JButton signupBtn = new JButton("회원가입");
    private JButton cancelBtn = new JButton("취소");
    
    public JoinFrame() {
        super("회원가입");
        setLayout(null);
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        int y = 20;
        int labelX = 50;
        int fieldX = 200;
        int fieldWidth = 200;
        int buttonX = 410;
        int gap = 40;
        
        // ID
        idLabel.setBounds(labelX, y, 140, 30);
        add(idLabel);
        idField.setBounds(fieldX, y, fieldWidth, 30);
        add(idField);
        idCheckBtn.setBounds(buttonX, y, 100, 30);
        idCheckBtn.addActionListener(this);
        add(idCheckBtn);
        y += gap;
        
        // Password
        pwLabel.setBounds(labelX, y, 140, 30);
        add(pwLabel);
        pwField.setBounds(fieldX, y, fieldWidth, 30);
        add(pwField);
        y += gap;
        
        // Password Confirm
        pwConfirmLabel.setBounds(labelX, y, 140, 30);
        add(pwConfirmLabel);
        pwConfirmField.setBounds(fieldX, y, fieldWidth, 30);
        add(pwConfirmField);
        y += gap;
        
        // Nickname
        nicknameLabel.setBounds(labelX, y, 140, 30);
        add(nicknameLabel);
        nicknameField.setBounds(fieldX, y, fieldWidth, 30);
        add(nicknameField);
        nicknameCheckBtn.setBounds(buttonX, y, 100, 30);
        nicknameCheckBtn.addActionListener(this);
        add(nicknameCheckBtn);
        y += gap;
        
        // Name
        nameLabel.setBounds(labelX, y, 140, 30);
        add(nameLabel);
        nameField.setBounds(fieldX, y, fieldWidth, 30);
        add(nameField);
        y += gap;
        
        // Email
        emailLabel.setBounds(labelX, y, 140, 30);
        add(emailLabel);
        emailIdField.setBounds(fieldX, y, 120, 30);
        add(emailIdField);
        emailAtLabel.setBounds(fieldX + 125, y, 20, 30);
        add(emailAtLabel);
        emailDomainCombo.setBounds(fieldX + 145, y, 120, 30);
        add(emailDomainCombo);
        y += gap;
        
        // Phone
        phoneLabel.setBounds(labelX, y, 140, 30);
        add(phoneLabel);
        phoneField.setBounds(fieldX, y, fieldWidth, 30);
        add(phoneField);
        y += gap;
        
        // Postal Code
        postalCodeLabel.setBounds(labelX, y, 140, 30);
        add(postalCodeLabel);
        postalCodeField.setBounds(fieldX, y, 150, 30);
        postalCodeField.setEditable(false);
        postalCodeField.setBackground(Color.LIGHT_GRAY);
        add(postalCodeField);
        postalCodeSearchBtn.setBounds(360, y, 70, 30);
        postalCodeSearchBtn.addActionListener(this);
        add(postalCodeSearchBtn);
        y += gap;
        
        // Address
        addressLabel.setBounds(labelX, y, 140, 30);
        add(addressLabel);
        addressField.setBounds(fieldX, y, 330, 30);
        addressField.setEditable(false);
        addressField.setBackground(Color.LIGHT_GRAY);
        add(addressField);
        y += gap;
        
        // Detail Address
        detailAddressLabel.setBounds(labelX, y, 140, 30);
        add(detailAddressLabel);
        detailAddressField.setBounds(fieldX, y, 330, 30);
        add(detailAddressField);
        y += gap;
        
        // Gender
        genderLabel.setBounds(labelX, y, 140, 30);
        add(genderLabel);
        genderComboBox.setBounds(fieldX, y, fieldWidth, 30);
        add(genderComboBox);
        y += gap;
        
        // Birth Date (JDatePicker)
        birthDateLabel.setBounds(labelX, y, 140, 30);
        add(birthDateLabel);
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        birthDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        birthDatePicker.setBounds(fieldX, y, fieldWidth, 30);
        add(birthDatePicker);
        y += gap;
        
        // Profile Image
        profileImageLabel.setBounds(labelX, y, 140, 30);
        add(profileImageLabel);
        profileImageBtn.setBounds(fieldX, y, 100, 30);
        profileImageBtn.addActionListener(this);
        add(profileImageBtn);
        profileImagePreview.setBounds(310, y, 100, 100);
        profileImagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(profileImagePreview);
        y += 120;
        
        // Signup and Cancel buttons
        signupBtn.setBounds(150, y, 120, 40);
        signupBtn.addActionListener(this);
        add(signupBtn);
        
        cancelBtn.setBounds(290, y, 120, 40);
        cancelBtn.addActionListener(this);
        add(cancelBtn);
        
        // ID 필드 변경 시 중복확인 플래그 리셋
        idField.addActionListener(e -> idChecked = false);
        idField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { idChecked = false; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { idChecked = false; }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { idChecked = false; }
        });
        
        // Nickname 필드 변경 시 중복확인 플래그 리셋
        nicknameField.addActionListener(e -> nicknameChecked = false);
        nicknameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { nicknameChecked = false; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { nicknameChecked = false; }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { nicknameChecked = false; }
        });
        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == idCheckBtn) {
            checkIdDuplicate();
        } else if (e.getSource() == nicknameCheckBtn) {
            checkNicknameDuplicate();
        } else if (e.getSource() == postalCodeSearchBtn) {
            searchPostalCode();
        } else if (e.getSource() == profileImageBtn) {
            selectProfileImage();
        } else if (e.getSource() == signupBtn) {
            performSignup();
        } else if (e.getSource() == cancelBtn) {
            dispose();
        }
    }
    
    private void checkIdDuplicate() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 콜백 설정
            network.MessageReceiver.setDuplicateCheckCallback((type, message) -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (type == dto.type.DtoType.ID_OK) {
                        JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.", "중복확인", JOptionPane.INFORMATION_MESSAGE);
                        idChecked = true;
                    } else if (type == dto.type.DtoType.ID_DUPLICATE) {
                        JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.", "중복확인", JOptionPane.WARNING_MESSAGE);
                        idChecked = false;
                    }
                    network.MessageReceiver.setDuplicateCheckCallback(null); // 콜백 제거
                });
            });
            
            PrintWriter out = new PrintWriter(Application.socket.getOutputStream(), true);
            out.println("ID_CHECK:" + id);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "서버 통신 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkNicknameDuplicate() {
        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 콜백 설정
            network.MessageReceiver.setDuplicateCheckCallback((type, message) -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (type == dto.type.DtoType.NICKNAME_OK) {
                        JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.", "중복확인", JOptionPane.INFORMATION_MESSAGE);
                        nicknameChecked = true;
                    } else if (type == dto.type.DtoType.NICKNAME_DUPLICATE) {
                        JOptionPane.showMessageDialog(this, "이미 사용 중인 닉네임입니다.", "중복확인", JOptionPane.WARNING_MESSAGE);
                        nicknameChecked = false;
                    }
                    network.MessageReceiver.setDuplicateCheckCallback(null); // 콜백 제거
                });
            });
            
            PrintWriter out = new PrintWriter(Application.socket.getOutputStream(), true);
            out.println("NICKNAME_CHECK:" + nickname);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "서버 통신 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchPostalCode() {
        PostalCodeSearchDialog dialog = new PostalCodeSearchDialog(this);
        String[] result = dialog.showDialog();
        if (result != null) {
            postalCodeField.setText(result[0]);
            addressField.setText(result[1]);
        }
    }
    
    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
            }
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage originalImage = ImageIO.read(selectedImageFile);
                Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                profileImagePreview.setIcon(new ImageIcon(scaledImage));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "이미지를 읽을 수 없습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performSignup() {
        // 필수 필드 검증
        String id = idField.getText().trim();
        String password = new String(pwField.getPassword());
        String passwordConfirm = new String(pwConfirmField.getPassword());
        String nickname = nicknameField.getText().trim();
        String name = nameField.getText().trim();
        String emailId = emailIdField.getText().trim();
        String emailDomain = (String) emailDomainCombo.getSelectedItem();
        String email = emailId + "@" + emailDomain;
        String phone = phoneField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        
        // 필수 필드 체크
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!idChecked) {
            JOptionPane.showMessageDialog(this, "아이디 중복확인을 해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "비밀번호를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 비밀번호 유효성 검사 (8자 이상, 영문+숫자+특수문자)
        if (!isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, 
                "비밀번호는 8자 이상이어야 하며,\n영문, 숫자, 특수문자를 모두 포함해야 합니다.", 
                "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "닉네임을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!nicknameChecked) {
            JOptionPane.showMessageDialog(this, "닉네임 중복확인을 해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (emailId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이메일을 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "전화번호를 입력하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if ("선택".equals(gender)) {
            JOptionPane.showMessageDialog(this, "성별을 선택하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 생년월일 체크
        if (birthDatePicker.getModel().getValue() == null) {
            JOptionPane.showMessageDialog(this, "생년월일을 선택하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 선택 필드
        String postalCode = postalCodeField.getText().trim();
        String address = addressField.getText().trim();
        String detailAddress = detailAddressField.getText().trim();
        
        // 생년월일 포맷팅
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String birthDate = sdf.format(birthDatePicker.getModel().getValue());
        
        // 프로필 이미지 파일명 (실제 파일명만 저장)
        String profileImg = selectedImageFile != null ? selectedImageFile.getName() : "";
        
        // JoinRequest 생성
        JoinRequest request = new JoinRequest(
            id, name, password, profileImg, "", 
            nickname, email, phone, address, detailAddress, 
            postalCode, gender, birthDate
        );
        
        try {
            // 콜백 설정
            network.MessageReceiver.setDuplicateCheckCallback((type, message) -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    if (type == dto.type.DtoType.SIGNUP_SUCCESS) {
                        JOptionPane.showMessageDialog(this, "회원가입이 완료되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else if (type == dto.type.DtoType.SIGNUP_FAIL) {
                        JOptionPane.showMessageDialog(this, "회원가입에 실패했습니다.", "실패", JOptionPane.ERROR_MESSAGE);
                    } else if (type == dto.type.DtoType.SIGNUP_INVALID_PASSWORD) {
                        JOptionPane.showMessageDialog(this, "비밀번호가 유효하지 않습니다.", "실패", JOptionPane.ERROR_MESSAGE);
                    }
                    network.MessageReceiver.setDuplicateCheckCallback(null); // 콜백 제거
                });
            });
            
            // 1. JoinRequest를 문자열로 전송
            PrintWriter out = new PrintWriter(Application.socket.getOutputStream(), true);
            out.println(request.toString());
            
            // 2. 프로필 이미지 바이트 전송 (있는 경우)
            DataOutputStream dos = new DataOutputStream(Application.socket.getOutputStream());
            if (selectedImageFile != null && selectedImageFile.exists()) {
                byte[] imageBytes = readFileToBytes(selectedImageFile);
                dos.writeInt(imageBytes.length);
                dos.write(imageBytes);
                dos.flush();
            } else {
                // 이미지가 없으면 길이 0 전송
                dos.writeInt(0);
                dos.flush();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "서버 통신 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = Pattern.compile("[a-zA-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find();
        
        return hasLetter && hasDigit && hasSpecial;
    }
    
    private byte[] readFileToBytes(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        return baos.toByteArray();
    }
    
    // DatePicker용 포맷터
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
        
        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }
        
        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
