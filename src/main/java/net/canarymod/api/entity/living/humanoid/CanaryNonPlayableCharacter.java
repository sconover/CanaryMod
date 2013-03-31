package net.canarymod.api.entity.living.humanoid;


import java.util.ArrayList;
import java.util.List;
import net.canarymod.api.CanaryPacket;
import net.canarymod.api.entity.living.CanaryEntityLiving;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.world.CanaryWorld;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.world.position.Position;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;


/**
 * NonPlayableCharacter implementation
 * 
 * @author Jason (darkdiplomat)
 */
public class CanaryNonPlayableCharacter extends CanaryEntityLiving implements NonPlayableCharacter {
    private final List<NPCBehavior> behaviors;

    /**
     * Constructs a new wrapper for EntityNonPlayableCharacter
     * 
     * @param entity
     *            the EntityVillager to wrap
     * @param inHand
     *            the Item to set inHand
     */
    public CanaryNonPlayableCharacter(EntityNonPlayableCharacter npc) {
        super(npc);
        this.getHandle().setNPC(this);
        this.behaviors = new ArrayList<NPCBehavior>();
    }

    /**
     * Constructs a new NonPlayableCharacter
     * 
     * @param name
     *            the Name to give to the NPC
     * @param location
     *            the Location to put the NPC
     * @param inHand
     *            the Item to set in the NPC's hand
     */
    public CanaryNonPlayableCharacter(String name, Location location) {
        this(new EntityNonPlayableCharacter(name, location));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getHandle().bS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        getHandle().bS = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void teleportTo(Position position) {
        getHandle().a(position.getX(), position.getY(), position.getZ(), getRotation(), getPitch());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void teleportTo(Location location) {
        getHandle().a(location.getX(), location.getY(), location.getZ(), location.getRotation(), location.getPitch());
        if (this.getWorld() != location.getWorld()) {
            // I don't know yet
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void teleportTo(int x, int y, int z) {
        getHandle().a(x, y, z, getRotation(), getPitch());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item getItemHeld() {
        return getHandle().getPlayerInventory().getSlot(getHandle().bK.c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItemInHandSlot(int slot) {
        if (slot > 0 && slot < 9) {
            getHandle().bK.c = slot;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inventory getInventory() {
        return getHandle().getPlayerInventory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ghost(Player player) {
        player.sendPacket(new CanaryPacket(new Packet29DestroyEntity(this.getID())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void haunt(Player player) {
        player.sendPacket(new CanaryPacket(new Packet20NamedEntitySpawn(this.getHandle())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lookAt(Player player) {
        double xDiff = player.getX() - getX();
        double yDiff = player.getY() - getY();
        double zDiff = player.getZ() - getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        double pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;

        if (zDiff < 0.0) {
            yaw = yaw + (Math.abs(180 - yaw) * 2);
        }
        teleportTo(new Location(this.getWorld(), getX(), getY(), getZ(), (float) yaw, (float) pitch));

        getHandle().bR = (float) yaw - 90; // Camera/Head Position
        getHandle().c(); // Update Entity
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lookAtNearest() {
        EntityPlayer ep = ((CanaryWorld) getWorld()).getHandle().a((net.minecraft.server.Entity) getHandle(), 25);
        if (ep != null && ((CanaryEntityLiving) ep.getCanaryEntity()).isPlayer()) {
            lookAt((Player) ep.getCanaryEntity());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NonPlayableCharacter despawn() {
        // TODO: Remove entity from trackers but do not destroy
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean spawn() {
        if (super.spawn()) {
            // TODO: Special spawning stuffs
        }
        return false;
    }

    @Override
    public void update() {
        synchronized (behaviors) {
            for (NPCBehavior behavior : behaviors) {
                behavior.onUpdate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityNonPlayableCharacter getHandle() {
        return (EntityNonPlayableCharacter) entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NPCBehavior> getBehaviors() {
        return this.behaviors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NPCBehavior removeBehavior(NPCBehavior behavior) {
        if (this.behaviors.contains(behavior)) {
            return this.behaviors.remove(behaviors.indexOf(behavior));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addBehavior(NPCBehavior behavior) {
        return this.behaviors.add(behavior);
    }

    @Override
    public String toString() {
        return String.format("NPC[id=%d, name=%s]", getID(), getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CanaryNonPlayableCharacter)) {
            return false;
        }
        return obj == this;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 89 * hash + this.getID();
        hash = 89 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        return hash;
    }
}