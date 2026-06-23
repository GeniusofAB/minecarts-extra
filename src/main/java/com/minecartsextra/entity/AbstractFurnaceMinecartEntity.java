package com.minecartsextra.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public abstract class AbstractFurnaceMinecartEntity extends AbstractMinecartContainer implements ILeashedMinecart {
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(AbstractFurnaceMinecartEntity.class, EntityDataSerializers.INT);

    protected int litTime;
    protected int litDuration;
    protected int cookingProgress;
    protected int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int index) {
            switch (index) {
                case 0: return AbstractFurnaceMinecartEntity.this.litTime;
                case 1: return AbstractFurnaceMinecartEntity.this.litDuration;
                case 2: return AbstractFurnaceMinecartEntity.this.cookingProgress;
                case 3: return AbstractFurnaceMinecartEntity.this.cookingTotalTime;
                default: return 0;
            }
        }
        public void set(int index, int value) {
            switch (index) {
                case 0: AbstractFurnaceMinecartEntity.this.litTime = value; break;
                case 1: AbstractFurnaceMinecartEntity.this.litDuration = value; break;
                case 2: AbstractFurnaceMinecartEntity.this.cookingProgress = value; break;
                case 3: AbstractFurnaceMinecartEntity.this.cookingTotalTime = value; break;
            }
        }
        public int getCount() { return 4; }
    };

    protected AbstractFurnaceMinecartEntity(EntityType<? extends AbstractFurnaceMinecartEntity> type, Level level) {
        super(type, level);
    }

    protected AbstractFurnaceMinecartEntity(EntityType<? extends AbstractFurnaceMinecartEntity> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
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
    public int getContainerSize() {
        return 3;
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
            boolean isLit = this.isLit();
            boolean changed = false;
            if (this.isLit()) {
                --this.litTime;
            }

            ItemStack fuel = this.getItem(1);
            if (this.isLit() || !fuel.isEmpty() && !this.getItem(0).isEmpty()) {
                AbstractCookingRecipe recipe = this.level().getRecipeManager().getRecipeFor(this.getRecipeType(), this, this.level()).orElse(null);
                int maxStackSize = this.getMaxStackSize();
                if (!this.isLit() && this.canBurn(recipe, maxStackSize)) {
                    this.litTime = ForgeHooks.getBurnTime(fuel, this.getRecipeType());
                    this.litDuration = this.litTime;
                    if (this.isLit()) {
                        changed = true;
                        if (fuel.hasCraftingRemainingItem()) {
                            this.setItem(1, fuel.getCraftingRemainingItem());
                        } else if (!fuel.isEmpty()) {
                            fuel.shrink(1);
                            if (fuel.isEmpty()) {
                                this.setItem(1, fuel.getCraftingRemainingItem());
                            }
                        }
                    }
                }

                if (this.isLit() && this.canBurn(recipe, maxStackSize)) {
                    ++this.cookingProgress;
                    this.cookingTotalTime = this.getTotalCookTime();
                    if (this.cookingProgress >= this.cookingTotalTime) {
                        this.cookingProgress = 0;
                        this.burn(recipe, maxStackSize);
                        changed = true;
                    }
                } else {
                    this.cookingProgress = 0;
                }
            } else if (!this.isLit() && this.cookingProgress > 0) {
                this.cookingProgress = Math.max(0, this.cookingProgress - 2);
            }

            if (isLit != this.isLit()) {
                changed = true;
                this.updateDisplayBlock();
            }

            if (changed) {
                this.setChanged();
            }
        }
    }

    protected boolean isLit() {
        return this.litTime > 0;
    }

    protected abstract RecipeType<? extends AbstractCookingRecipe> getRecipeType();

    protected boolean canBurn(AbstractCookingRecipe recipe, int maxStackSize) {
        if (!this.getItem(0).isEmpty() && recipe != null) {
            ItemStack result = recipe.getResultItem(this.level().registryAccess());
            if (result.isEmpty()) return false;
            ItemStack output = this.getItem(2);
            if (output.isEmpty()) return true;
            if (!ItemStack.isSameItem(output, result)) return false;
            if (output.getCount() + result.getCount() <= maxStackSize && output.getCount() + result.getCount() <= output.getMaxStackSize()) return true;
            return output.getCount() + result.getCount() <= result.getMaxStackSize();
        }
        return false;
    }

    protected void burn(AbstractCookingRecipe recipe, int maxStackSize) {
        if (recipe != null && this.canBurn(recipe, maxStackSize)) {
            ItemStack input = this.getItem(0);
            ItemStack result = recipe.getResultItem(this.level().registryAccess());
            ItemStack output = this.getItem(2);
            if (output.isEmpty()) {
                this.setItem(2, result.copy());
            } else if (output.is(result.getItem())) {
                output.grow(result.getCount());
            }

            input.shrink(1);
        }
    }

    protected int getTotalCookTime() {
        return this.level().getRecipeManager().getRecipeFor(this.getRecipeType(), this, this.level())
                .map(AbstractCookingRecipe::getCookingTime).orElse(200);
    }

    protected void updateDisplayBlock() {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("BurnTime", (short)this.litTime);
        tag.putShort("CookTime", (short)this.cookingProgress);
        tag.putShort("CookTimeTotal", (short)this.cookingTotalTime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.litTime = tag.getShort("BurnTime");
        this.cookingProgress = tag.getShort("CookTime");
        this.cookingTotalTime = tag.getShort("CookTimeTotal");
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }
}
