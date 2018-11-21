package com.porpit.ultimatestack.util;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper {
    public static List<String> getItemOreNames(Item item){
        List<String> OreNames=new ArrayList<>();
        int[] oreIds=OreDictionary.getOreIDs(new ItemStack(item));
        if(oreIds.length>0){
            for (int oreID:
                 oreIds) {
                OreNames.add(OreDictionary.getOreName(oreID));
            }
        }
        return OreNames;
    }
}
