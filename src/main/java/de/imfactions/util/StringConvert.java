package de.imfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StringConvert {
    private StringConvert() {
    }

    public static String toString(ItemStack stack) {
        String s = "";
        s += stack.getType().name();
        s += "," + stack.getAmount();
        ItemMeta meta = stack.getItemMeta();
        s += "," + meta.getDisplayName();
        if (meta.getLore().size() > 0) {
            s += meta.getLore().get(0);
            for (int i = 1; i < meta.getLore().size(); i++) {
                s += ";" + meta.getLore().get(i);
            }
        } else {
            s += ",";
        }
        return s;
    }

    public static ItemStack toItemStack(String stack) {
        String[] array = stack.split(",");
        ItemStack item = new ItemStack(Material.getMaterial(array[0]));
        item.setAmount(Integer.parseInt(array[1]));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(array[2]);
        return item;
    }

    public static String toString(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static Location toLocation(String location, String world) {
        String[] array = location.split(",");
        return new Location(Bukkit.getWorld("world"), Double.parseDouble(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2]));
    }
}
