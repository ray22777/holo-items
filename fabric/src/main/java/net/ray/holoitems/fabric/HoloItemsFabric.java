package net.ray.holoitems.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.ray.holoitems.config.ConfigGetter;
import net.ray.holoitems.config.IndicatorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public final class HoloItemsFabric implements ModInitializer {
    public static final String MOD_ID = "assets/holo_items";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(IndicatorConfig.class, GsonConfigSerializer::new);
        ConfigGetter.iconfig = AutoConfig.getConfigHolder(IndicatorConfig.class).getConfig();
    }
}
