/*
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

package de.markusbordihn.easynpc.client.screen.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.markusbordihn.easynpc.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class Checkbox extends AbstractButton {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/gui/checkbox.png");

  protected final Checkbox.OnChange onChange;
  private final boolean showLabel;
  private final Font font;
  private boolean selected;

  public Checkbox(int left, int top, String label, boolean selected, Checkbox.OnChange onChange) {
    this(
        left,
        top,
        label != null && !label.isBlank() && Character.isLowerCase(label.codePointAt(0))
            ? Component.translatable(Constants.TEXT_CONFIG_PREFIX + label)
            : Component.literal(label != null ? label : ""),
        selected,
        true,
        onChange);
  }

  public Checkbox(
      int left, int top, String label, Object data, boolean selected, Checkbox.OnChange onChange) {
    this(
        left,
        top,
        label != null && !label.isBlank() && Character.isLowerCase(label.codePointAt(0))
            ? Component.translatable(Constants.TEXT_CONFIG_PREFIX + label, data)
            : Component.literal(label != null ? label : ""),
        selected,
        true,
        onChange);
  }

  public Checkbox(int left, int top, String label, boolean selected) {
    this(left, top, Component.translatable(Constants.TEXT_CONFIG_PREFIX + label), selected, true);
  }

  public Checkbox(int left, int top, Component component, boolean selected, boolean showLabel) {
    this(left, top, component, selected, showLabel, null);
  }

  public Checkbox(
      int left,
      int top,
      Component component,
      boolean selected,
      boolean showLabel,
      Checkbox.OnChange onChange) {
    super(left, top, 16, 16, component);
    this.selected = selected;
    this.showLabel = showLabel;
    Minecraft minecraft = Minecraft.getInstance();
    this.font = minecraft.font;
    this.onChange = onChange;
  }

  public void onPress() {
    this.selected = !this.selected;
    if (this.onChange != null) {
      this.onChange.onChange(this);
    }
  }

  public boolean selected() {
    return this.selected;
  }

  public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    if (this.active) {
      if (this.isFocused()) {
        narrationElementOutput.add(
            NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
      } else {
        narrationElementOutput.add(
            NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
      }
    }
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderButton(guiGraphics, mouseX, mouseY, partialTicks);
  }

  public void renderButton(GuiGraphics guiGraphics, int left, int top, float partialTicks) {
    RenderSystem.enableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.blendFunc(
        GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    guiGraphics.blit(
        TEXTURE,
        this.getX(),
        this.getY(),
        this.active ? (this.isHoveredOrFocused() ? 16.0F : 0.0F) : 32.0F,
        this.selected ? 16.0F : 0.0F,
        16,
        16,
        64,
        64);
    if (this.showLabel) {
      Text.drawString(
          guiGraphics,
          this.font,
          this.getMessage(),
          this.getX() + 18,
          this.getY() + (this.height - 8) / 2);
    }
  }

  public interface OnChange {

    void onChange(Checkbox checkbox);
  }
}
