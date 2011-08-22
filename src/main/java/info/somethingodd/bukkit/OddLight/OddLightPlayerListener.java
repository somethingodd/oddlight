package info.somethingodd.bukkit.OddLight;

import org.bukkit.event.player.PlayerListener;

public class OddLightPlayerListener extends PlayerListener {
    private OddLight oddLight = null;

    public OddLightPlayerListener(OddLight oddLight) {
        this.oddLight = oddLight;
    }
}
