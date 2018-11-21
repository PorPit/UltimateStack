package com.porpit.ultimatestack.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLoader {
    public static Item testItem = new ItemTest();
    public static Item testItem2 = new ItemTest();

    public ItemLoader(FMLPreInitializationEvent event) {
        //register(testItem, "test_item");
        //register(testItem2, "test_item2");

    }

    @SideOnly(Side.CLIENT)
    public static void registerRenders() {
        registerRender(testItem);
    }

    private static void register(Item item, String name) {
        ForgeRegistries.ITEMS.register(item.setRegistryName(name));
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Item item, int meta, String name) {
        ModelResourceLocation model = new ModelResourceLocation(name, "inventory");
        ModelLoader.setCustomModelResourceLocation(item, meta, model);
    }

    @SideOnly(Side.CLIENT)
    private static void registerRender(Item item) {
        System.out.println(item.getRegistryName().toString());
        registerRender(item, 0, item.getRegistryName().toString());
    }
}
