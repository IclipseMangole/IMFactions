package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

/**
 * Created by Iclipse on 20.06.2020
 */
public class ItemModifier {
    private ItemModifierType itemModifierType;
    private double itemModifierValue;

    public ItemModifier(ItemModifierType itemModifierType, double itemModifierValue) {
        this.itemModifierType = itemModifierType;
        this.itemModifierValue = itemModifierValue;
    }

    public ItemModifierType getItemModifierType() {
        return itemModifierType;
    }

    public void setItemModifierType(ItemModifierType itemModifierType) {
        this.itemModifierType = itemModifierType;
    }

    public double getItemModifierValue() {
        return itemModifierValue;
    }

    public void setItemModifierValue(double itemModifierValue) {
        this.itemModifierValue = itemModifierValue;
    }
}
