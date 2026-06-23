package com.minecartsextra.entity;

import net.minecraft.world.entity.Entity;

public interface ILeashedMinecart {
    void setLeashHolderId(int id);
    int getLeashHolderId();
    
    default Entity getLeashHolder(Entity minecart) {
        int id = getLeashHolderId();
        return id == -1 ? null : minecart.level().getEntity(id);
    }
}
