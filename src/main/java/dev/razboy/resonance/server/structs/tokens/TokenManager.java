package dev.razboy.resonance.server.structs.tokens;

import dev.razboy.resonance.Resonance;
import dev.razboy.resonance.server.structs.client.Client;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class TokenManager {
    private final HashMap<String, Token> tokens = new HashMap<>();
    private final HashMap<String, String> uuids = new HashMap<>();
    private Resonance plugin;

    public TokenManager(Resonance plugin) {
        this.plugin = plugin;
    }
    public Token generateToken(Player player) {
        return generateToken(player.getUniqueId().toString());
    }
    public Token generateToken(String uuid) {
        if (tokens.containsKey(uuid)) {
            if (System.currentTimeMillis() - tokens.get(uuid).getCreationTime() > 300000) {
                tokens.replace(uuid, new Token(getToken(), uuid));
                uuids.replace(tokens.get(uuid).getToken(), uuid);
            }
        } else {
            tokens.put(uuid, new Token(getToken(), uuid));
            uuids.put(tokens.get(uuid).getToken(), uuid);
        }
        return tokens.get(uuid);
    }
    public Client checkToken(String token) {
        System.out.println("Token (" + token + ") stored:" + uuids.containsKey(token));
        if (uuids.containsKey(token)) {
            String uuid = uuids.get(token);
            System.out.println("UUID: " + uuid);
            System.out.println(System.currentTimeMillis()-tokens.get(uuids.get(token)).getCreationTime());
            if (System.currentTimeMillis() - tokens.get(uuids.get(token)).getCreationTime() > 300000) {
                uuids.remove(token);
                tokens.remove(uuid);
                return null;
            } else {
                uuids.remove(token);
                tokens.remove(uuid);
                return new Client(uuid, new Token(getToken(32), uuid));
            }
        }
        return null;
    }





    private String getToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
    private String getToken() {
        return getToken(8);
    }
}
