package com.minecartsextra.entity;

import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import java.util.Map;

public class DispenserMinecartEntity extends AbstractMinecartContainer implements ILeashedMinecart {
    private static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(DispenserMinecartEntity.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(DispenserMinecartEntity.class, EntityDataSerializers.INT);
    private BlockPos lastActivatorPos;

    public DispenserMinecartEntity(EntityType<? extends DispenserMinecartEntity> type, Level level) {
        super(type, level);
    }

    public DispenserMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.DISPENSER_MINECART.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FACING, Direction.NORTH);
        this.entityData.define(LEASH_HOLDER_ID, -1);
    }

    public void setFacing(Direction direction) {
        this.entityData.set(FACING, direction);
        this.setDisplayBlockState(this.getDefaultDisplayBlockState());
    }

    public Direction getFacing() {
        return this.entityData.get(FACING);
    }

    @Override
    public void setLeashHolderId(int id) { this.entityData.set(LEASH_HOLDER_ID, id); }
    @Override
    public int getLeashHolderId() { return this.entityData.get(LEASH_HOLDER_ID); }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, this.getFacing());
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new DispenserMenu(id, playerInventory, this);
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean active) {
        if (active) {
            BlockPos currentPos = new BlockPos(x, y, z);
            if (lastActivatorPos == null || !lastActivatorPos.equals(currentPos)) {
                this.dispense();
                this.lastActivatorPos = currentPos;
            }
        } else {
            this.lastActivatorPos = null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && lastActivatorPos != null) {
            if (!this.level().getBlockState(this.blockPosition()).is(Blocks.ACTIVATOR_RAIL)) {
                this.lastActivatorPos = null;
            }
        }
    }

    protected void dispense() {
        int i = this.getRandomSlot();
        if (i < 0) {
            this.level().levelEvent(1001, this.blockPosition(), 0);
        } else {
            ItemStack itemstack = this.getItem(i);
            DispenseItemBehavior dispenseitembehavior = this.getDispenseBehavior(itemstack);
            if (dispenseitembehavior != DispenseItemBehavior.NOOP) {
                this.setItem(i, dispenseitembehavior.dispense(new MinecartSource(this), itemstack));
            }
        }
    }

    protected DispenseItemBehavior getDispenseBehavior(ItemStack stack) {
        Map<Item, DispenseItemBehavior> registry = ObfuscationReflectionHelper.getPrivateValue(DispenserBlock.class, null, "DISPENSER_REGISTRY");
        return registry.get(stack.getItem());
    }

    private int getRandomSlot() {
        int i = -1;
        int j = 1;
        for (int k = 0; k < this.getContainerSize(); ++k) {
            if (!this.getItem(k).isEmpty() && this.random.nextInt(j++) == 0) {
                i = k;
            }
        }
        return i;
    }

    @Override
    public void destroy(DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.DISPENSER_MINECART.get());
            if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
            this.spawnAtLocation(itemstack);
            net.minecraft.world.Containers.dropContents(this.level(), this.blockPosition(), this);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Facing", this.getFacing().get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setFacing(Direction.from3DDataValue(tag.getInt("Facing")));
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.DISPENSER_MINECART.get();
    }
}
