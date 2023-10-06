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

package de.markusbordihn.easynpc.client.screen.configuration.preset;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.data.CustomPresetData;
import de.markusbordihn.easynpc.menu.configuration.preset.CustomExportPresetConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandler;

@OnlyIn(Dist.CLIENT)
public class ExportCustomPresetConfigurationScreen
    extends ExportPresetConfigurationScreen<CustomExportPresetConfigurationMenu> {

  // Buttons and Boxes
  private EditBox nameBox;
  protected Button exportPresetButton;
  protected Button openCustomExportPresetFolder;

  // Text
  private List<FormattedCharSequence> textComponents = Collections.emptyList();
  protected int numberOfTextLines = 1;

  // Cache
  private File customPresetFile;
  private String customPresetFileName = "";

  public ExportCustomPresetConfigurationScreen(CustomExportPresetConfigurationMenu menu,
      Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  private void validateName() {
    String nameValue = this.nameBox.getValue();
    this.exportPresetButton.active = nameValue != null && !nameValue.isEmpty();

    // Update filename
    customPresetFileName = nameValue;

    // Re-render filename for info text.
    this.textComponents =
        this.font.split(Component.translatable(Constants.TEXT_CONFIG_PREFIX + "export_preset_text",
            customPresetFile.getParentFile(), customPresetFileName), this.imageWidth - 25);
    this.numberOfTextLines = this.textComponents.size();
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.customExportPresetButton.active = false;

    // Preset file
    customPresetFile = CustomPresetData.getPresetFile(skinModel, uuid);
    customPresetFileName = customPresetFile.getName();

    // Name Edit Box
    this.nameBox = new EditBox(this.font, this.contentLeftPos + 5, this.bottomPos - 65, 270, 18,
        Component.translatable("Name"));
    this.nameBox.setMaxLength(64);
    this.nameBox.setValue(customPresetFileName);
    this.nameBox.setResponder(consumer -> this.validateName());
    this.addRenderableWidget(this.nameBox);

    // Pre-format text
    this.textComponents =
        this.font.split(
            Component.translatable(Constants.TEXT_CONFIG_PREFIX + "export_preset_text",
                customPresetFile.getParentFile(), customPresetFile.getName()),
            this.imageWidth - 25);
    this.numberOfTextLines = this.textComponents.size();

    // Open custom export preset folder button
    this.openCustomExportPresetFolder = this.addRenderableWidget(menuButton(this.contentLeftPos + 5,
        this.bottomPos - 95, 275, "open_custom_export_preset_folder", button -> {
          Util.getPlatform().openFile(customPresetFile.getParentFile());
        }));

    // Export button
    this.exportPresetButton = this.addRenderableWidget(
        menuButton(this.contentLeftPos + 65, this.bottomPos - 40, 150, "export", button -> {
          NetworkMessageHandler.exportPreset(uuid, this.nameBox.getValue());
          exportPresetButton.active = false;
        }));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    if (!this.textComponents.isEmpty()) {
      for (int line = 0; line < this.numberOfTextLines; ++line) {
        FormattedCharSequence formattedCharSequence = this.textComponents.get(line);
        guiGraphics.drawString(this.font, formattedCharSequence, leftPos + 15,
            topPos + 45 + (line * (font.lineHeight + 2)), Constants.FONT_COLOR_DEFAULT, false);
      }
    }

  }

}
