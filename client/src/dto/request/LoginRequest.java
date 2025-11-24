package dto.request;
import dto.type.DtoType;

public class LoginRequest extends DTO {
    private String id;
    private String pw;
    private String nickname; // 닉네임 추가

    // 기존 생성자 유지
    public LoginRequest(String id, String pw) {
        super(DtoType.LOGIN);
        this.id = id;
        this.pw = pw;
    }

    // 새로운 생성자 추가
    public LoginRequest(String id, String pw, String nickname) {
        super(DtoType.LOGIN);
        this.id = id;
        this.pw = pw;
        this.nickname = nickname;
    }

    // Getter
    public String getId() { return id; }
    public String getPw() { return pw; }
    public String getNickname() { return nickname; }

    // Setter
    public void setId(String id) { this.id = id; }
    public void setPw(String pw) { this.pw = pw; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    @Override
    public String toString() {
        return super.toString() + id + "," + pw + (nickname != null ? "," + nickname : "");
    }
}