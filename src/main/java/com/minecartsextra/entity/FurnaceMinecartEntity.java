package com.minecartsextra.entity;

import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceMinecartEntity extends AbstractFurnaceMinecartEntity {
    public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> type, Level level) {
        super(type, level);
    }

    public FurnaceMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.FURNACE_MINECART.get(), x, y, z, level);
    }

    @Override
    protected RecipeType<? extends AbstractCookingRecipe> getRecipeType() {
        return RecipeType.SMELTING;
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.FURNACE.defaultBlockState();
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new FurnaceMenu(id, playerInventory, this, this.dataAccess);
    }

    @Override
    protected void updateDisplayBlock() {
        this.setDisplayBlockState(this.isLit() ? Blocks.FURNACE.defaultBlockState().setValue(net.minecraft.world.level.block.FurnaceBlock.LIT, true) : Blocks.FURNACE.defaultBlockState());
    }

    @Override
    public void destroy(net.minecraft.world.damagesource.DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.FURNACE_MINECART.get());
            if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
            this.spawnAtLocation(itemstack);
            net.minecraft.world.Containers.dropContents(this.level(), this.blockPosition(), this);
        }
    }

    @Override
    protected Item getDropItem() {
        return ModItems.FURNACE_MINECART.get();
    }
}
