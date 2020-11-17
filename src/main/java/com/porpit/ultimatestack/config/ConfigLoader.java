package com.porpit.ultimatestack.config;

import com.google.common.collect.Lists;
import com.porpit.ultimatestack.UltimateStack;
import com.porpit.ultimatestack.util.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.*;


@Config(modid = UltimateStack.MODID)
public class ConfigLoader {

    @Config.Ignore
    private static Map<String, Configuration> modsConfigMap = new HashMap<>();

    @Config.Ignore
    public static final int MAX_STACK_SIZE = 999999;
    @Config.Ignore
    public static boolean isServerSuport = false;

    @Config.RangeInt(min = 1, max = MAX_STACK_SIZE)
    public static Map<String, Integer> itemMaxStackSizeMap = new HashMap<>();

    @Config.Ignore
    public static Set<String> itemTypes = new HashSet<>();

    @Config.Ignore
    public static Map<String, String> itemTypeMap = new HashMap<>();

    @Config.RangeInt(min = 1, max = 20)
    public static Map<String, Short> oreOrTypeOverrideLevel = new HashMap<>();

    @Config.RangeInt(min = 1, max = MAX_STACK_SIZE)
    public static Map<String, Integer> oreOrTypeMaxStackSizeMap = new HashMap<>();
//    public static Map<String, Short> typeMaxStackSizeMap = new HashMap<>();

    @Config.Ignore
    public static String modConfigurationDirectory;

    @Config.RangeInt(min = 1, max = MAX_STACK_SIZE)
    public static int defaultMaxStackSize = 64;

    public ConfigLoader(FMLPreInitializationEvent event) {

        modConfigurationDirectory = event.getModConfigurationDirectory().getPath() + File.separator + "UltimateStack";
        loadModConfig("minecraft");
        load();
        loadItemTypes();
    }

