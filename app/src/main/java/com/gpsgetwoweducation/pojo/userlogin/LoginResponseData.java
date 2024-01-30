package com.gpsgetwoweducation.pojo.userlogin;

public class LoginResponseData {
    private boolean is_password_incorrect;
    private String remaining_attempts_count;
    private String token;

    public boolean isIs_password_incorrect() {
        return is_password_incorrect;
    }

    public void setIs_password_incorrect(boolean is_password_incorrect) {
        this.is_password_incorrect = is_password_incorrect;
    }

    public String getRemaining_attempts_count() {
        return remaining_attempts_count;
    }

    public void setRemaining_attempts_count(String remaining_attempts_count) {
        this.remaining_attempts_count = remaining_attempts_count;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
