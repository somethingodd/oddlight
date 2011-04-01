package info.somethingodd.bukkit.OddLight;

public class OddLightTorchData {
    private static byte data;
    private static boolean relit;

    public OddLightTorchData(Byte b, boolean r) {
        data = b;
        relit = r;
    }

    public Byte getData() {
        return data;
    }

    public boolean relit() {
        return relit;
    }
}
