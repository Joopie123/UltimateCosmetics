package com.j0ach1mmall3.ultimatecosmetics.config;

import com.j0ach1mmall3.jlib.inventory.CustomItem;
import com.j0ach1mmall3.jlib.inventory.GUI;
import com.j0ach1mmall3.jlib.inventory.GuiItem;
import com.j0ach1mmall3.jlib.logging.JLogger;
import com.j0ach1mmall3.jlib.methods.General;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.jlib.plugin.JLibPlugin;
import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.Methods;
import com.j0ach1mmall3.ultimatecosmetics.api.Cosmetic;
import com.j0ach1mmall3.ultimatecosmetics.api.CosmeticType;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.CosmeticStorage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 2/03/2016
 */
public abstract class CosmeticConfig extends ConfigLoader {
    private final List<String> worldsBlacklist;
    private final String command;
    private final String noPermissionMessage;
    private final Sound giveSound;
    private final Sound removeSound;
    private final String guiName;
    private final int guiSize;
    private final CustomItem noPermissionItem;
    private final boolean noPermissionItem_Enabled;
    private final GuiItem removeItem;
    private final GuiItem homeItem;
    private final GuiItem previousItem;
    private final GuiItem nextItem;
    private final List<CosmeticStorage> cosmetics;
    private final int maxPage;

    public CosmeticConfig(String config, JLibPlugin plugin, String section) {
        super(config, plugin);
        this.worldsBlacklist = this.config.getStringList("WorldsBlacklist");
        this.command = this.config.getString("Command");
        this.noPermissionMessage = this.config.getString("NoPermissionMessage");
        this.giveSound = this.config.getString("GiveSound") == null ? null : Sound.valueOf(this.config.getString("GiveSound"));
        this.removeSound = this.config.getString("RemoveSound") == null ? null : Sound.valueOf(this.config.getString("RemoveSound"));
        this.guiName = this.config.getString("GUIName");
        this.guiSize = this.config.getInt("GUISize");
        this.noPermissionItem = new CustomItem(this.customConfig.getItemNew(this.config, "NoPermissionItem.Item"));
        this.noPermissionItem_Enabled = this.config.getBoolean("NoPermissionItem.Enabled");
        this.removeItem = this.customConfig.getGuiItemNew(this.config, "RemoveItem");
        this.homeItem = this.customConfig.getGuiItemNew(this.config, "HomeItem");
        this.previousItem = this.customConfig.getGuiItemNew(this.config, "PreviousItem");
        this.nextItem = this.customConfig.getGuiItemNew(this.config, "NextItem");
        this.cosmetics = this.getCosmetics(section);
        this.maxPage = this.getMaxPageInternal();
        plugin.getjLogger().log(ChatColor.GREEN + section + " config successfully loaded!", JLogger.LogLevel.EXTENDED);
    }

    private int getMaxPageInternal() {
        int page = 0;
        for (CosmeticStorage cosmeticStorage : this.cosmetics) {
            if (cosmeticStorage.getPage() > page) page = cosmeticStorage.getPage();
        }
        return page;
    }

    private List<CosmeticStorage> getCosmetics(String section) {
        List<CosmeticStorage> cosmeticStorages = new ArrayList<>();
        for(String s : this.customConfig.getKeys(section)) {
            cosmeticStorages.add(this.getCosmeticStorageByIdentifier(section, s));
        }
        return cosmeticStorages;
    }

    protected CosmeticStorage getCosmeticStorageByIdentifier(String section, String identifier) {
        String path = section + '.' + identifier + '.';
        return new CosmeticStorage(
                (Main) this.storage.getPlugin(),
                identifier,
                this.config.getString(path + "Permission"),
                this.customConfig.getGuiItemNew(this.config, path)
        );
    }

    public final CustomItem getNoPermissionItem(CosmeticStorage cosmeticStorage) {
        if ("%cosmeticsname%".equals(this.noPermissionItem.getItemMeta().getDisplayName())) {
            CustomItem item = new CustomItem(this.noPermissionItem.clone());
            item.setName(item.getItemMeta().getDisplayName().replace("%cosmeticsname%", cosmeticStorage.getGuiItem().getItem().getItemMeta().getDisplayName()));
            return item;
        }
        return this.noPermissionItem;
    }

    public final CosmeticStorage getByIdentifier(String identifier) {
        for(CosmeticStorage cosmeticStorage : this.cosmetics) {
            if(cosmeticStorage.getIdentifier().equals(identifier)) return cosmeticStorage;
        }
        return null;
    }

    public final CosmeticStorage getByPosition(int page, int position) {
        for(CosmeticStorage cosmeticStorage : this.cosmetics) {
            if(cosmeticStorage.getPage() == page && cosmeticStorage.getGuiItem().getPosition() == position) return cosmeticStorage;
        }
        return null;
    }

