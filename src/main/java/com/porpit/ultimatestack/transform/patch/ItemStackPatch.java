package com.porpit.ultimatestack.transform.patch;

import com.porpit.ultimatestack.config.ConfigLoader;

public class ItemStackPatch{

    public int getMaxStackSize()
    {
        return ConfigLoader.getMaxStackSizeSetting(this);
    }

}
