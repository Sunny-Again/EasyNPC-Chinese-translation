/*
 * Copyright 2022 Markus Bordihn
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

package de.markusbordihn.easynpc.utils;

import de.markusbordihn.easynpc.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValueUtils {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String POSITION_NUMBER_MATCH_PATTERN = "^-?\\d+(\\.?\\d*)?$";
  private static final String DEGREE_NUMBER_MATCH_PATTERN = "^-?\\d+(\\.?\\d*)?$";
  private static final String POSITIVE_NUMBER_MATCH_PATTERN = "^\\d+$";
  private static final String FLOAT_NUMBER_MATCH_PATTERN = "^-?\\d+(\\.?\\d*)?$";
  private static final String DOUBLE_NUMBER_MATCH_PATTERN = "^-?\\d+(\\.?\\d*)?$";

  private ValueUtils() {}

  public static boolean isFloatValue(String text) {
    return text != null
        && (text.isEmpty()
            || (text.matches(FLOAT_NUMBER_MATCH_PATTERN) && Float.parseFloat(text) >= 0.0F));
  }

  public static boolean isDoubleValue(String text) {
    return text != null
        && (text.isEmpty()
            || (text.matches(DOUBLE_NUMBER_MATCH_PATTERN) && Double.parseDouble(text) >= 0.0D));
  }

  public static boolean isDoubleValue(String text, double min, double max) {
    return text != null
        && !text.isEmpty()
        && text.matches(DOUBLE_NUMBER_MATCH_PATTERN)
        && Double.parseDouble(text) >= min
        && Double.parseDouble(text) <= max;
  }

  public static boolean isNumericValue(String text, int min, int max) {
    return text != null
        && !text.isEmpty()
        && text.matches(POSITIVE_NUMBER_MATCH_PATTERN)
        && Integer.parseInt(text) >= min
        && Integer.parseInt(text) <= max;
  }

  public static boolean isPositiveNumericValueOrZero(String text) {
    return text != null
        && !text.isEmpty()
        && text.matches(POSITIVE_NUMBER_MATCH_PATTERN)
        && Integer.parseInt(text) >= 0;
  }

  public static boolean isDegreeValue(String text) {
    return text != null
        && !text.isEmpty()
        && text.matches(DEGREE_NUMBER_MATCH_PATTERN)
        && Double.parseDouble(text) >= -180
        && Double.parseDouble(text) <= 180;
  }

  public static boolean isPositionValue(String text) {
    return text != null
        && !text.isEmpty()
        && text.matches(POSITION_NUMBER_MATCH_PATTERN)
        && Double.parseDouble(text) >= -32000000
        && Double.parseDouble(text) <= 32000000;
  }

  public static boolean isNumericValue(String text) {
    return text != null && (text.isEmpty() || (text.matches("^-?\\d+$")));
  }

  public static boolean isPositionValueInRange(String text, double min, double max) {
    return text != null
        && !text.isEmpty()
        && text.matches(POSITION_NUMBER_MATCH_PATTERN)
        && Double.parseDouble(text) >= -32000000
        && Double.parseDouble(text) <= 32000000
        && Double.parseDouble(text) >= min
        && Double.parseDouble(text) <= max;
  }

  public static Double getDoubleValue(String value) {
    if (value != null && !value.isEmpty()) {
      try {
        return Double.parseDouble(value);
      } catch (NumberFormatException e) {
        log.error("Failed to parse double value: {}", value);
      }
    }
    return 0.0;
  }

  public static Integer getIntValue(String value) {
    if (value != null && !value.isEmpty()) {
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException e) {
        log.error("Failed to parse integer value: {}", value);
      }
    }
    return 0;
  }
}
