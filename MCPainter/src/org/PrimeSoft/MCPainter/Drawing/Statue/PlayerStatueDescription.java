/*
 * The MIT License
 *
 * Copyright 2014 SBPrime.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.PrimeSoft.MCPainter.Drawing.Statue;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.PrimeSoft.MCPainter.MCPainterMain;
import org.PrimeSoft.MCPainter.utils.HttpUtils;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.configuration.ConfigurationSection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author SBPrime
 */
public class PlayerStatueDescription extends StatueDescription {

    /**
     * The base 64 decoder
     */
    private static final Base64 s_base64 = new Base64();

    /**
     * The profile URL
     */
    private final String NAME_TO_UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    /**
     * The session url
     */
    private final String UUID_TO_PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public PlayerStatueDescription(ConfigurationSection bp) {
        super(null, bp);
    }

    public String getSkinFile(String playerName) {
        JSONObject responseUUID = HttpUtils.downloadJson(String.format(NAME_TO_UUID_URL, playerName));
        if (responseUUID == null || !responseUUID.containsKey("id")) {
            MCPainterMain.log("Unable to get session UUID for " + playerName);
            return null;
        }

        JSONObject profile = HttpUtils.downloadJson(String.format(UUID_TO_PROFILE_URL, responseUUID.get("id")));
        if (profile == null || !profile.containsKey("properties")) {
            MCPainterMain.log("Unable to get player profile for " + playerName);
            return null;
        }

        JSONArray properties = (JSONArray) (profile.get("properties"));
        for (int i = 0; i < properties.size(); i++) {
            JSONObject prop = (JSONObject) properties.get(i);

            if (!prop.containsKey("name") || !prop.get("name").equals("textures")
                    || !prop.containsKey("value")) {
                continue;
            }
            
            
            byte[] data = s_base64.decode((String) prop.get("value"));
            if (data == null || data.length <= 0) {
                continue;
            }
            
            JSONObject textureEntry = null;
            
            try {
                textureEntry = (JSONObject) JSONValue.parseWithException(new String(data));
            } catch (ParseException ex) {
                continue;
            }
            
            if (!textureEntry.containsKey("textures")) {
                continue;
            }
            
            textureEntry = (JSONObject) textureEntry.get("textures");
            
            if (textureEntry.containsKey("SKIN")) {
                textureEntry = (JSONObject)textureEntry.get("SKIN");
            }
            
            if (textureEntry.containsKey("url")) {
                return (String)textureEntry.get("url");
            }
        }

        MCPainterMain.log("Unable to detect the texture profile " + playerName);
        return null;
    }
}
