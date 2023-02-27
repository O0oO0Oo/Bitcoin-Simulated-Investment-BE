package com.cryptocurrency.investment.config.response;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    /**
     * User
     */

    // common 공통 에러 처리
    INVALID_FORMAT(2000,"Invalid %s format."), // Email, [a-zA-Z0-9가-힣]
    INVALID_PERMISSION(2001,"You don't have permission. %s"),

    // POST login 로그인
    USER_LOGIN_SUCCEED(1000,"Login succeed."),
    USER_LOGIN_FAILED(2100,"Login failed, please check your email and password."),
    
    // POST join 회원가입
    USER_JOIN_SUCCEED(1000,"Join succeed. Welcome %s!"),
    // email, username already registered
    USER_JOIN_PASSWORD_FAIL(2100,"Write according to the password rules."),
    USER_JOIN_VALIDATION_FAIL(2101,"Validation code is incorrect. Check your email and validation code."),
    USER_JOIN_VALIDATION_CODE_EXPIRED(2102,"Validation code has expired. Please request verification code again."),

    // GET email 이메일 중복 확인
    USER_EMAIL_AVAILABLE(1000,"This email is available."),
    USER_EMAIL_UNAVAILABLE(2000,"Please use a different email."),

    // POST email 이메일 인증
    USER_EMAIL_VALIDATION_SENT(1000,"An email with a verification code has been sent to %s"),
    USER_EMAIL_VALIDATION_SENT_FAILED(2100,"Mail transmission failed due to an internal error."),
    USER_EMAIL_VALIDATION_ALREADY_SENT(2101,"Email has already been sent. Please check your spam folder. Re-request is possible after %d seconds."),

    // GET username 유저 이름 중복 확인
    USER_USERNAME_AVAILABLE(1000,"This username can be used."),
    USER_USERNAME_UNAVAILABLE(2100,"Please use a different username."),

    // GET user 유저 조회 / 실패 -> 2001 INVALID_JWT
    USER_INFO_GET_SUCCEED(1000,"User information search succeeded."),
    USER_INFO_GET_FAILED(2100,"User information search failed."),

    // PUT user 수정 / 실패 -> 2000 INVALID_FORMAT, 2001 INVALID_JWT
    USER_INFO_PUT_SUCCEED(1000, "User information has been modified."),
    USER_INFO_PUT_FAILED(2100,"User information modification failed."),
    USER_INFO_PUT_SUCCEED_GET_FAILED(2101,"User information has been modified. but User information search failed."),

    // DELETE user 삭제 / 실패 -> 2001 INVALID_JWT
    USER_INFO_DELETE_SUCCEED(1000, "User information has been deleted."),
    USER_INFO_DELETE_FAILED(2100,"User information deletion failed."),

    // POST attendance 출석하기
    USER_ATTENDANCE_SUCCEED(1000,"Attendance succeed."),
    USER_ATTENDANCE_FAILED(2100,"Attendance failed."),
    USER_ATTENDANCE_ALREADY_DONE(2101,"Attendance already done."),


    // GET attendance 유저 출석 목록 불러오기
    USER_ATTENDANCE_LIST_SUCCEED(1000,"Attendance list request succeeded."),
    USER_ATTENDANCE_LIST_FAILED(2100,"Attendance list request failed."),


    /**
     * Crypto Price
     */
    PRICE_REQUEST_SUCCEED(1000,"Request succeed."),
    PRICE_REQUEST_FAILED(2100,"Request failed."),
    // alert

    /**
     * Crypto
     */
    CRYPTO_REQUEST_SUCCEED(1000,"Request succeed."),
    CRYPTO_REQUEST_FAILED(2100,"Request failed."),
    CRYPTO_INVALID_STATUS(2101,"Invalid status : %s"),

    /**
     * Crypto Admin
     */
    // Post
    CRYPTO_NAME_NOT_EXIST(2102,"%s is a name that doesn't exists."),
    CRYPTO_NAME_EXIST(2103,"%s is a name that already exists."),

    // PUT
    CRYPTO_PUT_SUCCEED(1000,"Crypto information has been modified."),
    CRYPTO_PUT_FAILED(2100,"Crypto information modification failed."),

    // DELETE
    CRYPTO_DELETE_SUCCEED(1000,"Crypto information has been deleted."),
    CRYPTO_DELETE_FAILED(2100,"Crypto information deletion failed."),
    ;

    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
