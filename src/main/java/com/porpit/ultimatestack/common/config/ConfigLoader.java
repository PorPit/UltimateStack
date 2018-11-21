package com.porpit.ultimatestack.common.config;

import com.porpit.ultimatestack.UltimateStack;
import com.porpit.ultimatestack.util.ItemHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.util.*;

public class ConfigLoader {

    private static Map<String, Configuration> modsConfigMap = new HashMap<>();

    public static final int MAX_STACK_SIZE = Short.MAX_VALUE;

    public static boolean isServerSuport = false;

    public static Map<String, Short> itemMaxStackSizeMap = new HashMap<>();

    public static Map<String, Short> oreOverrideLevel = new HashMap<>();

    public static Map<String, Short> oreMaxStackSizeMap = new HashMap<>();


    public static String modConfigurationDirectory;

    public ConfigLoader(FMLPreInitializationEvent event) {

        modConfigurationDirectory = event.getModConfigurationDirectory().getPath() + "\\UltimateStack";
        loadModConfig("minecraft");
        load();
    }

    public static void loadModConfig(String key) {
        Configuration config = new Configuration(new File(modConfigurationDirectory + "\\ItemMaxStackSize\\" + key + ".cfg"));
        config.load();
        modsConfigMap.put(key, config);
    }

    public static Configuration getModConfig(String key) {
        if (!modsConfigMap.containsKey(key)) {
            loadModConfig(key);
        }
        return modsConfigMap.get(key);
    }


    public static void saveAllModConfig() {
        modsConfigMap.forEach((key, value) ->
        {
            value.save();
        });
    }

    public static void load() {
        UltimateStack.logger.info("Started loading config. ");
        loadCustomOreData();
        UltimateStack.logger.info("Finished loading config. ");
    }

    public static void loadCustomOreData(){
        Configuration customOreConfig = new Configuration(new File(modConfigurationDirectory + "\\CustomOre.cfg"));
        customOreConfig.load();
        Iterator<Property> iterator= customOreConfig.getCategory("test").getValues().values().iterator();
        customOreConfig.getCategory("test").setComment("t1");
        while (iterator.hasNext()){
            Property p=iterator.next();
            UltimateStack.logger.info("register ore:"+p.getName());
            UltimateStack.logger.info( Arrays.asList(p.getStringList()));
        }


        customOreConfig.save();
    }

    public static void loadItemData() {
        UltimateStack.logger.info("Started loading Item. ");
        itemMaxStackSizeMap.clear();
        oreOverrideLevel.clear();
        oreMaxStackSizeMap.clear();
        String allowRange = "  #范围(range):" + 1 + "-" + MAX_STACK_SIZE + "  ";

        Configuration oreConfig = new Configuration(new File(modConfigurationDirectory + "\\OreMaxStackSetting.cfg"));
        oreConfig.load();
        for (int i = 0; i < OreDictionary.getOreNames().length; i++) {
            String oreName = OreDictionary.getOreNames()[i];
            oreConfig.setCategoryComment("OreLevelSetting", "设置Ore的级别，级别高的可以覆盖级别低的设定  范围1-10");
            Property p = oreConfig.get("OreLevelSetting", oreName, 1);
            int level = p.getInt();
            if (p.getInt() <= 0) {
                level = 1;
            }
            if (p.getInt() > 10) {
                level = 10;
            }
            oreOverrideLevel.put(oreName, (short) level);


            oreConfig.setCategoryComment("OreStackMaxSize", "设置Item对应的矿典的最大堆叠  " + allowRange + " 设置为0 则不进行修改");
            p = oreConfig.get("OreStackMaxSize", oreName, 0);
            if (p.getInt()>0) {
                int maxStackSize;
                maxStackSize = p.getInt();
                if (maxStackSize > MAX_STACK_SIZE) {
                    maxStackSize = MAX_STACK_SIZE;
                } else if (maxStackSize <= 0) {
                    maxStackSize = 1;
                }
                p.set(maxStackSize);
                oreMaxStackSizeMap.put(oreName, (short) maxStackSize);
            }

        }


        Iterator<Item> iterator= ForgeRegistries.ITEMS.iterator();
        while (iterator.hasNext()){
            Item item=iterator.next();
            if(item.getRegistryName()==null||item.equals(Items.AIR))
            {
               continue;
            }
            Configuration config = getModConfig(item.getRegistryName().getResourceDomain());
            config.setCategoryComment("ItemStackMaxSize", "设置Item对应最大堆叠  " + allowRange + " 设置为0则由矿典决定");
            Property p = config.get("ItemStackMaxSize", item.getRegistryName().toString(), 0, new TextComponentTranslation(item.getUnlocalizedName() + ".name").getUnformattedComponentText() + allowRange + " \n 设置为0则由矿典决定 或者 不进行修改"+"\n 包括此Item 的矿典:"+ItemHelper.getItemOreNames(item));
            int maxStackSize = item.getItemStackLimit();
            if (p.getInt()>0) {
                maxStackSize = p.getInt();
                if (maxStackSize > MAX_STACK_SIZE) {
                    maxStackSize = MAX_STACK_SIZE;
                } else if (maxStackSize <= 0) {
                    maxStackSize = 1;
                }
                p.set(maxStackSize);
            } else {
                String oreName= getHighLevelOreMaxSetting(ItemHelper.getItemOreNames(item));
                if(oreName!=null){
                    maxStackSize=oreMaxStackSizeMap.get(oreName);
                    p.setComment("设置Item对应最大堆叠  " + allowRange + " 设置为0则由矿典决定 目前已被矿典:"+oreName+"设置默认值为:"+maxStackSize);
                }
            }
            item.setMaxStackSize(maxStackSize);
            itemMaxStackSizeMap.put(item.getRegistryName().toString(), (short) maxStackSize);
        }

        oreConfig.save();
        saveAllModConfig();
        UltimateStack.logger.info("Finished loading Item. ");
    }

    public static String getHighLevelOreMaxSetting (List<String> oreNames){
        if(!oreNames.isEmpty()){
            oreNames.sort((it,it2)->{
                return Integer.compare(oreOverrideLevel.get(it),oreOverrideLevel.get(it) );
            });
            Iterator<String> iterator= oreNames.iterator();
            while (iterator.hasNext()){
                String oreName= iterator.next();
                if(oreMaxStackSizeMap.containsKey(oreName)){
                    return oreName;
                }
            }
        }
        return null;
    }

    public static void loadItemDataFormServer(Map<String, Short> itemMaxStackSizeMap) {
        itemMaxStackSizeMap.forEach((key, value) -> {
            ForgeRegistries.ITEMS.iterator().forEachRemaining(item -> {
                if (item.getRegistryName().toString().equals(key)) {
                    item.setMaxStackSize(value);
                }
            });
        });
    }
}
