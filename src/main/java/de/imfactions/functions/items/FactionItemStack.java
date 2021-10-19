package de.imfactions.functions.items;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

 
 
 
 
 
 
 
 public class FactionItemStack
 {
   private FactionItem factionItem;
   private int itemsStacked;
   private int amount;
   
   public FactionItemStack(FactionItem factionItem, int itemsStacked, int amount) {
       this.factionItem = factionItem;
       this.itemsStacked = itemsStacked;
   }
   
   public FactionItem getFactionItem() {
       return this.factionItem;
   }
   
   public ItemRarity getRarity() {
       return this.factionItem.getRarity();
   }
   
   public int getItemsStacked() {
       return this.itemsStacked;
   }
   
   public int getAmount() {
       return this.amount;
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
 
   
   public ArrayList<ItemModifier> getItemModifiers() {
       return this.factionItem.getModifiers(getLevel());
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public static FactionItemStack of(ItemStack itemStack) {
       int customModelData = itemStack.getItemMeta().getCustomModelData();
       ItemRarity rarity = ItemRarity.values()[customModelData / 100];
       FactionItem factionItem = FactionItem.of(itemStack, rarity);
       int itemsStacked = customModelData % 100;
       return new FactionItemStack(factionItem, itemsStacked, itemStack.getAmount());
   }
 }


