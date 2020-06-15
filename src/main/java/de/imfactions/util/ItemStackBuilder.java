package de.imfactions.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A simple builder class for the ItemStack
 * by <i>@ExpDev</i>
 * <p>
 * <b>Note:</b> Colors are parsed, so you can use & and § for the name and lore
 * <p>
 * <i>Uses <b>Java 8</b>, so go download it if you ain't already!</i>
 */
public class ItemStackBuilder {

    // Fundamentals
    private Material material = Material.AIR;
    private int amount = 1;
    private short durability = 0; // id -> 324:2 <- durability

    // Meta
    private String localizedName = null;
    private String name = null;
    private List<String> lore = null;

    // Features
    private boolean unbreakable = false;

    // Enchantments and flags
    private Map<Enchantment, Integer> enchantments = null;
    private Set<ItemFlag> itemFlags = null;

    private GameProfile profile;

    // Construction of a new builder
    public ItemStackBuilder() {
    }

    public ItemStackBuilder(Material material) {
        this.material = material;
    }

    public static ItemStackBuilder fromItemStack(ItemStack stack) {
        ItemStackBuilder builder = new ItemStackBuilder(stack.getType());
        builder.withAmount(stack.getAmount());
        builder.withData(((Damageable) stack.getItemMeta()).getDamage());
        builder.withLore(stack.getItemMeta().getLore());
        builder.withEnchantments(stack.getEnchantments());
        builder.withItemFlags(stack.getItemMeta().getItemFlags());
        if (stack.getType().equals(Material.PLAYER_HEAD)) {
            builder.withProfile(SkullUtils.getProfile(stack));
        }
        return builder;
    }

    // Utility methods for smooth and extensive color parsing
    private static String parseColor(String string) {
        string = parseColorAmp(string);
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static String parseColorAmp(String string) {
        string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
        string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
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

    // In case you are too lazy to cast (if you're using int)
    public ItemStackBuilder withData(int data) {
        return withData((short) data);
    }

    // Meta
    public ItemStackBuilder withLocalizedName(String localizedName) {
        this.localizedName = localizedName;
        return this;
    }

    public ItemStackBuilder withName(String name) {
        this.name = name;
        return this;
    }

    // Multiple ways you can set the lore
    // I prefer #withLore("&1Line 1", "&2Line 2", "&3Etc...")
    public ItemStackBuilder withLore(List<String> lines) {
        this.lore = lines;
        return this;
    }

    public ItemStackBuilder withLore(String... lines) {
        return withLore(Arrays.asList(lines));
    }

    // Just calls ItemMeta#setUnbreakable(true), don't know if compatible with old versions
    public ItemStackBuilder makeUnbreakable() {
        this.unbreakable = true;
        return this;
    }

    // Enchantments
    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        // Make sure we have something to add the enchantment to
        if (enchantments == null) {
            this.enchantments = new HashMap<Enchantment, Integer>();
        }

        enchantments.put(enchantment, level);
        return this;
    }

    // Flags
    public ItemStackBuilder withItemFlags(Set<ItemFlag> flags) {
        this.itemFlags = flags;
        return this;
    }

    // Can be used to add only 1 ItemFlag (#withItemFlags(ItemFlag.HIDE_ENCHANTMENTS))
    public ItemStackBuilder withItemFlags(ItemFlag... flags) {
        return withItemFlags(new HashSet<ItemFlag>(Arrays.asList(flags)));
    }

    public ItemStackBuilder withProfile(GameProfile profile) {
        this.profile = profile;
        return this;
    }

    public SkullBuilder toSkullBuilder() {
        return new SkullBuilder(this);
    }

    /**
     * Builds the ItemStack with durability from this instance
     *
     * @return ItemStack with meta
     */
    public ItemStack buildStack() {
        // Creating a new ItemStack
        ItemStack itemStack = new ItemStack(material, amount);

        // Getting the stack's meta
        final ItemMeta itemMeta = itemStack.getItemMeta();

        // Meta

        ((Damageable) itemMeta).setDamage(durability);

        // Set localized name if not null
        if (localizedName != null) {
            itemMeta.setLocalizedName(ItemStackBuilder.parseColor(localizedName));
        }
        // Set displayname if name is not null
        if (name != null) {
            itemMeta.setDisplayName(ItemStackBuilder.parseColor(name));
        }
        // Set lore if it is not null nor empty
        if (lore != null && !lore.isEmpty()) {
            itemMeta.setLore(lore.stream().map(ItemStackBuilder::parseColor).collect(Collectors.toList()));
        }
        // Add enchantments if any
        if (enchantments != null && !enchantments.isEmpty()) {
            // Doing this so I don't have to keep unsafe and safe enchantments separately
            // Ignore all stupid enchantment restrictions ;)
            enchantments.forEach((ench, lvl) -> itemMeta.addEnchant(ench, lvl, true));
        }
        // Add flags if any
        if (itemFlags != null && !itemFlags.isEmpty()) {
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[itemFlags.size()]));
        }
        // Deprecated in newer versions, but newer method does not exist in older
        // Only call when unbreakable is true, to refrain from calling as much as possible
        // You could of course always implement your own unbreakable method here
        if (unbreakable) itemMeta.setUnbreakable(true);

        // Set the new ItemMeta
        itemStack.setItemMeta(itemMeta);

        if (profile != null) {
            SkullUtils.setProfile(profile, itemStack);
        }

        // Lastly, return the stack
        return itemStack;
    }

    /**
     * A simple builder for a skull with owner
     * <p>
     * <b>Note:</b> Uses the ItemStackBuilder builder ;)
     */
    public class SkullBuilder {

        // Fundamentals
        private ItemStackBuilder stackBuilder;

        // Meta
        private String owner;

        private SkullBuilder(ItemStackBuilder stackBuilder) {
            this.stackBuilder = stackBuilder;
        }

        // Meta
        public SkullBuilder withOwner(String ownerName) {
            this.owner = ownerName;
            return this;
        }
    }

}
