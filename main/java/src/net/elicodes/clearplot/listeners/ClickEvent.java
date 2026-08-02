package net.elicodes.clearplot.listeners;

import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.manager.CPManager;
import net.elicodes.clearplot.runnables.ClearTask;
import net.elicodes.clearplot.utils.ActionBar;
import net.elicodes.clearplot.utils.ConfigHelper;
import net.elicodes.clearplot.utils.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

public class ClickEvent implements Listener {

    private CPManager manager = Main.getPlugin().manager;

    @EventHandler
    void evento(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase(ConfigHelper.menu("name"))) {
            e.setCancelled(true);

            String nickName = player.getName().toLowerCase();
            if (!manager.getCleaning().containsKey(nickName)) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                for (String line : ConfigHelper.msgList("no_process")) {
                    player.sendMessage(line);
                }
                return;
            }
            ClearTask task = manager.getCleaning().get(nickName);
            if(e.getSlot() == ConfigHelper.menuInt("cancel-slot")) {
                task.stop();
                player.closeInventory();
                ActionBar.send(player, ConfigHelper.msg("actionbar.clean_cancel"), 2);
                for (String line : ConfigHelper.msgList("clean_cancel")) {
                    player.sendMessage(line);
                }
            }else if(e.getSlot() == 14) {
                task.setActionbar(!task.isActionbar());
                if (!task.isActionbar()) ActionBar.send(player, ConfigHelper.msg("actionbar.off_actionbar"));
                ItemBuilder ib = task.isActionbar() ? new ItemBuilder(ConfigHelper.menuInt("off_actionbar.id"), (short)ConfigHelper.menuInt("off_actionbar.data")) : new ItemBuilder(ConfigHelper.menuInt("on_actionbar.id"), (short)ConfigHelper.menuInt("on_actionbar.data"));
                e.getView().setItem(ConfigHelper.menuInt("actionbar-slot"), ib
                        .setName(task.isActionbar() ? ConfigHelper.menu("off_actionbar.name") : ConfigHelper.menu("on_actionbar.name"))
                        .setLore(task.isActionbar() ? ConfigHelper.menuList("off_actionbar.lore") : ConfigHelper.menuList("on_actionbar.lore"))
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .glow(task.isActionbar() ? ConfigHelper.menuBoolean("off_actionbar.glow") : ConfigHelper.menuBoolean("on_actionbar.glow"))
                        .toItemStack());
            }
        }
    }
}
