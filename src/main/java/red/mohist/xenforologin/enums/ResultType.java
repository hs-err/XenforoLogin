package red.mohist.xenforologin.enums;

import com.google.common.collect.ImmutableMap;

public enum ResultType {
    OK, SERVER_ERROR, PASSWORD_INCORRECT, ERROR_NAME, NO_USER, UNKNOWN,USER_EXIST,EMAIL_EXIST,EMAIL_WRONG;

    ImmutableMap<String, String> inheritedObject;
    private boolean shouldLogin;

    ResultType() {
        shouldLogin = false;
    }

    public ResultType inheritedObject(ImmutableMap<String, String> inherited) {
        inheritedObject = inherited;
        return this;
    }

    public ResultType shouldLogin(boolean should) {
        shouldLogin = should;
        return this;
    }

    public ImmutableMap<String, String> getInheritedObject() {
        return inheritedObject;
    }

    public boolean isShouldLogin() {
        return shouldLogin;
    }
}
