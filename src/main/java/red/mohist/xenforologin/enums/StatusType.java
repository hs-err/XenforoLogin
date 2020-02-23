package red.mohist.xenforologin.enums;

import com.google.common.collect.ImmutableMap;

public enum StatusType {
    NEED_CHECK,NEED_LOGIN,NEED_REGISTER_EMAIL,NEED_REGISTER_PASSWORD,NEED_REGISTER_CONFIRM,LOGINED;
    public String email;
    public String password;
    public StatusType setEmail(String t){
        email=t;
        return this;
    }
    public StatusType setPassword(String t){
        password=t;
        return this;
    }
}
