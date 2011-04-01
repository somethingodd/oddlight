package info.somethingodd.bukkit.OddLight;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import info.somethingodd.bukkit.OddItem.OddItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class OddLight extends JavaPlugin {
    private PermissionHandler Permissions = null;
    private static final String dataDir = "plugins" + File.separator + "Odd";
    private static final String config = dataDir + File.separator + "light.txt";
    private static Logger log;
    private static PluginDescriptionFile info;
    private static String logPrefix;
    private static Integer duration = null;
    private static ConcurrentMap<Location, OddLightTorchData> lights = new ConcurrentHashMap<Location, OddLightTorchData>();
    private static Material replacement = null;
    private static Boolean relightable = null;
    private static Boolean getAllTorches = null;
    private static Set<World> worlds = new HashSet<World>();
    private static OddLightBlockListener OLBL;
    private static OddLightPlayerListener OLPL;
    private static OddLightWorldListener OLWL;
    protected OddItem oddItem;

    protected boolean getAll() {
        return getAllTorches;
    }

    protected int getDuration() {
        return duration;
    }

    protected ConcurrentMap<Location, OddLightTorchData> getLights() {
        return lights;
    }

    protected boolean getRelightable() {
        return relightable;
    }

    protected Material getReplacement() {
        return replacement;
    }

    protected Set<World> getWorlds() {
        return worlds;
    }

    @Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (commandLabel.equals("oddlight")) {
            if (!sender.isOp()) {
                if (Permissions == null)
                    return true;
                if (args.length > 0 && !Permissions.has((Player) sender, "odd.light."+args[0]))
                    return true;
            }
            if (args.length == 0)
                return false;
            if (args[0].equals("duration")) {
                if (args.length == 1) {
                    sender.sendMessage("Duration is " + getDuration());
                    return true;
                } else if (args.length == 2) {
                    int multi = 1;
                    String d = args[1];
                    if (args[1].endsWith("m")) {
                        multi = 60;
                        d = d.substring(0, d.length() - 1);
                    } else if (args[1].endsWith("h")) {
                        multi = 3600;
                        d = d.substring(0, d.length() - 1);
                    }
                    try {
                        duration = Integer.decode(d);
                        duration *= multi;
                    } catch (NumberFormatException nfe) {
                        sender.sendMessage("Invalid duration: " + args[1]);
                    }
                }
            } else if (args[0].equals("worlds")) {
                if (args.length == 1) {
                    Set<String> w = new HashSet<String>();
                    for (World world : worlds) {
                        w.add(world.getName());
                    }
                    sender.sendMessage("Worlds: " + w.toString());
                    return true;
                } else {
                    Set<World> wTemp = new HashSet<World>();
                    for (int x = 1; x < args.length; x++) {
                        World w = getServer().getWorld(args[x].trim());
                        if (w == null) {
                            sender.sendMessage("Invalid world: " + args[x]);
                            return true;
                        }
                        wTemp.add(w);
                    }
                    worlds = wTemp;
                }
            }
        }
        return false;
	}

    @Override
	public void onDisable() {
        OLBL = null;
        OLPL = null;
        OLWL = null;
		log.info(logPrefix + "disabled");
	}

    @Override
	public void onEnable() {
		log.info(logPrefix + info.getVersion() + " enabled");
        Plugin p;
        p = getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            getServer().getPluginManager().enablePlugin(p);
            Permissions = ((Permissions) p).getHandler();
            log.info(logPrefix + "Using " + p.getDescription().getFullName());
        } else {
            log.info(logPrefix + "Permissions not found. Using op-only mode.");
        }
        p = getServer().getPluginManager().getPlugin("OddItem");
        if (p != null) {
            getServer().getPluginManager().enablePlugin(p);
            oddItem = (OddItem) p;
            log.info(logPrefix + "Using " + p.getDescription().getFullName());
        }
        parseConfig(readConfig());
        OLBL = new OddLightBlockListener(this);
        OLPL = new OddLightPlayerListener(this);
        OLWL = new OddLightWorldListener(this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PHYSICS, OLBL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, OLBL, Event.Priority.Normal, this);
        if (getAllTorches)
            getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, OLWL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.CHUNK_UNLOAD, OLWL, Event.Priority.Normal,  this);
        if (relightable)
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, OLPL, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, OLBL, Event.Priority.Normal, this);
    }

    @Override
    public void onLoad() {
        info = getDescription();
        log = getServer().getLogger();
        logPrefix = "[" + info.getName() + "] ";
        oddItem = null;
        Permissions = null;
    }

	private void parseConfig(String s) {
        String[] l = s.split(System.getProperty("line.separator"));
        for (String Al : l) {
            if (Al.startsWith("duration:")) {
                String d = Al.split(" ", 2)[1];
                int t;
                try {
                    if (d.endsWith("h")) {
                        t = 3600;
                        d = d.substring(0, d.length() - 1);
                    } else if (d.endsWith("m")) {
                        t = 60;
                        d = d.substring(0, d.length() - 1);
                    } else {
                        t = 1;
                    }
                    duration = Integer.decode(d);
                    duration *= t;
                } catch (NumberFormatException nfe) {
                    log.warning(logPrefix + "Invalid duration: " + d);
                }
            } else if (Al.startsWith("getalltorches: ")) {
                String g = Al.split(" ", 2)[1];
                if (g.equalsIgnoreCase("true") || g.equalsIgnoreCase("false")) {
                    getAllTorches = Boolean.valueOf(g);
                }
            } else if (Al.startsWith("relightable: ")) {
                String r = Al.split(" ", 2)[1];
                if (r.equalsIgnoreCase("true") || r.equalsIgnoreCase("false")) {
                    relightable = Boolean.valueOf(r);
                }
            } else if (Al.startsWith("replacement: ")) {
                String r = Al.split(" ", 2)[1];
                Material m = null;
                ItemStack is = null;
                if (oddItem != null) {
                    try {
                        is = oddItem.getItemStack(r);
                    } catch (IllegalArgumentException iae) {
                        log.warning(logPrefix + "OddItem unfound: " + r + " - Maybe you meant " + iae.getMessage() + "?");
                    }
                }
                if (is == null) {
                    try {
                        m = Material.getMaterial(Integer.decode(r));
                    } catch (NumberFormatException nfe) {
                        m = Material.getMaterial(r);
                    }
                } else {
                    m = is.getType();
                }
                replacement = m;
            } else if (Al.startsWith("worlds: ")) {
                String worldConfig = Al.split(" ", 2)[1];
                String worldConfigs[] = worldConfig.split(",");
                for (String w : worldConfigs) {
                    String w2 = w.trim();
                    World world = getServer().getWorld(w2);
                    if (world != null)
                        worlds.add(world);
                    else
                        log.warning(logPrefix + "Invalid world: " + w2);
                }
            }
        }
        if (duration == null) {
            log.warning(logPrefix + "Duration invalid or not set. Defaulting to 5 minutes.");
            duration = 300;
        } else {
            log.info(logPrefix + "Duration is " + duration.toString() + " seconds.");
        }
        if (getAllTorches == null) {
            log.warning(logPrefix + "GetAllTorches is invalid or not set. Defaulting to false.");
            getAllTorches = false;
        } else {
            log.info(logPrefix + "GetAllTorches is " + getAllTorches.toString() + ".");
        }
        getAllTorches = false;
        if (relightable == null) {
            log.warning(logPrefix + "Relightable invalid or not set. Defaulting to true.");
            relightable = true;
        } else {
            log.info(logPrefix + "Relightable is " + relightable.toString() + ".");
        }
        if (replacement == null) {
            log.warning(logPrefix + "Replacement invalid or not set. Defaulting to REDSTONE_TORCH_OFF.");
            replacement = Material.REDSTONE_TORCH_OFF;
        } else {
            log.info(logPrefix + "Replacement is " + replacement.name() + ".");
        }
        if (worlds.isEmpty()) {
            log.warning(logPrefix + "Worlds invalid or not set. Adding default world.");
            worlds.add(getServer().getWorlds().get(0));
        }
	}

	private static String readConfig() {
		boolean dirExists = new File(dataDir).exists();
		if (!dirExists) {
			try {
				new File(dataDir).mkdir(); 
			} catch (SecurityException se) {
				log.severe(se.getMessage());
				return null;
			}
		}
		boolean fileExists = new File(config).exists();
		if (!fileExists) {
			try {
				new File(config).createNewFile();
			} catch (IOException ioe) {
				log.severe(ioe.getMessage());
				return null;
			}
		}
		File file = new File(config);
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader input =  new BufferedReader(new FileReader(file));
			try {
				String line = input.readLine();
				while (line != null) {
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
					line = input.readLine();
				}
			} catch (IOException ioe) {
                log.warning(logPrefix + "Error reading config: " + ioe.getMessage());
			} finally {
				input.close();
			}
		}
		catch (IOException ie){
			log.severe(ie.getMessage());
		}
		return contents.toString();
	}
}
