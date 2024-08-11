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

package de.markusbordihn.easynpc.client.screen.configuration.actions;

import de.markusbordihn.easynpc.data.action.ActionEventType;
import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicActionConfigurationScreen<T extends ConfigurationMenu>
    extends ActionConfigurationScreen<T> {

  public BasicActionConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.basicActionButton.active = false;

    // On Interaction Actions
    this.addRenderableWidget(
        this.getActionDataButton(
            this.contentLeftPos,
            this.contentTopPos + 10,
            ActionEventType.ON_INTERACTION,
            ConfigurationType.BASIC_ACTION));

    // On Hurt Actions
    this.addRenderableWidget(
        this.getActionDataButton(
            this.contentLeftPos,
            this.contentTopPos + 60,
            ActionEventType.ON_HURT,
            ConfigurationType.BASIC_ACTION));

    // On Death Actions
    this.addRenderableWidget(
        this.getActionDataButton(
            this.contentLeftPos,
            this.contentTopPos + 110,
            ActionEventType.ON_DEATH,
            ConfigurationType.BASIC_ACTION));
  }
}