package dev.bodner.jack.lux;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.bodner.jack.lux.json.PlayerRequest;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Util {

    public static UUID getPlayerID(String name){
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        UUID id = null;

        if (Bukkit.getPlayer(name) != null){
            id = Bukkit.getPlayer(name).getUniqueId();
            System.out.println(id);
        }
        else {
            try {
                URL request = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                HttpURLConnection connection = (HttpURLConnection) request.openConnection();
                connection.setRequestMethod("GET");
                JsonObject jsonObject = (JsonObject)jsonParser.parse(new InputStreamReader(connection.getInputStream()));
                PlayerRequest playerRequest = gson.fromJson(jsonObject, PlayerRequest.class);
                id = UUID.fromString(playerRequest.getId().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")); //https://stackoverflow.com/questions/18986712/creating-a-uuid-from-a-string-with-no-dashes/32594202
                connection.disconnect();
            }
            catch (Exception e){
                System.out.println(e.toString());
                System.out.println(id);
            }
        }
        return id;
    }
}
