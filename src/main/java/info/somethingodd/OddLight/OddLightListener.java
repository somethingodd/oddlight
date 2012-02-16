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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class OddLightListener implements Listener {
    private OddLight oddLight;

    public OddLightListener(OddLight oddLight) {
        this.oddLight = oddLight;
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        File chunkFile = new File(oddLight.getDataFolder() + File.separator + chunk.getWorld().getUID() + "-" + chunk.getX() + "-" + chunk.getZ());
        Map<Location, Integer> torches = Collections.synchronizedMap(new HashMap<Location, Integer>());
        if (!chunkFile.exists() && oddLight.oddLightConfiguration.newChunks) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                    for (int z = 0; z < 16; z++) {
                        if (chunk.getBlock(x, y, z).getTypeId() == 50) {
                            torches.put(new Location(chunk.getWorld(), (double) x, (double) y, (double) z), oddLight.oddLightConfiguration.getDuration());
                        }
                    }
                }
            }
        }
        YamlConfiguration chunkConfiguration = new YamlConfiguration();
        try {
            chunkConfiguration.load(chunkFile);
            for (String l : chunkConfiguration.getKeys(false)) {
                String[] ls = l.split("-");
                Location loc = new Location(chunk.getWorld(), Double.valueOf(ls[0]), Double.valueOf(ls[1]), Double.valueOf(ls[2]));
                torches.put(loc, chunkConfiguration.getInt(l));
            }
        } catch (Exception e) {
            oddLight.log.severe(oddLight.logPrefix + "Couldn't load chunk at " + chunk.getWorld().getUID() + "," + chunk.getX() + "," + chunk.getZ() + ": " + e.getMessage());
            e.printStackTrace();
        }
        oddLight.getLights().put(chunk, torches);
        // Run tasks
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        File chunkFile = new File(oddLight.getDataFolder() + File.separator + "chunk-" + chunk.getX() + "-" + chunk.getZ());
        YamlConfiguration chunkConfiguration = new YamlConfiguration();
        for (Location l : oddLight.getLights(chunk).keySet()) {
            StringBuilder loc = new StringBuilder();
            loc.append(l.getBlockX()).append("-");
            loc.append(l.getBlockY()).append("-");
            loc.append(l.getBlockZ());
            chunkConfiguration.set(loc.toString(), oddLight.getLights(chunk).get(l));
        }
        try {
            chunkConfiguration.save(chunkFile);
        } catch (Exception e) {
            oddLight.log.severe(oddLight.logPrefix + "Couldn't save chunk at " + chunk.getWorld().getUID() + "," + chunk.getX() + "," + chunk.getZ() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void placeTorch(BlockPlaceEvent event) {
        if (!event.isCancelled() && event.canBuild()) {
            if (event.getBlockPlaced().getTypeId() == 50) {
                oddLight.getLights(event.getBlockPlaced().getChunk()).put(event.getBlockPlaced().getLocation(), oddLight.oddLightConfiguration.getDuration());
                // Run task here
            }
        }
    }

    @EventHandler
    public void removeTorch(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getTypeId() == 50) {
            oddLight.getLights(event.getBlock().getChunk()).remove(event.getBlock().getLocation());
            // Cancel tasks
        }
    }

    @EventHandler
    public void manipulateTorch(PlayerInteractEvent event) {
        if (!event.isCancelled() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getTypeId() == 50) {
            if (oddLight.oddLightConfiguration.getLighters().containsKey(event.getItem()) && oddLight.getLights(event.getClickedBlock().getChunk()).containsKey(event.getClickedBlock().getLocation())) {
                event.getClickedBlock().setTypeId(50, false);
                oddLight.getLights(event.getClickedBlock().getChunk()).put(event.getClickedBlock().getLocation(), oddLight.oddLightConfiguration.getLighters().get(event.getItem()).duration());
            }
        }
    }
}