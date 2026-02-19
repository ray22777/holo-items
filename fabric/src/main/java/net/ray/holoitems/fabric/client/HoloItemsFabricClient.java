package net.ray.holoitems.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.ray.HologramAPI.HologramAPI;
import net.ray.holoitems.ItemHandler;

public final class HoloItemsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            HolographicIndicatorCommand.register(dispatcher);
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ItemHandler.update();
        });
    }
}
