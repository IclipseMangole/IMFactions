package de.imfactions.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by NicoH on 06.12.2016.
 */
public class SkullUtils {

    public static ItemStack getPlayerSkull(Player p) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        ((Damageable) meta).setDamage(3);
        List<String> lore = meta.getLore();
        meta.setOwningPlayer(p);
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.clear();
        lore.add(null);
        meta.setLore(lore);
        meta.setDisplayName(p.getName());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getPlayerSkull(UUID uuid) {

        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        ((Damageable) meta).setDamage(3);
        List<String> lore = meta.getLore();
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUIDFetcher.getName(uuid));
        meta.setOwningPlayer(player);
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.clear();
        lore.add(null);
        meta.setLore(lore);
        meta.setDisplayName(UUIDFetcher.getName(uuid));
        item.setItemMeta(meta);
        return item;


        /*
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        ((Damageable) headMeta).setDamage(3);
        GameProfile profile = new GameProfile(uuid, UUIDFetcher.getName(uuid));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
         */
    }

    public static ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        ((Damageable) headMeta).setDamage(3);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    public static GameProfile getProfile(ItemStack stack) {
        SkullMeta headMeta = (SkullMeta) stack.getItemMeta();
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            return (GameProfile) profileField.get(headMeta);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        return null;
    }

    public static void setProfile(GameProfile profile, ItemStack stack) {

        SkullMeta headMeta = (SkullMeta) stack.getItemMeta();
        ((Damageable) headMeta).setDamage(3);

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        stack.setItemMeta(headMeta);
    }

}
