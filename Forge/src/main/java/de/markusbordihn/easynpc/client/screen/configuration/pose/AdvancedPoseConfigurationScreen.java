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

package de.markusbordihn.easynpc.client.screen.configuration.pose;

import de.markusbordihn.easynpc.client.screen.ScreenHelper;
import de.markusbordihn.easynpc.client.screen.components.Checkbox;
import de.markusbordihn.easynpc.client.screen.components.SliderButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.data.model.ModelPart;
import de.markusbordihn.easynpc.menu.configuration.pose.AdvancedPoseConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedPoseConfigurationScreen
    extends PoseConfigurationScreen<AdvancedPoseConfigurationMenu> {

  // Slider Buttons reference for positioning
  protected SliderButton headRotationSliderButton;
  protected SliderButton bodyRotationSliderButton;
  protected SliderButton armsRotationSliderButton;
  protected SliderButton leftArmRotationSliderButton;
  protected SliderButton rightArmRotationSliderButton;
  protected SliderButton leftLegRotationSliderButton;
  protected SliderButton rightLegRotationSliderButton;

  public AdvancedPoseConfigurationScreen(
      AdvancedPoseConfigurationMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  private SliderButton createVisibilityRotationSlider(
      int left, int top, ModelPart modelPart, String label) {
    // Model Part Rotation
    SliderButton sliderRotationButtonX = createRotationSliderCompact(left, top, modelPart, label);

    // Model Part Visibility
    boolean modelPartVisibility = this.entity.isModelPartVisible(modelPart);
    this.addRenderableWidget(
        new Checkbox(
            sliderRotationButtonX.getX() + 3,
            top - sliderRotationButtonX.getHeight(),
            "",
            modelPartVisibility,
            checkbox ->
                NetworkMessageHandler.modelVisibilityChange(uuid, modelPart, checkbox.selected())));

    return sliderRotationButtonX;
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.advancedPoseButton.active = false;

    // Position and size
    int sliderTopPos = this.contentTopPos + 16;
    int sliderLeftPos = this.contentLeftPos - 3;
    int sliderLeftSpace = 200;
    int sliderTopSpace = 66;

    // Head parts
    if (this.entity.hasHeadModelPart()) {
      this.headRotationSliderButton =
          createVisibilityRotationSlider(sliderLeftPos, sliderTopPos, ModelPart.HEAD, "head");
    }

    // Body parts
    if (this.entity.hasBodyModelPart()) {
      sliderLeftPos += sliderLeftSpace;
      this.bodyRotationSliderButton =
          createVisibilityRotationSlider(sliderLeftPos, sliderTopPos, ModelPart.BODY, "body");
    }

    sliderTopPos += sliderTopSpace;

    // Right arm rotations
    if (this.entity.hasRightArmModelPart()) {
      sliderLeftPos = this.contentLeftPos;
      this.rightArmRotationSliderButton =
          createVisibilityRotationSlider(
              sliderLeftPos, sliderTopPos, ModelPart.RIGHT_ARM, "right_arm");
    }

    // Left arm rotations and arms rotations.
    if (this.entity.hasLeftArmModelPart()) {
      sliderLeftPos += sliderLeftSpace;
      this.leftArmRotationSliderButton =
          createVisibilityRotationSlider(
              sliderLeftPos, sliderTopPos, ModelPart.LEFT_ARM, "left_arm");
    } else if (this.entity.hasArmsModelPart()) {
      sliderLeftPos = this.contentLeftPos;
      this.armsRotationSliderButton =
          createVisibilityRotationSlider(sliderLeftPos, sliderTopPos, ModelPart.ARMS, "arms");
    }

    sliderTopPos += sliderTopSpace;
    sliderLeftPos = this.contentLeftPos;

    // Right leg rotations
    if (this.entity.hasRightLegModelPart()) {
      this.rightLegRotationSliderButton =
          createVisibilityRotationSlider(
              sliderLeftPos, sliderTopPos, ModelPart.RIGHT_LEG, "right_leg");
    }

    // Left leg rotations
    if (this.entity.hasLeftLegModelPart()) {
      sliderLeftPos += sliderLeftSpace;
      this.leftLegRotationSliderButton =
          createVisibilityRotationSlider(
              sliderLeftPos, sliderTopPos, ModelPart.LEFT_LEG, "left_leg");
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    // Avatar
    ScreenHelper.renderCustomPoseEntityAvatar(
        this.contentLeftPos + 152,
        this.contentTopPos + 155,
        50,
        this.contentLeftPos + 140 - this.xMouse,
        this.contentTopPos + 30 - this.yMouse,
        this.entity);

    // Body parts texts
    if (this.entity.hasHeadModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.head",
          this.headRotationSliderButton.getX() + 20,
          this.headRotationSliderButton.getY() - 12);
    }
    if (this.entity.hasBodyModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.body",
          this.bodyRotationSliderButton.getX() + 20,
          this.bodyRotationSliderButton.getY() - 12);
    }
    if (this.entity.hasLeftArmModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.left_arm",
          this.leftArmRotationSliderButton.getX() + 20,
          this.leftArmRotationSliderButton.getY() - 12);
    } else if (this.entity.hasArmsModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.arms",
          this.armsRotationSliderButton.getX() + 20,
          this.armsRotationSliderButton.getY() - 12);
    }
    if (this.entity.hasRightArmModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.right_arm",
          this.rightArmRotationSliderButton.getX() + 20,
          this.rightArmRotationSliderButton.getY() - 12);
    }
    if (this.entity.hasLeftLegModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.left_leg",
          this.leftLegRotationSliderButton.getX() + 20,
          this.leftLegRotationSliderButton.getY() - 12);
    }
    if (this.entity.hasRightLegModelPart()) {
      Text.drawConfigString(
          guiGraphics,
          this.font,
          "pose.right_leg",
          this.rightLegRotationSliderButton.getX() + 5,
          this.rightLegRotationSliderButton.getY() - 12);
    }
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

    // Entity background
    guiGraphics.fill(
        this.contentLeftPos + 99,
        this.contentTopPos,
        this.contentLeftPos + 206,
        this.contentTopPos + 188,
        0xff000000);
    guiGraphics.fill(
        this.contentLeftPos + 100,
        this.contentTopPos + 1,
        this.contentLeftPos + 205,
        this.contentTopPos + 187,
        0xffaaaaaa);
  }
}