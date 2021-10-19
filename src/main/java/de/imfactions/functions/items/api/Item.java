package de.imfactions.functions.items.api;

import de.imfactions.functions.items.ItemRarity;
import de.imfactions.functions.items.api.modifiers.ItemModifierType;
import de.imfactions.functions.items.api.modifiers.ItemModifierValue;
import de.imfactions.util.ColorUtils;
import de.imfactions.util.ItemAttributes;
import de.imfactions.util.ItemStackBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;


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
        return this.itemStructure;
    }

    public de.imfactions.functions.items.ItemRarity getRarity() {
        return this.itemStructure.getRarity();
    }

    public int getItemsStacked() {
        return this.itemsStacked;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getDurability() {
        return this.durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getLevel() {
        if (this.itemsStacked < 3)
            return 0;
        if (this.itemsStacked < 9)
            return 1;
        if (this.itemsStacked < 24)
            return 2;
        if (this.itemsStacked < 49)
            return 3;
        if (this.itemsStacked < 99) {
            return 4;
        }
        return 5;
    }


    public HashMap<ItemModifierType, ItemModifierValue> getItemModifiers() {
        return this.itemStructure.getModifiers(getLevel());
    }

    private ArrayList<String> getModifierLore() {
        ArrayList<String> list = new ArrayList<>();
        getItemModifiers().forEach((itemModifierType, itemModifierValue) -> list.add("§r" + itemModifierType.toString(itemModifierValue.value)));
        return list;
    }

    private ArrayList<String> getLore() {
        ArrayList<String> lores = new ArrayList<>();
        String line = "";
        for (int i = 0; i < 30; i++) {
            line = line + "═";
        }
        lores.add("§r§7§o" + this.itemStructure.getSubtitle() + "§r");
        lores.addAll(getModifierLore());
        lores.add("§r" + getRarity().getColor() + "╔" + line);
        lores.add("§r" + getRarity().getColor() + "║" + ColorUtils.brighter(getRarity().getColor()) + "Level " + getLevel() + "§8 - (" + (this.itemsStacked + 1) + "|" + (nextLevelStage() + 1) + ")");
        lores.add("§r" + getRarity().getColor() + "║" + getProgressBar("§o§l■§r", 30, this.itemsStacked, nextLevelStage()));
        lores.add("§r" + getRarity().getColor() + "║§f" + (nextLevelStage() - this.itemsStacked) + " till next Level");
        lores.add("§r" + getRarity().getColor() + "╚" + line);
        double damage = getItemModifiers().containsKey(ItemModifierType.DAMAGE) ? (((Double) ((ItemModifierValue) getItemModifiers().get(ItemModifierType.DAMAGE)).value).doubleValue() * 2.0D + ItemAttributes.getDamage(this.itemStructure.getMaterial())) : ItemAttributes.getDamage(this.itemStructure.getMaterial());
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? ((Double) ((ItemModifierValue) getItemModifiers().get(ItemModifierType.ATTACK_SPEED)).value).doubleValue() : ItemAttributes.getAttackSpeed(this.itemStructure.getMaterial());
        DecimalFormat format = new DecimalFormat("0.0");
        lores.add("§r" + this.itemStructure.getChatColor() + "╔" + line);
        lores.add("§r" + this.itemStructure.getChatColor() + "║" + ColorUtils.brighter(this.itemStructure.getChatColor()) + " Attack Damage:§7 " + (damage / 2.0D) + ChatColor.of(ColorUtils.toHex(153, 0, 0)) + "§l❤");
        lores.add("§r" + this.itemStructure.getChatColor() + "║" + ColorUtils.brighter(this.itemStructure.getChatColor()) + " Attack Speed:§7 " + format.format(attackSpeed) + " Hits per second");
        lores.add("§r" + this.itemStructure.getChatColor() + "║" + ColorUtils.brighter(this.itemStructure.getChatColor()) + " Durability:§7 " + getProgressBar("⚒", 15, this.durability, this.itemStructure.getMaterial().getMaxDurability()) + " §7(" + this.durability + "|" + this.itemStructure.getMaterial().getMaxDurability() + ")");
        lores.add("§r" + this.itemStructure.getChatColor() + "╚" + line);
        return lores;
    }

    public void updateLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(getLore());
        stack.setItemMeta(meta);
    }

    private String getProgressBar(String s, int amount, int value, int maxValue) {
        String bar = "";
        for (int i = 0; i < amount; i++) {
            if (i / amount < value / maxValue) {
                bar = bar + ChatColor.of("#2A8000") + s;
            } else {
                bar = bar + ChatColor.of("#E60026") + s;
            }
        }
        return bar;
    }


    public ItemStack toItemStack() {
        ItemStack stack = (new ItemStackBuilder(this.itemStructure.getMaterial())).withName("§r" + this.itemStructure.getChatColor() + this.itemStructure.getDisplayName()).withCustomModelData(this.itemStructure.getRarity().getId() * 100 + this.itemsStacked).withAmount(this.amount).withItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE}).withLore(getLore()).withAttackSpeed(getAttackSpeed()).buildStack();
        if (this.itemStructure.getRarity() == de.imfactions.functions.items.ItemRarity.LEGENDARY) {
            if (this.itemStructure.getMaterial() == Material.BOW) {
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
        float hsv = (float) (System.currentTimeMillis() % duration * 1000.0D / duration * 1000.0D);
        hsv = (float) (hsv + place * duration * 1000.0D / max / duration * 1000.0D);
        return ChatColor.of(ColorUtils.toHex(hsv, 1.0F, 1.0F));
    }

    private int nextLevelStage() {
        if (this.itemsStacked < 3)
            return 3;
        if (this.itemsStacked < 9)
            return 9;
        if (this.itemsStacked < 24)
            return 24;
        if (this.itemsStacked < 49) {
            return 49;
        }
        return 99;
    }


    public double getAttackSpeed() {
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? ((Double) ((ItemModifierValue) getItemModifiers().get(ItemModifierType.ATTACK_SPEED)).value).doubleValue() : ItemAttributes.getAttackSpeed(this.itemStructure.getMaterial());
        return attackSpeed;
    }


    public static Item of(ItemStack itemStack) {
        int customModelData = itemStack.getItemMeta().getCustomModelData();
        de.imfactions.functions.items.ItemRarity rarity = de.imfactions.functions.items.ItemRarity.values()[customModelData / 100];
        ItemStructure factionItem = ItemStructure.of(itemStack, rarity);
        int itemsStacked = customModelData % 100;
        return new Item(factionItem, itemsStacked, itemStack.getAmount(), itemStack.getType().getMaxDurability() - ((Damageable) itemStack.getItemMeta()).getDamage());
    }

    public static boolean isItem(ItemStack itemStack) {
        return itemStack.getItemMeta().hasCustomModelData();
    }

    public static ItemStack createItem(Material material, ItemRarity rarity, int itemsStacked) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(Integer.valueOf(rarity.getId() * 100 + itemsStacked));
        itemStack.setItemMeta(meta);
        return of(itemStack).toItemStack();
    }
}


