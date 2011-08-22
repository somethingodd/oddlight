package info.somethingodd.bukkit.OddLight;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class OddLight extends JavaPlugin {
    private final String configFile = "plugins" + File.separator + "OddLight.yml";
    private PermissionHandler ph = null;
    private Logger log = null;
    private PluginDescriptionFile info = null;
    private String logPrefix = null;
    private Integer duration = null;
    protected Material replacement = null;
    protected Boolean relightable = null;
    protected Set<OddLightTorch> torches = null;
    protected Set<World> worlds = null;
    private OddLightBlockListener OLBL = null;
    private OddLightPlayerListener OLPL = null;
    private OddLightWorldListener OLWL = null;
    protected OddItem oddItem;

    private void configure() {

    }

    protected void loadChunk(Chunk chunk) {
        File file = new File("plugins" + File.separator + "OddLight" + File.separator + chunk.hashCode());
        Configuration data = new Configuration(file);
        if (!file.exists())
            data.save();

    }

    @Override
	public void onDisable() {
        torches = null;
        worlds = null;
        OLBL = null;
        OLPL = null;
        OLWL = null;
        ph = null;
		log.info(logPrefix + "disabled");
	}

    @Override
	public void onEnable() {
		log.info(logPrefix + info.getVersion() + " enabled");
        configure();
        Plugin p;
        p = getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            getServer().getPluginManager().enablePlugin(p);
            ph = ((Permissions) p).getHandler();
            log.info(logPrefix + "Using " + p.getDescription().getFullName());
        }
        oddItem = (OddItem) getServer().getPluginManager().getPlugin("OddItem");
        torches = new HashSet<OddLightTorch>();
        worlds = new HashSet<World>();
        OLBL = new OddLightBlockListener(this);
        OLPL = new OddLightPlayerListener(this);
        OLWL = new OddLightWorldListener(this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PHYSICS, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, OLWL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.CHUNK_UNLOAD, OLWL, Event.Priority.Normal,  this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, OLWL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, OLPL, Event.Priority.Normal, this);
    }

    @Override
    public void onLoad() {
        info = getDescription();
        log = getServer().getLogger();
        logPrefix = "[" + info.getName() + "] ";
    }

    protected boolean uglyPermissions(String permission, Player player) {
        if (ph != null)
            return ph.has(player, permission);
        return player.hasPermission(permission);
    }
}
