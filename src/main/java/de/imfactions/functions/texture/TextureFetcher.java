package de.imfactions.functions.texture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TextureFetcher {

    private final TextureTable textureTable;
    private final String TEXTURE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private final Map<UUID, Property> skinCache = new HashMap<>();

    public TextureFetcher(TextureTable textureTable) {
        this.textureTable = textureTable;
    }

    public Property getSkin(UUID uuid){
        if (skinCache.containsKey(uuid)) {
            return skinCache.get(uuid);
        }
        String id = uuid.toString().replace("-", "");
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(TEXTURE_URL, id)).openConnection();
            connection.setReadTimeout(5000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
            String texture = obj.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            String signature = obj.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("signature").getAsString();
            Property skin = new Property("textures", texture, signature);
            skinCache.put(uuid, skin);
            return skin;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Property getSkin(String url) throws Exception {
        if(!textureTable.isTextureSaved(url)) {
            String output = query(url);
            JsonObject obj = new JsonParser().parse(output).getAsJsonObject();
            JsonObject dta = obj.get("data").getAsJsonObject();
            if (dta.has("texture")) {
                JsonObject tex = dta.get("texture").getAsJsonObject();
                Property var9 = new Property("textures", tex.get("value").getAsString(), tex.get("signature").getAsString());
                textureTable.insertTexture(url, var9);
                return var9;
            } else {
                throw new Exception("Error with parsing skin");
            }
        }else{
            return textureTable.getTexture(url);
        }
    }

    private String query(String url){
        String skinType = "";
        String skinVariant = skinType == null || !skinType.equalsIgnoreCase("steve") && !skinType.equalsIgnoreCase("slim") ? "" : "&variant=" + skinType;
        try {
            String output = queryURL("url=" + URLEncoder.encode(url, "UTF-8") + skinVariant);
            return output;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String queryURL(String query) throws IOException {
        int i = 0;

        while (i < 3) {
            try {
                HttpsURLConnection con = (HttpsURLConnection) (new URL("https://api.mineskin.org/generate/url/")).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-length", String.valueOf(query.length()));
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("User-Agent", "SkinsRestorer");
                con.setConnectTimeout(90000);
                con.setReadTimeout(90000);
                con.setDoOutput(true);
                con.setDoInput(true);

                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                output.writeBytes(query);
                output.close();
                StringBuilder outStr = new StringBuilder();

                InputStream is;
                try {
                    is = con.getInputStream();
                } catch (Exception var9) {
                    is = con.getErrorStream();
                }

                DataInputStream input = new DataInputStream(is);

                for (int c = input.read(); c != -1; c = input.read()) {
                    outStr.append((char) c);
                }

                input.close();
                return outStr.toString();
            } catch (Exception var10) {
                ++i;
            }
        }

        return "";
    }


}
