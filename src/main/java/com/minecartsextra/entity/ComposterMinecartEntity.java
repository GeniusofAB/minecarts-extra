package com.minecartsextra.entity;

import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ComposterMinecartEntity extends AbstractMinecart implements ILeashedMinecart {
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(ComposterMinecartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEASH_HOLDER_ID = SynchedEntityData.defineId(ComposterMinecartEntity.class, EntityDataSerializers.INT);

    public ComposterMinecartEntity(EntityType<? extends ComposterMinecartEntity> type, Level level) {
        super(type, level);
    }

    public ComposterMinecartEntity(Level level, double x, double y, double z) {
        super(ModEntities.COMPOSTER_MINECART.get(), level, x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEVEL, 0);
        this.entityData.define(LEASH_HOLDER_ID, -1);
    }

    @Override
    public void setLeashHolderId(int id) { this.entityData.set(LEASH_HOLDER_ID, id); }
    @Override
    public int getLeashHolderId() { return this.entityData.get(LEASH_HOLDER_ID); }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.COMPOSTER.defaultBlockState().setValue(ComposterBlock.LEVEL, this.getCompostLevel());
    }

    public int getCompostLevel() {
        return this.entityData.get(LEVEL);
    }

    public void setCompostLevel(int level) {
        this.entityData.set(LEVEL, level);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int i = this.getCompostLevel();

        if (i < 7 && ComposterBlock.COMPOSTABLES.containsKey(itemstack.getItem())) {
            if (!this.level().isClientSide) {
                float chance = ComposterBlock.COMPOSTABLES.getFloat(itemstack.getItem());
                if (this.random.nextFloat() < chance) {
                    this.setCompostLevel(i + 1);
                    this.level().playSound(null, this.blockPosition(), SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    this.level().playSound(null, this.blockPosition(), SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }
            this.level().addParticle(ParticleTypes.COMPOSTER, this.getX() + 0.5D, this.getY() + 0.7D, this.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (i == 8) {
            if (!this.level().isClientSide) {
                double d0 = (double)(this.level().random.nextFloat() * 0.7F) + (double)0.15F;
                double d1 = (double)(this.level().random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
                double d2 = (double)(this.level().random.nextFloat() * 0.7F) + (double)0.15F;
                ItemEntity itementity = new ItemEntity(this.level(), this.getX() + d0, this.getY() + d1, this.getZ() + d2, new ItemStack(Items.BONE_MEAL));
                itementity.setDefaultPickUpDelay();
                this.level().addFreshEntity(itementity);
                this.setCompostLevel(0);
                this.level().playSound(null, this.blockPosition(), SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.getCompostLevel() == 7) {
            if (this.random.nextInt(20) == 0) {
                this.setCompostLevel(8);
                this.level().playSound(null, this.blockPosition(), SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void destroy(DamageSource source) {
        this.discard();
        if (this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(ModItems.COMPOSTER_MINECART.get());
            if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
            this.spawnAtLocation(itemstack);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CompostLevel", this.getCompostLevel());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setCompostLevel(tag.getInt("CompostLevel"));
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.CHEST;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.COMPOSTER_MINECART.get();
    }
}
