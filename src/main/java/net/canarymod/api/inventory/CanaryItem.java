package net.canarymod.api.inventory;


import net.canarymod.api.CanaryEnchantment;
import net.canarymod.api.Enchantment;
import net.canarymod.api.nbt.CanaryCompoundTag;
import net.canarymod.api.nbt.CanaryListTag;
import net.canarymod.api.nbt.CanaryStringTag;
import net.canarymod.api.nbt.CompoundTag;
import net.canarymod.api.nbt.ListTag;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

/**
 * Item wrapper implementation
 * 
 * @author Jason (darkdiplomat)
 */
public class CanaryItem implements Item {

    private ItemType type;
    private int slot = -1;
    private ItemStack item;

    /**
     * Constructs a new CanaryItem
     * 
     * @param oItemStack
     */
    public CanaryItem(ItemStack oItemStack) {
        type = ItemType.fromId(oItemStack.c);
        item = oItemStack;
    }

    public CanaryItem(int id, int amount) {
        this.type = ItemType.fromId(id);
        item = new ItemStack(id, amount, 0);
    }

    public CanaryItem(int itemId, int amount, int targetSlot) {
        item = new ItemStack(itemId, amount, 0);
        slot = targetSlot;
        type = ItemType.fromId(itemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return type.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(int id) {
        type = ItemType.fromId(id);
        item.c = type.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDamage() {
        return item.k();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmount() {
        return item.a;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSlot() {
        return slot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAmount(int amount) {
        item.a = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDamage(int damage) {
        item.b(damage);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxAmount() {
        return item.getBaseItem().getMaxStackSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxAmount(int amount) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDisplayName() {
        return item.t();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        return item.s();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayName(String name) {
        item.c(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeDisplayName() {
        getDataTag().getCompoundTag("display").remove("Name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRepairCost() {
        return item.B();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRepairCost(int cost) {
        item.c(cost);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String[] getLore() {
        if (!hasLore()) {
            return null;
        }
        ListTag lore = getDataTag().getCompoundTag("display").getListTag("Lore");
        String[] rt = new String[lore.size()];
        for (int index = 0; index < rt.length; index++) {
            rt[index] = ((CanaryStringTag) lore.get(index)).getValue();
        }
        return rt;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setLore(String... lore) {
        CompoundTag tag = getDataTag();
        if (tag == null) {
            tag = new CanaryCompoundTag("tag");
            setDataTag(tag);
        }
        if (!tag.containsKey("display")) {
            tag.put("display", new CanaryCompoundTag("display"));
        }
        CanaryListTag list = new CanaryListTag("");
        for (String line : lore) {
            list.add(new CanaryStringTag("", line));
        }
        tag.getCompoundTag("display").put("Lore", list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasLore() {
        return item.p() && getDataTag().containsKey("display");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnchanted() {
        return item.x();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment() {
        return getEnchantment(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enchantment getEnchantment(int index) {
        if (isEnchanted()) {
            int size = getHandle().r().c();
            if (index >= size) {
                index = 0;
            }
            CompoundTag tag = new CanaryCompoundTag((NBTTagCompound) getHandle().r().b(index));
            return new CanaryEnchantment(Enchantment.Type.fromId(tag.getShort("id")), tag.getShort("lvl"));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Enchantment[] getEnchantments() {
        Enchantment[] enchantments = null;
        if (isEnchanted()) {
            int size = getHandle().r().c();
            enchantments = new Enchantment[size];
            CanaryListTag nbtTagList = new CanaryListTag(getHandle().r());
            for (int i = 0; i < size; i++) {
                CompoundTag tag = (CompoundTag) nbtTagList.get(i);
                enchantments[i] = new CanaryEnchantment(Enchantment.Type.fromId(tag.getShort("id")), tag.getShort("lvl"));
            }
        }
        return enchantments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEnchantments(Enchantment... enchantments) {
        if (enchantments != null) {
            for (Enchantment ench : enchantments) {
                getHandle().a(((CanaryEnchantment) ench).getHandle(), ench.getLevel());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnchantments(Enchantment... enchantments) {
        removeAllEnchantments();
        addEnchantments(enchantments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEnchantment(Enchantment enchantment) {
        // hmmmmmm....... No sure yet.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEnchantments() {
        getDataTag().remove("ench");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDataTag() {
        return item.p();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompoundTag getDataTag() {
        return item.p() ? new CanaryCompoundTag(item.q()) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataTag(CompoundTag tag) {
        getHandle().d = tag == null ? null : ((CanaryCompoundTag) tag).getHandle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompoundTag getMetaTag() {
        CompoundTag dataTag = getDataTag();
        if (dataTag == null) {
            dataTag = new CanaryCompoundTag("tag");
            setDataTag(dataTag);
        }
        if (!dataTag.containsKey("Canary")) {
            dataTag.put("Canary", new CanaryCompoundTag("Canary"));
        }
        return dataTag.getCompoundTag("Canary");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompoundTag writeToTag(CompoundTag tag) {
        return new CanaryCompoundTag(getHandle().b(((CanaryCompoundTag) tag).getHandle()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromTag(CompoundTag tag) {
        getHandle().c(((CanaryCompoundTag) tag).getHandle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseItem getBaseItem() {
        return item.getBaseItem();
    }

    /**
     * Gets the ItemStack being wrapped
     */
    public ItemStack getHandle() {
        return item;
    }
}
