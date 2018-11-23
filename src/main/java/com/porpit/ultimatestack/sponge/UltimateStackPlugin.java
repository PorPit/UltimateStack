package com.porpit.ultimatestack.sponge;

import com.google.inject.Inject;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = "ultimatestackplugin",
        name = "UltimateStackPlugin"
)
public class UltimateStackPlugin {

    @Inject
    private Logger logger;
    @Inject
    private Metrics2 metrics;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        metrics.addCustomChart(new Metrics2.SimplePie("platform", () -> "Sponge"));
    }
}

