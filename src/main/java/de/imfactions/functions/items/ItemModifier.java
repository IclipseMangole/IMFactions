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
    private ItemModifierValue itemModifierValue;

    public ItemModifier(ItemModifierType itemModifierType, ItemModifierValue itemModifierValue) {
        this.itemModifierType = itemModifierType;
        this.itemModifierValue = itemModifierValue;
    }

    public ItemModifierType getItemModifierType() {
        return itemModifierType;
    }

    public void setItemModifierType(ItemModifierType itemModifierType) {
        this.itemModifierType = itemModifierType;
    }

    public ItemModifierValue getItemModifierValue() {
        return itemModifierValue;
    }

    public void setItemModifierValue(ItemModifierValue itemModifierValue) {
        this.itemModifierValue = itemModifierValue;
    }
}
