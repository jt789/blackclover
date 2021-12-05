package com.github.jowashere.blackclover.init;

import com.github.jowashere.blackclover.api.internal.BCMAttribute;
import com.github.jowashere.blackclover.api.internal.BCMSpell;
import com.github.jowashere.blackclover.common.spells.adders.*;
import com.github.jowashere.blackclover.events.GrimoireTextures;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AttributeInit {

    public static final BCMAttribute NULL = new BCMAttribute("null", 0, false,  null);

    public static final List<Supplier<BCMAttribute>> attributeList = new ArrayList<>();

    public static final BCMAttribute WIND = new BCMAttribute("wind", 30, false, 0, 16,  BCMSpell.Type.WIND_MAGIC).setAttributeMessage("You have Wind Magic.").setAttributeColour(1).setSpellAdder(new AddWindSpells()).setGrimoireTextures(GrimoireTextures.WindGrimoires);
    public static final BCMAttribute LIGHTNING = new BCMAttribute("lightning", 30, false, 0, 48,  BCMSpell.Type.LIGHTNING_MAGIC).setAttributeMessage("You have Lightning Magic").setAttributeColour(3).setSpellAdder(new AddLightningSpells()).setGrimoireTextures(GrimoireTextures.LightningGrimoires);
    public static final BCMAttribute SWORD = new BCMAttribute("sword", 20, false, 0, 0,  BCMSpell.Type.SWORD_MAGIC).setAttributeMessage("You have Sword Magic").setAttributeColour(8).setSpellAdder(new AddAntiMagicSpells()).setGrimoireTextures(GrimoireTextures.AntiMagicGrimoires);
    public static final BCMAttribute SLASH = new BCMAttribute("slash", 20, false, 0, 0,  BCMSpell.Type.SLASH_MAGIC).setAttributeMessage("You have Slash Magic.").setAttributeColour(1).setSpellAdder(new AddSlashSpells()).setGrimoireTextures(GrimoireTextures.AntiMagicGrimoires);
    public static final BCMAttribute DARKNESS = new BCMAttribute("darkness", 10, false, 0, 64,  BCMSpell.Type.DARKNESS_MAGIC).setAttributeMessage("You have Darkness Magic.").setAttributeColour(2).setSpellAdder(new AddDarknessSpells()).setGrimoireTextures(GrimoireTextures.DarknessGrimoires);
    public static final BCMAttribute LIGHT = new BCMAttribute("light", 5, false, 0, 64,  BCMSpell.Type.LIGHT_MAGIC).setAttributeMessage("You have Light Magic.").setAttributeColour(8).setSpellAdder(new AddLightSpells()).setGrimoireTextures(GrimoireTextures.DarknessGrimoires);
    public static final BCMAttribute ANTI_MAGIC = new BCMAttribute("antimagic", 1, false, 0, 0,  BCMSpell.Type.ANTI_MAGIC).setAttributeMessage("You have no magic.").setAttributeColour(0).setSpellAdder(new AddAntiMagicSpells()).setGrimoireTextures(GrimoireTextures.AntiMagicGrimoires);

}
