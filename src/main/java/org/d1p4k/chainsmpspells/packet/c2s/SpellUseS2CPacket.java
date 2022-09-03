package org.d1p4k.chainsmpspells.packet.c2s;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.d1p4k.chainsmpspells.packet.AbstractC2SPacket;
import org.d1p4k.chainsmpspells.spell.spells.SuicideSpell;

public class SpellUseS2CPacket extends AbstractC2SPacket {
    public static Identifier packetId = new Identifier("css", "usespell");
    private byte spellId;

    public SpellUseS2CPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
        super(server, player, serverPlayNetworkHandler, buf, packetSender);
        handle();
    }

    public void handle() {
        this.spellId = buf.readByte();
        switch (spellId) {
            case 1 -> new SuicideSpell(player, SuicideSpell.spellId, 1).cast();
            //case 2 -> new
        }
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(packetId, SpellUseS2CPacket::new);
    }

}
