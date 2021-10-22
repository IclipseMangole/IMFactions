package de.imfactions.functions.items;

import de.imfactions.functions.items.modifiers.ItemModifierType;
import de.imfactions.functions.items.modifiers.ItemModifierValue;
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


public class FactionItemStack {
    private FactionItem factionItem;
    private int itemsStacked;
    private short durability;

    public FactionItemStack(FactionItem factionItem, int itemsStacked, short durability) {
        this.factionItem = factionItem;
        this.itemsStacked = itemsStacked;
        this.durability = durability;
    }

    public FactionItemStack(FactionItem factionItem, int itemsStacked) {
        this(factionItem, itemsStacked, factionItem.getMaterial().getMaxDurability());
    }

    public FactionItem getFactionItem() {
        return this.factionItem;
    }

    public de.imfactions.functions.items.ItemRarity getRarity() {
        return this.factionItem.getRarity();
    }

    public int getItemsStacked() {
        return this.itemsStacked;
    }

    public short getDurability() {
        return this.durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public int getLevel() {
        if (this.itemsStacked < 2)
            return 1;
        if (this.itemsStacked < 4)
            return 2;
        if (this.itemsStacked < 8)
            return 3;
        if (this.itemsStacked < 16)
            return 4;
        return 5;
    }


    public HashMap<ItemModifierType, ItemModifierValue> getItemModifiers() {
        return this.factionItem.getModifiers(getLevel());
    }

    private ArrayList<String> getModifierLore() {
        ArrayList<String> list = new ArrayList<>();
        getItemModifiers().forEach((itemModifierType, itemModifierValue) -> list.add("§r" + itemModifierType.toString(itemModifierValue.value)));
        return list;
    }

    private ArrayList<String> getLore() {
        ArrayList<String> lores = new ArrayList<>();

        ChatColor frameColor = getRarity().getColor();
        ChatColor textColor = ColorUtils.brighter(frameColor);
        String line = "";
        for (int i = 0; i < 30; i++) {
            line = line + "═";
        }
        lores.add("§r§7§o\"" + this.factionItem.getSubtitle() + "\"§r");
        lores.addAll(getModifierLore());
        lores.add("§r" + frameColor + "╔" + line);
        lores.add("§r" + frameColor + "║" + textColor + "Level " + getLevel() + "§8 - (" + this.itemsStacked + "|" + nextLevelStage() + ")");
        lores.add("§r" + frameColor + "║" + getLevelBar("§o§l■§r", 32, this.itemsStacked - lastLevelStage(), nextLevelStage() - lastLevelStage()));
        lores.add("§r" + frameColor + "║ §7" + (nextLevelStage() - this.itemsStacked) + " till next Level");
        lores.add("§r" + frameColor + "╚" + line);

        double damage = getAttackDamage();
        double attackSpeed = getAttackSpeed();
        DecimalFormat format = new DecimalFormat("0.0");
        lores.add("§r" + frameColor + "╔" + line);
        lores.add("§r" + frameColor + "║" + textColor + " Attack Damage:§7 " + (damage / 2.0D) + ChatColor.of(ColorUtils.toHex(153, 0, 0)) + "§l❤");
        lores.add("§r" + frameColor + "║" + textColor + " Attack Speed:§7 " + format.format(attackSpeed) + " Hits per second");
        lores.add("§r" + frameColor + "║" + textColor + " Durability:§7 " + getProgressBar("⚒", 15, this.durability, this.factionItem.getMaterial().getMaxDurability()) + " §7(" + this.durability + "|" + this.factionItem.getMaterial().getMaxDurability() + ")");
        lores.add("§r" + frameColor + "╚" + line);
        return lores;
    }

    public void updateLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(getLore());
        stack.setItemMeta(meta);
    }

    private String getLevelBar(String s, int amount, int value, int maxValue) {
        if(value == maxValue){
            String bar = "";
            for(int i = 0; i < amount; i++) {
                bar = bar + ChatColor.of("#a88f00") + s;
            }
            return bar;
        }
        return getProgressBar(s, amount, value, maxValue);
    }

    private String getProgressBar(String s, int amount, int value, int maxValue) {
        String bar = "";
        for (double i = 0.0; i < amount; i++) {
            if (i / (double) amount < ((double) value / (double) maxValue)) {
                bar = bar + ChatColor.of("#2A8000") + s;
            } else {
                bar = bar + ChatColor.of("#E60026") + s;
            }
        }
        return bar;
    }


    public ItemStack toItemStack() {
        ItemStack stack = (new ItemStackBuilder(this.factionItem.getMaterial())).withName(factionItem.getDisplayName()).withCustomModelData(this.factionItem.getRarity().getId() * 100 + this.itemsStacked).withItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE}).withLore(getLore()).withAttackSpeed(getAttackSpeed()).withDamage(getAttackDamage()).buildStack();
        if (this.factionItem.getRarity() == de.imfactions.functions.items.ItemRarity.LEGENDARY) {
            if (this.factionItem.getMaterial() == Material.BOW) {
                stack.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
            } else {
                stack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            }
        }
        return stack;
    }

    private int nextLevelStage() {
        if (this.itemsStacked < 2)
            return 2;
        if (this.itemsStacked < 4)
            return 4;
        if (this.itemsStacked < 8)
            return 8;
        return 16;
    }

    private int lastLevelStage() {
        if (this.itemsStacked >= 16)
            return 16;
        if (itemsStacked >= 8)
            return 8;
        if (itemsStacked >= 4)
            return 4;
        if (itemsStacked >= 2)
            return 2;
        return 1;
    }

    public double getAttackDamage() {
        return getItemModifiers().containsKey(ItemModifierType.DAMAGE) ? (Double) getItemModifiers().get(ItemModifierType.DAMAGE).value : ItemAttributes.getDamage(this.factionItem.getMaterial());
    }

    public double getAttackSpeed() {
        return getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? (Double) getItemModifiers().get(ItemModifierType.ATTACK_SPEED).value : ItemAttributes.getAttackSpeed(this.factionItem.getMaterial());
    }


    /**
     * 000-099:   Common
     * 100-199:   Uncommon
     * 200-299:   Rare
     * 300-399:   Epic
     * 400-499:   Legendary
     * <p>
     * X01:       Level 1
     * X02-X03:   Level 2
     * X04-X07:   Level 3
     * X08-X15:   Level 4
     * X16-X16:   Level 5
     */
    public static FactionItemStack of(ItemStack itemStack) {
        int customModelData = itemStack.getItemMeta().getCustomModelData();
        de.imfactions.functions.items.ItemRarity rarity = de.imfactions.functions.items.ItemRarity.values()[customModelData / 100];
        FactionItem factionItem = FactionItem.of(itemStack, rarity);
        int itemsStacked = customModelData % 100;
        return new FactionItemStack(factionItem, itemsStacked, (short) (itemStack.getType().getMaxDurability() - ((Damageable) itemStack.getItemMeta()).getDamage()));
    }

    public static boolean isItem(ItemStack itemStack) {
        return itemStack.getItemMeta().hasCustomModelData();
    }

}


