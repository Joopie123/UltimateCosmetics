package com.j0ach1mmall3.ultimatecosmetics.internal.gui;

import com.j0ach1mmall3.jlib.integration.Placeholders;
import com.j0ach1mmall3.jlib.inventory.GUI;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.CosmeticsAPI;
import com.j0ach1mmall3.ultimatecosmetics.api.events.PlayerOpenGuiEvent;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.ParticleStorage;
import com.j0ach1mmall3.ultimatecosmetics.internal.Methods;
import com.j0ach1mmall3.ultimatecosmetics.internal.config.Particles;
import com.j0ach1mmall3.ultimatecosmetics.internal.particles.ParticleImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by j0ach1mmall3 on 17:51 3/10/2015 using IntelliJ IDEA.
 */
public final class ParticlesGuiHandler extends GuiHandler {
    public ParticlesGuiHandler(Main plugin) {
        super(plugin);
    }

    public static void open(Player p, int page) {
        Player p1 = p;
        Particles config = plugin.getParticles();
        CosmeticsAPI api = plugin.getApi();
        GUI gui = buildGui(config.getGuiName(), config.getGuiSize(), config.getHomeItem(), config.getPreviousItem(), config.getNextItem());
        for (ParticleStorage particle : config.getParticles()) {
            int position = getRealPosition(particle.getPosition(), page, config.getGuiSize());
            if (position != -1) {
                ItemStack item = particle.getItem().clone();
                if (api.hasParticle(p1)) {
                    if (api.getParticle(p1).getParticleStorage().getIdentifier().equals(particle.getIdentifier()))
                        item.addUnsafeEnchantment(glow, 1);
                }
                gui.setItem(position, item);
                if (config.isNoPermissionItemEnabled() && !Methods.hasPermission(p1, particle.getPermission())) {
                    gui.setItem(position, config.getNoPermissionItem(particle));
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
        Particles config = plugin.getParticles();
        if (gui.getName().equals(Placeholders.parse(config.getGuiName(), p))) {
            CosmeticsAPI api = plugin.getApi();
            if (config.getHomeItem().getItem().isSimilar(item)) {
                if (plugin.getBabies().getGuiClickSound() != null)
                    Sounds.playSound(p, plugin.getBabies().getGuiClickSound());
                CosmeticsGuiHandler.open(p);
                return;
            }
            if (config.getRemoveItem().getItem().isSimilar(item)) {
                if (api.hasParticle(p)) api.getParticle(p).remove();
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
            ParticleStorage particle = api.getParticleByItemStack(item);
            if (!Methods.hasPermission(p, particle.getPermission())) {
                plugin.informPlayerNoPermission(p, config.getNoPermissionMessage());
                return;
            }
            new ParticleImpl(p, particle).give();
            Sounds.playSound(p, config.getGiveSound());
            p.closeInventory();
        }
    }
}
