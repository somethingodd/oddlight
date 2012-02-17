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

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightChunk implements ConfigurationSerializable {

    public OddLightChunk(Map<String, Object> serializable) {

    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public static OddLightChunk deserialize(Map<String, Object> serialized) {
        return new OddLightChunk(serialized);
    }

    public static OddLightChunk valueOf(Map<String, Object> serialized) {
        return new OddLightChunk(serialized);
    }
}
