package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Iclipse on 20.06.2020
 */
public class FactionItemStack {

    private FactionItem factionItem;
    private int itemsStacked;
    private int amount;

    public FactionItemStack(FactionItem factionItem, int itemsStacked, int amount) {
        this.factionItem = factionItem;
        this.itemsStacked = itemsStacked;
    }

    public FactionItem getFactionItem() {
        return factionItem;
    }

    public ItemRarity getRarity() {
        return factionItem.getRarity();
    }

    public int getItemsStacked() {
        return itemsStacked;
    }

    public int getAmount() {
        return amount;
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

    public ArrayList<ItemModifier> getItemModifiers() {
        return factionItem.getModifiers(getLevel());
    }


    /**
     * 001-099:   Common
     * 101-199:   Uncommon
     * 201-299:   Rare
     * 301-399:   Epic
     * 401-499:   Legendary
     * <p>
     * X00-X02:   Level 0
     * X03-X08:   Level 1
     * X9-X23:   Level 2
     * X24-X48:   Level 3
     * X49-X98:   Level 4
     * X99-X99:   Level 5
     */
    public static FactionItemStack of(ItemStack itemStack) {
        FactionItem factionItem;
        int itemsStacked;

        int customModelData = itemStack.getItemMeta().getCustomModelData();
        ItemRarity rarity = ItemRarity.valueOf(customModelData / 100);

        factionItem = FactionItem.of(itemStack, rarity);
        itemsStacked = customModelData % 100;

        return new FactionItemStack(factionItem, itemsStacked, itemStack.getAmount());

    }
}
