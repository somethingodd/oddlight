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
package info.somethingodd.bukkit.OddLight;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLight extends JavaPlugin {
    protected Logger log = null;
    protected String logPrefix = null;
    protected OddLightConfiguration oddLightConfiguration;
    private OddLightListener oddLightListener;
    private Map<Chunk, Map<Location, Integer>> lights;

    @Override
	public void onDisable() {
        log.info(logPrefix + "disabled");
        log = null;
        logPrefix = null;
        oddLightConfiguration = null;
        oddLightListener = null;
    }

    @Override
	public void onEnable() {
        log = getServer().getLogger();
        logPrefix = "["+ getDescription().getName() + "] ";
		log.info(logPrefix + getDescription().getVersion() + " enabled");
        oddLightConfiguration = new OddLightConfiguration(this);
        oddLightConfiguration.configure();
        oddLightListener = new OddLightListener(this);
        getServer().getPluginManager().registerEvents(oddLightListener, this);
    }

    protected Map<Chunk, Map<Location, Integer>> getLights() {
        if (lights == null)
            lights = Collections.synchronizedMap(new HashMap<Chunk, Map<Location, Integer>>());
        return lights;
    }

    protected Map<Location, Integer> getLights(Chunk chunk) {
        if (!lights.containsKey(chunk))
            lights.put(chunk, Collections.synchronizedMap(new HashMap<Location, Integer>()));
        return lights.get(chunk);
    }
}
