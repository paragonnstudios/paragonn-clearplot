package net.elicodes.clearplot.commands;

import net.elicodes.clearplot.Main;
import net.elicodes.clearplot.manager.CPManager;
import net.elicodes.clearplot.runnables.ClearTask;
import net.elicodes.clearplot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ClearPlotCommand implements CommandExecutor {

    private CPManager manager = Main.getPlugin().manager;

    private EC_Config config = Main.getPlugin().config;

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lb, String[] a) {
        if(a.length == 0) {
            showHelp(s);
            return true;
        }

        if(a[0].equalsIgnoreCase("give")) {
            if (!s.hasPermission(config.getString("CMD Permission"))) {
                showHelp(s);
                return true;
            }

            if (a.length < 3) {
                s.sendMessage(ConfigHelper.msg("no_arguments.give"));
                return true;
            }

            Player target = Bukkit.getPlayer(a[1]);
            if (target == null) {
                for(String line : ConfigHelper.msgList("no_player")) {
                    s.sendMessage(line);
                }
                return true;
            }

            try {
                int quantia = Integer.parseInt(a[2]);
                ItemStack cpItem = manager.getItem().clone();
                cpItem.setAmount(quantia);
                if(InventoryAPI.verifyInv(target)) {
                    target.getInventory().addItem(cpItem);
                }else {
                    for(String line : ConfigHelper.msgList("full")) {
                        s.sendMessage(line.replace("@player", target.getName()));
                    }
                    return true;
                }
                for(String line : ConfigHelper.msgList("give")) {
                    s.sendMessage(line.replace("@quantia", String.valueOf(quantia)).replace("@player", target.getName()));
                }
            } catch (NumberFormatException ignored) {
                s.sendMessage(ConfigHelper.msg("no_number"));
            }
        }else if(a[0].equalsIgnoreCase("menu")) {
            if(!(s instanceof Player)) {
                for(String line : ConfigHelper.msgList("console")) {
                    s.sendMessage(line);
                }
                return true;
            }
            Player player = (Player) s;
            if(manager.getCleaning().containsKey(player.getName().toLowerCase())) {
                ClearTask task = manager.getCleaning().get(player.getName().toLowerCase());
                Inventory inv = Bukkit.createInventory(null, ConfigHelper.menuInt("rows")*9, ConfigHelper.menu("name"));

                inv.setItem(ConfigHelper.menuInt("cancel-slot"), new ItemBuilder(Material.valueOf(ConfigHelper.menu("cancel.material")), (short)ConfigHelper.menuInt("cancel.data"))
                        .setName(ConfigHelper.menu("cancel.name"))
                        .setLore(ConfigHelper.menuList("cancel.lore"))
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .glow(ConfigHelper.menuBoolean("cancel.glow"))
                        .toItemStack());

                ItemBuilder ib = task.isActionbar() ? new ItemBuilder(ConfigHelper.menuInt("off_actionbar.id"), (short)ConfigHelper.menuInt("off_actionbar.data")) : new ItemBuilder(ConfigHelper.menuInt("on_actionbar.id"), (short)ConfigHelper.menuInt("on_actionbar.data"));
                inv.setItem(ConfigHelper.menuInt("actionbar-slot"), ib
                                .setName(task.isActionbar() ? ConfigHelper.menu("off_actionbar.name") : ConfigHelper.menu("on_actionbar.name"))
                                .setLore(task.isActionbar() ? ConfigHelper.menuList("off_actionbar.lore") : ConfigHelper.menuList("on_actionbar.lore"))
                                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                                .glow(task.isActionbar() ? ConfigHelper.menuBoolean("off_actionbar.glow") : ConfigHelper.menuBoolean("on_actionbar.glow"))
                                .toItemStack());
                player.openInventory(inv);
            }else player.sendMessage(ConfigHelper.msg("no_process"));
        }else showHelp(s);
        return false;
    }

    private void showHelp(CommandSender s) {
        if(s.hasPermission(config.getString("CMD Permission"))) {
            for(String line : ConfigHelper.msgList("help_admin")) {
                s.sendMessage(line);
            }
        }else {
            for(String line : ConfigHelper.msgList("help")) {
                s.sendMessage(line);
            }
        }
    }
}
