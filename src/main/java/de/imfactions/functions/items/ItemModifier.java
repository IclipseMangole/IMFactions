package de.imfactions.functions.items;


public class ItemModifier {
    private ItemModifierType itemModifierType;
    private ItemModifierValue itemModifierValue;

    public ItemModifier(ItemModifierType itemModifierType, ItemModifierValue itemModifierValue) {
        this.itemModifierType = itemModifierType;
        this.itemModifierValue = itemModifierValue;
    }

    public ItemModifierType getItemModifierType() {
        return this.itemModifierType;
    }

    public void setItemModifierType(ItemModifierType itemModifierType) {
        this.itemModifierType = itemModifierType;
    }

    public ItemModifierValue getItemModifierValue() {
        return this.itemModifierValue;
    }

    public void setItemModifierValue(ItemModifierValue itemModifierValue) {
        this.itemModifierValue = itemModifierValue;
    }
}


