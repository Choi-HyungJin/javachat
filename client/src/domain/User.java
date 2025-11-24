package domain;

import java.util.Date;

public class User {

    private String id;
    private String nickname;
    private Date createdAt;
    private String role = "USER";
    private boolean online;
    private boolean banned;

    public User(String id, String nickname) {
        this(id, nickname, "USER", false, false);
    }

    public User(String id, String nickname, String date) {
        this(id, nickname, "USER", false, false);
        this.createdAt = new Date(date);
    }

    public User(String id, String nickname, String role, boolean online, boolean banned) {
        this.id = id;
        this.nickname = nickname;
        this.role = role;
        this.online = online;
        this.banned = banned;
        this.createdAt = new Date();
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
