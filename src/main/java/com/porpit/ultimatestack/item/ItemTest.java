package com.porpit.ultimatestack.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemTest extends Item {

    public ItemTest() {
        super();
        this.maxStackSize=9999;
        this.setUnlocalizedName("testItem");
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {

        System.out.println(stack.getMaxStackSize());
        return super.getItemUseAction(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        System.out.println("1,"+playerIn.getHeldItem(handIn).getMaxStackSize());
        System.out.println(playerIn.inventory.getInventoryStackLimit());
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
