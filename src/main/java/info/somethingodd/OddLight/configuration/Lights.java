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
package info.somethingodd.OddLight.configuration;

import info.somethingodd.OddItem.OddItem;
import info.somethingodd.OddLight.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class Lights implements ConfigurationSerializable {
    private Chunk chunk;
    private Set<Light> lights;

    public Lights(Map<String, Object> serialized) {
        String chunkId = (String) serialized.get("chunk");
        UUID worldId = UUID.fromString(chunkId.substring(0, chunkId.indexOf("u") - 1));
        World world = Bukkit.getWorld(worldId);
        int x = Integer.valueOf(chunkId.substring(chunkId.indexOf("u") + 1, chunkId.indexOf("x") - 1));
        int z = Integer.valueOf(chunkId.substring(chunkId.indexOf("x") + 1));
        chunk = world.getChunkAt(x, z);
        Map<String, Object> lights = ((ConfigurationSection) serialized.get("lights")).getValues(false);
        for (String key : lights.keySet()) {
            this.lights.add(Light.valueOf(((ConfigurationSection) lights.get(key)).getValues(false)));
        }
    }

    public Lights(ChunkSnapshot chunkSnapshot) {
        World world = Bukkit.getWorld(chunkSnapshot.getWorldName());
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    if (chunkSnapshot.getBlockTypeId(x, y, z) == 50) {
                        lights.add(new Light(new Location(world, x, y, z), new ItemStack(50)));
                    }
                }
            }
        }
    }

    public Lights() {
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Set<Light> getLights() {
        return lights;
    }

    public void rainOn() {
        World world = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = world.getHighestBlockAt(x, z);
                while (block.getTypeId() == 50) {
                    block.setTypeId(Configuration.getReplacement(new ItemStack(block.getTypeId())).getTypeId());
                    block = world.getHighestBlockAt(x, z);
                }
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new TreeMap<String, Object>(OddItem.ALPHANUM_COMPARATOR);
        StringBuilder chunkString = new StringBuilder();
        chunkString.append(chunk.getWorld().getUID().toString()).append("u");
        chunkString.append(chunk.getX()).append("x").append(chunk.getZ());
        serialized.put("chunk", chunkString.toString());
        serialized.put("lights", lights);
        return serialized;
    }

    public static Lights deserialize(Map<String, Object> serialized) {
        return new Lights(serialized);
    }

    public static Lights valueOf(Map<String, Object> serialized) {
        return new Lights(serialized);
    }
}
