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

package de.markusbordihn.easynpc.entity.easynpc.npc;

import de.markusbordihn.easynpc.data.skin.SkinModel;
import de.markusbordihn.easynpc.data.sound.SoundDataSet;
import de.markusbordihn.easynpc.data.sound.SoundType;
import de.markusbordihn.easynpc.entity.EasyNPCBaseModelEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

public class Fairy extends EasyNPCBaseModelEntity<Fairy> {

  public static final String ID = "fairy";

  private static final float DEFAULT_SCALE_X = 0.4f;
  private static final float DEFAULT_SCALE_Y = 0.4f;
  private static final float DEFAULT_SCALE_Z = 0.4f;

  public Fairy(EntityType<? extends PathfinderMob> entityType, Level level) {
    this(entityType, level, Variant.GREEN);
  }

  public Fairy(EntityType<? extends PathfinderMob> entityType, Level level, Enum<?> variant) {
    super(entityType, level, variant);
    this.moveControl = new FlyingMoveControl(this, 20, true);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 16.0D)
        .add(Attributes.FOLLOW_RANGE, 32.0D)
        .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.FLYING_SPEED, 0.6F)
        .add(Attributes.ATTACK_DAMAGE, 0.0D)
        .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
        .add(Attributes.ATTACK_SPEED, 0.0D)
        .add(Attributes.ARMOR, 0.0D)
        .add(Attributes.ARMOR_TOUGHNESS, 0.0D);
  }

  @Override
  protected PathNavigation createNavigation(Level level) {
    return new FlyingPathNavigation(this, level);
  }

  @Override
  public Float getDefaultScaleX() {
    return Fairy.DEFAULT_SCALE_X;
  }

  @Override
  public Float getDefaultScaleY() {
    return Fairy.DEFAULT_SCALE_Y;
  }

  @Override
  public Float getDefaultScaleZ() {
    return Fairy.DEFAULT_SCALE_Z;
  }

  @Override
  public SkinModel getSkinModel() {
    return SkinModel.FAIRY;
  }

  @Override
  public boolean hasLeftLegModelPart() {
    return false;
  }

  @Override
  public boolean canUseArmor() {
    return false;
  }

  @Override
  public Enum<?>[] getVariants() {
    return Variant.values();
  }

  @Override
  public Enum<?> getDefaultVariant() {
    return Variant.GREEN;
  }

  @Override
  public Enum<?> getVariant(String name) {
    try {
      return Variant.valueOf(name);
    } catch (IllegalArgumentException e) {
      return getDefaultVariant();
    }
  }

  @Override
  public int getEntityGuiScaling() {
    return 65;
  }

  @Override
  public int getEntityDialogTop() {
    return -38;
  }

  @Override
  public int getEntityDialogScaling() {
    return 75;
  }

  @Override
  public boolean canFly() {
    return true;
  }

  @Override
  public SoundDataSet getDefaultSoundDataSet(SoundDataSet soundDataSet, String variantName) {
    soundDataSet.addSound(SoundType.AMBIENT, SoundEvents.PARROT_AMBIENT);
    soundDataSet.addSound(SoundType.DEATH, SoundEvents.PARROT_DEATH);
    soundDataSet.addSound(SoundType.HURT, SoundEvents.PARROT_HURT);
    soundDataSet.addSound(SoundType.EAT, SoundEvents.PARROT_EAT);
    soundDataSet.addSound(SoundType.TRADE, SoundEvents.VILLAGER_TRADE);
    soundDataSet.addSound(SoundType.TRADE_YES, SoundEvents.VILLAGER_YES);
    soundDataSet.addSound(SoundType.TRADE_NO, SoundEvents.VILLAGER_NO);
    return soundDataSet;
  }

  public enum Variant {
    GREEN,
    RED,
    BLUE
  }
}
