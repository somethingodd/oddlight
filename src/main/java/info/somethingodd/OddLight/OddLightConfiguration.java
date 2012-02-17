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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightConfiguration {
    private Logger log;
    private Map<ItemStack, OddLightLighter> lighters;
    protected Map<Chunk, Map<Location, Integer>> lights;
    private boolean newChunks;
    private OddLight oddLight;

    private int duration;

    public OddLightConfiguration(OddLight oddLight) {
        this.oddLight = oddLight;
        mainConfigFile = new File(dataFolder + File.separator + "OddLight.yml");
        makeDefaults();
    }

    protected void configure() {
        String[] filenames = {"config.yml"};
        try {
            initialConfig(filenames);
        } catch (Exception e) {
            oddLight.log.warning("Exception writing initial configuration files: " + e.getMessage());
            e.printStackTrace();
        }
        YamlConfiguration yamlConfiguration = (YamlConfiguration) oddLight.getConfig();
        newChunks = yamlConfiguration.getBoolean("newChunks");
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
        File chunkFile = new File(oddLight.getDataFolder(), getChunkFile(chunk));
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

    }

    private String getChunkFile(Chunk chunk) {
        StringBuilder chunkString = new StringBuilder();
        chunkString.append(chunk.getWorld().getUID()).append(File.separator);
        chunkString.append(chunk.getX()).append("-");
        chunkString.append(chunk.getZ()).append(".yml");
        return chunkString.toString();
    }

    private void initialConfig(String[] filenames) throws IOException {
        for (String filename : filenames) {
            File file = new File(oddLight.getDataFolder(), filename);
            if (!file.exists()) {
                BufferedReader src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
                BufferedWriter dst = new BufferedWriter(new FileWriter(file));
                try {
                    file.mkdirs();
                    file.createNewFile();
                    src = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + filename)));
                    dst = new BufferedWriter(new FileWriter(file));
                    String line = src.readLine();
                    while (line != null) {
                        dst.write(line + "\n");
                        line = src.readLine();
                    }
                    src.close();
                    dst.close();
                    oddLight.log.info("Wrote default " + filename);
                } catch (IOException e) {
                    oddLight.log.warning("Error writing default " + filename);
                } finally {
                    try {
                        src.close();
                        dst.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}