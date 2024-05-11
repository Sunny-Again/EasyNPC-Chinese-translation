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

package de.markusbordihn.easynpc.menu;

import de.markusbordihn.easynpc.data.configuration.ConfigurationType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public class MenuHandler implements MenuHandlerInterface {

  @Override
  public void openConfigurationMenu(
      ConfigurationType configurationType,
      ServerPlayer serverPlayer,
      EasyNPC<?> easyNPC,
      int pageIndex) {}

  @Override
  public void openDialogEditorMenu(
      ServerPlayer serverPlayer,
      EasyNPC<?> easyNPC,
      UUID dialogId,
      ConfigurationType formerConfigurationType,
      int pageIndex) {}

  @Override
  public void openDialogButtonEditorMenu(
      ServerPlayer serverPlayer,
      EasyNPC<?> easyNPC,
      UUID dialogId,
      UUID dialogButtonId,
      ConfigurationType formerConfigurationType,
      int pageIndex) {}

  @Override
  public void openDialogTextEditorMenu(
      ServerPlayer serverPlayer,
      EasyNPC<?> easyNPC,
      UUID dialogId,
      ConfigurationType formerConfigurationType,
      int pageIndex) {}

  @Override
  public void openDialogMenu(
      ServerPlayer serverPlayer, EasyNPC<?> easyNPC, UUID dialogId, int pageIndex) {
    openDialogMenu(serverPlayer, ModMenuTypes.DIALOG_MENU, easyNPC, dialogId, pageIndex);
  }

  @Override
  public void openTestMenu(ServerPlayer serverPlayer, UUID npcUUID) {
    openTestMenu(serverPlayer, ModMenuTypes.TEST_MENU, npcUUID);
  }
}