package com.github.jowashere.blackclover.client.gui.player.spells;

import com.github.jowashere.blackclover.Main;
import com.github.jowashere.blackclover.api.internal.BCMSpell;
import com.github.jowashere.blackclover.capabilities.player.IPlayerHandler;
import com.github.jowashere.blackclover.capabilities.player.PlayerCapability;
import com.github.jowashere.blackclover.capabilities.player.PlayerProvider;
import com.github.jowashere.blackclover.client.gui.widgets.spells.GuiButtonSpell;
import com.github.jowashere.blackclover.init.KeybindInit;
import com.github.jowashere.blackclover.networking.NetworkLoader;
import com.github.jowashere.blackclover.networking.packets.settings.PacketKeybindSet;
import com.github.jowashere.blackclover.util.helpers.GUIHelper;
import com.github.jowashere.blackclover.util.helpers.SpellHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

public abstract class AbstractSpellScreen extends Screen {

    public String spellToggle = "";
    final ITextComponent guiTitle;
    public int guiLeft;
    public int guiTop;

    AbstractClientPlayerEntity player;
    IPlayerHandler playerc;

    public abstract void registerSpells(IPlayerHandler playerCapability);
    public abstract void setSpellsBooleans(IPlayerHandler playerCapability);

    protected AbstractSpellScreen(ITextComponent titleIn) {
        super(titleIn);
        this.guiTitle = titleIn;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }else {
            boolean handled = this.assignSpells(keyCode, scanCode);
            return handled;
        }
    }

    protected boolean assignSpells(int keyCode, int scanCode) {

        int intendedKey = 0;

        if(KeybindInit.KEYBIND1.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 1;
        }else if(KeybindInit.KEYBIND2.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 2;
        }else if(KeybindInit.KEYBIND3.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 3;
        }else if(KeybindInit.KEYBIND4.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 4;
        }else if(KeybindInit.KEYBIND5.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 5;
        }else if(KeybindInit.KEYBIND6.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 6;
        }else if(KeybindInit.KEYBIND7.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 7;
        }else if(KeybindInit.KEYBIND8.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 8;
        }else if(KeybindInit.KEYBIND9.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))){
            intendedKey = 9;
        }

        for (int i = 1; i < 10; i ++){
            if(i == intendedKey){
                playerc.setKeybind(i, this.spellToggle);
                NetworkLoader.INSTANCE.sendToServer(new PacketKeybindSet(i, this.spellToggle, false));
                player.sendMessage(new StringTextComponent("Keybind " + i + " Set to: " + new TranslationTextComponent(this.spellToggle).getString()), player.getUUID());
                this.spellToggle = "";
            }else if(i != intendedKey && playerc.returnKeybind(i) == this.spellToggle){
                playerc.setKeybind(i, "");
                NetworkLoader.INSTANCE.sendToServer(new PacketKeybindSet(i, "", false));
            }
        }

        return false;
    }

    @Override
    protected void init() {
        buttons.clear();
        this.guiLeft = (this.width) / 2;
        this.guiTop = (this.height) / 2;
        Minecraft mc = Minecraft.getInstance();
        this.player = mc.player;
        LazyOptional<IPlayerHandler> player_cap = player.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
        this.playerc = player_cap.orElse(new PlayerCapability());

        registerSpells(playerc);

    }

    @Override
    public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        Minecraft mc = Minecraft.getInstance();
        LazyOptional<IPlayerHandler> player_cap = mc.player.getCapability(PlayerProvider.CAPABILITY_PLAYER, null);
        IPlayerHandler playerc = player_cap.orElse(new PlayerCapability());

        setSpellsBooleans(playerc);

        mc.textureManager.bind(new ResourceLocation(Main.MODID + ":textures/gui/magestatsbackground.png"));
        mc.gui.blit(matrixStack,this.guiLeft - 113, this.guiTop - 120, 0, 0, 227, 241);
        GUIHelper.drawStringWithBorder(matrixStack, font, this.guiTitle.getString(), this.guiLeft - (font.width(this.guiTitle.getString()) / 2), this.guiTop - 105, 0x2B2B2B);

        for (Widget button : this.buttons) {
            button.renderButton(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        }
        this.checkToggled(matrixStack);
        this.checkHovered(matrixStack, p_render_1_, p_render_2_);
        this.checkCovered(matrixStack);

        int posX = mc.getWindow().getGuiScaledWidth()  - 23;
        int posY = mc.getWindow().getGuiScaledHeight() - 1;

        boolean hasColorVisual = true;

        ResourceLocation WIDGETS = new ResourceLocation(Main.MODID + ":textures/gui/widgets.png");

        GlStateManager._pushMatrix();
        {
            GlStateManager._color4f(1, 1, 1, 1);
            GlStateManager._disableLighting();
            mc.getTextureManager().bind(WIDGETS);

            for (int i = 0; i < 9; i++)
            {
                BCMSpell spell = null;

                if(i == 0){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(1));
                }else if (i == 1){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(2));
                }else if (i == 2){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(3));
                }else if (i == 3){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(4));
                }else if (i == 4){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(5));
                }else if (i == 5){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(6));
                }else if (i == 6){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(7));
                }else if (i == 7){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(8));
                }else if (i == 8){
                    spell = SpellHelper.getSpellFromName(playerc.returnKeybind(9));
                }

                if(spell == null)
                {
                    GuiUtils.drawTexturedModalRect((posX - 200 + (i * 50)) / 2, posY - 23, 0, 0, 23, 23, 0);
                    continue;
                }

                String number = "";

                float threshold = 23;
                float charge = 23;

                // Drawing the slot
                GuiUtils.drawTexturedModalRect( matrixStack, (posX - 200 + (i * 50)) / 2, posY - 23, 0, 0, 23, 23, 0);
                // Reverting the color back to avoid future slots being wrongly colored
                GlStateManager._color4f(1, 1, 1, 1);

                // Drawing the spells
                mc.getTextureManager().bind(spell.getResourceLocationForGUI());
                GuiUtils.drawTexturedModalRect( matrixStack, ((posX - 200 + (i * 50)) / 2) + 4, posY - 19, spell.getU(), spell.getV(), 16, 16, 0);

                // Reverting the color back to avoid future slots being wrongly colored
                GlStateManager._color4f(1, 1, 1, 1);

                // Drawing the ability icons
                GlStateManager._translated(0, 0, 2);
                mc.getTextureManager().bind(WIDGETS);
            }

            //GlStateManager.disableBlend();
        }
        GlStateManager._popMatrix();

    }

    public void checkHovered(MatrixStack matrixStack, int p_render_1_, int p_render_2_)
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.002F);
        GL11.glPushMatrix();

        for (Widget button : this.buttons) {
            if (button.isHovered() && button instanceof GuiButtonSpell) {
                renderTooltip(matrixStack, new StringTextComponent(new TranslationTextComponent("spell." + ((GuiButtonSpell) button).getTranslationName()).getString()), p_render_1_, p_render_2_);
            }
        }
        GL11.glPopMatrix();
    }

    public void checkCovered(MatrixStack matrixStack) {
        Minecraft mc = Minecraft.getInstance();
        mc.textureManager.bind(new ResourceLocation(Main.MODID, "textures/gui/spells.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.002F);
        GL11.glPushMatrix();
        for (Widget button : this.buttons) {
            if (button instanceof GuiButtonSpell) {
                if (!((GuiButtonSpell) button).hasSpell()) {
                    mc.gui.blit(matrixStack, ((GuiButtonSpell) button).widthIn, ((GuiButtonSpell) button).heightIn, 496, 496, 16, 16, 512, 512);
                }
            }
        }
        GL11.glPopMatrix();
    }

    public void checkToggled(MatrixStack matrixStack)
    {
        Minecraft mc = Minecraft.getInstance();
        mc.textureManager.bind(new ResourceLocation(Main.MODID, "textures/gui/spells.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.002F);
        GL11.glPushMatrix();
        for (Widget button : this.buttons) {
            if (button instanceof GuiButtonSpell && this.spellToggle.equalsIgnoreCase("spell." + ((GuiButtonSpell) button).getTranslationName())) {
                mc.gui.blit(matrixStack, ((GuiButtonSpell) button).widthIn, ((GuiButtonSpell) button).heightIn, 496, 480, 16,16, 512, 512);
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
