package de.imfactions.util.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuItem {
    private PopupMenu menu;
    private int number;
    private ItemStack icon;
    private String text;
    private String lore = "";

    /**
     * Create a new menu item with the given title text on mouse over
     * <p>
     * Icon defaults to a piece of paper, and no number is displayed.
     *
     * @param text The title text to display on mouse over
     */
    public MenuItem(String text) {
        this(text, new ItemStack(Material.PAPER));
    }

    public MenuItem(ItemStack item) {
        this(item.getItemMeta().getDisplayName(), item, item.getAmount());
    }

    public MenuItem(ItemStack item, int amount) {
        this(item.getItemMeta().getDisplayName(), item, amount);
    }

    /**
     * Create a new menu item with the given title text on mouse over, and using
     * the given MaterialData as its icon
     *
     * @param text The title text to display on mouse over
     * @param icon The material to use as its icon
     */
    public MenuItem(String text, ItemStack icon) {
        this(text, icon, 1);
    }

    public MenuItem(String text, ItemStack icon, String description) {
        this(text, icon);
        setLore(description);
    }

    /**
     * Create a new menu item with the given title text on mouseover, using the
     * given MaterialData as its icon, and displaying the given number
     *
     * @param text   The title text to display on mouse over
     * @param icon   The material to use as its icon
     * @param number The number to display on the item (must be greater than 1)
     */
    public MenuItem(String text, ItemStack icon, int number) {
        this.text = text;
        this.icon = icon;
        this.number = number;
    }

    protected void addToMenu(PopupMenu menu) {
        this.menu = menu;
    }

    protected void removeFromMenu(PopupMenu menu) {
        if (this.menu == menu) {
            this.menu = null;
        }
    }

    /**
     * Get the menu on which this item resides
     *
     * @return The popup menu
     */
    public PopupMenu getMenu() {
        return menu;
    }

    /**
     * Get the number displayed on this item. 1 for no number displayed
     *
     * @return The number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the MaterialData used as the icon for this menu item
     *
     * @return The icon
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Get the title text used as the mouse over text for this menu item
     *
     * @return The title text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the lore for a item
     *
     * @param lore Text
     */
    public void setLore(String lore) {
        this.lore = lore;
    }

    /**
     * Gets the lore from a item
     *
     * @return Text
     */
    public String getLore() {
        if (lore != "") return lore;
        else return null;
    }

    protected ItemStack getItemStack() {
        ItemStack slot = getIcon();
        slot.setAmount(getNumber());
        ItemMeta meta = slot.getItemMeta();

        if (getLore() != null) {
            List<String> loreList = new ArrayList<>();
            loreList.add(lore);
            meta.setLore(loreList);
        }

        meta.setDisplayName(getText());

        slot.setItemMeta(meta);
        return slot;
    }

    /**
     * Called when a player clicks this menu item
     *
     * @param player The clicking player
     */
    public abstract void onClick(Player player);
}
