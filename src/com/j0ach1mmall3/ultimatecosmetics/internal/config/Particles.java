package com.j0ach1mmall3.ultimatecosmetics.internal.config;

import com.j0ach1mmall3.jlib.integration.Placeholders;
import com.j0ach1mmall3.jlib.inventory.CustomItem;
import com.j0ach1mmall3.jlib.inventory.GuiItem;
import com.j0ach1mmall3.jlib.methods.General;
import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.ParticleStorage;
import com.j0ach1mmall3.ultimatecosmetics.internal.Methods;
import com.j0ach1mmall3.ultimatecosmetics.internal.particles.shapes.ParticleShape;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by j0ach1mmall3 on 14:24 23/08/2015 using IntelliJ IDEA.
 */
public final class Particles extends ConfigLoader {
    private final Main plugin;
    private final boolean enabled;
    private final List<String> worldsBlacklist;
    private final String command;
    private final String noPermissionMessage;
    private final Sound giveSound;
    private final Sound removeSound;
    private final double updateInterval;
    private final int viewDistance;
    private final String guiName;
    private final int guiSize;
    private final CustomItem noPermissionItem;
    private final boolean noPermissionItem_Enabled;
    private final GuiItem removeItem;
    private final GuiItem homeItem;
    private final GuiItem previousItem;
    private final GuiItem nextItem;
    private final List<ParticleStorage> particles;

    private final int maxPage;

    public Particles(Main plugin) {
        super("particles.yml", plugin);
        this.plugin = plugin;
        Config pluginConfig = plugin.getBabies();
        this.enabled = this.config.getBoolean("Enabled");
        this.worldsBlacklist = this.config.getStringList("WorldsBlacklist");
        this.command = this.config.getString("Command");
        this.noPermissionMessage = this.config.getString("NoPermissionMessage");
        this.giveSound = Sound.valueOf(this.config.getString("GiveSound"));
        this.removeSound = Sound.valueOf(this.config.getString("RemoveSound"));
        this.updateInterval = this.config.getDouble("UpdateInterval");
        this.viewDistance = this.config.getInt("ViewDistance");
        this.guiName = this.config.getString("GUIName");
        this.guiSize = Parsing.parseInt(this.config.getString("GUISize"));
        this.noPermissionItem = Methods.getNoPermissionItem(this.config);
        this.noPermissionItem_Enabled = this.config.getBoolean("NoPermissionItem.Enabled");
        this.removeItem = Methods.getGuiItem(this.config, "RemoveItem");
        this.homeItem = Methods.getGuiItem(this.config, "HomeItem");
        this.previousItem = Methods.getGuiItem(this.config, "PreviousItem");
        this.nextItem = Methods.getGuiItem(this.config, "NextItem");
        this.particles = getParticlesInternal();
        this.maxPage = getMaxPageInternal();
        if (pluginConfig.getLoggingLevel() >= 2)
            General.sendColoredMessage(plugin, "Particles config successfully loaded!", ChatColor.GREEN);
    }

    private int getMaxPageInternal() {
        int biggestSlot = 0;
        for (ParticleStorage particle : this.particles) {
            if (particle.getPosition() > biggestSlot) biggestSlot = particle.getPosition();
        }
        return biggestSlot / 44;
    }

    private List<ParticleStorage> getParticlesInternal() {
        List<ParticleStorage> particlez = new ArrayList<>();
        for(String s : this.customConfig.getKeys("Particles")) {
            particlez.add(this.getParticleByIdentifier(s));
        }
        return particlez;
    }

    private ParticleStorage getParticleByIdentifier(String identifier) {
        String path = "Particles." + identifier + '.';
        String item = this.config.getString(path + "Item");
        return new ParticleStorage(
                this.plugin,
                identifier,
                new CustomItem(Parsing.parseMaterial(item), 1, Parsing.parseData(item), Placeholders.parse(this.config.getString(path + "Name")), Placeholders.parse(this.config.getString(path + "Description"))),
                this.config.getInt(path + "Position"),
                this.config.getString(path + "Permission"),
                this.config.getString(path + "Particle"),
                this.config.getInt(path + "ID"),
                this.config.getInt(path + "Data"),
                (float) this.config.getDouble(path + "Speed"),
                ParticleShape.valueOf(this.config.getString(path + "Shape").toUpperCase())
        );
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }

    public String getCommand() {
        return this.command;
    }

    public String getGuiName() {
        return this.guiName;
    }

    public Iterable<ParticleStorage> getParticles() {
        return Collections.unmodifiableList(this.particles);
    }

    public Sound getGiveSound() {
        return this.giveSound;
    }

    public Sound getRemoveSound() {
        return this.removeSound;
    }

    public int getGuiSize() {
        return this.guiSize;
    }

    public List<String> getWorldsBlacklist() {
        return Collections.unmodifiableList(this.worldsBlacklist);
    }

    public GuiItem getRemoveItem() {
        return this.removeItem;
    }

    public GuiItem getHomeItem() {
        return this.homeItem;
    }

    public GuiItem getPreviousItem() {
        return this.previousItem;
    }

    public GuiItem getNextItem() {
        return this.nextItem;
    }

    public boolean isNoPermissionItemEnabled() {
        return this.noPermissionItem_Enabled;
    }

    public CustomItem getNoPermissionItem() {
        return this.noPermissionItem;
    }

    public CustomItem getNoPermissionItem(ParticleStorage particle) {
        if ("%cosmeticsname%".equals(this.noPermissionItem.getItemMeta().getDisplayName())) {
            CustomItem item = new CustomItem(this.noPermissionItem.clone());
            item.setName(item.getItemMeta().getDisplayName().replace("%cosmeticsname%", particle.getItem().getItemMeta().getDisplayName()));
            return item;
        }
        return this.noPermissionItem;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public double getUpdateInterval() {
        return this.updateInterval;
    }

    public int getMaxPage() {
        return this.maxPage;
    }
}
