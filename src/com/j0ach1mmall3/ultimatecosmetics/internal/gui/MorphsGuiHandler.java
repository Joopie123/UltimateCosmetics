package com.j0ach1mmall3.ultimatecosmetics.internal.gui;

import com.j0ach1mmall3.jlib.integration.Placeholders;
import com.j0ach1mmall3.jlib.inventory.GUI;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.CosmeticsAPI;
import com.j0ach1mmall3.ultimatecosmetics.api.events.PlayerOpenGuiEvent;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.MorphStorage;
import com.j0ach1mmall3.ultimatecosmetics.internal.Methods;
import com.j0ach1mmall3.ultimatecosmetics.internal.config.Morphs;
import com.j0ach1mmall3.ultimatecosmetics.internal.morphs.MorphImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by j0ach1mmall3 on 17:51 3/10/2015 using IntelliJ IDEA.
 */
public final class MorphsGuiHandler extends GuiHandler {
    public MorphsGuiHandler(Main plugin) {
        super(plugin);
    }

    public static void open(Player p, int page) {
        Player p1 = p;
        Morphs config = plugin.getMorphs();
        CosmeticsAPI api = plugin.getApi();
        GUI gui = buildGui(config.getGuiName(), config.getGuiSize(), config.getHomeItem(), config.getPreviousItem(), config.getNextItem());
        for (MorphStorage morph : config.getMorphs()) {
            int position = getRealPosition(morph.getPosition(), page, config.getGuiSize());
            if (position != -1) {
                ItemStack item = morph.getItem().clone();
                if (api.hasMorph(p1)) {
                    if (api.getMorph(p1).getMorphStorage().getIdentifier().equals(morph.getIdentifier()))
                        item.addUnsafeEnchantment(glow, 1);
                }
                gui.setItem(position, item);
                if (config.isNoPermissionItemEnabled() && !Methods.hasPermission(p1, morph.getPermission())) {
                    gui.setItem(position, config.getNoPermissionItem(morph));
                }
            }
        }
        gui.setItem(config.getRemoveItem());
        PlayerOpenGuiEvent event = new PlayerOpenGuiEvent(p1, gui);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        gui = event.getGui();
        p1 = event.getPlayer();
        PAGEMAP.put(p1.getName(), page);
        gui.open(p1);
    }

    @Override
    protected void handleClick(GUI gui, Player p, ItemStack item) {
        Morphs config = plugin.getMorphs();
        if (gui.getName().equals(Placeholders.parse(config.getGuiName(), p))) {
            CosmeticsAPI api = plugin.getApi();
            if (config.getHomeItem().getItem().isSimilar(item)) {
                if (plugin.getBabies().getGuiClickSound() != null)
                    Sounds.playSound(p, plugin.getBabies().getGuiClickSound());
                CosmeticsGuiHandler.open(p);
                return;
            }
            if (config.getRemoveItem().getItem().isSimilar(item)) {
                if (api.hasMorph(p)) api.getMorph(p).remove();
                Sounds.playSound(p, config.getRemoveSound());
                p.closeInventory();
                return;
            }
            if (Methods.isNoPermissionItem(config.getNoPermissionItem(), item)) {
                plugin.informPlayerNoPermission(p, config.getNoPermissionMessage());
                return;
            }
            if (config.getPreviousItem().getItem().isSimilar(item)) {
                if (plugin.getBabies().getGuiClickSound() != null)
                    Sounds.playSound(p, plugin.getBabies().getGuiClickSound());
                int currPage = PAGEMAP.get(p.getName());
                if (currPage == 0) open(p, config.getMaxPage());
                else open(p, currPage - 1);
                return;
            }
            if (config.getNextItem().getItem().isSimilar(item)) {
                if (plugin.getBabies().getGuiClickSound() != null)
                    Sounds.playSound(p, plugin.getBabies().getGuiClickSound());
                int currPage = PAGEMAP.get(p.getName());
                if (currPage == config.getMaxPage()) open(p, 0);
                else open(p, currPage + 1);
                return;
            }
            MorphStorage morph = api.getMorphByItemStack(item);
            if (!Methods.hasPermission(p, morph.getPermission())) {
                plugin.informPlayerNoPermission(p, config.getNoPermissionMessage());
                return;
            }
            new MorphImpl(p, morph).give();
            Sounds.playSound(p, config.getGiveSound());
            p.closeInventory();
        }
    }
}
