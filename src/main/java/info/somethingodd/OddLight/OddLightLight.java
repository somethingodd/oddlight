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

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightLight implements ConfigurationSerializable {
    private Location location;
    private int typeId;
    private int duration;

    @Override
    public Map<String, Object> serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(location.getWorld()).append(",");
        sb.append(location.getBlockX()).append(",");
        sb.append(location.getBlockY()).append(",");
        sb.append(location.getBlockZ());

        sb.append(typeId).append(",").append(duration);
        return null;
    }
}
