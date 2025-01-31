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

import de.markusbordihn.easynpc.client.screen.components.SliderButton.Type;
import de.markusbordihn.easynpc.network.components.TextComponent;
import de.markusbordihn.easynpc.utils.ValueUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class RangeSliderButton extends AbstractWidget {

  public static final Component DECREASE_TEXT = TextComponent.getText("-");
  public static final Component INCREASE_TEXT = TextComponent.getText("+");
  public static final Component RESET_TEXT = TextComponent.getText("↺");
  public static final Component EDIT_TEXT = TextComponent.getText("✎");
  public static final Component DONE_TEXT = TextComponent.getText("✔");
  private static final int DEFAULT_WIDTH = 170;
  private static final int DEFAULT_HEIGHT = 14;
  private static final int DECREASE_BUTTON_WIDTH = 12;
  private static final int INCREASE_BUTTON_WIDTH = 12;
  private static final int RESET_BUTTON_WIDTH = 12;
  private static final int EDIT_BUTTON_WIDTH = 12;
  private final SliderButton sliderButton;
  private final TextButton textButtonDecrease;
  private final TextButton textButtonIncrease;
  private final TextButton textButtonReset;
  private final TextButton textButtonEdit;
  private final TextButton textButtonDone;
  private final TextField textField;
  private final SliderButton.Type sliderType;
  private final boolean showButtons;

  public RangeSliderButton(
      int left,
      int top,
      String label,
      double value,
      double minValue,
      double maxValue,
      double defaultValue,
      double stepSize,
      SliderButton.OnChange onChange) {
    this(
        left,
        top,
        DEFAULT_WIDTH,
        DEFAULT_HEIGHT,
        label,
        value,
        minValue,
        maxValue,
        defaultValue,
        stepSize,
        onChange);
  }

  public RangeSliderButton(
      int left,
      int top,
      int width,
      int height,
      double value,
      double defaultValue,
      SliderButton.Type sliderType,
      SliderButton.OnChange onChange) {
    this(
        left,
        top,
        width,
        height,
        sliderType.name(),
        value,
        SliderButton.getMinValue(sliderType),
        SliderButton.getMaxValue(sliderType),
        defaultValue,
        SliderButton.getStepSize(sliderType),
        onChange);
  }

  public RangeSliderButton(
      int left,
      int top,
      int width,
      int height,
      double value,
      double defaultValue,
      SliderButton.Type sliderType,
      boolean showButtons,
      SliderButton.OnChange onChange) {
    this(
        left,
        top,
        width,
        height,
        sliderType.name(),
        value,
        SliderButton.getMinValue(sliderType),
        SliderButton.getMaxValue(sliderType),
        defaultValue,
        SliderButton.getStepSize(sliderType),
        sliderType,
        showButtons,
        onChange);
  }

  public RangeSliderButton(
      int left,
      int top,
      int width,
      int height,
      String label,
      double value,
      double minValue,
      double maxValue,
      double defaultValue,
      double stepSize,
      SliderButton.OnChange onChange) {
    this(
        left,
        top,
        width,
        height,
        label,
        value,
        minValue,
        maxValue,
        defaultValue,
        stepSize,
        Type.DOUBLE,
        true,
        onChange);
  }

  public RangeSliderButton(
      int left,
      int top,
      int width,
      int height,
      String label,
      double value,
      double minValue,
      double maxValue,
      double defaultValue,
      double stepSize,
      SliderButton.Type sliderType,
      boolean showButtons,
      SliderButton.OnChange onChange) {
    super(left, top, width, height, TextComponent.getBlankText());
    Font font = Minecraft.getInstance().font;
    this.sliderType = sliderType;
    this.showButtons = showButtons;
    this.sliderButton =
        new SliderButton(
            this.showButtons ? left + DECREASE_BUTTON_WIDTH : left,
            top,
            this.getDefaultSliderWidth(),
            height,
            value,
            minValue,
            maxValue,
            button -> updateSliderValue(button, onChange),
            this.sliderType);
    this.textField =
        switch (this.sliderType) {
          case DEGREE ->
              new DegreeNumberField(
                  font,
                  this.showButtons ? left + DECREASE_BUTTON_WIDTH : left,
                  top,
                  this.getDefaultSliderWidth(),
                  height,
                  value);
          case POSITION ->
              new PositionNumberField(
                  font,
                  this.showButtons ? left + DECREASE_BUTTON_WIDTH : left,
                  top,
                  this.getDefaultSliderWidth(),
                  height,
                  value,
                  minValue,
                  maxValue);
          default ->
              new PositiveNumberField(
                  font,
                  this.showButtons ? left + DECREASE_BUTTON_WIDTH : left,
                  top,
                  this.getDefaultSliderWidth(),
                  height,
                  value);
        };
    this.textField.setResponder(
        text -> {
          if (ValueUtils.isDoubleValue(text, minValue, maxValue)) {
            this.sliderButton.setDefaultValue(Double.parseDouble(text));
          }
        });
    this.textButtonDecrease =
        new TextButton(
            this.sliderButton.getX() - DECREASE_BUTTON_WIDTH,
            top,
            DECREASE_BUTTON_WIDTH,
            height,
            DECREASE_TEXT,
            button -> {
              if (this.sliderButton.getTargetDoubleValue() - stepSize >= minValue) {
                this.sliderButton.setDefaultValue(
                    this.sliderButton.getTargetDoubleValue() - stepSize);
                this.updateTextField();
              }
            });
    this.textButtonDecrease.active = showButtons;
    this.textButtonIncrease =
        new TextButton(
            this.sliderButton.getX() + this.sliderButton.getWidth(),
            top,
            INCREASE_BUTTON_WIDTH,
            height,
            INCREASE_TEXT,
            button -> {
              if (this.sliderButton.getTargetDoubleValue() + stepSize <= maxValue) {
                this.sliderButton.setDefaultValue(
                    this.sliderButton.getTargetDoubleValue() + stepSize);
                this.updateTextField();
              }
            });
    this.textButtonIncrease.active = showButtons;
    this.textButtonReset =
        new TextButton(
            this.textButtonIncrease.getX() + this.textButtonIncrease.getWidth(),
            top,
            RESET_BUTTON_WIDTH,
            height,
            RESET_TEXT,
            button -> {
              this.sliderButton.setDefaultValue(defaultValue);
              this.updateTextField();
            });
    this.textButtonReset.active = showButtons;
    this.textButtonEdit =
        new TextButton(
            this.textButtonReset.getX() + this.textButtonReset.getWidth(),
            top,
            EDIT_BUTTON_WIDTH,
            height,
            EDIT_TEXT,
            this::showTextField);
    this.textButtonEdit.active = showButtons;
    this.textButtonDone =
        new TextButton(
            this.textButtonReset.getX() + this.textButtonReset.getWidth(),
            top,
            EDIT_BUTTON_WIDTH,
            height,
            DONE_TEXT,
            this::showSliderButton);
    this.textButtonDone.active = showButtons;
  }

  public void reset() {
    this.sliderButton.reset();
    this.updateTextField();
  }

  public int getDefaultSliderWidth() {
    if (this.showButtons) {
      return this.width
          - (DECREASE_BUTTON_WIDTH
              + INCREASE_BUTTON_WIDTH
              + RESET_BUTTON_WIDTH
              + EDIT_BUTTON_WIDTH);
    }
    return this.width;
  }

  private void updateSliderValue(SliderButton sliderButton, SliderButton.OnChange onChange) {
    onChange.onChange(sliderButton);
  }

  private void updateTextField() {
    String sliderValue =
        switch (this.sliderType) {
          case DEGREE ->
              String.format("%.1f", this.sliderButton.getTargetDoubleValue()).replace(",", ".");
          default ->
              String.format("%.2f", this.sliderButton.getTargetDoubleValue()).replace(",", ".");
        };
    if (!this.textField.getValue().equals(sliderValue)) {
      this.textField.setValue(sliderValue);
    }
  }

  public void showTextField() {
    this.showTextField(null);
  }

  public void showSliderButton() {
    this.showSliderButton(null);
  }

  private void showTextField(Button button) {
    this.updateTextField();
    this.sliderButton.visible = false;
    this.sliderButton.setFocused(false);
    this.textButtonEdit.visible = false;
    this.textButtonDone.visible = true;
    this.textField.visible = true;
  }

  private void showSliderButton(Button button) {
    this.sliderButton.visible = true;
    this.textButtonEdit.visible = true;
    this.textButtonDone.visible = false;
    this.textField.visible = false;
    this.textField.setFocused(false);
  }

  public float getTargetValue() {
    return this.sliderButton.getTargetValue();
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    if (sliderButton.isVisible()) {
      sliderButton.render(guiGraphics, mouseX, mouseY, partialTicks);
    } else if (textField.isVisible()) {
      textField.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    if (textButtonDecrease.isActive()) {
      textButtonDecrease.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    if (textButtonIncrease.isActive()) {
      textButtonIncrease.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    if (textButtonReset.isActive()) {
      textButtonReset.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    if (textButtonEdit.isActive() && textButtonEdit.isVisible()) {
      textButtonEdit.render(guiGraphics, mouseX, mouseY, partialTicks);
    } else if (textButtonDone.isActive() && textButtonDone.isVisible()) {
      textButtonDone.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (sliderButton.isVisible() && sliderButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }
    if (textField.isVisible() && textField.mouseClicked(mouseX, mouseY, button)) {
      textField.setFocused(true);
      return true;
    }

    textButtonDecrease.mouseClicked(mouseX, mouseY, button);
    textButtonIncrease.mouseClicked(mouseX, mouseY, button);
    textButtonReset.mouseClicked(mouseX, mouseY, button);

    if (textButtonEdit.isVisible()) {
      textButtonEdit.mouseClicked(mouseX, mouseY, button);
    } else if (textButtonDone.isVisible()) {
      textButtonDone.mouseClicked(mouseX, mouseY, button);
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (textField.isVisible()) {
      textField.mouseReleased(mouseX, mouseY, button);
    }
    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseScrolled(double x, double y, double distance) {
    if (sliderButton.isVisible()) {
      sliderButton.mouseScrolled(x, y, distance);
    } else if (textField.isVisible()) {
      textField.mouseScrolled(x, y, distance);
    }
    return super.mouseScrolled(x, y, distance);
  }

  @Override
  public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
    if (sliderButton.isVisible() && sliderButton.isMouseOver(mouseX, mouseY)) {
      sliderButton.triggerOnDrag(mouseX, mouseY, deltaX, deltaY);
    }
  }

  @Override
  public boolean charTyped(char character, int keyCode) {
    if (sliderButton.isVisible()) {
      return sliderButton.charTyped(character, keyCode);
    } else if (textField.isVisible()) {
      return textField.charTyped(character, keyCode);
    }
    return false;
  }

  @Override
  public boolean keyPressed(int keyCode, int unused1, int unused2) {
    if (sliderButton.isVisible()) {
      return sliderButton.keyPressed(keyCode, unused1, unused2);
    } else if (textField.isVisible()) {
      return textField.keyPressed(keyCode, unused1, unused2);
    }
    return false;
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    if (this.active) {
      String usageKey =
          this.isFocused() ? "narration.slider.usage.focused" : "narration.slider.usage.hovered";
      narrationElementOutput.add(
          NarratedElementType.USAGE, TextComponent.getTranslatedTextRaw(usageKey));
    }
  }
}
