package red.mohist.xenforologin.core.modules;

public class LocationInfo {
    public String world;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public LocationInfo(String world, double x, double y, double z) {
        this.world=world;
        this.x=x;
        this.y=y;
        this.z=z;
        this.yaw=0;
        this.pitch=0;
    }
    public LocationInfo(String world, double x, double y, double z,float yaw,float pitch) {
        this.world=world;
        this.x=x;
        this.y=y;
        this.z=z;
        this.yaw=yaw;
        this.pitch=pitch;
    }
}
