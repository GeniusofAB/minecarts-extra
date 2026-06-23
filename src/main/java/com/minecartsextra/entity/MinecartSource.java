package com.minecartsextra.entity;

import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSource implements BlockSource {
    private final AbstractMinecart minecart;

    public MinecartSource(AbstractMinecart minecart) {
        this.minecart = minecart;
    }

    @Override
    public double x() {
        return this.minecart.getX();
    }

    @Override
    public double y() {
        return this.minecart.getY();
    }

    @Override
    public double z() {
        return this.minecart.getZ();
    }

    @Override
    public BlockPos getPos() {
        return this.minecart.blockPosition();
    }

    @Override
    public BlockState getBlockState() {
        return this.minecart.getDisplayBlockState();
    }

    @Override
    public <T extends BlockEntity> T getEntity() {
        return null;
    }

    @Override
    public ServerLevel getLevel() {
        return (ServerLevel) this.minecart.level();
    }
}
