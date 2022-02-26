package com.github.jowashere.blackclover.spells.light;

import com.github.jowashere.blackclover.api.internal.AbstractToggleSpell;
import com.github.jowashere.blackclover.init.AttributeInit;
import com.github.jowashere.blackclover.particles.light.LightParticleData;
import com.github.jowashere.blackclover.util.helpers.BCMHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.awt.*;

public class LightHealing extends AbstractToggleSpell {

    public LightHealing() {
        super("light_healing", AttributeInit.LIGHT);

        this.setManaCost(0.95F);
        this.setCooldown(120);
        this.setToggleTimer(60);
        this.setUnlockLevel(20);
        this.setUV(80, 64);

        this.action = this::action;

    }

    public void action(LivingEntity caster, float manaIn) {
        if (!caster.level.isClientSide) {
            LightParticleData lightParticleData = new LightParticleData(new Color(255, 255, 255),  0.5);

            int magicLevel = BCMHelper.getMagicLevel(caster);

            caster.addEffect(new EffectInstance(Effects.REGENERATION, 5, Math.max(1, magicLevel/2), false, false, false));
            //IPacket<?> ipacket = new SSpawnParticlePacket(lightParticleData, true, caster.getX(), caster.getY(), caster.getZ(), 2, 0, 1, 0, 1);

            //for (int j = 0; j < caster.level.players().size(); ++j)
            //{
              //  ServerPlayerEntity player = (ServerPlayerEntity) caster.level.players().get(j);
                //BlockPos blockpos = new BlockPos(player.getX(), player.getY(), player.getZ());
                //if (blockpos.closerThan(new Vector3d(caster.getX(), caster.getY(), caster.getZ()), 512))
                //{
                 //   player.connection.send(ipacket);
               // }
            //}
            ((ServerWorld) caster.level).sendParticles(lightParticleData,
                    caster.getX(), caster.getY(), caster.getZ(),
                    2, 0, 1, 0, 0.1);
        }
    }
}
