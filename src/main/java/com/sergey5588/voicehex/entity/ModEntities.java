package com.sergey5588.voicehex.entity;

import com.sergey5588.voicehex.entity.custom.MagicMissile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.sergey5588.voicehex.Voicehex.MOD_ID;

public class ModEntities {
    public static final EntityType<MagicMissile> MAGIC_MISSILE_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(MOD_ID, "magic_missile"),
            EntityType.Builder.create(MagicMissile::new, SpawnGroup.MISC).dimensions(1.0f, 1.0f).build("magic_missile")
    );
    public static void init() {}

}
