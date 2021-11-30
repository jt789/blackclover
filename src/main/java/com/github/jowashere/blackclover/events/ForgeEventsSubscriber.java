package com.github.jowashere.blackclover.events;

import com.github.jowashere.blackclover.Main;
import com.github.jowashere.blackclover.api.BCMRegistry;
import com.github.jowashere.blackclover.api.internal.BCMSpell;
import com.github.jowashere.blackclover.capabilities.player.IPlayerHandler;
import com.github.jowashere.blackclover.capabilities.player.PlayerCapability;
import com.github.jowashere.blackclover.capabilities.player.PlayerProvider;
import com.github.jowashere.blackclover.events.bcevents.MagicLevelChangeEvent;
import com.github.jowashere.blackclover.init.AttributeInit;
import com.github.jowashere.blackclover.networking.NetworkLoader;
import com.github.jowashere.blackclover.networking.packets.PacketSetGrimoire;
import com.github.jowashere.blackclover.networking.packets.PacketSpellModeToggle;
import com.github.jowashere.blackclover.networking.packets.PacketToggleInfusionBoolean;
import com.github.jowashere.blackclover.networking.packets.mana.PacketManaBoolean;
import com.github.jowashere.blackclover.networking.packets.modes.PacketModeSync;
import com.github.jowashere.blackclover.networking.packets.settings.PacketSetGrimoireTexture;
import com.github.jowashere.blackclover.networking.packets.spells.PacketSpellNBTSync;
import com.github.jowashere.blackclover.util.helpers.SpellHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventsSubscriber {

    private static boolean curiosLoaded = false;

    @SubscribeEvent
    public static void onLevelChange(MagicLevelChangeEvent event){
        ModifiableAttributeInstance maxHpAttribute = event.getPlayer().getAttribute(Attributes.MAX_HEALTH);

        maxHpAttribute.setBaseValue(Math.max(event.getNewLevel(), 20));

        ((ServerPlayerEntity) event.getPlayer()).connection.send(new SUpdateHealthPacket(event.getPlayer().getHealth(), event.getPlayer().getFoodData().getFoodLevel(), event.getPlayer().getFoodData().getSaturationLevel()));
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event){

        LazyOptional<IPlayerHandler> capabilities = event.getPlayer().getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
        IPlayerHandler playercap = capabilities.orElse(new PlayerCapability());

        ModifiableAttributeInstance maxHpAttribute = event.getPlayer().getAttribute(Attributes.MAX_HEALTH);

        maxHpAttribute.setBaseValue(Math.max(playercap.ReturnMagicLevel(), 20));

        event.getPlayer().setHealth(event.getPlayer().getMaxHealth());

        ((ServerPlayerEntity) event.getPlayer()).connection.send(new SUpdateHealthPacket(event.getPlayer().getHealth(), event.getPlayer().getFoodData().getFoodLevel(), event.getPlayer().getFoodData().getSaturationLevel()));

    }

    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide) {
            if (event.getEntity() instanceof PlayerEntity) {
                PlayerEvents.PlayerJoinedWorld(event);

                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) event.getEntity();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;

        if (player.isAlive()) {
            IPlayerHandler playercap = player.getCapability(PlayerProvider.CAPABILITY_PLAYER).orElseThrow(() -> new RuntimeException("CAPABILITY_PLAYER NOT FOUND!"));
            for (BCMSpell spell : BCMRegistry.SPELLS.getValues()) {
                if (spell.isToggle()) {
                    String nbtName = spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName();
                    if (player.getPersistentData().getBoolean(nbtName)) {

                        int modifier0 = Math.max(0, playercap.ReturnMagicLevel() / 5);
                        int modifier1 = Math.max(0, playercap.ReturnMagicLevel() / 5) - 1;
                        spell.act(player, modifier0, modifier1, SpellHelper.findSpellKey(player, spell));

                    }
                }
            }

            if (!player.level.isClientSide) {
                if(playercap.ReturnMagicAttribute().equals(AttributeInit.ANTI_MAGIC)){
                    playercap.setManaBoolean(false);
                    NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketManaBoolean(false, true));
                }else {
                    playercap.setManaBoolean(true);
                    NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketManaBoolean(true, true));
                }

                PlayerEvents.RegenerateMana(event);
                PlayerEvents.SetPlayerSpells(event);
                PlayerEvents.SpecialSpellNbt(event);
                PlayerEvents.Cooldowns(event);
                PlayerEvents.ManaRuns(event);
                PlayerEvents.MagicBuffs(event);
            }
        }

        if(curiosLoaded)
            return;

        if(event.phase != TickEvent.Phase.START)
            return;
    }

    @SubscribeEvent
    public static void onPlayerTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof PlayerEntity)
        {
            PlayerEntity target = (PlayerEntity) event.getTarget();
            if (!event.getEntity().level.isClientSide) {
                int playerID = event.getPlayer().getId();
                LazyOptional<IPlayerHandler> playerCapability = event.getPlayer().getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
                IPlayerHandler playercap = playerCapability.orElse(new PlayerCapability());

                int targetID = target.getId();
                LazyOptional<IPlayerHandler> targetCapability = target.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
                IPlayerHandler targetcap = targetCapability.orElse(new PlayerCapability());
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketModeSync(targetcap.returnPlayerMode().getName(), targetID, true));
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketSpellModeToggle(true, targetcap.returnSpellModeToggle(), targetID));
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketToggleInfusionBoolean(1, true, targetcap.ReturnManaSkinToggled(), targetID));
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketToggleInfusionBoolean(2, true, targetcap.returnReinforcementToggled(), targetID));
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketSetGrimoire(targetcap.returnHasGrimoire(), true, targetID));
                NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketSetGrimoireTexture(targetcap.getGrimoireTexture(), true, targetID));
                for (BCMSpell spell : BCMRegistry.SPELLS.getValues()) {
                    NetworkLoader.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> (ServerPlayerEntity) event.getPlayer()), new PacketSpellNBTSync(targetID, spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName(), target.getPersistentData().getBoolean(spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName())));
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        for (BCMSpell spell : BCMRegistry.SPELLS.getValues()) {
            if (source.getDirectEntity() instanceof PlayerEntity) {
                PlayerEntity attacker = (PlayerEntity) source.getDirectEntity();
                LivingEntity target = event.getEntityLiving();
                if (attacker.getPersistentData().getBoolean(spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName())) {
                    spell.throwAttackEvent(attacker, target);
                }
                if (target instanceof PlayerEntity && target.getPersistentData().getBoolean(spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName())) {
                    if (spell.throwDamageEvent(event.getAmount(), event.getSource(), (PlayerEntity) target)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            for (BCMSpell spell : BCMRegistry.SPELLS.getValues()) {
                if (event.getEntity().getPersistentData().getBoolean(spell.getCorrelatedPlugin().getPluginId() + "_" + spell.getName())) {
                    if (spell.throwDeathEvent((PlayerEntity) event.getEntity())) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }


}

