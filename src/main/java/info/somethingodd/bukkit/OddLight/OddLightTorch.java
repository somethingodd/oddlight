package info.somethingodd.bukkit.OddLight;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.Serializable;

public class OddLightTorch implements Serializable {
    private Integer task = null;
    private Location location = null;

    public OddLightTorch(Block block) {
        location = block.getLocation();
    }

    public OddLightTorch(Location location) {
        this.location = location;
    }

    public OddLightTorch(String oddLightTorch) {
        this.location.setX(Double.valueOf(oddLightTorch.substring(0,oddLightTorch.indexOf(".")-1)));
        this.location.setY(Double.valueOf(oddLightTorch.substring(oddLightTorch.indexOf(".") + 1, oddLightTorch.indexOf("." + 1, oddLightTorch.indexOf(".") - 1))));
        this.location.setZ(Double.valueOf(oddLightTorch.substring(oddLightTorch.lastIndexOf(".") + 1)));
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object e) {
        if (e == null) return false;
        if (!(e instanceof OddLightTorch)) return false;
        if (this == e) return true;
        if (this.hashCode() == e.hashCode()) return true;
        return false;
    }
}
