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
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.nbt.CompoundTag;

public class BrewingStandMinecartEntity extends AbstractMinecartContainer implements ILeashedMinecart {
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(BrewingStandMinecartEntity.class, EntityDataSerializers.INT);
    private int brewTime;
    private int fuel;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            switch (index) {
                case 0: return BrewingStandMinecartEntity.this.brewTime;
                case 1: return BrewingStandMinecartEntity.this.fuel;
                default: return 0;
            }
        }
        public void set(int index, int value) {
            switch (index) {
                case 0: BrewingStandMinecartEntity.this.brewTime = value; break;
                case 1: BrewingStandMinecartEntity.this.fuel = value; break;
            }
        }
        public int getCount() { return 2; }
    };

    public BrewingStandMinecartEntity(EntityType<? extends BrewingStandMinecartEntity> type, Level level) {
        super(type, level);
    }

    public BrewingStandMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.BREWING_STAND_MINECART.get(), x, y, z, level);
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
        return Blocks.BREWING_STAND.defaultBlockState();
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.openMenu(this);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            ItemStack fuelStack = this.getItem(4);
            if (this.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
                this.fuel = 20;
                fuelStack.shrink(1);
                this.setChanged();
            }

            boolean canBrew = this.canBrew();
            if (this.brewTime > 0) {
                --this.brewTime;
                if (this.brewTime == 0) {
                    if (canBrew) {
                        this.doBrew();
                        this.setChanged();
                    }
                } else if (!canBrew) {
                    this.brewTime = 0;
                    this.setChanged();
                }
            } else if (canBrew && this.fuel > 0) {
                --this.fuel;
                this.brewTime = 400;
                this.setChanged();
            }
        }
    }

    private boolean canBrew() {
        ItemStack ingredient = this.getItem(3);
        if (ingredient.isEmpty()) return false;
        if (!PotionBrewing.isIngredient(ingredient)) return false;
        for (int i = 0; i < 3; ++i) {
            ItemStack potion = this.getItem(i);
            if (!potion.isEmpty() && PotionBrewing.hasMix(potion, ingredient)) {
                return true;
            }
        }
        return false;
    }

    private void doBrew() {
        ItemStack ingredient = this.getItem(3);
        for (int i = 0; i < 3; ++i) {
            ItemStack potion = this.getItem(i);
            if (!potion.isEmpty()) {
                this.setItem(i, PotionBrewing.mix(ingredient, potion));
            }
        }
        ingredient.shrink(1);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new BrewingStandMenu(id, playerInventory, this, this.dataAccess);
    }

    @Override
    public void destroy(DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.BREWING_STAND_MINECART.get());
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
        return ModItems.BREWING_STAND_MINECART.get();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("BrewTime", (short)this.brewTime);
        tag.putShort("Fuel", (short)this.fuel);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.brewTime = tag.getShort("BrewTime");
        this.fuel = tag.getShort("Fuel");
    }
}
