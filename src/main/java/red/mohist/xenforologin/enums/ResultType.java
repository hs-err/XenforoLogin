package red.mohist.xenforologin.enums;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.XenforoLogin;

public enum ResultType {
    OK, SERVER_ERROR, PASSWORD_INCORRECT, ERROR_NAME, NO_USER, UNKNOWN;

    ImmutableMap<String,String> inheritedObject;
    private boolean should_login;

    ResultType(){
        should_login=false;
    }
    public ResultType inheritedObject(ImmutableMap<String,String> inherited) {
        inheritedObject = inherited;
        return this;
    }

    public ResultType setLogin(boolean should) {
        should_login=should;
        return this;
    }

    public ImmutableMap<String,String> getInheritedObject() {
        return inheritedObject;
    }
    public void handle(Player player){
        switch (this) {
            case OK:
                if(should_login) {
                    XenforoLogin.instance.login(player);
                }
                break;
            case PASSWORD_INCORRECT:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.password")));
                break;
            case ERROR_NAME:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.name_incorrect",
                                getInheritedObject())));
                break;
            case NO_USER:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.no_user")));
                break;
            case UNKNOWN:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.unknown",
                                getInheritedObject())));
                break;
            case SERVER_ERROR:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.server")));
                break;
        }
    }
}
