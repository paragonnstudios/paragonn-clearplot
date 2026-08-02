package net.elicodes.clearplot.listeners;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.manager.CPManager;
import net.elicodes.clearplot.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class InteractEvent implements Listener {

    private CPManager manager = Main.getPlugin().manager;

    @EventHandler
    void evento(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack it = player.getItemInHand();
        Action action = e.getAction();
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if(it.isSimilar(manager.getItem())) {
                manager.clearPlot(player);
            }
        }
    }
}
