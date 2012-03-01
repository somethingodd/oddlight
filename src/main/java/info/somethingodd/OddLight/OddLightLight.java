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
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightLight implements ConfigurationSerializable {
    private Location location;
    private ItemStack type;
    private ItemStack replacement;
    private int duration;

    public OddLightLight(Map<String, Object> serialized) {

    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new TreeMap<String, Object>(OddItem.ALPHANUM_COMPARATOR);
        serialized.put("location", location);
        serialized.put("type", type.serialize());
        serialized.put("replacement", replacement.serialize());
        serialized.put("duration", duration);
        return serialized;
    }

    public static OddLightLight deserialize(Map<String, Object> serialized) {
        return new OddLightLight(serialized);
    }

    public static OddLightLight valueOf(Map<String, Object> serialized) {
        return new OddLightLight(serialized);
    }
}
