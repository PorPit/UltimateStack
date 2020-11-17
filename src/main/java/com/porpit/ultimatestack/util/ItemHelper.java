package com.porpit.ultimatestack.util;


import com.porpit.ultimatestack.config.ConfigLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper {
    public static List<String> getItemOreOrTypeNames(ItemStack itemstack){
        List<String> OreNames=new ArrayList<>();
        int[] oreIds=OreDictionary.getOreIDs(itemstack);
        if(oreIds.length>0){
            for (int oreID:
                 oreIds) {
                OreNames.add(OreDictionary.getOreName(oreID));
            }
        }
        String itemKey=itemstack.getItem().getRegistryName()+":"+itemstack.getMetadata();
        if(ConfigLoader.itemTypeMap.containsKey(itemKey)){
            OreNames.add(ConfigLoader.itemTypeMap.get(itemKey));
        }
        return OreNames;
    }
}
