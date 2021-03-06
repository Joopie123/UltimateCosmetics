package com.j0ach1mmall3.ultimatecosmetics.modules.fireworks;

import com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.CosmeticStorage;
import com.j0ach1mmall3.ultimatecosmetics.config.CosmeticConfig;
import org.bukkit.entity.Player;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 22/08/2015
 */
public final class Fireworks extends CosmeticConfig {
    public Fireworks(FireworksModule module) {
        super("fireworks.yml", module.getParent(), "Fireworks");
    }

    @Override
    public Cosmetic getCosmetic(CosmeticStorage cosmeticStorage, Player player) {
        return new Firework(this, player, cosmeticStorage);
    }
}
