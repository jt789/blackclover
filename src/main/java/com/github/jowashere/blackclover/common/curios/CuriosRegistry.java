/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.jowashere.blackclover.common.curios;

import com.github.jowashere.blackclover.Main;
import com.github.jowashere.blackclover.common.curios.inventory.container.CuriosContainer;
import com.github.jowashere.blackclover.common.curios.item.AmuletItem;
import com.github.jowashere.blackclover.common.curios.item.CrownItem;
import com.github.jowashere.blackclover.common.curios.item.KnucklesItem;
import com.github.jowashere.blackclover.common.curios.item.RingItem;
import com.github.jowashere.blackclover.common.curios.objects.FortuneBonusModifier;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;


@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CuriosRegistry {

  @ObjectHolder("blackclover:ring")
  public static final Item RING;

  @ObjectHolder("blackclover:amulet")
  public static final Item AMULET;

  @ObjectHolder("blackclover:crown")
  public static final Item CROWN;

  @ObjectHolder("blackclover:knuckles")
  public static final Item KNUCKLES;

  @ObjectHolder("blackclover:curios_container")
  public static final ContainerType<CuriosContainer> CONTAINER_TYPE;

  static {
    RING = null;
    AMULET = null;
    CROWN = null;
    KNUCKLES = null;
    CONTAINER_TYPE = null;
  }

  @SubscribeEvent
  public static void registerItems(RegistryEvent.Register<Item> evt) {
    evt.getRegistry()
        .registerAll(new RingItem(), new AmuletItem(), new CrownItem(), new KnucklesItem());
  }

  @SubscribeEvent
  public static void registerContainer(RegistryEvent.Register<ContainerType<?>> evt) {
    evt.getRegistry().register(
        IForgeContainerType.create(CuriosContainer::new).setRegistryName("curios_container"));
  }

  @SubscribeEvent
  public static void registerLootModifiers(
      final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
    final IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();
    registry.register(new FortuneBonusModifier.Serializer()
        .setRegistryName(new ResourceLocation(Main.MODID, "fortune_bonus")));
  }
}