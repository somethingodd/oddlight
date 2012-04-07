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

import info.somethingodd.OddLight.configuration.Lights;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class Configuration {
    private static boolean appliedToOldChunks;
    private static int defaultDuration;
    private static final Map<ItemStack, Integer> durations;
    private static final Map<ItemStack, ItemStack> replacements;
    private static final Map<Chunk, Lights> chunks;
    private static OddLight oddLight;

    static {
        oddLight = (OddLight) Bukkit.getPluginManager().getPlugin("OddLight");
        chunks = new HashMap<Chunk, Lights>();
        durations = new HashMap<ItemStack, Integer>();
        replacements = new HashMap<ItemStack, ItemStack>();
    }

    public static boolean isAppliedToOldChunks() {
        return appliedToOldChunks;
    }

    public static int getDuration(ItemStack type) {
        Integer duration = durations.get(type);
        if (duration == null) {
            return defaultDuration;
        }
        return duration;
    }

    public static ItemStack getReplacement(ItemStack type) {
        ItemStack replacement = replacements.get(type);
        if (replacement == null) {
            return new ItemStack(85); // Fence
        }
        return replacement;
    }

    public static void configure() {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) oddLight.getConfig();
        appliedToOldChunks = yamlConfiguration.getBoolean("appliedToOldChunks");
        defaultDuration = yamlConfiguration.getInt("defaultDuration");
    }

    public static void load(Chunk chunk) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        Lights lights = new Lights();
        try {
            yamlConfiguration.load(getFileName(chunk));
            lights = Lights.valueOf(yamlConfiguration.getConfigurationSection("chunk").getValues(false));
        } catch (FileNotFoundException e) {
            if (isAppliedToOldChunks()) {
                lights = new Lights(chunk.getChunkSnapshot());
            }
        } catch (Exception e) {
            oddLight.getLogger().warning("Error while loading chunk (" + chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ() + ")");
            e.printStackTrace();
        }
        Chunks.putChunk(chunk, lights);
    }

    public static void save(Chunk chunk) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("chunk", Chunks.getChunk(chunk));
        try {
            yamlConfiguration.save(getFileName(chunk));
        } catch (Exception e) {
            oddLight.getLogger().severe("Error while attempting to save chunk");
            e.printStackTrace();
        }
    }

    public static String getFileName(Chunk chunk) {
        StringBuilder fileName = new StringBuilder(chunk.getWorld().getUID().toString()).append("u");
        fileName.append(chunk.getX()).append("x");
        fileName.append(chunk.getZ());
        return fileName.toString();
    }
}