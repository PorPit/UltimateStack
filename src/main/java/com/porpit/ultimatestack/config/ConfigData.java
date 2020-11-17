package com.porpit.ultimatestack.config;

import com.porpit.ultimatestack.UltimateStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;

import java.util.HashMap;
import java.util.Map;

@Config(modid = UltimateStack.MODID)
public class ConfigData {
    @RangeInt(min = 1, max = 999999)
    @LangKey("config.my_mod.general.thing")
    public static int MAX_STACK_SIZE = 999999;

}
