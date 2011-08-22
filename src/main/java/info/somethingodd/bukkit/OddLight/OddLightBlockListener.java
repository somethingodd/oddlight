package info.somethingodd.bukkit.OddLight;

import org.bukkit.event.block.BlockListener;

public class OddLightBlockListener extends BlockListener {
    private OddLight oddLight = null;

    public OddLightBlockListener(OddLight oddLight) {
        this.oddLight = oddLight;
    }
}
