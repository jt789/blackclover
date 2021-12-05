package com.github.jowashere.blackclover.common.spells.adders;

import com.github.jowashere.blackclover.api.BCMRegistry;
import com.github.jowashere.blackclover.api.internal.BCMSpell;
import com.github.jowashere.blackclover.capabilities.player.IPlayerHandler;
import com.github.jowashere.blackclover.capabilities.player.PlayerCapability;
import com.github.jowashere.blackclover.capabilities.player.PlayerProvider;
import com.github.jowashere.blackclover.networking.NetworkLoader;
import com.github.jowashere.blackclover.networking.packets.spells.PacketSetSpellBoolean;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class AddAntiMagicSpells extends AbstractAddSpells{

    @Override
    public void add(PlayerEntity player) {
        if (player.isAlive()) {

            LazyOptional<IPlayerHandler> capabilities = player.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
            IPlayerHandler playercap = capabilities.orElse(new PlayerCapability());

            for (BCMSpell spell : BCMRegistry.SPELLS.getValues()) {

                if(spell.getName().equals("bull_thrust")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("black_mode")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("black_slash")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("black_divider")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("causality_break")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("causality_break_self")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }

                if(spell.getName().equals("black_meteorite")){
                    if(!playercap.hasSpellBoolean(spell)){
                        playercap.setSpellBoolean(spell, true);
                        NetworkLoader.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketSetSpellBoolean(spell.getName(), true,true));
                    }
                }
            }
        }
    }

}