    public final void openGui(CosmeticType cosmeticType, Player player, int page) {
        Main plugin = (Main) this.storage.getPlugin();
        GUI gui = new GUI(this.guiName, this.guiSize);
        gui.setItem(this.homeItem);
        gui.setItem(this.previousItem);
        gui.setItem(this.nextItem);
        gui.setItem(this.removeItem);
        for(CosmeticStorage cosmeticStorage : this.cosmetics) {
            if(cosmeticStorage.getPage() == page) {
                int position = cosmeticStorage.getGuiItem().getPosition();
                ItemStack itemStack = cosmeticStorage.getGuiItem().getItem().clone();
                for(Cosmetic cosmetic : plugin.getApi().getCosmetics(player, cosmeticType)) {
                    if(cosmetic.getCosmeticStorage().getIdentifier().equals(cosmeticStorage.getIdentifier())) itemStack.addEnchantment(plugin.getGlow(), 1);
                }
                gui.setItem(position, itemStack);
                if (this.isNoPermissionItemEnabled() && !General.hasCustomPermission(player, cosmeticStorage.getPermission())) gui.setItem(position, this.getNoPermissionItem(cosmeticStorage));
            }
        }
        gui.open(player);
    }

    public final void handleClick(CosmeticType cosmeticType, InventoryClickEvent e, int page) {
        GUI gui = new GUI(this.guiName, 9);
        if (gui.hasClicked(e)) {
            Main plugin = (Main) this.storage.getPlugin();
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if (item.getItemMeta().getDisplayName().isEmpty()) return;
            Player p = (Player) e.getWhoClicked();
            if (item.containsEnchantment(plugin.getGlow())) return;
            int slot = e.getSlot();
            if(slot == this.homeItem.getPosition()) {
                plugin.getGuiHandler().openMainGui(p);
                if(((Config) plugin.getBabies()).getGuiClickSound() != null) Sounds.playSound(p, ((Config) plugin.getBabies()).getGuiClickSound());
                return;
            }
            if(slot == this.previousItem.getPosition()) {
                int newPage = page == 0 ? this.maxPage : page - 1;
                plugin.getGuiHandler().openGui(p, cosmeticType, newPage);
                if(((Config) plugin.getBabies()).getGuiClickSound() != null) Sounds.playSound(p, ((Config) plugin.getBabies()).getGuiClickSound());
                return;
            }
            if(slot == this.nextItem.getPosition()) {
                int newPage = page == this.maxPage ? 0 : page + 1;
                plugin.getGuiHandler().openGui(p, cosmeticType, newPage);
                if(((Config) plugin.getBabies()).getGuiClickSound() != null) Sounds.playSound(p, ((Config) plugin.getBabies()).getGuiClickSound());
                return;
            }
            if(slot == this.removeItem.getPosition()) {
                for(Cosmetic cosmetic : plugin.getApi().getCosmetics(p, cosmeticType)) {
                    cosmetic.remove();
                    if(this.removeSound != null) Sounds.playSound(p, this.removeSound);
                }
                p.closeInventory();
                return;
            }
            if(Methods.isNoPermissionItem(this.noPermissionItem, e.getCurrentItem())) {
                plugin.informPlayerNoPermission(p, this.noPermissionMessage);
                return;
            }
            CosmeticStorage cosmeticStorage = this.getByPosition(page, e.getSlot());
            if(cosmeticStorage != null) {
                this.getCosmetic(cosmeticStorage, p).give();
                if(this.giveSound != null) Sounds.playSound(p, this.giveSound);
                p.closeInventory();
            }
        }
    }

    public final List<String> getWorldsBlacklist() {
        return this.worldsBlacklist;
    }

    public final String getCommand() {
        return this.command;
    }

    public final String getNoPermissionMessage() {
        return this.noPermissionMessage;
    }

    public final Sound getGiveSound() {
        return this.giveSound;
    }

    public final Sound getRemoveSound() {
        return this.removeSound;
    }

    public final String getGuiName() {
        return this.guiName;
    }

    public final int getGuiSize() {
        return this.guiSize;
    }

    public final CustomItem getNoPermissionItem() {
        return this.noPermissionItem;
    }

    public final boolean isNoPermissionItemEnabled() {
        return this.noPermissionItem_Enabled;
    }

    public final GuiItem getRemoveItem() {
        return this.removeItem;
    }

    public final GuiItem getHomeItem() {
        return this.homeItem;
    }

    public final GuiItem getPreviousItem() {
        return this.previousItem;
    }

    public final GuiItem getNextItem() {
        return this.nextItem;
    }

    public final List<CosmeticStorage> getCosmetics() {
        return this.cosmetics;
    }

    public final int getMaxPage() {
        return this.maxPage;
    }

    public abstract Cosmetic getCosmetic(CosmeticStorage cosmeticStorage, Player player);
}
