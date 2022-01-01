package com.github.jowashere.blackclover.world.gen;

import com.github.jowashere.blackclover.Main;
import com.github.jowashere.blackclover.init.EntityInit;
import com.github.jowashere.blackclover.init.StructuresInit;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.StructureSpawnListGatherEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class ModEntityGeneration
{
    //entities that spawn in specific biomes
    public static void onEntitySpawn(final BiomeLoadingEvent event)
    {
        RegistryKey key = RegistryKey.create(Registry.BIOME_REGISTRY, event.getName());
        Set types = BiomeDictionary.getTypes(key);

        if (types.contains(BiomeDictionary.Type.PLAINS)) // Only spawns in plains
        {
            //Weight of spawn: 100, minimum: 4, max: 6
            event.getSpawns().addSpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityInit.BANDIT.get(), 100, 4, 6)).build();
        }
    }

    //entities that spawn in specific structures
    public static void onEntitySpawnInStructure(final StructureSpawnListGatherEvent event)
    {
        if (event.getEntitySpawns().containsKey(EntityClassification.CREATURE))
        {
            boolean isCamp = event.getStructure().equals(StructuresInit.BANDIT_CAMP.get());

            if (isCamp)
            {
                System.out.println("Spawning");
                event.addEntitySpawn(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityInit.BANDIT.get(), 200, 5, 5));
            }
        }
    }
}
