package dto.request;

import dto.type.DtoType;

public class JoinRequest extends DTO {
    private String userId;
    private String name;
    private String password;
    private String profileImg;
    private String statusMsg;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private String detailAddress;
    private String postalCode;
    private String gender;
    private String birthDate;

    public JoinRequest() {
        super(DtoType.SIGNUP);
    }

    public JoinRequest(String message) {
        super(DtoType.SIGNUP);
        String[] parts = message.split(",", -1);
        if (parts.length >= 13) {
            this.userId = parts[0];
            this.name = parts[1];
            this.password = parts[2];
            this.profileImg = parts[3];
            this.statusMsg = parts[4];
            this.nickname = parts[5];
            this.email = parts[6];
            this.phone = parts[7];
            this.address = parts[8];
            this.detailAddress = parts[9];
            this.postalCode = parts[10];
            this.gender = parts[11];
            this.birthDate = parts[12];
        }
    }

    public JoinRequest(String userId, String name, String password, String profileImg, String statusMsg,
                      String nickname, String email, String phone, String address, String detailAddress,
                      String postalCode, String gender, String birthDate) {
        super(DtoType.SIGNUP);
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.profileImg = profileImg;
        this.statusMsg = statusMsg;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return super.toString() + String.join(",", 
            userId != null ? userId : "",
            name != null ? name : "",
            password != null ? password : "",
            profileImg != null ? profileImg : "",
            statusMsg != null ? statusMsg : "",
            nickname != null ? nickname : "",
            email != null ? email : "",
            phone != null ? phone : "",
            address != null ? address : "",
            detailAddress != null ? detailAddress : "",
            postalCode != null ? postalCode : "",
            gender != null ? gender : "",
            birthDate != null ? birthDate : ""
        );
    }

    // Getter 메서드
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getProfileImg() { return profileImg; }
    public String getStatusMsg() { return statusMsg; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getDetailAddress() { return detailAddress; }
    public String getPostalCode() { return postalCode; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }

    // 각 필드에 대한 setter 메서드
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }
    public void setStatusMsg(String statusMsg) { this.statusMsg = statusMsg; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

}
