package red.mohist.xenforologin.enums;

public enum ResultType {
    OK, SERVER_ERROR, PASSWORD_INCORRECT, ERROR_NAME, NO_USER, UNKNOWN;

    Object inheritedObject;

    public ResultType inheritedObject(Object inheritedObject) {
        this.inheritedObject = inheritedObject;
        return this;
    }

    public Object getInheritedObject() {
        return inheritedObject;
    }
}
