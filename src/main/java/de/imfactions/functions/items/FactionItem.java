package de.imfactions.functions.items;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

 
 
 
 
 
 
 public class FactionItem
 {
    private static ArrayList<FactionItem> factionItems = new ArrayList<>();
   
   private String displayName;
   private String subtitle;
   private Material material;
   private ItemRarity rarity;
   private HashMap<Integer, ArrayList<ItemModifier>> modifiersPerLevel;
   private String description;
   
   public FactionItem(String displayName, String subtitle, Material material, ItemRarity rarity, HashMap<Integer, ArrayList<ItemModifier>> modifiersPerLevel, String description) {
      this.displayName = displayName;
      this.subtitle = subtitle;
      this.material = material;
      this.rarity = rarity;
      this.modifiersPerLevel = modifiersPerLevel;
      this.description = description;
      factionItems.add(this);
   }
   
   public String getDisplayName() {
      return this.displayName;
   }
   
   public String getSubtitle() {
      return this.subtitle;
   }
   
   public Material getMaterial() {
      return this.material;
   }
   
   public ItemRarity getRarity() {
      return this.rarity;
   }
   
   public HashMap<Integer, ArrayList<ItemModifier>> getModifiersPerLevel() {
      return this.modifiersPerLevel;
   }
   
   public ArrayList<ItemModifier> getModifiers(int level) {
      return this.modifiersPerLevel.get(Integer.valueOf(level));
   }
   
   public String getDescription() {
      return this.description;
   }
   
   public static ArrayList<FactionItem> getFactionItems() {
      return factionItems;
   }
   
   public static FactionItem of(ItemStack itemStack, ItemRarity rarity) {
      for (FactionItem entry : factionItems) {
         if (entry.getMaterial().equals(itemStack.getType()) && entry.getRarity() == rarity) {
            return entry;
         }
      }
      return null;
   }
 }


