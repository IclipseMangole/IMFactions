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
    private int amount;
    private short durability;

    public FactionItemStack(FactionItem factionItem, int itemsStacked, int amount, short durability) {
        this.factionItem = factionItem;
        this.itemsStacked = itemsStacked;
        this.amount = amount;
        this.durability = durability;
    }

    public FactionItemStack(FactionItem factionItem, int itemsStacked, int amount) {
        this(factionItem, itemsStacked, amount, factionItem.getMaterial().getMaxDurability());
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

    public int getAmount() {
        return this.amount;
    }

    public short getDurability() {
        return this.durability;
    }

    public void setDurability(short durability) {
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
        return this.factionItem.getModifiers(getLevel());
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
        lores.add("§r§7§o" + this.factionItem.getSubtitle() + "§r");
        lores.addAll(getModifierLore());
        lores.add("§r" + getRarity().getColor() + "╔" + line);
        lores.add("§r" + getRarity().getColor() + "║" + ColorUtils.brighter(getRarity().getColor()) + "Level " + getLevel() + "§8 - (" + (this.itemsStacked + 1) + "|" + (nextLevelStage() + 1) + ")");
        lores.add("§r" + getRarity().getColor() + "║" + getProgressBar("§o§l■§r", 30, this.itemsStacked, nextLevelStage()));
        lores.add("§r" + getRarity().getColor() + "║§f" + (nextLevelStage() - this.itemsStacked) + " till next Level");
        lores.add("§r" + getRarity().getColor() + "╚" + line);
        double damage = getItemModifiers().containsKey(ItemModifierType.DAMAGE) ? ((Double) getItemModifiers().get(ItemModifierType.DAMAGE).value * 2.0D + ItemAttributes.getDamage(this.factionItem.getMaterial())) : ItemAttributes.getDamage(this.factionItem.getMaterial());
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? (Double) getItemModifiers().get(ItemModifierType.ATTACK_SPEED).value : ItemAttributes.getAttackSpeed(this.factionItem.getMaterial());
        DecimalFormat format = new DecimalFormat("0.0");
        lores.add("§r" + this.factionItem.getChatColor() + "╔" + line);
        lores.add("§r" + this.factionItem.getChatColor() + "║" + ColorUtils.brighter(this.factionItem.getChatColor()) + " Attack Damage:§7 " + (damage / 2.0D) + ChatColor.of(ColorUtils.toHex(153, 0, 0)) + "§l❤");
        lores.add("§r" + this.factionItem.getChatColor() + "║" + ColorUtils.brighter(this.factionItem.getChatColor()) + " Attack Speed:§7 " + format.format(attackSpeed) + " Hits per second");
        lores.add("§r" + this.factionItem.getChatColor() + "║" + ColorUtils.brighter(this.factionItem.getChatColor()) + " Durability:§7 " + getProgressBar("⚒", 15, this.durability, this.factionItem.getMaterial().getMaxDurability()) + " §7(" + this.durability + "|" + this.factionItem.getMaterial().getMaxDurability() + ")");
        lores.add("§r" + this.factionItem.getChatColor() + "╚" + line);
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
        ItemStack stack = (new ItemStackBuilder(this.factionItem.getMaterial())).withName("§r" + this.factionItem.getChatColor() + this.factionItem.getDisplayName()).withCustomModelData(this.factionItem.getRarity().getId() * 100 + this.itemsStacked).withAmount(this.amount).withItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE}).withLore(getLore()).withAttackSpeed(getAttackSpeed()).buildStack();
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
        double attackSpeed = getItemModifiers().containsKey(ItemModifierType.ATTACK_SPEED) ? ((Double) ((ItemModifierValue) getItemModifiers().get(ItemModifierType.ATTACK_SPEED)).value).doubleValue() : ItemAttributes.getAttackSpeed(this.factionItem.getMaterial());
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
     * X9-X23:   Level 2
     * X24-X48:   Level 3
     * X49-X98:   Level 4
     * X99-X99:   Level 5
     */
    public static FactionItemStack of(ItemStack itemStack) {
        int customModelData = itemStack.getItemMeta().getCustomModelData();
        de.imfactions.functions.items.ItemRarity rarity = de.imfactions.functions.items.ItemRarity.values()[customModelData / 100];
        FactionItem factionItem = FactionItem.of(itemStack, rarity);
        int itemsStacked = customModelData % 100;
        return new FactionItemStack(factionItem, itemsStacked, itemStack.getAmount(), (short) (itemStack.getType().getMaxDurability() - ((Damageable) itemStack.getItemMeta()).getDamage()));
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


