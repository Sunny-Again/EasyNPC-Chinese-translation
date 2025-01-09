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

package de.markusbordihn.easynpc.client.screen.configuration.rotation;

import de.markusbordihn.easynpc.client.screen.components.Checkbox;
import de.markusbordihn.easynpc.client.screen.components.SliderButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.data.model.ModelPart;
import de.markusbordihn.easynpc.data.rotation.CustomRotation;
import de.markusbordihn.easynpc.entity.easynpc.data.ModelData;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import de.markusbordihn.easynpc.network.components.TextComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DefaultRotationConfigurationScreen<T extends ConfigurationMenu>
    extends RotationConfigurationScreen<T> {

  protected Button rootRotationXResetButton;
  protected Button rootRotationYResetButton;
  protected Button rootRotationZResetButton;
  protected SliderButton rootRotationXSliderButton;
  protected SliderButton rootRotationYSliderButton;
  protected SliderButton rootRotationZSliderButton;
  protected Checkbox rootRotationCheckbox;
  protected float rootRotationX = 0f;
  protected float rootRotationY = 0f;
  protected float rootRotationZ = 0f;

  public DefaultRotationConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.showCloseButton = true;
    this.renderBackground = false;
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.defaultRotationButton.active = false;

    // Root Rotations
    ModelData<?> modelData = this.getEasyNPC().getEasyNPCModelData();
    CustomRotation rootRotation = modelData.getModelRootRotation();
    this.rootRotationX = rootRotation.x();
    this.rootRotationY = rootRotation.y();
    this.rootRotationZ = rootRotation.z();

    // Root Rotation X
    this.rootRotationXSliderButton =
        this.addRenderableWidget(
            new SliderButton(
                this.contentLeftPos,
                this.contentTopPos,
                60,
                "rootRotationX",
                (float) Math.toDegrees(rootRotation.x()),
                SliderButton.Type.DEGREE,
                slider -> {
                  this.rootRotationX = (float) Math.toRadians(slider.getTargetValue());
                  NetworkMessageHandlerManager.getServerHandler()
                      .rotationChange(
                          this.getEasyNPCUUID(),
                          ModelPart.ROOT,
                          new CustomRotation(
                              this.rootRotationX, this.rootRotationY, this.rootRotationZ));
                }));
    this.rootRotationXResetButton =
        this.addRenderableWidget(
            new TextButton(
                this.rootRotationXSliderButton.getX() + this.rootRotationXSliderButton.getWidth(),
                this.contentTopPos,
                10,
                TextComponent.getText("↺"),
                button -> {
                  this.rootRotationX = 0f;
                  this.rootRotationXSliderButton.reset();
                }));

    // Root Rotation Y
    this.rootRotationYSliderButton =
        this.addRenderableWidget(
            new SliderButton(
                this.rootRotationXResetButton.getX() + this.rootRotationXResetButton.getWidth() + 5,
                this.contentTopPos,
                60,
                "rootRotationY",
                (float) Math.toDegrees(rootRotation.y()),
                SliderButton.Type.DEGREE,
                slider -> {
                  this.rootRotationY = (float) Math.toRadians(slider.getTargetValue());
                  NetworkMessageHandlerManager.getServerHandler()
                      .rotationChange(
                          this.getEasyNPCUUID(),
                          ModelPart.ROOT,
                          new CustomRotation(
                              this.rootRotationX, this.rootRotationY, this.rootRotationZ));
                }));
    this.rootRotationYResetButton =
        this.addRenderableWidget(
            new TextButton(
                this.rootRotationYSliderButton.getX() + this.rootRotationYSliderButton.getWidth(),
                this.contentTopPos,
                10,
                TextComponent.getText("↺"),
                button -> {
                  this.rootRotationY = 0f;
                  this.rootRotationYSliderButton.reset();
                }));

    // Root Rotation Z
    this.rootRotationZSliderButton =
        this.addRenderableWidget(
            new SliderButton(
                this.rootRotationYResetButton.getX() + this.rootRotationYResetButton.getWidth() + 5,
                this.contentTopPos,
                60,
                "rootRotationZ",
                (float) Math.toDegrees(rootRotation.z()),
                SliderButton.Type.DEGREE,
                slider -> {
                  this.rootRotationZ = (float) Math.toRadians(slider.getTargetValue());
                  NetworkMessageHandlerManager.getServerHandler()
                      .rotationChange(
                          this.getEasyNPCUUID(),
                          ModelPart.ROOT,
                          new CustomRotation(
                              this.rootRotationX, this.rootRotationY, this.rootRotationZ));
                }));
    this.rootRotationZResetButton =
        this.addRenderableWidget(
            new TextButton(
                this.rootRotationZSliderButton.getX() + this.rootRotationZSliderButton.getWidth(),
                this.contentTopPos,
                10,
                TextComponent.getText("↺"),
                button -> {
                  this.rootRotationZ = 0f;
                  this.rootRotationZSliderButton.reset();
                }));

    // Lock Root Rotation Checkbox
    this.rootRotationCheckbox =
        this.addRenderableWidget(
            new Checkbox(
                this.rootRotationZResetButton.getX() + this.rootRotationZResetButton.getWidth() + 5,
                this.contentTopPos + 2,
                "lock_rotation",
                modelData.getModelLockRotation(),
                checkbox ->
                    NetworkMessageHandlerManager.getServerHandler()
                        .modelLockRotationChange(this.getEasyNPCUUID(), checkbox.selected())));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    // Rotation Text
    if (rootRotationXSliderButton != null) {
      Text.drawString(
          guiGraphics,
          this.font,
          "Rotation X",
          this.rootRotationXSliderButton.getX() + 5,
          this.rootRotationXSliderButton.getY() + 25);
    }
    if (rootRotationYSliderButton != null) {
      Text.drawString(
          guiGraphics,
          this.font,
          "Rotation Y",
          this.rootRotationYSliderButton.getX() + 5,
          this.rootRotationYSliderButton.getY() + 25);
    }
    if (rootRotationZSliderButton != null) {
      Text.drawString(
          guiGraphics,
          this.font,
          "Rotation Z",
          this.rootRotationZSliderButton.getX() + 5,
          this.rootRotationZSliderButton.getY() + 25);
    }
  }
}