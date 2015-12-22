package com.j0ach1mmall3.ultimatecosmetics.internal.config;

import com.j0ach1mmall3.jlib.integration.Placeholders;
import com.j0ach1mmall3.jlib.inventory.CustomItem;
import com.j0ach1mmall3.jlib.inventory.GuiItem;
import com.j0ach1mmall3.jlib.methods.General;
import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.MusicStorage;
import com.j0ach1mmall3.ultimatecosmetics.internal.Methods;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by j0ach1mmall3 on 13:39 23/08/2015 using IntelliJ IDEA.
 */
public final class Music extends ConfigLoader {
    private final Main plugin;
    private final boolean enabled;
    private final List<String> worldsBlacklist;
    private final String command;
    private final String noPermissionMessage;
    private final Sound giveSound;
    private final Sound removeSound;
    private final String songsPath;
    private final boolean loop;
    private final String guiName;
    private final int guiSize;
    private final CustomItem noPermissionItem;
    private final boolean noPermissionItem_Enabled;
    private final GuiItem removeItem;
    private final GuiItem homeItem;
    private final GuiItem previousItem;
    private final GuiItem nextItem;
    private final List<MusicStorage> musics;

    private final int maxPage;

    public Music(Main plugin) {
        super("music.yml", plugin);
        this.plugin = plugin;
        Config pluginConfig = plugin.getBabies();
        this.enabled = this.config.getBoolean("Enabled");
        this.worldsBlacklist = this.config.getStringList("WorldsBlacklist");
        this.command = this.config.getString("Command");
        this.noPermissionMessage = this.config.getString("NoPermissionMessage");
        this.giveSound = Sound.valueOf(this.config.getString("GiveSound"));
        this.removeSound = Sound.valueOf(this.config.getString("RemoveSound"));
        this.songsPath = plugin.getDataFolder() + this.config.getString("SongsPath");
        this.loop = this.config.getBoolean("Loop");
        this.guiName = this.config.getString("GUIName");
        this.guiSize = Parsing.parseInt(this.config.getString("GUISize"));
        this.noPermissionItem = Methods.getNoPermissionItem(this.config);
        this.noPermissionItem_Enabled = this.config.getBoolean("NoPermissionItem.Enabled");
        this.removeItem = Methods.getGuiItem(this.config, "RemoveItem");
        this.homeItem = Methods.getGuiItem(this.config, "HomeItem");
        this.previousItem = Methods.getGuiItem(this.config, "PreviousItem");
        this.nextItem = Methods.getGuiItem(this.config, "NextItem");
        this.musics = getMusicsInternal();
        this.maxPage = getMaxPageInternal();
        saveExampleSong();
        if (pluginConfig.getLoggingLevel() >= 2)
            General.sendColoredMessage(plugin, "Music config successfully loaded!", ChatColor.GREEN);
    }

    private int getMaxPageInternal() {
        int biggestSlot = 0;
        for (MusicStorage music : this.musics) {
            if (music.getPosition() > biggestSlot) biggestSlot = music.getPosition();
        }
        return biggestSlot / 44;
    }

    private List<MusicStorage> getMusicsInternal() {
        List<MusicStorage> musicz = new ArrayList<>();
        for(String s : this.customConfig.getKeys("Music")) {
            musicz.add(this.getMusicByIdentifier(s));
        }
        return musicz;
    }

    private MusicStorage getMusicByIdentifier(String identifier) {
        String path = "Music." + identifier + '.';
        String item = this.config.getString(path + "Item");
        return new MusicStorage(
                this.plugin,
                identifier,
                new CustomItem(Parsing.parseMaterial(item), 1, Parsing.parseData(item), Placeholders.parse(this.config.getString(path + "Name")), Placeholders.parse(this.config.getString(path + "Description"))),
                this.config.getInt(path + "Position"),
                this.config.getString(path + "Permission"),
                this.config.getString(path + "SongName")
        );
    }

    private void saveExampleSong() {
        File dir = new File(this.songsPath);
        if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
            dir.mkdir();
        File song = new File(this.songsPath, "ExampleMusic.nbs");
        if (!song.exists()) {
            this.plugin.saveResource("ExampleMusic.nbs", false);
            try {
                Files.move(new File(this.plugin.getDataFolder(), "ExampleMusic.nbs").toPath(), song.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Iterable<MusicStorage> getMusics() {
        return Collections.unmodifiableList(this.musics);
    }

    public String getGuiName() {
        return this.guiName;
    }

    public Sound getRemoveSound() {
        return this.removeSound;
    }

    public Sound getGiveSound() {
        return this.giveSound;
    }

    public String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean isEnabled() {
        return this.enabled;
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

    public int getGuiSize() {
        return this.guiSize;
    }

    public boolean isNoPermissionItemEnabled() {
        return this.noPermissionItem_Enabled;
    }

    public CustomItem getNoPermissionItem() {
        return this.noPermissionItem;
    }

    public CustomItem getNoPermissionItem(MusicStorage music) {
        if ("%cosmeticsname%".equals(this.noPermissionItem.getItemMeta().getDisplayName())) {
            CustomItem item = new CustomItem(this.noPermissionItem.clone());
            item.setName(item.getItemMeta().getDisplayName().replace("%cosmeticsname%", music.getItem().getItemMeta().getDisplayName()));
            return item;
        }
        return this.noPermissionItem;
    }

    public boolean isLoop() {
        return this.loop;
    }

    public String getSongsPath() {
        return this.songsPath;
    }

    public int getMaxPage() {
        return this.maxPage;
    }
}
