package net.elicodes.clearplot.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryAPI {

    public static boolean verifyInv(Player player) {
        int i2 = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack itemContent : contents) {
            if (itemContent == null) ++i2;
        }
        return i2 > 0;
    }
}
