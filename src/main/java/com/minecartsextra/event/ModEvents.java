package com.minecartsextra.event;

import com.minecartsextra.MinecartsExtra;
import com.minecartsextra.entity.*;
import com.minecartsextra.registry.ModEntities;
import com.minecartsextra.network.ModMessages;
import com.minecartsextra.network.S2CLeashSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MinecartsExtra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack stack = player.getItemInHand(event.getHand());

        if (target instanceof AbstractMinecart minecart) {
            CompoundTag tag = minecart.getPersistentData();
            boolean isLeashed = tag.contains("LeashHolderUUID");

            if (isLeashed && (player.isShiftKeyDown() || stack.is(Items.LEAD))) {
                if (!level.isClientSide) {
                    tag.remove("LeashHolderUUID");
                    tag.remove("LeashToFence");
                    syncLeash(minecart, -1);
                    if (!player.getAbilities().instabuild) {
                        minecart.spawnAtLocation(Items.LEAD);
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                return;
            }

            if (stack.is(Items.LEAD) && !isLeashed) {
                if (!level.isClientSide) {
                    tag.putUUID("LeashHolderUUID", player.getUUID());
                    syncLeash(minecart, player.getId());
                    stack.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                return;
            }

            if (isLeashed && player.isShiftKeyDown()) {
                event.setCanceled(true);
                return;
            }

            if (target instanceof Minecart && target.getType() == net.minecraft.world.entity.EntityType.MINECART) {
                handleTransformation(event, target, player, level, stack);
            } else if (target instanceof AbstractMinecart && player.isShiftKeyDown() && stack.isEmpty()) {
                // Return to normal minecart
                if (!level.isClientSide) {
                    level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                    ItemStack drop = new ItemStack(target.getPickResult().getItem());
                    if (target.hasCustomName()) drop.setHoverName(target.getCustomName());
                    player.addItem(drop);
                    
                    Minecart normal = new Minecart(level, target.getX(), target.getY(), target.getZ());
                    normal.setDeltaMovement(target.getDeltaMovement());
                    normal.setYRot(target.getYRot());
                    target.discard();
                    level.addFreshEntity(normal);
                }
                event.setCanceled(true);
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            }
        }
    }

    private static void syncLeash(AbstractMinecart minecart, int holderId) {
        if (minecart instanceof ILeashedMinecart leashed) {
            leashed.setLeashHolderId(holderId);
        }
        ModMessages.sendToClients(new S2CLeashSyncPacket(minecart.getId(), holderId));
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof AbstractMinecart minecart) {
            CompoundTag tag = minecart.getPersistentData();
            if (tag.contains("LeashHolderUUID")) {
                UUID holderUUID = tag.getUUID("LeashHolderUUID");
                Entity holder = event.getEntity().level().getEntities((Entity)null, minecart.getBoundingBox().inflate(30), e -> e.getUUID().equals(holderUUID)).stream().findFirst().orElse(null);
                if (holder != null) {
                    ModMessages.sendToPlayer(new S2CLeashSyncPacket(minecart.getId(), holder.getId()), (ServerPlayer) event.getEntity());
                }
            }
        }
    }

    private static void handleTransformation(PlayerInteractEvent.EntityInteract event, Entity target, Player player, Level level, ItemStack stack) {
        Entity newMinecart = null;
        if (stack.is(Items.CRAFTING_TABLE)) newMinecart = new CraftingMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.DISPENSER)) {
            DispenserMinecartEntity d = new DispenserMinecartEntity(level, target.getX(), target.getY(), target.getZ());
            d.setFacing(calculatePerpendicularFacing(player, level, target));
            newMinecart = d;
        } else if (stack.is(Items.DROPPER)) {
            DropperMinecartEntity d = new DropperMinecartEntity(level, target.getX(), target.getY(), target.getZ());
            d.setFacing(calculatePerpendicularFacing(player, level, target));
            newMinecart = d;
        } else if (stack.is(Items.FURNACE)) newMinecart = new FurnaceMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.BLAST_FURNACE)) newMinecart = new BlastFurnaceMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.SMOKER)) newMinecart = new SmokerMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.BARREL)) newMinecart = new BarrelMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.COMPOSTER)) newMinecart = new ComposterMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.STONECUTTER)) newMinecart = new StonecutterMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.GRINDSTONE)) newMinecart = new GrindstoneMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.ANVIL)) newMinecart = new AnvilMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.ENCHANTING_TABLE)) newMinecart = new EnchantingTableMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.BREWING_STAND)) newMinecart = new BrewingStandMinecartEntity(level, target.getX(), target.getY(), target.getZ());
        else if (stack.is(Items.REDSTONE_LAMP)) newMinecart = new RedstoneLampMinecartEntity(ModEntities.REDSTONE_LAMP_MINECART.get(), level);

        if (newMinecart != null) {
            if (!level.isClientSide) {
                newMinecart.setDeltaMovement(target.getDeltaMovement());
                newMinecart.setYRot(target.getYRot());
                if (target.hasCustomName()) newMinecart.setCustomName(target.getCustomName());
                target.discard();
                level.addFreshEntity(newMinecart);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level.playSound(null, newMinecart.getX(), newMinecart.getY(), newMinecart.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        if (level.getBlockState(pos).is(net.minecraft.tags.BlockTags.FENCES)) {
            if (!level.isClientSide) {
                for (Entity entity : level.getEntitiesOfClass(AbstractMinecart.class, player.getBoundingBox().inflate(10))) {
                    CompoundTag tag = entity.getPersistentData();
                    if (tag.contains("LeashHolderUUID") && tag.getUUID("LeashHolderUUID").equals(player.getUUID())) {
                        LeashFenceKnotEntity knot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
                        tag.putUUID("LeashHolderUUID", knot.getUUID());
                        syncLeash((AbstractMinecart)entity, knot.getId());
                        tag.putBoolean("LeashToFence", true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.level.isClientSide) {
            ServerLevel level = (ServerLevel) event.level;
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof AbstractMinecart minecart) {
                    CompoundTag tag = minecart.getPersistentData();
                    if (tag.contains("LeashHolderUUID")) {
                        UUID holderUUID = tag.getUUID("LeashHolderUUID");
                        Entity holder = level.getEntity(holderUUID);
                        if (holder == null) {
                            for (Player p : level.players()) {
                                if (p.getUUID().equals(holderUUID)) {
                                    holder = p;
                                    break;
                                }
                            }
                        }
                        if (holder != null) {
                            updateLeashPhysics(minecart, holder);
                        } else {
                            tag.remove("LeashHolderUUID");
                            tag.remove("LeashToFence");
                            syncLeash(minecart, -1);
                        }
                    }
                }
            }
        }
    }

    private static void updateLeashPhysics(AbstractMinecart minecart, Entity holder) {
        double dist = minecart.distanceTo(holder);
        if (dist > 10.0D) {
            minecart.getPersistentData().remove("LeashHolderUUID");
            syncLeash(minecart, -1);
            minecart.spawnAtLocation(Items.LEAD);
            return;
        }
        double pullThreshold = 3.5D;
        double stopThreshold = 2.0D;
        if (dist > pullThreshold) {
            double speed = 0.02D * (dist - pullThreshold);
            speed = Math.min(speed, 0.04D);
            Vec3 pullDir = holder.position().subtract(minecart.position()).normalize();
            minecart.setDeltaMovement(minecart.getDeltaMovement().add(pullDir.x * speed, 0, pullDir.z * speed).scale(0.95D));
        } else if (dist < stopThreshold) {
            minecart.setDeltaMovement(minecart.getDeltaMovement().scale(0.7D));
        }
    }

    private static Direction calculatePerpendicularFacing(Player player, Level level, Entity target) {
        BlockState railState = level.getBlockState(target.blockPosition());
        Direction playerDirection = player.getDirection();
        if (railState.getBlock() instanceof BaseRailBlock) {
            RailShape shape = ((BaseRailBlock)railState.getBlock()).getRailDirection(railState, level, target.blockPosition(), null);
            if (shape == RailShape.NORTH_SOUTH || shape == RailShape.ASCENDING_NORTH || shape == RailShape.ASCENDING_SOUTH) {
                return (playerDirection == Direction.EAST || playerDirection == Direction.WEST) ? playerDirection : Direction.EAST;
            }
            if (shape == RailShape.EAST_WEST || shape == RailShape.ASCENDING_EAST || shape == RailShape.ASCENDING_WEST) {
                return (playerDirection == Direction.NORTH || playerDirection == Direction.SOUTH) ? playerDirection : Direction.NORTH;
            }
        }
        return playerDirection;
    }
}
