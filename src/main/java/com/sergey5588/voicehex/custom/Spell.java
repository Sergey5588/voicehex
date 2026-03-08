package com.sergey5588.voicehex.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.function.Consumer;

public class Spell {
    public String name;
    Consumer<ServerPlayNetworking.Context> effect;

    public Spell(String name, Consumer<ServerPlayNetworking.Context> effect) {
        this.name = name;
        this.effect = effect;
    }

    public void cast(ServerPlayNetworking.Context context) {
        effect.accept(context);
    }
}
