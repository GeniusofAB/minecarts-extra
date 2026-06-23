package com.minecartsextra.registry;

import com.minecartsextra.MinecartsExtra;
import com.minecartsextra.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MinecartsExtra.MOD_ID);

    public static final RegistryObject<EntityType<DispenserMinecartEntity>> DISPENSER_MINECART = ENTITIES.register("dispenser_minecart",
        () -> EntityType.Builder.<DispenserMinecartEntity>of(DispenserMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("dispenser_minecart"));
    public static final RegistryObject<EntityType<DropperMinecartEntity>> DROPPER_MINECART = ENTITIES.register("dropper_minecart",
        () -> EntityType.Builder.<DropperMinecartEntity>of(DropperMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("dropper_minecart"));
    public static final RegistryObject<EntityType<CraftingMinecartEntity>> CRAFTING_TABLE_MINECART = ENTITIES.register("crafting_table_minecart",
        () -> EntityType.Builder.<CraftingMinecartEntity>of(CraftingMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("crafting_table_minecart"));
    public static final RegistryObject<EntityType<FurnaceMinecartEntity>> FURNACE_MINECART = ENTITIES.register("furnace_minecart",
        () -> EntityType.Builder.<FurnaceMinecartEntity>of(FurnaceMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("furnace_minecart"));
    public static final RegistryObject<EntityType<BlastFurnaceMinecartEntity>> BLAST_FURNACE_MINECART = ENTITIES.register("blast_furnace_minecart",
        () -> EntityType.Builder.<BlastFurnaceMinecartEntity>of(BlastFurnaceMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("blast_furnace_minecart"));
    public static final RegistryObject<EntityType<SmokerMinecartEntity>> SMOKER_MINECART = ENTITIES.register("smoker_minecart",
        () -> EntityType.Builder.<SmokerMinecartEntity>of(SmokerMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("smoker_minecart"));
    public static final RegistryObject<EntityType<BarrelMinecartEntity>> BARREL_MINECART = ENTITIES.register("barrel_minecart",
        () -> EntityType.Builder.<BarrelMinecartEntity>of(BarrelMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("barrel_minecart"));
    public static final RegistryObject<EntityType<ComposterMinecartEntity>> COMPOSTER_MINECART = ENTITIES.register("composter_minecart",
        () -> EntityType.Builder.<ComposterMinecartEntity>of(ComposterMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("composter_minecart"));
    public static final RegistryObject<EntityType<StonecutterMinecartEntity>> STONECUTTER_MINECART = ENTITIES.register("stonecutter_minecart",
        () -> EntityType.Builder.<StonecutterMinecartEntity>of(StonecutterMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("stonecutter_minecart"));
    public static final RegistryObject<EntityType<GrindstoneMinecartEntity>> GRINDSTONE_MINECART = ENTITIES.register("grindstone_minecart",
        () -> EntityType.Builder.<GrindstoneMinecartEntity>of(GrindstoneMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("grindstone_minecart"));
    public static final RegistryObject<EntityType<AnvilMinecartEntity>> ANVIL_MINECART = ENTITIES.register("anvil_minecart",
        () -> EntityType.Builder.<AnvilMinecartEntity>of(AnvilMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("anvil_minecart"));
    public static final RegistryObject<EntityType<EnchantingTableMinecartEntity>> ENCHANTING_TABLE_MINECART = ENTITIES.register("enchanting_table_minecart",
        () -> EntityType.Builder.<EnchantingTableMinecartEntity>of(EnchantingTableMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("enchanting_table_minecart"));
    public static final RegistryObject<EntityType<BrewingStandMinecartEntity>> BREWING_STAND_MINECART = ENTITIES.register("brewing_stand_minecart",
        () -> EntityType.Builder.<BrewingStandMinecartEntity>of(BrewingStandMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("brewing_stand_minecart"));
    public static final RegistryObject<EntityType<RedstoneLampMinecartEntity>> REDSTONE_LAMP_MINECART = ENTITIES.register("redstone_lamp_minecart",
        () -> EntityType.Builder.<RedstoneLampMinecartEntity>of(RedstoneLampMinecartEntity::new, MobCategory.MISC).sized(0.98F, 0.7F).build("redstone_lamp_minecart"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
