package com.j0ach1mmall3.ultimatecosmetics.modules.particles;

import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.CosmeticStorage;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.ParticleCosmeticStorage;
import com.j0ach1mmall3.ultimatecosmetics.config.CosmeticConfig;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 23/08/2015
 */
public final class Particles extends CosmeticConfig {
    private final int updateInterval;
    private final int viewDistance;
    private final int giveDelay;

    public Particles(ParticlesModule module) {
        super("particles.yml", module.getParent(), "Particles");
        this.updateInterval = this.config.getInt("UpdateInterval");
        this.viewDistance = this.config.getInt("ViewDistance");
        this.giveDelay = this.config.getInt("GiveDelay");
    }

    @Override
    public Cosmetic getCosmetic(CosmeticStorage cosmeticStorage, Player player) {
        return new Particle(this, player, (ParticleCosmeticStorage) cosmeticStorage);
    }

    @Override
    protected CosmeticStorage getCosmeticStorageByIdentifier(String section, String identifier) {
        String path = section + '.' + identifier + '.';
        return new ParticleCosmeticStorage(
                (Main) this.storage.getPlugin(),
                identifier,
                this.config.getString(path + "Permission"),
                this.customConfig.getGuiItemNew(this.config, path),
                Effect.valueOf(this.config.getString(path + "Effect")),
                this.config.getInt(path + "ID"),
                this.config.getInt(path + "Data"),
                Parsing.parseFloat(this.config.getString(path + "Speed")),
                ParticleCosmeticStorage.Shape.valueOf(this.config.getString(path + "Shape"))
        );
    }

    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getGiveDelay() {
        return this.giveDelay;
    }
}
