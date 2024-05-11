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

package de.markusbordihn.easynpc.menu.testing;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.data.screen.ScreenContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMenu extends AbstractContainerMenu {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private final ScreenContainerData containerData;

  public TestMenu(MenuType<?> menuType, int containerId, final Inventory playerInventory) {
    this(menuType, containerId, playerInventory, new ScreenContainerData());
  }

  public TestMenu(
      MenuType<?> menuType,
      int containerId,
      final Inventory playerInventory,
      final ScreenContainerData containerData) {
    super(menuType, containerId);

    this.containerData = containerData;

    this.addDataSlots(containerData);

    log.info("TestMenu created with containerId: {} and data: {}", containerId, containerData);
  }

  public ScreenContainerData getContainerData() {
    return this.containerData;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.isAlive();
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    Slot slot = this.slots.get(slotIndex);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack itemStack = slot.getItem();

    // Store changes if itemStack is not empty.
    if (itemStack.isEmpty()) {
      slot.set(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return ItemStack.EMPTY;
  }
}