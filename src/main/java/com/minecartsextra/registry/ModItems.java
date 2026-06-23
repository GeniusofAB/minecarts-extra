package com.minecartsextra.registry;

import com.minecartsextra.MinecartsExtra;
import com.minecartsextra.item.ModMinecartItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinecartsExtra.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinecartsExtra.MOD_ID);

    public static final RegistryObject<Item> DISPENSER_MINECART = ITEMS.register("dispenser_minecart", () -> new ModMinecartItem(ModEntities.DISPENSER_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DROPPER_MINECART = ITEMS.register("dropper_minecart", () -> new ModMinecartItem(ModEntities.DROPPER_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CRAFTING_TABLE_MINECART = ITEMS.register("crafting_table_minecart", () -> new ModMinecartItem(ModEntities.CRAFTING_TABLE_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FURNACE_MINECART = ITEMS.register("furnace_minecart", () -> new ModMinecartItem(ModEntities.FURNACE_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLAST_FURNACE_MINECART = ITEMS.register("blast_furnace_minecart", () -> new ModMinecartItem(ModEntities.BLAST_FURNACE_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SMOKER_MINECART = ITEMS.register("smoker_minecart", () -> new ModMinecartItem(ModEntities.SMOKER_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BARREL_MINECART = ITEMS.register("barrel_minecart", () -> new ModMinecartItem(ModEntities.BARREL_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMPOSTER_MINECART = ITEMS.register("composter_minecart", () -> new ModMinecartItem(ModEntities.COMPOSTER_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STONECUTTER_MINECART = ITEMS.register("stonecutter_minecart", () -> new ModMinecartItem(ModEntities.STONECUTTER_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GRINDSTONE_MINECART = ITEMS.register("grindstone_minecart", () -> new ModMinecartItem(ModEntities.GRINDSTONE_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ANVIL_MINECART = ITEMS.register("anvil_minecart", () -> new ModMinecartItem(ModEntities.ANVIL_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ENCHANTING_TABLE_MINECART = ITEMS.register("enchanting_table_minecart", () -> new ModMinecartItem(ModEntities.ENCHANTING_TABLE_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BREWING_STAND_MINECART = ITEMS.register("brewing_stand_minecart", () -> new ModMinecartItem(ModEntities.BREWING_STAND_MINECART, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> REDSTONE_LAMP_MINECART = ITEMS.register("redstone_lamp_minecart", () -> new ModMinecartItem(ModEntities.REDSTONE_LAMP_MINECART, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> MINECARTS_TAB = CREATIVE_MODE_TABS.register("minecarts_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.minecartsextra"))
            .icon(() -> new ItemStack(CRAFTING_TABLE_MINECART.get()))
            .displayItems((params, output) -> {
                output.accept(DISPENSER_MINECART.get());
                output.accept(DROPPER_MINECART.get());
                output.accept(CRAFTING_TABLE_MINECART.get());
                output.accept(FURNACE_MINECART.get());
                output.accept(BLAST_FURNACE_MINECART.get());
                output.accept(SMOKER_MINECART.get());
                output.accept(BARREL_MINECART.get());
                output.accept(COMPOSTER_MINECART.get());
                output.accept(STONECUTTER_MINECART.get());
                output.accept(GRINDSTONE_MINECART.get());
                output.accept(ANVIL_MINECART.get());
                output.accept(ENCHANTING_TABLE_MINECART.get());
                output.accept(BREWING_STAND_MINECART.get());
                output.accept(REDSTONE_LAMP_MINECART.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
