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

import info.somethingodd.OddLight.configuration.Light;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Gordon Pettey (petteyg359@gmail.com)
 */
public class Listener implements org.bukkit.event.Listener {
    private OddLight oddLight;
    private static boolean raining = false;

    public Listener(OddLight oddLight) {
        this.oddLight = oddLight;
    }

    public static boolean isRaining() {
        return raining;
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        Configuration.load(event.getChunk());
        if (isRaining()) {
            Chunks.rainOn(event.getChunk());
        }
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        Configuration.save(event.getChunk());
    }

    @EventHandler
    public void torchPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && event.canBuild() && event.getBlockPlaced().getTypeId() == 50) {
            Light light = new Light(event.getBlockPlaced().getLocation(), new ItemStack(event.getBlockPlaced().getTypeId()));
        }
    }

    @EventHandler
    public void torchRemove(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getTypeId() == 50) {
        }
    }

    @EventHandler
    public void torchInteract(PlayerInteractEvent event) {
        if (!event.isCancelled() && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getTypeId() == 50) {
        }
    }

    @EventHandler
    public void rain(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            raining = true;
            for (World world : oddLight.getServer().getWorlds()) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    Chunks.rainOn(chunk);
                }
            }
        } else {
            raining = false;
        }
    }
}