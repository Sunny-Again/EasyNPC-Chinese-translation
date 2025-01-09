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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.validator.NameValidator;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayersUtils {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String TEXTURES_STRING = "textures";
  private static final String SESSION_PROFILE_URL =
      "https://sessionserver.mojang.com/session/minecraft/profile/%s";
  private static final String API_PROFILE_URL =
      "https://api.mojang.com/users/profiles/minecraft/%s";
  private static final Map<String, UUID> userUUIDCache = new HashMap<>();
  private static UUID lastUserUUIDForUserTexture;

  protected PlayersUtils() {}

  public static Optional<GameProfile> getGameProfile(MinecraftServer server, String username) {
    if (server == null || username == null || username.isEmpty()) {
      return Optional.empty();
    }
    final GameProfileCache gameProfileCache = server.getProfileCache();
    return gameProfileCache.get(username);
  }

  public static UUID getUserUUID(MinecraftServer server, String username) {
    // Check if username is already a valid UUID.
    UUID uuid = getUUIDfromString(username);
    if (uuid != null) {
      return uuid;
    }

    return PlayersUtils.getGameProfile(server, username)
        .map(GameProfile::getId)
        .map(UUID::toString)
        .map(PlayersUtils::getUUIDfromString)
        .orElse(null);
  }

  public static UUID getUUIDfromString(String uuidString) {
    try {
      return UUID.fromString(uuidString);
    } catch (IllegalArgumentException exception) {
      // Ignore the case where string is not valid UUID
    }
    return null;
  }

  public static UUID getUserUUID(String username) {
    if (username == null || username.isEmpty() || !NameValidator.isValidPlayerName(username)) {
      log.error("Unable to get user UUID with invalid username: {}", username);
      return null;
    }

    // Check cache for already known or failed usernames.
    if (userUUIDCache.containsKey(username)) {
      return userUUIDCache.get(username);
    }

    // Get user UUID over API.
    try {
      String url = String.format(API_PROFILE_URL, username);
      String json = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
      JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
      String uuidString = jsonObject.get("id").getAsString();

      // Verify UUID string
      if (uuidString == null || uuidString.isEmpty()) {
        log.error("Unable to get user UUID with invalid response: {}", json);
        return null;
      }

      // Format UUID string and return UUID.
      String formattedUUID =
          uuidString.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
      UUID userUUID = UUID.fromString(formattedUUID);
      log.debug("Found user {} with UUID {} ...", username, userUUID);
      userUUIDCache.put(username, userUUID);
      return userUUID;
    } catch (IOException e) {
      log.error("Unable to get UUID from user {}:", username, e);
      userUUIDCache.put(username, null);
      return null;
    }
  }

  public static String getUserTexture(UUID userUUID) {
    // Simple reload protected to avoid spawning to the session server.
    if (lastUserUUIDForUserTexture != null && lastUserUUIDForUserTexture.equals(userUUID)) {
      log.error("Ignore duplicated user texture request for {}!", userUUID);
      return null;
    }
    lastUserUUIDForUserTexture = userUUID;

    // Create sessions request and parse result, if any.
    String sessionURL = String.format(SESSION_PROFILE_URL, userUUID);
    try {
      String data = IOUtils.toString(new URL(sessionURL), StandardCharsets.UTF_8);
      if (data == null || data.isEmpty()) {
        log.error("Unable to get user texture with {}", sessionURL);
        return null;
      }
      return getUserTextureFromSessionResponse(data);
    } catch (IOException ioException) {
      log.error("Unable to get user texture with {}:", sessionURL, ioException);
      return null;
    }
  }

  public static String getUserTextureFromSessionResponse(String data) {
    JsonObject jsonObject = getJsonObject(data);
    if (jsonObject == null || !jsonObject.has("properties")) {
      log.error("Unable to get valid JSON data from session response: {}", data);
      return "";
    }

    JsonArray properties = jsonObject.getAsJsonArray("properties");
    log.debug("getUserTextureFromSessionRequest: {}", properties);
    for (JsonElement property : properties) {
      JsonObject propertyObject = property.getAsJsonObject();
      if (propertyObject.has("name")
          && TEXTURES_STRING.equals(propertyObject.get("name").getAsString())
          && propertyObject.has("value")) {
        String textureData =
            new String(Base64.getDecoder().decode(propertyObject.get("value").getAsString()));
        String userTexture = getUserTextureFromTextureData(textureData);
        String userTextureModel = getUserTextureModelFromTextureData(textureData);
        log.debug(
            "Found user texture {} with model {} ...",
            userTexture,
            userTextureModel == null || userTextureModel.isEmpty() ? "default" : userTextureModel);
        return userTexture;
      }
    }

    log.error("Unable to get user texture from session response: {}", data);
    return "";
  }

  public static String getUserTextureFromTextureData(String data) {
    JsonObject jsonObject = getJsonObject(data);
    log.debug("getUserTextureFromTextureData: {}", jsonObject);
    if (jsonObject != null && jsonObject.has(TEXTURES_STRING)) {
      JsonObject textureObject = jsonObject.getAsJsonObject(TEXTURES_STRING);
      if (textureObject.has("SKIN")) {
        JsonObject skinObject = textureObject.getAsJsonObject("SKIN");
        if (skinObject.has("url")) {
          return skinObject.get("url").getAsString();
        }
      }
    }
    log.error("Unable to get user texture from texture data: {}", data);
    return "";
  }

  public static String getUserTextureModelFromTextureData(String data) {
    JsonObject jsonObject = getJsonObject(data);
    log.debug("getUserTextureModelFromTextureData: {}", jsonObject);
    if (jsonObject != null && jsonObject.has(TEXTURES_STRING)) {
      JsonObject textureObject = jsonObject.getAsJsonObject(TEXTURES_STRING);
      if (textureObject.has("SKIN")) {
        JsonObject skinObject = textureObject.getAsJsonObject("SKIN");
        if (skinObject.has("metadata")) {
          JsonObject metaDataObject = skinObject.getAsJsonObject("metadata");
          if (metaDataObject.has("model")) {
            return metaDataObject.get("model").getAsString();
          }
        }
      }
    }
    log.debug("Unable to get user texture model from texture data: {}", data);
    return "";
  }

  public static JsonObject getJsonObject(String data) {
    if (data == null || data.isEmpty()) {
      return null;
    }
    try {
      JsonElement jsonElement = JsonParser.parseString(data);
      if (jsonElement != null && jsonElement.isJsonObject()) {
        return jsonElement.getAsJsonObject();
      }
    } catch (JsonParseException jsonParseException) {
      log.error("ERROR: Unable to parse json data: {}", data);
    }
    return null;
  }
}