package mod.gottsch.forge.legacyvault.core.util;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * @author Mark Gottschling on Nov 13, 2022
 *
 */
public class LangUtil {
	public static final String NEWLINE = "";
	public static final String INDENT2 = "  ";
	public static final String INDENT4 = "    ";
	
	/**
	 * 
	 * @param tooltip
	 * @param consumer
	 */
	public static void appendAdvancedHoverText(String modid, List<Component> tooltip, Consumer<List<Component>> consumer) {
		if (!Screen.hasShiftDown()) {
			tooltip.add(Component.literal(NEWLINE));
			// TODO how do make this call to tooltip generic for any mod because it would require the modid
			tooltip.add(Component.translatable(tooltip(modid, "hold_shift")).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.literal(LangUtil.NEWLINE));
		}
		else {
			consumer.accept(tooltip);
		}
	}

    public static String name(String modid, String prefix, String suffix) {
    	return StringUtils.stripEnd(prefix.trim(), ".")
    			+ "."
    			+ modid
    			+ "."
    			+ StringUtils.stripStart(suffix.trim(), ".");
    }
    
    public static String item(String modid, String suffix) {
    	return name(modid, "item", suffix);
    }
    
    public static String tooltip(String modid, String suffix) {
    	return name(modid, "tooltip", suffix);
    }
    
    public static String screen(String modid, String suffix) {
    	return name(modid, "screen", suffix);
    }

	public static String chat(String modid, String suffix) {
		return name(modid, "chat", suffix);
	}
	
	public static void appendAdvancedHoverText(List<Component> tooltip, Consumer<List<Component>> consumer) {
		LangUtil.appendAdvancedHoverText(LegacyVault.MOD_ID, tooltip, consumer);
	}
	
    public static String name(String prefix, String suffix) {
    	return name(LegacyVault.MOD_ID, prefix, suffix);
    }
    
    /**
     * 
     * @param suffix
     * @return
     */
    public static String item(String suffix) {
    	return name(LegacyVault.MOD_ID, "item", suffix);
    }
    
    public static String tooltip(String suffix) {
    	return name(LegacyVault.MOD_ID, "tooltip", suffix);
    }
    
    public static String screen(String suffix) {
    	return name(LegacyVault.MOD_ID, "screen", suffix);
    }

	public static String chat(String suffix) {
		return name(LegacyVault.MOD_ID, "chat", suffix);
	}
}
