package com.minecartsextra.client;

import com.minecartsextra.MinecartsExtra;
import com.minecartsextra.registry.ModEntities;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecartsExtra.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Ванильные вагонетки
        event.registerEntityRenderer(EntityType.MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(EntityType.CHEST_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.CHEST_MINECART));
        event.registerEntityRenderer(EntityType.FURNACE_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.FURNACE_MINECART));
        event.registerEntityRenderer(EntityType.TNT_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.TNT_MINECART));
        event.registerEntityRenderer(EntityType.HOPPER_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.HOPPER_MINECART));
        event.registerEntityRenderer(EntityType.COMMAND_BLOCK_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.COMMAND_BLOCK_MINECART));
        event.registerEntityRenderer(EntityType.SPAWNER_MINECART, (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));

        // Сущности мода
        event.registerEntityRenderer(ModEntities.DISPENSER_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.CHEST_MINECART));
        event.registerEntityRenderer(ModEntities.DROPPER_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.CHEST_MINECART));
        event.registerEntityRenderer(ModEntities.CRAFTING_TABLE_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.FURNACE_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.FURNACE_MINECART));
        event.registerEntityRenderer(ModEntities.BLAST_FURNACE_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.FURNACE_MINECART));
        event.registerEntityRenderer(ModEntities.SMOKER_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.FURNACE_MINECART));
        event.registerEntityRenderer(ModEntities.BARREL_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.CHEST_MINECART));
        event.registerEntityRenderer(ModEntities.COMPOSTER_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.STONECUTTER_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.GRINDSTONE_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.ANVIL_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.ENCHANTING_TABLE_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.BREWING_STAND_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
        event.registerEntityRenderer(ModEntities.REDSTONE_LAMP_MINECART.get(), (context) -> new LeashRenderer<>(context, ModelLayers.MINECART));
    }
}
