package dto.type;

public enum DtoType {
    LOGIN,
    LOGOUT,//로그아웃
    ID_CHECK,//아이디중복확인 요청
    ID_OK,//아이디사용가능
    ID_DUPLICATE,//아이디중복
    NICKNAME_CHECK,//닉네임중복확인 요청
    NICKNAME_OK,//닉네임사용가능
    NICKNAME_DUPLICATE,//닉네임중복
    CREATE_CHAT,
    ENTER_CHAT, EXIT_CHAT,
    MESSAGE,
    SIGNUP,
    SIGNUP_SUCCESS,//회원가입성공
    SIGNUP_FAIL,//회원가입실패
    SIGNUP_INVALID_PASSWORD,//비밀번호 형식 오류
    ADDRESS_RESULT,//우편번호 검증 결과
    USER_LIST, CHAT_ROOM_LIST,
    CHAT_HISTORY,//채팅방 이전 대화목록
    FRIEND_LIST, // 친구 목록 응답
    FRIEND_ADD, FRIEND_ADD_RESULT,
    FRIEND_REMOVE, FRIEND_REMOVE_RESULT,
    FRIEND_CHAT_START, // 1:1 채팅 시작 요청
    FRIEND_CHAT_INVITE // 1:1 채팅 초대 알림
}
