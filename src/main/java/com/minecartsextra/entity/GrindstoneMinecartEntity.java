package com.minecartsextra.entity;

import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class GrindstoneMinecartEntity extends AbstractMinecart implements ILeashedMinecart {
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(GrindstoneMinecartEntity.class, EntityDataSerializers.INT);

    public GrindstoneMinecartEntity(EntityType<? extends GrindstoneMinecartEntity> type, Level level) {
        super(type, level);
    }

    public GrindstoneMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.GRINDSTONE_MINECART.get(), level, x, y, z);
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
        return Blocks.GRINDSTONE.defaultBlockState();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            player.openMenu(new SimpleMenuProvider((id, inventory, p) -> 
                new GrindstoneMenu(id, inventory, ContainerLevelAccess.create(this.level(), this.blockPosition())) {
                    @Override
                    public boolean stillValid(Player p_39368_) {
                        return true;
                    }
                }, 
                Component.translatable("container.grindstone")));
            player.awardStat(Stats.INTERACT_WITH_GRINDSTONE);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void destroy(DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.GRINDSTONE_MINECART.get());
            if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
            this.spawnAtLocation(itemstack);
        }
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.GRINDSTONE_MINECART.get();
    }
}
