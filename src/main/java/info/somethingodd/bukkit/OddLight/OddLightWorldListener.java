package info.somethingodd.bukkit.OddLight;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.Plugin;

public class OddLightWorldListener extends WorldListener {
    private static OddLight oddLight;

    public OddLightWorldListener(Plugin p) {
        oddLight = (OddLight) p;
    }

    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (oddLight.getWorlds().contains(chunk.getWorld()))
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    for (int y = 0; y < 128; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().equals(Material.TORCH)) {
                            oddLight.getLights().put(block.getLocation(), new OddLightTorchData(block.getData(), false));
                            oddLight.getServer().getScheduler().scheduleAsyncDelayedTask(oddLight, new OddLightTorchDecay(block.getLocation(), false, oddLight), oddLight.getDuration());
                        }
                    }
    }

    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        if (oddLight.getWorlds().contains(chunk.getWorld())) {
            int cx = chunk.getX();
            int cz = chunk.getZ();
            for (Location location : oddLight.getLights().keySet()) {
                int bx = location.getBlockX();
                int bz = location.getBlockZ();
                if (bx >= cx && bx <= cx + 15 && bz >= cz && bz <= cz + 15) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}