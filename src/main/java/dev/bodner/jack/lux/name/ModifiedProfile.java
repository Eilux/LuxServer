package dev.bodner.jack.lux.name;

import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class ModifiedProfile extends GameProfile {
    private final String replacementName;
    public ModifiedProfile(UUID id, String name, String replacementName) {
        super(id, name);
        this.replacementName = replacementName;
    }

    @Override
    public String getName() {
        return replacementName;
    }
}
