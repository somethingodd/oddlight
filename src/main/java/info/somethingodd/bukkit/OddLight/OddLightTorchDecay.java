package info.somethingodd.bukkit.OddLight;

import org.bukkit.Location;
import org.bukkit.Material;

public class OddLightTorchDecay implements Runnable {
    Location location;
    OddLight oddLight;
    boolean relit;

    public OddLightTorchDecay (Location l, boolean r, OddLight oddLight) {
        location = l;
        relit = r;
        this.oddLight = oddLight;
    }

    @Override
    public void run() {
        Boolean r = oddLight.getRelightable();
        OddLightTorchData OLTD = oddLight.getLights().get(location);
        if (OLTD != null) {
            if (r && !relit) {
                location.getBlock().setType(oddLight.getReplacement());
                location.getBlock().setData(OLTD.getData());
            } else {
                location.getBlock().setType(Material.AIR);
                oddLight.getLights().remove(location);
            }
        }
    }
}
