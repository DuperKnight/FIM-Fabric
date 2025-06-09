package fish.crafting.fimfabric.tools;

public interface PosRotated extends Positioned {

    float pitch();
    float yaw();

    void setRotation(float pitch, float yaw);

}
