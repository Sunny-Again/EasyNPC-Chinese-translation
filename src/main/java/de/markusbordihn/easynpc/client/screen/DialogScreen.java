/**
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.dialog.DialogType;
import de.markusbordihn.easynpc.dialog.DialogUtils;
import de.markusbordihn.easynpc.entity.EasyNPCEntity;
import de.markusbordihn.easynpc.menu.DialogMenu;

@OnlyIn(Dist.CLIENT)
public class DialogScreen extends AbstractContainerScreen<DialogMenu> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final EasyNPCEntity entity;

  // Internal
  protected Button yesDialogButton = null;
  protected Button noDialogButton = null;
  protected float xMouse;
  protected float yMouse;
  private List<FormattedCharSequence> cachedDialogComponents = Collections.emptyList();

  // Dialog Options
  protected DialogType dialogType = DialogType.BASIC;
  protected String dialog;
  protected TextComponent dialogComponent;
  protected int numberOfDialogLines = 1;

  public DialogScreen(DialogMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.entity = menu.getEntity();
  }

  protected void renderDialog(PoseStack poseStack) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DIALOG);

    // Dialog background according numbers of lines.
    int minNumberOfLines = Math.max(2, this.numberOfDialogLines);
    int backgroundShift = minNumberOfLines * (font.lineHeight + 2);
    this.blit(poseStack, leftPos + 70, topPos + 10 + 30, 0, 130 - backgroundShift, 200,
        Math.min(120, backgroundShift));
    this.blit(poseStack, leftPos + 70, topPos + 10, 0, 0, 200, 30);

    // Distribute text for the across the lines.
    if (!this.cachedDialogComponents.isEmpty()) {
      for (int line = 0; line < this.numberOfDialogLines; ++line) {
        FormattedCharSequence formattedCharSequence = this.cachedDialogComponents.get(line);
        this.font.draw(poseStack, formattedCharSequence, leftPos + 87f,
            topPos + 17f + (line * (font.lineHeight + 2)), 0);
      }
    }
  }

  private void setDialog(String text) {
    if (text == null || text.isBlank()) {
      return;
    }

    // Parse dialog Text and replace placeholders.
    Minecraft minecraft = this.minecraft;
    this.dialog =
        DialogUtils.parseDialog(text, this.entity, minecraft != null ? minecraft.player : null);
    this.dialogComponent = new TextComponent(this.dialog);

    // Split dialog text to lines.
    this.cachedDialogComponents = this.font.split(this.dialogComponent, 176);
    this.numberOfDialogLines = Math.min(128 / font.lineHeight, this.cachedDialogComponents.size());
  }

  @Override
  public void init() {
    super.init();

    // Pre-Work and Pre-Cache
    if (this.entity != null) {
      // Dialog text.
      this.dialogType = this.entity.getDialogType();
      setDialog(this.entity.getDialog());

      // Render additional Buttons for Yes/No Dialog.
      if (this.dialogType == DialogType.YES_NO) {
        int dialogButtonTop = this.topPos + 55 + (numberOfDialogLines * (font.lineHeight));
        this.yesDialogButton =
            this.addRenderableWidget(new Button(this.leftPos + 20, dialogButtonTop, 95, 20,
                new TextComponent(this.entity.getYesDialogButton()), onPress -> {
                  log.info("Yes Dialog ...");
                  setDialog(this.entity.getYesDialog());
                  this.yesDialogButton.visible = false;
                  this.noDialogButton.visible = false;
                }));
        this.noDialogButton =
            this.addRenderableWidget(new Button(this.leftPos + 125, dialogButtonTop, 95, 20,
                new TextComponent(this.entity.getNoDialogButton()), onPress -> {
                  log.info("No Dialog ...");
                  setDialog(this.entity.getNoDialog());
                  this.yesDialogButton.visible = false;
                  this.noDialogButton.visible = false;
                }));
      }
    }

    // Default stats
    this.imageHeight = 170;
    this.imageWidth = 275;

    // Basic Position
    this.titleLabelX = 8;
    this.titleLabelY = 6;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.leftPos = (this.width - this.imageWidth) / 2;
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    super.render(poseStack, x, y, partialTicks);
    this.xMouse = x;
    this.yMouse = y;

    if (this.entity == null) {
      return;
    }

    // Render Avatar
    int avatarPositionTop = 55 + this.entity.getEntityDialogTop();
    int left = this.leftPos + 40;
    int top = this.topPos + 60 + avatarPositionTop;
    ScreenHelper.renderEntityDialog(left, top, Math.round(left - 140 - (this.xMouse * 0.25)),
        Math.round(top - 120 - (this.yMouse * 0.5)), this.entity);

    // Render Dialog
    renderDialog(poseStack);
  }

  @Override
  protected void renderLabels(PoseStack poseStack, int x, int y) {
    this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, Constants.TEXTURE_DEMO_BACKGROUND);

    // Main screen
    this.blit(poseStack, leftPos, topPos, 0, 0, 250, 170);
    this.blit(poseStack, leftPos + 243, topPos, 215, 0, 35, 170);
  }

}