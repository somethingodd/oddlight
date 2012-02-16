/* This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.somethingodd.OddLight;

import info.somethingodd.OddItem.OddItem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightConfiguration {
    private Logger log;
    private String logPrefix;
    private File mainConfigFile;
    private Map<ItemStack, OddLightLighter> lighters;
    private YamlConfiguration defaults;
    protected Map<Chunk, Map<Location, Integer>> lights;
    protected boolean newChunks;
    private File dataFolder;

    private int duration;

    public OddLightConfiguration(OddLight oddLight) {
        log = oddLight.log;
        logPrefix = oddLight.logPrefix;
        dataFolder = oddLight.getDataFolder();
        mainConfigFile = new File(dataFolder + File.separator + "OddLight.yml");
        makeDefaults();
    }

    protected void configure() {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(mainConfigFile);
        } catch (Exception e) {
            log.severe(logPrefix + "Error opening configuration: " + e.getMessage());
            e.printStackTrace();
        }
        configuration.setDefaults(defaults);
        ConfigurationSection lightersSection = configuration.getConfigurationSection("lighters");
        for (String s : lightersSection.getKeys(false)) {
            ConfigurationSection lighterData = lightersSection.getConfigurationSection(s);
            if (lighters == null) lighters = Collections.synchronizedMap(new HashMap<ItemStack, OddLightLighter>());
            lighters.put(OddItem.getItemStack(s), new OddLightLighter(lighterData.getBoolean("consumed"), lighterData.getInt("duration")));
        }
        newChunks = configuration.getBoolean("newChunks");
    }

    protected int getDuration() {
        return duration;
    }

    protected Map<ItemStack, OddLightLighter> getLighters() {
        return Collections.unmodifiableMap(lighters);
    }

    protected void loadChunk(Chunk chunk) {
        if (lights == null) {
            lights = Collections.synchronizedMap(new HashMap<Chunk, Map<Location, Integer>>());
        }
        File chunkFile = new File(dataFolder + File.separator + chunk.getWorld().getUID() + File.separator + String.valueOf(chunk.getX()) + "-" + String.valueOf(chunk.getZ()) + ".yml");
        YamlConfiguration chunkConfiguration = new YamlConfiguration();
        try {
            chunkConfiguration.load(chunkFile);
        } catch (Exception e) {
        }
        Map<Location, Integer> chunkLights = Collections.synchronizedMap(new HashMap<Location, Integer>());
        for (String x : chunkConfiguration.getKeys(false)) {
            String[] xp = x.split(" ");
            chunkLights.put(new Location(chunk.getWorld(), Double.valueOf(xp[0]), Double.valueOf(xp[1]), Double.valueOf(xp[2])), chunkConfiguration.getInt(x));
        }
        lights.put(chunk, chunkLights);
    }

    protected void saveChunk(Chunk chunk) {
        File chunkFile = new File(dataFolder + File.separator + chunk.getWorld().getUID() + File.separator + String.valueOf(chunk.getX()) + "-" + String.valueOf(chunk.getZ()) + ".yml");
        YamlConfiguration chunkConfiguration = new YamlConfiguration();
        for (Map.Entry<Location, Integer> e : lights.get(chunk).entrySet()) {
            chunkConfiguration.set(e.getKey().getBlockX() + " " + e.getKey().getBlockY() + " " + e.getKey().getBlockZ(), e.getValue());
        }
        try {
            chunkConfiguration.save(chunkFile);
        } catch (Exception e) {
        }
        lights.remove(chunk);
    }

    private void makeDefaults() {
        Map<String, Object> coal = new HashMap<String, Object>();
        coal.put("consumed", "true");
        coal.put("duration", "60m");
        Map<String, Object> flintandsteel = new HashMap<String, Object>();
        flintandsteel.put("consumed", "false");
        flintandsteel.put("duration", "30m");
        Map<String, Object> lightersMap = new HashMap<String, Object>();
        lightersMap.put("coal", coal);
        lightersMap.put("flintandsteel", flintandsteel);
        defaults.createSection("lighters", lightersMap);
    }
}