package com.minecartsextra.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.RegistryObject;

public class ModMinecartItem extends Item {
    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            Level level = source.getLevel();
            double d0 = source.x() + (double)direction.getStepX() * 1.125D;
            double d1 = source.y() + (double)direction.getStepY() * 1.125D;
            double d2 = source.z() + (double)direction.getStepZ() * 1.125D;
            BlockPos blockpos = source.getPos().relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
            double d3;
            if (blockstate.is(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
                    return this.defaultDispenseItemBehavior.dispense(source, stack);
                }

                BlockState blockstate1 = level.getBlockState(blockpos.below());
                RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, level, blockpos.below(), null) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railshape1.isAscending()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }

            AbstractMinecart abstractminecart = ((ModMinecartItem)stack.getItem()).createMinecart(level, d0, d1 + d3, d2);
            if (stack.hasCustomHoverName()) {
                abstractminecart.setCustomName(stack.getHoverName());
            }

            level.addFreshEntity(abstractminecart);
            stack.shrink(1);
            return stack;
        }

        protected void playSound(BlockSource source) {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };

    private final RegistryObject<? extends EntityType<?>> entityType;

    public ModMinecartItem(RegistryObject<? extends EntityType<?>> entityType, Item.Properties properties) {
        super(properties);
        this.entityType = entityType;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        } else {
            ItemStack itemstack = context.getItemInHand();
            if (!level.isClientSide) {
                RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
                double d0 = 0.0D;
                if (railshape.isAscending()) {
                    d0 = 0.5D;
                }

                AbstractMinecart abstractminecart = createMinecart(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D);
                if (itemstack.hasCustomHoverName()) {
                    abstractminecart.setCustomName(itemstack.getHoverName());
                }

                level.addFreshEntity(abstractminecart);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    private AbstractMinecart createMinecart(Level level, double x, double y, double z) {
        EntityType<?> type = entityType.get();
        if (type == com.minecartsextra.registry.ModEntities.DISPENSER_MINECART.get()) return new com.minecartsextra.entity.DispenserMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.DROPPER_MINECART.get()) return new com.minecartsextra.entity.DropperMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.CRAFTING_TABLE_MINECART.get()) return new com.minecartsextra.entity.CraftingMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.FURNACE_MINECART.get()) return new com.minecartsextra.entity.FurnaceMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.BLAST_FURNACE_MINECART.get()) return new com.minecartsextra.entity.BlastFurnaceMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.SMOKER_MINECART.get()) return new com.minecartsextra.entity.SmokerMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.BARREL_MINECART.get()) return new com.minecartsextra.entity.BarrelMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.COMPOSTER_MINECART.get()) return new com.minecartsextra.entity.ComposterMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.STONECUTTER_MINECART.get()) return new com.minecartsextra.entity.StonecutterMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.GRINDSTONE_MINECART.get()) return new com.minecartsextra.entity.GrindstoneMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.ANVIL_MINECART.get()) return new com.minecartsextra.entity.AnvilMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.ENCHANTING_TABLE_MINECART.get()) return new com.minecartsextra.entity.EnchantingTableMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.BREWING_STAND_MINECART.get()) return new com.minecartsextra.entity.BrewingStandMinecartEntity(level, x, y, z);
        if (type == com.minecartsextra.registry.ModEntities.REDSTONE_LAMP_MINECART.get()) return new com.minecartsextra.entity.RedstoneLampMinecartEntity(com.minecartsextra.registry.ModEntities.REDSTONE_LAMP_MINECART.get(), level);
        
        return null;
    }
}
