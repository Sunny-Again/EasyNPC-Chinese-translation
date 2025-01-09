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

package de.markusbordihn.easynpc.client.screen.configuration.position;

import de.markusbordihn.easynpc.client.screen.components.Checkbox;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.client.screen.components.TextField;
import de.markusbordihn.easynpc.data.attribute.EntityAttributes;
import de.markusbordihn.easynpc.data.attribute.EnvironmentalAttributeType;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandlerManager;
import de.markusbordihn.easynpc.network.components.TextComponent;
import de.markusbordihn.easynpc.utils.ValueUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;

public class DefaultPositionConfigurationScreen<T extends ConfigurationMenu>
    extends PositionConfigurationScreen<T> {

  private static final float POSITION_STEPS = 0.5f;
  protected EditBox positionXBox;
  protected EditBox positionYBox;
  protected EditBox positionZBox;
  protected Checkbox positionFreefallCheckbox;
  protected double positionX = 0.0D;
  protected double positionY = 0.0D;
  protected double positionZ = 0.0D;
  protected Button positionXMinusButton;
  protected Button positionXPlusButton;
  protected Button positionYMinusButton;
  protected Button positionYPlusButton;
  protected Button positionZMinusButton;
  protected Button positionZPlusButton;

  public DefaultPositionConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.showCloseButton = true;
    this.renderBackground = false;
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.defaultPositionButton.active = false;

    // Position Coordinates
    Vec3 entityPosition = this.getEasyNPCEntity().position();
    this.positionX = entityPosition.x;
    this.positionY = entityPosition.y;
    this.positionZ = entityPosition.z;

    // Define Positions
    int positionTopPos = this.contentTopPos + 10;

    // X Position
    this.positionXBox =
        this.addRenderableWidget(
            new TextField(this.font, this.contentLeftPos + 15, positionTopPos, 60));
    this.positionXBox.setMaxLength(8);
    this.positionXBox.setValue(String.valueOf(this.positionX));
    this.positionXBox.setResponder(
        consumer -> {
          this.positionX = ValueUtils.getDoubleValue(this.positionXBox.getValue());
          NetworkMessageHandlerManager.getServerHandler()
              .positionChange(
                  this.getEasyNPCUUID(), new Vec3(this.positionX, this.positionY, this.positionZ));
        });
    this.positionXMinusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionXBox.getX() - 15,
                this.positionXBox.getY(),
                15,
                "-",
                button -> {
                  this.positionX -= POSITION_STEPS;
                  this.positionXBox.setValue(String.valueOf(this.positionX));
                }));
    this.positionXPlusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionXBox.getX() + this.positionXBox.getWidth() + 1,
                this.positionXBox.getY(),
                15,
                "+",
                button -> {
                  this.positionX += POSITION_STEPS;
                  this.positionXBox.setValue(String.valueOf(this.positionX));
                }));

    // Y Position
    this.positionYBox =
        this.addRenderableWidget(
            new TextField(this.font, this.contentLeftPos + 111, positionTopPos, 60));
    this.positionYBox.setMaxLength(8);
    this.positionYBox.setValue(String.valueOf(this.positionY));
    this.positionYBox.setResponder(
        consumer -> {
          this.positionY = ValueUtils.getDoubleValue(this.positionYBox.getValue());
          NetworkMessageHandlerManager.getServerHandler()
              .positionChange(
                  this.getEasyNPCUUID(), new Vec3(this.positionX, this.positionY, this.positionZ));
        });
    this.positionYMinusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionYBox.getX() - 15,
                this.positionYBox.getY(),
                15,
                TextComponent.getText("-"),
                button -> {
                  this.positionY -= POSITION_STEPS;
                  this.positionYBox.setValue(String.valueOf(this.positionY));
                }));
    this.positionYPlusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionYBox.getX() + this.positionYBox.getWidth() + 1,
                this.positionYBox.getY(),
                15,
                TextComponent.getText("+"),
                button -> {
                  this.positionY += POSITION_STEPS;
                  this.positionYBox.setValue(String.valueOf(this.positionY));
                }));

    // Z Position
    this.positionZBox =
        this.addRenderableWidget(
            new TextField(this.font, this.contentLeftPos + 207, positionTopPos, 60));
    this.positionZBox.setMaxLength(8);
    this.positionZBox.setValue(String.valueOf(this.positionZ));
    this.positionZBox.setResponder(
        consumer -> {
          this.positionZ = ValueUtils.getDoubleValue(this.positionZBox.getValue());
          NetworkMessageHandlerManager.getServerHandler()
              .positionChange(
                  this.getEasyNPCUUID(), new Vec3(this.positionX, this.positionY, this.positionZ));
        });
    this.positionZMinusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionZBox.getX() - 15,
                this.positionZBox.getY(),
                15,
                TextComponent.getText("-"),
                button -> {
                  this.positionZ -= POSITION_STEPS;
                  this.positionZBox.setValue(String.valueOf(this.positionZ));
                }));
    this.positionZPlusButton =
        this.addRenderableWidget(
            new TextButton(
                this.positionZBox.getX() + this.positionZBox.getWidth() + 1,
                this.positionZBox.getY(),
                15,
                TextComponent.getText("+"),
                button -> {
                  this.positionZ += POSITION_STEPS;
                  this.positionZBox.setValue(String.valueOf(this.positionZ));
                }));

    // Freefall Checkbox
    EntityAttributes attributeData =
        this.getEasyNPC().getEasyNPCAttributeData().getEntityAttributes();
    this.positionFreefallCheckbox =
        this.addRenderableWidget(
            new Checkbox(
                this.contentLeftPos + 200,
                positionTopPos + 20,
                "free_fall",
                attributeData.getEnvironmentalAttributes().freefall(),
                checkbox ->
                    NetworkMessageHandlerManager.getServerHandler()
                        .environmentalAttributeChange(
                            this.getEasyNPCUUID(),
                            EnvironmentalAttributeType.FREEFALL,
                            checkbox.selected())));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    // Position Text
    Text.drawString(
        guiGraphics,
        this.font,
        "Position X",
        this.positionXBox.getX() + 5,
        this.positionXBox.getY() - 10);
    Text.drawString(
        guiGraphics,
        this.font,
        "Position Y",
        this.positionYBox.getX() + 5,
        this.positionYBox.getY() - 10);
    Text.drawString(
        guiGraphics,
        this.font,
        "Position Z",
        this.positionZBox.getX() + 5,
        this.positionZBox.getY() - 10);
  }
}