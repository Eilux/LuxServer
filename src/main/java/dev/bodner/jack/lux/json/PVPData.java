package dev.bodner.jack.lux.json;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class PVPData {

    @SerializedName("pvp_list")
    List<String> pvp_list;

    public PVPData(ArrayList<UUID> uuids) {
        this.pvp_list = Arrays.asList(new String[uuids.size()]);
        for (int i = 0; i<= pvp_list.size()-1; i++){
            this.pvp_list.set(i, uuids.get(i).toString());
        }
    }

    public PVPData(){
        this.pvp_list = Collections.emptyList();
    }

    public ArrayList<UUID> format(){
        ArrayList<UUID> results = new ArrayList<>();
        if (this.pvp_list != null) {
            for (String id : this.pvp_list) {
                results.add(UUID.fromString(id));
            }
        }
        return results;
    }
}
