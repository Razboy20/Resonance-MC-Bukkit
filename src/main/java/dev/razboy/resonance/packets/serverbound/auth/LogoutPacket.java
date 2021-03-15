package dev.razboy.resonance.packets.serverbound.auth;

import dev.razboy.resonance.packets.MalformedPacketException;
import dev.razboy.resonance.packets.PacketType;
import dev.razboy.resonance.packets.serverbound.ServerBoundPacket;
import org.json.JSONObject;

public class LogoutPacket extends ServerBoundPacket {
    private final static PacketType id = PacketType.LOGOUT;
    private final static String action = id.action;

    @Override
    public void readJson(JSONObject json) throws MalformedPacketException {

    }


}
