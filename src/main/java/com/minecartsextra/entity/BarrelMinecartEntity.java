package com.minecartsextra.entity;

import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.registry.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;

public class BarrelMinecartEntity extends AbstractMinecartContainer implements ILeashedMinecart {
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(BarrelMinecartEntity.class, EntityDataSerializers.INT);

    public BarrelMinecartEntity(EntityType<? extends BarrelMinecartEntity> type, Level level) {
        super(type, level);
    }

    public BarrelMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.BARREL_MINECART.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEASH_HOLDER_ID, -1);
    }

    @Override
    public void setLeashHolderId(int id) { this.entityData.set(LEASH_HOLDER_ID, id); }
    @Override
    public int getLeashHolderId() { return this.entityData.get(LEASH_HOLDER_ID); }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.BARREL.defaultBlockState();
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.openMenu(this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return ChestMenu.threeRows(id, playerInventory, this);
    }

    @Override
    public void destroy(DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.BARREL_MINECART.get());
            if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
            this.spawnAtLocation(itemstack);
            net.minecraft.world.Containers.dropContents(this.level(), this.blockPosition(), this);
        }
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.BARREL_MINECART.get();
    }
}
