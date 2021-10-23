package de.imfactions.functions.items.drops;

import de.imfactions.functions.items.FactionItem;
import de.imfactions.functions.items.FactionItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MobDrops {

    private final ItemStack obsidian = new ItemStack(Material.OBSIDIAN);
    private final ItemStack oakLog = new ItemStack(Material.OAK_LOG);
    private final ItemStack ironIngot = new ItemStack(Material.IRON_INGOT);
    private final ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT);
    private final ItemStack diamond = new ItemStack(Material.DIAMOND);
    private final ItemStack emerald = new ItemStack(Material.EMERALD);
    private final ItemStack bread = new ItemStack(Material.BREAD);
    private final ItemStack bone = new ItemStack(Material.BONE);
    private final ItemStack carrot = new ItemStack(Material.CARROT);

    private final ItemStack stoneClub = new FactionItemStack(FactionItem.get("Stone Club"), 1).toItemStack();
    private final ItemStack headCutter = new FactionItemStack(FactionItem.get("Head Cutter"), 1).toItemStack();
    private final ItemStack faramirsBow = new FactionItemStack(FactionItem.get("Faramir's Bow"), 1).toItemStack();
    private final ItemStack excalibur = new FactionItemStack(FactionItem.get("Excalibur"), 1).toItemStack();
    private final ItemStack yewArch = new FactionItemStack(FactionItem.get("Yew Arch"), 1).toItemStack();

    private final ArrayList<ItemStack> commonDrops;
    private final ArrayList<ItemStack> uncommonDrops;
    private final ArrayList<ItemStack> rareDrops;
    private final ArrayList<ItemStack> epicDrops;
    private final ArrayList<ItemStack> legendaryDrops;

    public MobDrops() {
        commonDrops = new ArrayList<>();
        uncommonDrops = new ArrayList<>();
        rareDrops = new ArrayList<>();
        epicDrops = new ArrayList<>();
        legendaryDrops = new ArrayList<>();

        setCommonDrops();
        setUncommonDrops();
    }

    private void setCommonDrops() {
        oakLog.setAmount(3);
        commonDrops.add(oakLog);
        commonDrops.add(bread);
        commonDrops.add(bone);
        commonDrops.add(carrot);
        commonDrops.add(stoneClub);
        commonDrops.add(headCutter);
        commonDrops.add(faramirsBow);
    }

    private void setUncommonDrops() {
        obsidian.setAmount(3);
        oakLog.setAmount(5);
        bread.setAmount(2);
        bone.setAmount(3);
        carrot.setAmount(3);
        uncommonDrops.add(obsidian);
        uncommonDrops.add(oakLog);
        uncommonDrops.add(bread);
        uncommonDrops.add(carrot);
        uncommonDrops.add(bone);
        uncommonDrops.add(stoneClub);
        uncommonDrops.add(headCutter);
        uncommonDrops.add(faramirsBow);
    }

    private void setRareDrops() {

    }

    private void setEpicDrops() {

    }

    private void setLegendaryDrops() {

    }

    public ArrayList<ItemStack> getCommonDrops() {
        return commonDrops;
    }

    public ArrayList<ItemStack> getUncommonDrops() {
        return uncommonDrops;
    }

    public ArrayList<ItemStack> getRareDrops() {
        return rareDrops;
    }

    public ArrayList<ItemStack> getLegendaryDrops() {
        return legendaryDrops;
    }

    public ArrayList<ItemStack> getEpicDrops() {
        return epicDrops;
    }
}
