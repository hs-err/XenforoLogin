package red.mohist.xenforologin.enums;

public enum StatusType {
    NEED_CHECK, NEED_LOGIN, NEED_REGISTER_EMAIL, NEED_REGISTER_PASSWORD, NEED_REGISTER_CONFIRM, LOGGED_IN;
    public String email;
    public String password;

    public StatusType setEmail(String t) {
        email = t;
        return this;
    }

    public StatusType setPassword(String t) {
        password = t;
        return this;
    }
}
