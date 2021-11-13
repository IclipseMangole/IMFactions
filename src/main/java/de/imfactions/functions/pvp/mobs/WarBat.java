package de.imfactions.functions.pvp.mobs;

import de.imfactions.functions.pvp.PVPZone;
import de.imfactions.functions.pvp.mobs.attributes.pathfinders.PathfinderGoalBatAttack;
import de.imfactions.functions.pvp.mobs.custommob.CustomMobAmbient;
import de.imfactions.util.reflection.ReflectionException;
import de.imfactions.util.reflection.ReflectionUtil;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowEntity;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class WarBat extends CustomMobAmbient {

    private final EntityBat bat;
    PathfinderTargetCondition bT;

    public WarBat(Location location, PVPZone pvpZone) {
        super(new EntityBat(EntityTypes.f, ((CraftWorld) location.getWorld()).getHandle()), location, ChatColor.DARK_GRAY + "War Bat");
        bat = (EntityBat) entityAmbient;
        try {
            bT = (PathfinderTargetCondition) ReflectionUtil.getObject(bat, "bT");
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        level = customMobLevel.getZoneLevel(pvpZone);
        legendary = customMobLevel.getRandomLegendary();
        setAttributes();
        setDrops();
        initPathfinder();
        this.setName(getHealth());
        net.minecraft.world.level.World worldServer = ((CraftWorld) location.getWorld()).getHandle();
        worldServer.addEntity(bat);
    }

    private void setDrops() {

    }

    private void setAttributes() {
        customAttributes.setMaxHealth(level);
        if (legendary)
            customAttributes.setMaxHealth(getMaxHealth() + 1);
        customAttributes.setFollowRange(20.0);
    }

    private void initPathfinder() {
        bT.d();
        bT.e();
        bat.bP.a();
        bat.bQ.a();
        bat.bP.a(0, new PathfinderGoalBatAttack(bat, 2));
        bat.bP.a(0, new PathfinderGoalFollowEntity(bat, 1.5D, 10.0F, 10.0F));
        bat.bQ.a(0, new PathfinderGoalNearestAttackableTarget<>(bat, EntityHuman.class, true));
        bat.bQ.a(0, new PathfinderGoalNearestAttackableTarget<>(bat, EntityPlayer.class, true));
    }
}
