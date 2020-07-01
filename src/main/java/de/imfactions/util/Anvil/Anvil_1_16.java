package de.imfactions.util.Anvil;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Anvil_1_16 {
    private int getRealNextContainerId(Player player) {
        return ((CraftPlayer) player).getHandle().nextContainerCounter();
    }

    /**
     * {@inheritDoc}
     */

    public int getNextContainerId(Player player, Object container) {
        return ((AnvilContainer) container).getContainerId();
    }

    /**
     * {@inheritDoc}
     */

    public void handleInventoryCloseEvent(Player player) {
        CraftEventFactory.handleInventoryCloseEvent(((CraftPlayer) player).getHandle());
    }

    /**
     * {@inheritDoc}
     */

    public void sendPacketOpenWindow(Player player, int containerId, String guiTitle) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, new ChatMessage(guiTitle)));
    }

    /**
     * {@inheritDoc}
     */

    public void sendPacketCloseWindow(Player player, int containerId) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutCloseWindow(containerId));
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainerDefault(Player player) {
        ((CraftPlayer) player).getHandle().activeContainer = ((CraftPlayer) player).getHandle().defaultContainer;
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainer(Player player, Object container) {
        ((CraftPlayer) player).getHandle().activeContainer = (Container) container;
    }

    /**
     * {@inheritDoc}
     */

    public void setActiveContainerId(Object container, int containerId) {
        //noop
    }

    /**
     * {@inheritDoc}
     */

    public void addActiveContainerSlotListener(Object container, Player player) {
        ((Container) container).addSlotListener(((CraftPlayer) player).getHandle());
    }

    /**
     * {@inheritDoc}
     */

    public Inventory toBukkitInventory(Object container) {
        return ((Container) container).getBukkitView().getTopInventory();
    }

    /**
     * {@inheritDoc}
     */

    public Object newContainerAnvil(Player player, String guiTitle) {
        return new AnvilContainer(player, guiTitle);
    }

    /**
     * Turns a {@link Player} into an NMS one
     *
     * @param player The player to be converted
     * @return the NMS EntityPlayer
     */
    private EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    /**
     * Modifications to ContainerAnvil that makes it so you don't have to have xp to use this anvil
     */
    private class AnvilContainer extends ContainerAnvil {

        public AnvilContainer(Player player, String guiTitle) {
            super(getRealNextContainerId(player), ((CraftPlayer) player).getHandle().inventory,
                    ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
            this.checkReachable = false;
            setTitle(new ChatMessage(guiTitle));
        }


        public void e() {
            super.e();
            this.levelCost.set(0);
        }

        public int getContainerId() {
            return windowId;
        }

    }
}
