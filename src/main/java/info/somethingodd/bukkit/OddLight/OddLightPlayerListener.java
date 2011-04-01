package info.somethingodd.bukkit.OddLight;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OddLightPlayerListener extends PlayerListener {
    Plugin plugin;
    OddLight oddLight;
    int duration = 20;

    public OddLightPlayerListener(Plugin p) {
        plugin = p;
        oddLight = (OddLight) plugin;
        duration *= oddLight.getDuration();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (oddLight.getWorlds().contains(event.getPlayer().getWorld())) {
            Block block = event.getClickedBlock().getLocation().getBlock();
            OddLightTorchData OLTD = oddLight.getLights().get(block.getLocation());
            if (OLTD != null && !block.getType().equals(Material.TORCH)) {
                Material inHand = event.getItem().getType();
                if (inHand.equals(Material.FLINT_AND_STEEL)) {
                    block.setType(Material.TORCH);
                    block.setData(OLTD.getData());
                    oddLight.getServer().broadcastMessage("Data: " + block.getData());
                    oddLight.getLights().put(block.getLocation(), new OddLightTorchData(block.getData(), true));
                    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(oddLight, new OddLightTorchDecay(block.getLocation(), OLTD.relit(), oddLight), duration / 2);
                } else if (inHand.equals(Material.COAL)) {
                    block.setType(Material.TORCH);
                    block.setData(OLTD.getData());
                    oddLight.getServer().broadcastMessage("Data: " + block.getData());
                    oddLight.getLights().put(block.getLocation(), new OddLightTorchData(block.getData(), false));
                    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(oddLight, new OddLightTorchDecay(block.getLocation(), OLTD.relit(), oddLight), duration);
                    event.getPlayer().getInventory().removeItem(new ItemStack(Material.COAL, 1));
                }
            }
            if (block.getType().equals(Material.TORCH))
                oddLight.getServer().broadcastMessage("Data: " + block.getData());
        }
    }

}
