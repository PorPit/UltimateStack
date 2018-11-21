package com.porpit.ultimatestack.transform;

import java.io.File;
import java.util.Map;

import com.porpit.ppcore.transform.TransformerNames;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class UltimateStackPatchingLoader implements IFMLLoadingPlugin {

	public static File location;

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { UltimateStackTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		location = (File) data.get("coremodLocation");
		TransformerNames.obfuscated = (Boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}