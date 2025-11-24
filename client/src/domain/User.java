package domain;

import java.util.Date;

public class User {

    private String id; // 아이디: 사용자 식별자

    private String nickname; // 이름: 채팅방에서 사용되는 이름

    private Date createdAt; // 로그인 시점

    public User(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        this.createdAt = new Date();
    }

    public User(String id, String nickname, String date) {
        this.id = id;
        this.nickname = nickname;
        this.createdAt = new Date(date);
    }

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getNickName() {
        return nickname;
    }
    
    public void setNickName(String nickname) {
        this.nickname = nickname;
    }
}
