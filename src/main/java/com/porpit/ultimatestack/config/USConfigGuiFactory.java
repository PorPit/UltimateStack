package com.porpit.ultimatestack.config;

import com.porpit.ultimatestack.UltimateStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Collections;
import java.util.Set;

public class USConfigGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft mc) {
        // 一般情况下，留空即可。
    }

    @Override
    public boolean hasConfigGui() {
        return true; // 当然要返回 true。
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        return new GuiConfig(parent, ConfigElement.from(ConfigLoader.class).getChildElements(), UltimateStack.MODID, false, false, "First line", "Second line");
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        // 这个方法已经被 deprecated 了，但是迟迟没删除，而且并没有正确实现。
        // 所以我们就不管它了。目前来看，返回 null 不会有问题，但慎重起见，还是。。。。。。。。
        // 返回一个正常的 Set 比较稳妥。
        return Collections.emptySet();
    }
}
