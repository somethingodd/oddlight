package info.somethingodd.bukkit.OddLight;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;
import org.bukkit.plugin.Plugin;

public class OddLightBlockListener extends BlockListener {
    Plugin plugin;
    OddLight oddLight;
    int duration = 20;

    public OddLightBlockListener(Plugin p) {
        plugin = p;
        oddLight = (OddLight) plugin;
        duration *= oddLight.getDuration();
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (oddLight.getWorlds().contains(event.getBlock().getWorld())) {
            Location location = event.getBlock().getLocation();
            if (oddLight.getLights().containsKey(location))
                oddLight.getLights().remove(location);
        }
    }

    @Override
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (oddLight.getWorlds().contains(event.getBlock().getWorld())) {
            Block block = event.getBlock().getLocation().getBlock();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (oddLight.getLights().get(new Location(block.getWorld(), block.getX() + x, block.getY() + y, block.getZ() + z)) != null) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (oddLight.getWorlds().contains(event.getBlock().getWorld())) {
            Location location = event.getBlock().getLocation();
            if (oddLight.getLights().get(location) != null) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (oddLight.getWorlds().contains(event.getBlockPlaced().getWorld())) {
            Block block = event.getBlockPlaced();
            if (block.getType().equals(Material.TORCH)) {
                oddLight.getServer().broadcastMessage("Data: " + block.getData());
                oddLight.getServer().getScheduler().scheduleSyncDelayedTask(oddLight, new OddLightTorchDataFix(), 20);
                oddLight.getServer().broadcastMessage("Data: " + block.getData());
                oddLight.getLights().put(block.getLocation(), new OddLightTorchData(block.getData(), false));
                plugin.getServer().getScheduler().scheduleAsyncDelayedTask(oddLight, new OddLightTorchDecay(block.getLocation(), false, oddLight), duration);
            }
        }
    }

    @Override
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if (oddLight.getWorlds().contains(event.getBlock().getWorld())) {
            Block block = event.getBlock();
            OddLightTorchData OLTD = oddLight.getLights().get(block.getLocation());
            if (OLTD != null) {
                block.setType(Material.AIR);
                block.setType(oddLight.getReplacement());
            }
        }
    }

}