    public void loadItemTypes() {
        getCustomOreData().forEach((key, value) -> {
                    ConfigLoader.itemTypes.add(key+"_USTYPE");
                    value.forEach(itemStack ->
                    {
                        ConfigLoader.itemTypeMap.put(itemStack.getItem().getRegistryName() + ":" + itemStack.getMetadata(), key+"_USTYPE");
//                    OreDictionary.registerOre(key, itemStack);
                        UltimateStack.logger.info("注册自定义物品类型:"+key+" - ItemStack:"+itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                    });
                }
        );

    }

    public static void loadModConfig(String key) {
        Configuration config = new Configuration(new File(modConfigurationDirectory + File.separator + "ItemMaxStackSize" + File.separator + key + ".cfg"));
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
        loadMainConfig();
        UltimateStack.logger.info("Finished loading config. ");
    }

    public static void loadMainConfig() {
        Configuration mainConfig = new Configuration(new File(modConfigurationDirectory + File.separator + "Setting.cfg"));
        mainConfig.load();

        String common = "设置物品(除了原始堆叠为1的物品)默认最大堆叠，可被其他设定覆盖   范围:1-" + MAX_STACK_SIZE;
        int maxStackSize = mainConfig.get(Configuration.CATEGORY_GENERAL, "Default MaxStackSize", 1000, common).getInt();
        if (maxStackSize > MAX_STACK_SIZE) {
            maxStackSize = MAX_STACK_SIZE;
            mainConfig.get(Configuration.CATEGORY_GENERAL, "Default MaxStackSize", 1000, common).set(maxStackSize);
        } else if (maxStackSize <= 0) {
            maxStackSize = 1;
            mainConfig.get(Configuration.CATEGORY_GENERAL, "Default MaxStackSize", 1000, common).set(maxStackSize);
        }
        defaultMaxStackSize =maxStackSize;
        mainConfig.save();
    }

    public static Map<String, List<ItemStack>> getCustomOreData() {
        Map<String, List<ItemStack>> oreItemsMap = new HashMap<>();

        File file = new File(modConfigurationDirectory + File.separator + "CustomOre.cfg");
        if (!file.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(
                        UltimateStack.class.getClassLoader().getResourceAsStream("assets/ultimatestack/defaultconfig/CustomOre.cfg"));

                BufferedReader bf = new BufferedReader(reader);


                BufferedWriter out = new BufferedWriter(new FileWriter(file));

                String line = null;
                while ((line = bf.readLine()) != null) {
                    out.write(line + '\n');
                }
                out.flush();
                out.close();
                UltimateStack.logger.info("成功读取默认文件 CustomOre.cfg");
            } catch (Exception e) {
                UltimateStack.logger.error("读取默认文件 CustomOre.cfg 失败");
            }

        }


        Configuration customOreConfig = new Configuration(file);
        customOreConfig.load();

        /*Iterator<Item> iterator2= ForgeRegistries.ITEMS.iterator();

        Set<String> ingotList=new HashSet<>();
        Set<String> vanillaItemList=new HashSet<>();
        Set<String> oreList=new HashSet<>();
        Set<String> dustList=new HashSet<>();


        while (iterator2.hasNext()){
            Item item=iterator2.next();
            if(item.getRegistryName()==null||item.equals(Items.AIR))
            {
                continue;
            }
            NonNullList<ItemStack> lst=NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, lst);
            System.out.println("Item:"+item.getRegistryName());
            ListIterator<ItemStack> itemStackListIterator=lst.listIterator();
            while (itemStackListIterator.hasNext()){
                String modId = item.getRegistryName().getResourceDomain();
                String registryName = item.getRegistryName().toString();

                ItemStack itemStack=itemStackListIterator.next();

                String translateKey = itemStack.getUnlocalizedName();
                String translateEnd = ".name";


                if (modId.equals("ic2") || modId.equals("advanced_solar_panels")) {
                    translateEnd = "";
                } else if (modId.equals("botania")) {
                    if (!translateKey.startsWith("tile.botania:")) {
                        translateKey = translateKey.replace("tile.", "tile.botania:");
                    }
                    if (!translateKey.startsWith("item.botania:")) {
                        translateKey = translateKey.replace("item.", "item.botania:");
                    }
                }
                String localName= new TextComponentTranslation(translateKey + translateEnd).getUnformattedComponentText();


                if(item.getRegistryName().toString().contains("ingot")||itemStack.getUnlocalizedName().contains("ingot")||localName.contains("锭"))
                {
                    ingotList.add(itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                }
                if((itemStack.getMaxStackSize()==64||itemStack.getMaxStackSize()==16)&&item.getRegistryName().toString().startsWith("minecraft:"))
                {
                    vanillaItemList.add(itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                }
                if(item.getRegistryName().toString().contains("ore")||itemStack.getUnlocalizedName().contains("ore")||localName.endsWith("矿石"))
                {
                    oreList.add(itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                }
                if(item.getRegistryName().toString().contains("dust")||itemStack.getUnlocalizedName().contains("dust")||localName.contains("粉")||localName.contains("渣")||localName.contains("尘")||localName.contains("碎片")||localName.contains("料"))
                {
                    dustList.add(itemStack.getItem().getRegistryName()+":"+itemStack.getMetadata());
                }
            }
        }
        String[] data=new String[ingotList.size()];
        customOreConfig.get("CustomOre","us_ingot" ,new String[] {"none"},"锭").set(ingotList.toArray(data));
        data=new String[vanillaItemList.size()];
        customOreConfig.get("CustomOre","us_mc_vanilla" ,new String[] {"none"},"原版物品").set(vanillaItemList.toArray(data));
        data=new String[oreList.size()];
        customOreConfig.get("CustomOre","us_ore" ,new String[] {"none"},"矿石").set(oreList.toArray(data));
        data=new String[dustList.size()];
        customOreConfig.get("CustomOre","us_dust" ,new String[] {"none"},"粉渣等杂物").set(dustList.toArray(data));

*/
        Iterator<Property> iterator = customOreConfig.getCategory("CustomOre").getValues().values().iterator();
        customOreConfig.getCategory("CustomOre").setComment("设置自定义物品类型   注意类型名称前要加S:");
        while (iterator.hasNext()) {
            Property p = iterator.next();
            if (p.getStringList().length > 0) {
                List<String> strings = Lists.newArrayList(p.getStringList());
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < p.getStringList().length; i++) {
                    String itemStackID = p.getStringList()[i];

                    String[] nameAndMeta = itemStackID.split(":");
                    String itemID = nameAndMeta[0];
                    itemID += (":" + nameAndMeta[1]);
                    int metaData = 0;
                    if (nameAndMeta.length >= 3) {
                        metaData = Integer.valueOf(nameAndMeta[2]);
                    }

                    Item item = Item.getByNameOrId(itemID);
                    if (item != null) {
                        items.add(new ItemStack(item, 1, metaData));
                    }
                }
                if (!items.isEmpty()) {
                    oreItemsMap.put(p.getName(), items);
                }
            }
        }
        customOreConfig.save();
        return oreItemsMap;
    }

    public static void loadItemData() {
        UltimateStack.logger.info("Started loading Item. ");
        itemMaxStackSizeMap.clear();
        oreOrTypeOverrideLevel.clear();
        oreOrTypeMaxStackSizeMap.clear();
        String allowRange = "  #范围(range):" + 1 + "-" + MAX_STACK_SIZE + "  ";

        File oreFile = new File(modConfigurationDirectory + File.separator + "OreMaxStackSetting.cfg");
        if (!oreFile.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(
                        UltimateStack.class.getClassLoader().getResourceAsStream("assets/ultimatestack/defaultconfig/OreMaxStackSetting.cfg"));

                BufferedReader bf = new BufferedReader(reader);


                BufferedWriter out = new BufferedWriter(new FileWriter(oreFile));

                String line = null;
                while ((line = bf.readLine()) != null) {
                    out.write(line + '\n');
                }
                out.flush();
                out.close();
                UltimateStack.logger.info("成功读取默认文件 OreMaxStackSetting.cfg");
            } catch (Exception e) {
                UltimateStack.logger.error("读取默认文件 OreMaxStackSetting.cfg 失败");
            }

        }

        Configuration oreConfig = new Configuration(oreFile);
        oreConfig.load();
        for (int i = 0; i < OreDictionary.getOreNames().length; i++) {
            String oreName = OreDictionary.getOreNames()[i];
            oreConfig.setCategoryComment("OreLevelSetting", "设置矿典或类型的级别，级别高的可以覆盖级别低的设定  范围1-10");
            Property p = oreConfig.get("OreLevelSetting", oreName, 1);
            int level = p.getInt();
            if (p.getInt() <= 0) {
                level = 1;
            }
            if (p.getInt() > 10) {
                level = 10;
            }
            oreOrTypeOverrideLevel.put(oreName, (short) level);


            oreConfig.setCategoryComment("OreStackMaxSize", "设置物品类型标签包含的物品的最大堆叠  " + allowRange + " 设置为0 则不进行修改");
            p = oreConfig.get("OreStackMaxSize", oreName, 0);
            if (p.getInt() > 0) {
                int maxStackSize;
                maxStackSize = p.getInt();
                if (maxStackSize > MAX_STACK_SIZE) {
                    maxStackSize = MAX_STACK_SIZE;
                } else if (maxStackSize <= 0) {
                    maxStackSize = 1;
                }
                p.set(maxStackSize);
                oreOrTypeMaxStackSizeMap.put(oreName,maxStackSize);
            }

        }

        for(String type : itemTypes){
            String typeReal=type.replaceAll("_USTYPE","");
            oreConfig.setCategoryComment("OreLevelSetting", "设置Ore的级别，级别高的可以覆盖级别低的设定  范围1-10");
            Property p = oreConfig.get("OreLevelSetting", typeReal, 1);
            int level = p.getInt();
            if (p.getInt() <= 0) {
                level = 1;
            }
            if (p.getInt() > 10) {
                level = 10;
            }
            oreOrTypeOverrideLevel.put(type, (short) level);


            oreConfig.setCategoryComment("OreStackMaxSize", "设置矿典或类型包含的物品的最大堆叠  " + allowRange + " 设置为0 则不进行修改");
            p = oreConfig.get("OreStackMaxSize", typeReal, 0);
            if (p.getInt() > 0) {
                int maxStackSize;
                maxStackSize = p.getInt();
                if (maxStackSize > MAX_STACK_SIZE) {
                    maxStackSize = MAX_STACK_SIZE;
                } else if (maxStackSize <= 0) {
                    maxStackSize = 1;
                }
                p.set(maxStackSize);
                oreOrTypeMaxStackSizeMap.put(type,maxStackSize);
            }
        }

        Iterator<Item> iterator = ForgeRegistries.ITEMS.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getRegistryName() == null || item.equals(Items.AIR)) {
                continue;
            }
            NonNullList<ItemStack> lst = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, lst);
            //System.out.println("Item:" + item.getRegistryName());
            lst.forEach(itemStack ->
            {
                String modId = item.getRegistryName().getResourceDomain();
                String registryName = item.getRegistryName().toString();
                int metedata = itemStack.getMetadata();
                String stackName = registryName + ":" + metedata;
                //System.out.println("ItemStack:" + item.getRegistryName() + ":" + itemStack.getMetadata() + ": NameKey" + itemStack.getUnlocalizedName());

                Configuration config = getModConfig(modId);
                String translateKey = itemStack.getUnlocalizedName();
                String translateEnd = ".name";
                if (modId.equals("ic2") || modId.equals("advanced_solar_panels")) {
                    translateEnd = "";
                } else if (modId.equals("botania")) {
                    if (!translateKey.startsWith("tile.botania:")) {
                        translateKey = translateKey.replace("tile.", "tile.botania:");
                    }
                    if (!translateKey.startsWith("item.botania:")) {
                        translateKey = translateKey.replace("item.", "item.botania:");
                    }
                }

                String comment = new TextComponentTranslation(translateKey + translateEnd).getUnformattedComponentText() + allowRange + "\n 包括此Item 的矿典或类型:" + ItemHelper.getItemOreOrTypeNames(itemStack);
                config.setCategoryComment("ItemStackMaxSize", "设置物品对应最大堆叠  " + allowRange + " 设置为0则由矿典决定");
                Property p = config.get("ItemStackMaxSize", stackName, 0, comment);
                int maxStackSize = itemStack.getMaxStackSize();
                if (maxStackSize != 1) {
                    maxStackSize = defaultMaxStackSize;
                }
                if (p.getInt() > 0) {
                    maxStackSize = p.getInt();
                    if (maxStackSize > MAX_STACK_SIZE) {
                        maxStackSize = MAX_STACK_SIZE;
                    } else if (maxStackSize <= 0) {
                        maxStackSize = 1;
                    }
                    p.set(maxStackSize);
                } else {
                    String oreName = getHighLevelOreMaxSetting(ItemHelper.getItemOreOrTypeNames(itemStack));
                    if (oreName != null) {
                        maxStackSize = oreOrTypeMaxStackSizeMap.get(oreName);
                        p.setComment(comment + "\n目前已被矿典(或类型):" + oreName + "设置默认值为:" + maxStackSize);
                    }
                }
                //item.setMaxStackSize(maxStackSize);
                itemMaxStackSizeMap.put(stackName, maxStackSize);

            });


        }

        oreConfig.save();
        saveAllModConfig();
        UltimateStack.logger.info("Finished loading Item. ");
    }

    public static String getHighLevelOreMaxSetting(List<String> oreNames) {
        if (!oreNames.isEmpty()) {
            oreNames.sort((it, it2) -> {
                return Integer.compare(oreOrTypeOverrideLevel.get(it), oreOrTypeOverrideLevel.get(it));
            });
            Iterator<String> iterator = oreNames.iterator();
            while (iterator.hasNext()) {
                String oreName = iterator.next();
                if (oreOrTypeMaxStackSizeMap.containsKey(oreName)) {
                    return oreName;
                }
            }
        }
        return null;
    }

    public static void loadItemDataFormServer(Map<String, Integer> itemMaxStackSizeMapFrom) {
        itemMaxStackSizeMap = itemMaxStackSizeMapFrom;
    }

    public static Integer getMaxStackSizeSetting(Object object) {
        if (object instanceof ItemStack) {
            ItemStack itemStack = ((ItemStack) object);
            Integer maxStackSize = itemMaxStackSizeMap.get(itemStack.getItem().getRegistryName() + ":" + itemStack.getMetadata());
            return maxStackSize == null ? itemStack.getItem().getItemStackLimit(itemStack) : maxStackSize;
        }
        return 1;
    }

}
