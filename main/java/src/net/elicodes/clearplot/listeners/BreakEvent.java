package net.elicodes.clearplot.listeners;

import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.manager.CPManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakEvent implements Listener {

    private CPManager manager = Main.getPlugin().manager;

    @EventHandler(priority = EventPriority.HIGH)
    void evento(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack it = player.getItemInHand();
        if (it.isSimilar(manager.getItem())) {
            e.setCancelled(true);
        }
    }
}
