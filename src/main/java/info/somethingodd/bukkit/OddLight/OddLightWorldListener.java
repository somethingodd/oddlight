package info.somethingodd.bukkit.OddLight;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

public class OddLightWorldListener extends WorldListener {
    private OddLight oddLight = null;

    public OddLightWorldListener(OddLight oddLight) {
        this.oddLight = oddLight;
    }

    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        oddLight.loadChunk(chunk);
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                for(int y = 0; y < 128; y++) {
                    if (chunk.getBlock(x, y, z).getType() == Material.TORCH) {
                        if !oddLight.torches.contains()
                    }
                }
            }
        }
    }

    @Override
    public void onChunkUnload(ChunkUnloadEvent event) {

    }
}
