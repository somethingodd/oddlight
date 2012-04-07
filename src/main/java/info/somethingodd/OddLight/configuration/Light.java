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
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class Light implements ConfigurationSerializable {
    private Location location;
    private ItemStack type;
    private ItemStack replacement;
    private int duration;

    public Light(Map<String, Object> serialized) {
        String loc = (String) serialized.get("location");
        UUID worldId = UUID.fromString(loc.substring(0, loc.indexOf("x") - 1));
        int x = Integer.valueOf(loc.substring(loc.indexOf("x") + 1, loc.indexOf("y") - 1));
        int y = Integer.valueOf(loc.substring(loc.indexOf("y") + 1, loc.indexOf("z") - 1));
        int z = Integer.valueOf(loc.substring(loc.indexOf("z") + 1));
        location = new Location(Bukkit.getWorld(worldId), x, y, z);
        type = (ItemStack) serialized.get("type");
        replacement = (ItemStack) serialized.get("replacement");
        duration = (Integer) serialized.get("duration");
    }

    public Light(Location location, ItemStack type) {
        this.location = location;
        this.type = type;
        this.replacement = Configuration.getReplacement(type);
        this.duration = Configuration.getDuration(type);
    }

    @Override
    public Map<String, Object> serialize() {
        Chunk chunk;
        Map<String, Object> serialized = new TreeMap<String, Object>(OddItem.ALPHANUM_COMPARATOR);
        StringBuilder locationString = new StringBuilder(location.getWorld().getUID().toString());
        locationString.append("x").append(location.getBlockX());
        locationString.append("y").append(location.getBlockY());
        locationString.append("z").append(location.getBlockZ());
        serialized.put("location", locationString.toString());
        serialized.put("type", type.serialize());
        serialized.put("replacement", replacement.serialize());
        serialized.put("duration", duration);
        return serialized;
    }

    public static Light deserialize(Map<String, Object> serialized) {
        return new Light(serialized);
    }

    public static Light valueOf(Map<String, Object> serialized) {
        return new Light(serialized);
    }
}
