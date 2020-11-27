package de.imfactions.functions.items.api;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import de.imfactions.util.ColorUtils;
import de.imfactions.util.ItemAttributes;
import de.imfactions.util.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static de.imfactions.util.ColorUtils.toHex;

/**
 * Created by Iclipse on 20.06.2020
 */
public class Item {

    private ItemStructure itemStructure;
    private int itemsStacked;
    private int amount;
    private int durability;

    public Item(ItemStructure itemStructure, int itemsStacked, int amount, int durability) {
        this.itemStructure = itemStructure;
        this.itemsStacked = itemsStacked;
        this.amount = amount;
        this.durability = durability;
    }

    public ItemStructure getItemStructure() {
        return itemStructure;
    }

    public ItemRarity getRarity() {
        return itemStructure.getRarity();
    }

    public int getItemsStacked() {
        return itemsStacked;
    }

    public int getAmount() {
        return amount;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getLevel() {
        if (itemsStacked < 3) {
            return 0;
        } else if (itemsStacked < 9) {
            return 1;
        } else if (itemsStacked < 24) {
            return 2;
        } else if (itemsStacked < 49) {
            return 3;
        } else if (itemsStacked < 99) {
            return 4;
        } else {
            return 5;
        }
    }

    public HashMap<ItemModifierType, ItemModifierValue> getItemModifiers() {
        return itemStructure.getModifiers(getLevel());
    }

    private ArrayList<String> getModifierLore() {
        ArrayList<String> list = new ArrayList<>();
        getItemModifiers().forEach((itemModifierType, itemModifierValue) -> {
            list.add("§r" + itemModifierType.toString(itemModifierValue.value));
        });
        return list;
    }

    private ArrayList<String> getLore() {
        ArrayList<String> lores = new ArrayList<>();
        String line = "";
        for (int i = 0; i < 30; i++) {
            line += "═";
        }

        lores.add("§r" + "§7§o" + itemStructure.getSubtitle() + "§r");
        lores.addAll(getModifierLore());
        lores.add("§r" + getRarity().getColor() + "╔" + line);
        lores.add("§r" + getRarity().getColor() + "║" + ColorUtils.brighter(getRarity().getColor()) + "Level " + getLevel() + "§8 - (" + (itemsStacked + 1) + "|" + (nextLevelStage() + 1) + ")");
        lores.add("§r" + getRarity().getColor() + "║" + getProgressBar("§o§l■§r", 30, itemsStacked, nextLevelStage()));
        lores.add("§r" + getRarity().getColor() + "║§f" + (nextLevelStage() - itemsStacked) + " till next Level");
        lores.add("§r" + getRarity().getColor() + "╚" + line);

        double damage = getItemModifiers().containsKey(ItemModifierType.DAMAGE) ? (double) getItemModifiers().get(ItemModifierType.DAMAGE).value * 2 + ItemAttributes.getDamage(itemStructure.getMaterial()) : ItemAttributes.getDamage(itemStructure.getMaterial());
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? (double) getItemModifiers().get(ItemModifierType.ATTACK_SPEED).value : ItemAttributes.getAttackSpeed(itemStructure.getMaterial());
        DecimalFormat format = new DecimalFormat("0.0");

        lores.add("§r" + itemStructure.getChatColor() + "╔" + line);
        lores.add("§r" + itemStructure.getChatColor() + "║" + ColorUtils.brighter(itemStructure.getChatColor()) + " Attack Damage:§7 " + damage / 2 + ChatColor.of(toHex(153, 0, 0)) + "§l❤");
        lores.add("§r" + itemStructure.getChatColor() + "║" + ColorUtils.brighter(itemStructure.getChatColor()) + " Attack Speed:§7 " + format.format(attackSpeed) + " Hits per second");
        lores.add("§r" + itemStructure.getChatColor() + "║" + ColorUtils.brighter(itemStructure.getChatColor()) + " Durability:§7 " + getProgressBar("⚒", 15, durability, itemStructure.getMaterial().getMaxDurability()) + " §7(" + durability + "|" + itemStructure.getMaterial().getMaxDurability() + ")");
        lores.add("§r" + itemStructure.getChatColor() + "╚" + line);

        return lores;
    }

    public void updateLore(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(getLore());
        stack.setItemMeta(meta);
    }

    private String getProgressBar(String s, int amount, int value, int maxValue) {
        String bar = "";
        for (int i = 0; i < amount; i++) {
            if ((double)i / (double) amount < (double)value / (double)maxValue) {
                bar += ChatColor.of("#2A8000") + s;
            } else {
                bar += ChatColor.of("#E60026") + s;
            }
        }
        return bar;
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStackBuilder(itemStructure.getMaterial())
                .withName("§r" + itemStructure.getChatColor() + itemStructure.getDisplayName())
                .withCustomModelData(itemStructure.getRarity().getId() * 100 + itemsStacked)
                .withAmount(amount)
                .withItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE)
                .withLore(getLore())
                .withAttackSpeed(getAttackSpeed())
                .buildStack();
        if(itemStructure.getRarity() == ItemRarity.LEGENDARY) {
            if (itemStructure.getMaterial() == Material.BOW) {
                stack.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
            } else {
                stack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            }
        }
        return stack;
    }

    private ChatColor rainbowColor(int duration) {
        return rainbowColor(duration, 0, 0);
    }

    private ChatColor rainbowColor(double duration, int place, int max) {
        float hsv = (float) ((System.currentTimeMillis() % (duration * 1000.0)) / (duration * 1000.0));
        hsv += place * (((duration * 1000.0) / max) / (duration * 1000.0));
        return ChatColor.of(toHex(hsv, 1f, 1f));
    }

    private int nextLevelStage() {
        if (itemsStacked < 3) {
            return 3;
        } else if (itemsStacked < 9) {
            return 9;
        } else if (itemsStacked < 24) {
            return 24;
        } else if (itemsStacked < 49) {
            return 49;
        } else {
            return 99;
        }
    }

    public double getAttackSpeed(){
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? (double) getItemModifiers().get(ItemModifierType.ATTACK_SPEED).value : ItemAttributes.getAttackSpeed(itemStructure.getMaterial());
        return attackSpeed;
    }



    /**
     * 000-099:   Common
     * 100-199:   Uncommon
     * 200-299:   Rare
     * 300-399:   Epic
     * 400-499:   Legendary
     * <p>
     * X00-X02:   Level 0
     * X03-X08:   Level 1
     * X09-X23:   Level 2
     * X24-X48:   Level 3
     * X49-X98:   Level 4
     * X99-X99:   Level 5
     */
    public static Item of(ItemStack itemStack) {
        ItemStructure factionItem;
        int itemsStacked;

        int customModelData = itemStack.getItemMeta().getCustomModelData();
        ItemRarity rarity = ItemRarity.valueOf(customModelData / 100);

        factionItem = ItemStructure.of(itemStack, rarity);
        itemsStacked = customModelData % 100;
        return new Item(factionItem, itemsStacked, itemStack.getAmount(), itemStack.getType().getMaxDurability() - ((Damageable) itemStack.getItemMeta()).getDamage());
    }

    public static boolean isItem(ItemStack itemStack) {
        return itemStack.getItemMeta().hasCustomModelData();
    }

    public static ItemStack createItem(Material material, ItemRarity rarity, int itemsStacked){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(rarity.getId() * 100 + itemsStacked);
        itemStack.setItemMeta(meta);
        return of(itemStack).toItemStack();
    }

}
