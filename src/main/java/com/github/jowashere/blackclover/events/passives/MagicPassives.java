package com.github.jowashere.blackclover.events.passives;

import com.github.jowashere.blackclover.Main;
import com.github.jowashere.blackclover.capabilities.player.IPlayerHandler;
import com.github.jowashere.blackclover.capabilities.player.PlayerCapability;
import com.github.jowashere.blackclover.capabilities.player.PlayerProvider;
import com.github.jowashere.blackclover.events.bcevents.MagicLevelChangeEvent;
import com.github.jowashere.blackclover.init.ModAttributes;
import com.github.jowashere.blackclover.networking.NetworkLoader;
import com.github.jowashere.blackclover.networking.packets.PacketToggleInfusionBoolean;
import com.github.jowashere.blackclover.networking.packets.mana.PacketManaSync;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class MagicPassives {

    @Mod.EventBusSubscriber(modid = Main.MODID)
    public static class CommonEvents
    {
        private static final AttributeModifier STEP_HEIGHT = new AttributeModifier(UUID.fromString("59406dcb-fd62-4e58-b6ca-87a071344b91"), "Step Height Multiplier", 1, AttributeModifier.Operation.ADDITION);
        private static final AttributeModifier FALL_RESISTANCE = new AttributeModifier(UUID.fromString("e81580bf-c648-4363-9d80-038b84af2364"), "Fall Resistance", 7, AttributeModifier.Operation.ADDITION);

        @SubscribeEvent
        public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event)
        {
            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            World world = player.level;

            if(world.isClientSide)
                return;

            LazyOptional<IPlayerHandler> capabilities = player.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler playercap = capabilities.orElse(new PlayerCapability());

            if (playercap.HasManaBoolean())
            {
                if(playercap.ReturnManaSkinToggled()){
                    if(!player.getAttribute(ModAttributes.DAMAGE_REDUCTION.get()).hasModifier(getResistanceModifier(player)))
                        player.getAttribute(ModAttributes.DAMAGE_REDUCTION.get()).addTransientModifier(getResistanceModifier(player));

                    if(!player.getAttribute(ModAttributes.FALL_RESISTANCE.get()).hasModifier(FALL_RESISTANCE))
                        player.getAttribute(ModAttributes.FALL_RESISTANCE.get()).addTransientModifier(FALL_RESISTANCE);

                    if(!player.getAttribute(Attributes.ARMOR).hasModifier(getArmourModifier(player)))
                        player.getAttribute(Attributes.ARMOR).addTransientModifier(getArmourModifier(player));

                    if(!player.getAttribute(Attributes.ARMOR_TOUGHNESS).hasModifier(getArmourModifier(player)))
                        player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(getArmourModifier(player));

                    if(playercap.ReturnMagicLevel() >= 55)
                        player.fallDistance = 0;

                    if (playercap.returnMana() > 3) {
                        player.getPersistentData().putInt("manaskintick", player.getPersistentData().getInt("manaskintick") + 1);
                        if (player.getPersistentData().getInt("manaskintick") >= 20) {

                            playercap.addMana((float) -3);
                            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketManaSync(playercap.returnMana()));
                            player.getPersistentData().putInt("manaskintick", 0);
                        }
                    } else {
                        playercap.setManaSkinToggled(false);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketToggleInfusionBoolean(1, true,false, player.getId()));
                        player.displayClientMessage(new TranslationTextComponent("spell." + Main.MODID + ".message.notenoughmana"), true);
                    }

                }else {
                    removeManaSkinAttributes(player);
                }


                if(playercap.returnReinforcementToggled()){
                    if(player.isSprinting()){
                        if(!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(getSpeedModifier(player)))
                            player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(getSpeedModifier(player));
                    }else{
                        if(player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(getSpeedModifier(player)))
                            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(getSpeedModifier(player));
                    }

                    if(!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(getStrengthModifier(player)))
                        player.getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(getStrengthModifier(player));

                    if(!player.getAttribute(ModAttributes.STEP_HEIGHT.get()).hasModifier(STEP_HEIGHT))
                        player.getAttribute(ModAttributes.STEP_HEIGHT.get()).addTransientModifier(STEP_HEIGHT);

                    if(!player.isCrouching()){
                        if(!player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).hasModifier(getJumpModifier(player)) && !player.isCrouching())
                            player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).addTransientModifier(getJumpModifier(player));
                    }else {
                        if(player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).hasModifier(getJumpModifier(player)))
                            player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).removeModifier(getJumpModifier(player));
                    }

                    if (playercap.returnMana() > 4) {
                        player.getPersistentData().putInt("reinforcementtick", player.getPersistentData().getInt("reinforcementtick") + 1);
                        if (player.getPersistentData().getInt("reinforcementtick") >= 20) {

                            playercap.addMana((float) -4);
                            NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketManaSync(playercap.returnMana()));
                            player.getPersistentData().putInt("reinforcementtick", 0);
                        }
                    } else {
                        playercap.setReinforcementToggled(false);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketToggleInfusionBoolean(2, true,false, player.getId()));
                        player.displayClientMessage(new TranslationTextComponent("spell." + Main.MODID + ".message.notenoughmana"), true);
                    }
                }else {
                    removeReinforcementAttributes(player);
                }
            } else {
                removeReinforcementAttributes(player);
                removeManaSkinAttributes(player);
            }
        }

        @SubscribeEvent
        public static void onLevelChange(MagicLevelChangeEvent event){

            World world = event.getPlayer().level;

            if(world.isClientSide)
                return;

            removeManaSkinAttributes(event.getPlayer());
            removeReinforcementAttributes(event.getPlayer());
        }

        public static void removeManaSkinAttributes(PlayerEntity player) {

            if(player.getAttribute(ModAttributes.DAMAGE_REDUCTION.get()).hasModifier(getResistanceModifier(player)))
                player.getAttribute(ModAttributes.DAMAGE_REDUCTION.get()).removeModifier(getResistanceModifier(player));

            if(player.getAttribute(ModAttributes.FALL_RESISTANCE.get()).hasModifier(FALL_RESISTANCE))
                player.getAttribute(ModAttributes.FALL_RESISTANCE.get()).removeModifier(FALL_RESISTANCE);

            if(player.getAttribute(Attributes.ARMOR).hasModifier(getArmourModifier(player)))
                player.getAttribute(Attributes.ARMOR).removeModifier(getArmourModifier(player));

            if(player.getAttribute(Attributes.ARMOR_TOUGHNESS).hasModifier(getArmourModifier(player)))
                player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(getArmourModifier(player));

        }

        public static void removeReinforcementAttributes(PlayerEntity player) {

            if(player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(getSpeedModifier(player)))
                player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(getSpeedModifier(player));

            if(player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(getStrengthModifier(player)))
                player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(getStrengthModifier(player));

            if(player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).hasModifier(getJumpModifier(player)))
                player.getAttribute(ModAttributes.JUMP_HEIGHT.get()).removeModifier(getJumpModifier(player));

            if(player.getAttribute(ModAttributes.STEP_HEIGHT.get()).hasModifier(STEP_HEIGHT))
                player.getAttribute(ModAttributes.STEP_HEIGHT.get()).removeModifier(STEP_HEIGHT);

        }

        private static AttributeModifier getArmourModifier(PlayerEntity playerEntity) {
            LazyOptional<IPlayerHandler> playerInCap = playerEntity.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler player_cap = playerInCap.orElse(new PlayerCapability());
            return new AttributeModifier(UUID.fromString("33280017-3519-4ed6-8a9e-b6ac952b6cd5"), "Mana Skin Armour Modifier"
                    , 3 + ((float)player_cap.ReturnMagicLevel()/20), AttributeModifier.Operation.ADDITION);
        }

        private static AttributeModifier getResistanceModifier(PlayerEntity playerEntity) {
            LazyOptional<IPlayerHandler> playerInCap = playerEntity.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler player_cap = playerInCap.orElse(new PlayerCapability());
            return new AttributeModifier(UUID.fromString("44599c7d-fbdf-4863-b89f-bc53f23707ff"), "Mana Skin Resistance Modifier"
                    , (player_cap.ReturnMagicLevel()/100)*0.75, AttributeModifier.Operation.ADDITION);
        }

        private static AttributeModifier getStrengthModifier(PlayerEntity playerEntity) {
            LazyOptional<IPlayerHandler> playerInCap = playerEntity.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler player_cap = playerInCap.orElse(new PlayerCapability());
            return new AttributeModifier(UUID.fromString("4d54f651-cf58-4157-8138-4a206129f023"), "Reinforcement Strength Modifier",
                    2 + (player_cap.ReturnMagicLevel()/10), AttributeModifier.Operation.ADDITION);

        }

        private static AttributeModifier getSpeedModifier(PlayerEntity playerEntity) {
            LazyOptional<IPlayerHandler> playerInCap = playerEntity.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler player_cap = playerInCap.orElse(new PlayerCapability());
            return new AttributeModifier(UUID.fromString("6bec2c8f-a85b-4955-9043-a473d59031b3"), "Reinforcement Speed Modifier",
                    0.02 * player_cap.ReturnMagicLevel(), AttributeModifier.Operation.MULTIPLY_BASE);
        }

        private static AttributeModifier getJumpModifier(PlayerEntity playerEntity) {
            LazyOptional<IPlayerHandler> playerInCap = playerEntity.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler player_cap = playerInCap.orElse(new PlayerCapability());
            return new AttributeModifier(UUID.fromString("1f08e9ed-f825-4fa3-a3e1-dd7cdf32aa3a"), "Reinforcement Jump Modifier",
                    1 + ((player_cap.ReturnMagicLevel()/100)*3), AttributeModifier.Operation.ADDITION);

        }

    }




}