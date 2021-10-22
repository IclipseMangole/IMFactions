//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package de.imfactions.util;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackBuilder {
    private Material material;
    private int amount;
    private short durability;
    private String localizedName;
    private String name;
    private List<String> lore;
    private int customModelData;
    private double attackSpeed;
    private double damage;
    private boolean unbreakable;
    private Map<Enchantment, Integer> enchantments;
    private Set<ItemFlag> itemFlags;
    private GameProfile profile;

    public ItemStackBuilder() {
        this.material = Material.AIR;
        this.amount = 1;
        this.durability = 0;
        this.localizedName = null;
        this.name = null;
        this.lore = null;
        this.customModelData = 0;
        this.attackSpeed = -1.0D;
        this.damage = -1.0D;
        this.unbreakable = false;
        this.enchantments = null;
        this.itemFlags = null;
    }

    public ItemStackBuilder(Material material) {
        this.material = Material.AIR;
        this.amount = 1;
        this.durability = 0;
        this.localizedName = null;
        this.name = null;
        this.lore = null;
        this.customModelData = 0;
        this.attackSpeed = -1.0D;
        this.damage = -1.0D;
        this.unbreakable = false;
        this.enchantments = null;
        this.itemFlags = null;
        this.material = material;
    }

    public static ItemStackBuilder fromItemStack(ItemStack stack) {
        ItemStackBuilder builder = new ItemStackBuilder(stack.getType());
        builder.withAmount(stack.getAmount());
        builder.withData(((Damageable)stack.getItemMeta()).getDamage());
        builder.withLore(stack.getItemMeta().getLore());
        builder.withCustomModelData(stack.getItemMeta().getCustomModelData());
        builder.withEnchantments(stack.getEnchantments());
        builder.withItemFlags(stack.getItemMeta().getItemFlags());
        if (stack.getType().equals(Material.PLAYER_HEAD)) {
            builder.withProfile(SkullUtils.getProfile(stack));
        }

        return builder;
    }

    private static String parseColor(String string) {
        string = parseColorAmp(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static String parseColorAmp(String string) {
        string = string.replaceAll("(§([a-z0-9]))", "§$2");
        string = string.replaceAll("(&([a-z0-9]))", "§$2");
        string = string.replace("&&", "&");
        return string;
    }

    public ItemStackBuilder asMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStackBuilder withData(short data) {
        this.durability = data;
        return this;
    }

    public ItemStackBuilder withData(int data) {
        return this.withData((short)data);
    }

    public ItemStackBuilder withLocalizedName(String localizedName) {
        this.localizedName = localizedName;
        return this;
    }

    public ItemStackBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemStackBuilder withLore(List<String> lines) {
        this.lore = lines;
        return this;
    }

    public ItemStackBuilder withLore(String... lines) {
        return this.withLore(Arrays.asList(lines));
    }

    public ItemStackBuilder withCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemStackBuilder withAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public ItemStackBuilder withDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public ItemStackBuilder makeUnbreakable() {
        this.unbreakable = true;
        return this;
    }

    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap();
        }

        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemStackBuilder withItemFlags(Set<ItemFlag> flags) {
        this.itemFlags = flags;
        return this;
    }

    public ItemStackBuilder withItemFlags(ItemFlag... flags) {
        return this.withItemFlags((Set)(new HashSet(Arrays.asList(flags))));
    }

    public ItemStackBuilder withProfile(GameProfile profile) {
        this.profile = profile;
        return this;
    }

    public ItemStackBuilder.SkullBuilder toSkullBuilder() {
        return new ItemStackBuilder.SkullBuilder(this);
    }

    public ItemStack buildStack() {
        ItemStack itemStack = new ItemStack(this.material, this.amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        ((Damageable)itemMeta).setDamage(this.durability);
        if (this.localizedName != null) {
            itemMeta.setLocalizedName(parseColor(this.localizedName));
        }

        if (this.name != null) {
            itemMeta.setDisplayName(parseColor(this.name));
        }

        if (this.lore != null && !this.lore.isEmpty()) {
            itemMeta.setLore((List)this.lore.stream().map(ItemStackBuilder::parseColor).collect(Collectors.toList()));
        }

        if (this.customModelData != 0) {
            itemMeta.setCustomModelData(this.customModelData);
        }

        if (this.enchantments != null && !this.enchantments.isEmpty()) {
            this.enchantments.forEach((ench, lvl) -> {
                itemMeta.addEnchant(ench, lvl, true);
            });
        }

        if (this.itemFlags != null && !this.itemFlags.isEmpty()) {
            itemMeta.addItemFlags((ItemFlag[])this.itemFlags.toArray(new ItemFlag[this.itemFlags.size()]));
        }

        if(attackSpeed != -1.0){
            AttributeModifier modifier = new AttributeModifier("generic.attack_speed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
        }



        if(damage != -1.0){
            AttributeModifier modifier = new AttributeModifier("generic.attack_damage", damage - 1, AttributeModifier.Operation.ADD_NUMBER);
            itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
            itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        }


        if (this.unbreakable) {
            itemMeta.setUnbreakable(true);
        }

        itemStack.setItemMeta(itemMeta);
        if (this.profile != null) {
            SkullUtils.setProfile(this.profile, itemStack);
        }

        return itemStack;
    }

    public class SkullBuilder {
        private ItemStackBuilder stackBuilder;
        private String owner;

        private SkullBuilder(ItemStackBuilder stackBuilder) {
            this.stackBuilder = stackBuilder;
        }

        public ItemStackBuilder.SkullBuilder withOwner(String ownerName) {
            this.owner = ownerName;
            return this;
        }
    }
}
