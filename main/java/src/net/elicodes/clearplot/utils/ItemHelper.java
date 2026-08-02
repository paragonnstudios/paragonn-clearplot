package net.elicodes.clearplot.utils;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemHelper {

    public static ItemStack create(String skullURL, String material, short data, String name, List<String> lore, boolean glow) {
        ItemStack it;
        if(skullURL.isEmpty()) {
            Material m = Material.valueOf(material);
            it = new ItemStack(m, 1, data);
        }else it = SkullURL.getSkull(skullURL);

        name = name.replace("&", "§");
        List<String> newLore = lore.stream().map(line -> line.replace("&", "§")).collect(Collectors.toList());
        ItemMeta mt = it.getItemMeta();
        mt.setDisplayName(name);
        mt.setLore(newLore);
        mt.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(mt);
        if(glow) it = glow(it);
        return it;
    }

    public static ItemStack glow(ItemStack it) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(it);
        NBTTagCompound tag = nmsStack.getTag() == null ? new NBTTagCompound() : nmsStack.getTag();

        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }
}
