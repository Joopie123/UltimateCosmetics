package com.j0ach1mmall3.ultimatecosmetics.modules.auras;

import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic;
import com.j0ach1mmall3.ultimatecosmetics.api.CosmeticType;
import com.j0ach1mmall3.ultimatecosmetics.config.CosmeticConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 9/03/2016
 */
public final class Aura extends Cosmetic {
    private static final Class<?> PACKET = ReflectionAPI.getNmsClass("PacketPlayOutScoreboardTeam");
    private final Object givePacket;
    private final Object removePacket;

    private String teamName = Random.getString(16, true, true);
    private AuraStorage.Color color;

    public Aura(CosmeticConfig cosmeticConfig, Player player, AuraStorage cosmeticStorage) {
        super(cosmeticConfig, player, cosmeticStorage, CosmeticType.AURA);
        this.color = cosmeticStorage.getColor();
        Object givePacket = null;
        Object removePacket = null;
        try {
            givePacket = PACKET.newInstance();
            ReflectionAPI.setField(givePacket, "c", this.color.asString());
            ReflectionAPI.setField(givePacket, "d", "");
            ReflectionAPI.setField(givePacket, "e", "always");
            ReflectionAPI.setField(givePacket, "f", "always");
            ReflectionAPI.setField(givePacket, "g", -1);
            ReflectionAPI.setField(givePacket, "h", Collections.singletonList(this.player.getName()));
            ReflectionAPI.setField(givePacket, "i", 0);
            ReflectionAPI.setField(givePacket, "j", 0);

            removePacket = PACKET.newInstance();
            ReflectionAPI.setField(removePacket, "i", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.givePacket = givePacket;
        this.removePacket = removePacket;
    }

    @Override
    public boolean giveInternal() {
        try {
            ReflectionAPI.setField(this.removePacket, "a", this.teamName);
            for(Player p : Bukkit.getOnlinePlayers()) {
                ReflectionAPI.sendPacket(p, this.removePacket);
            }

            this.teamName = Random.getString(16, true, true);

            ReflectionAPI.setField(this.givePacket, "a", this.teamName);
            ReflectionAPI.setField(this.givePacket, "b", this.teamName);

            if(((AuraStorage) this.cosmeticStorage).getColor() == AuraStorage.Color.RAINBOW) {
                if(this.color == AuraStorage.Color.RAINBOW) this.color = AuraStorage.Color.BLACK;
                ReflectionAPI.setField(this.givePacket, "c", this.color.asString());
                this.color = AuraStorage.Color.values()[this.color.ordinal() + 1];
            }

            for(Player p : Bukkit.getOnlinePlayers()) {
                ReflectionAPI.sendPacket(p, this.givePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.player.setGlowing(true);
        return true;
    }

    @Override
    protected void removeInternal() {
        try {
            ReflectionAPI.setField(this.removePacket, "a", this.teamName);
            for (Player p : Bukkit.getOnlinePlayers()) {
                ReflectionAPI.sendPacket(p, this.removePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.player.setGlowing(false);
    }
}
