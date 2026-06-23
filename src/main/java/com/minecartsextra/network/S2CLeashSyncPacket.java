package com.minecartsextra.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class S2CLeashSyncPacket {
    public static final Map<Integer, Integer> LEASH_MAP = new HashMap<>();
    
    private final int minecartId;
    private final int holderId;

    public S2CLeashSyncPacket(int minecartId, int holderId) {
        this.minecartId = minecartId;
        this.holderId = holderId;
    }

    public S2CLeashSyncPacket(FriendlyByteBuf buf) {
        this.minecartId = buf.readInt();
        this.holderId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(minecartId);
        buf.writeInt(holderId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (holderId == -1) {
                LEASH_MAP.remove(minecartId);
            } else {
                LEASH_MAP.put(minecartId, holderId);
            }
        });
        return true;
    }
}
