package com.j0ach1mmall3.ultimatecosmetics.internal.commands;

import com.j0ach1mmall3.jlib.integration.profilefetcher.PlayerProfile;
import com.j0ach1mmall3.jlib.integration.profilefetcher.ProfileFetcher;
import com.j0ach1mmall3.jlib.logging.JLogger;
import com.j0ach1mmall3.jlib.methods.General;
import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.jlib.storage.StorageAction;
import com.j0ach1mmall3.jlib.storage.database.CallbackHandler;
import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.j0ach1mmall3.ultimatecosmetics.api.CosmeticsAPI;
import com.j0ach1mmall3.ultimatecosmetics.internal.Methods;
import com.j0ach1mmall3.ultimatecosmetics.internal.balloons.BalloonImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.banners.BannerImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.data.CosmeticsQueue;
import com.j0ach1mmall3.ultimatecosmetics.internal.data.DataLoader;
import com.j0ach1mmall3.ultimatecosmetics.internal.fireworks.FireworkImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.gadgets.GadgetImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.hats.HatImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.hearts.HeartImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.morphs.MorphImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.mounts.MountImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.music.MusicImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.particles.ParticleImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.pets.PetImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.trails.TrailImpl;
import com.j0ach1mmall3.ultimatecosmetics.internal.wardrobe.OutfitImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * Created by j0ach1mmall3 on 10:39 24/08/2015 using IntelliJ IDEA.
 */
public final class Commands implements CommandExecutor {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
        for (String command : plugin.getDescription().getCommands().keySet()) {
            plugin.getCommand(command).setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        CosmeticsAPI api = this.plugin.getApi();
        final DataLoader loader = this.plugin.getDataLoader();
        if ("UltimateCosmetics".equalsIgnoreCase(cmd.getName())) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                sender.sendMessage(ChatColor.GOLD + this.plugin.getDescription().getName() + ' ' + ChatColor.DARK_PURPLE + this.plugin.getDescription().getVersion());
                sender.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.DARK_PURPLE + this.plugin.getDescription().getAuthors().get(0));
                sender.sendMessage(ChatColor.GOLD + this.plugin.getDescription().getDescription());
                sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                return true;
            }
            if ("reload".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission("uc.reload")) {
                    this.plugin.informPlayerNoPermission(sender, this.plugin.getLang().getCommandNoPermission());
                    return true;
                }
                this.plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "Done reloading the plugin!");
                return true;
            }
            if("debug".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission("uc.debug")) {
                    this.plugin.informPlayerNoPermission(sender, this.plugin.getLang().getCommandNoPermission());
                    return true;
                }
                new JLogger(this.plugin).dumpDebug(this.plugin.getDataLoader().getStorage().getActions().toArray(new StorageAction[this.plugin.getDataLoader().getStorage().getActions().size()]), new ConfigLoader[]{
                        this.plugin.getBabies(),
                        this.plugin.getMisc(),
                        this.plugin.getLang(),
                        this.plugin.getBalloons(),
                        this.plugin.getBanners(),
                        this.plugin.getFireworks(),
                        this.plugin.getGadgets(),
                        this.plugin.getHats(),
                        this.plugin.getHearts(),
                        this.plugin.getMorphs(),
                        this.plugin.getMounts(),
                        this.plugin.getMusic(),
                        this.plugin.getParticles(),
                        this.plugin.getPets(),
                        this.plugin.getTrails(),
                        this.plugin.getWardrobe()
                }, new CallbackHandler<String>() {
                    @Override
                    public void callback(String s) {
                        sender.sendMessage(ChatColor.GREEN + "Debug dump can be found at " + ChatColor.GOLD + s);
                    }
                });
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Usage: /uc reload, /uc debug");
            return true;
        }
        if ("GiveAmmo".equalsIgnoreCase(cmd.getName())) {
            if (checkAmmoRequirements(sender, args, "uc.giveammo")) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], false);
            int amount = Parsing.parseInt(args[2]);
            loader.giveAmmo(args[1], target, amount);
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[2] + " Ammo for " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("RemoveAmmo".equalsIgnoreCase(cmd.getName())) {
            if (checkAmmoRequirements(sender, args, "uc.removeammo")) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], false);
            int amount = Parsing.parseInt(args[2]);
            loader.takeAmmo(args[1], target, amount);
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[2] + " Ammo for " + args[1] + " from " + args[0] + '!');
            return true;
        }
        if ("RemoveAllCosmetics".equalsIgnoreCase(cmd.getName())) {
            if (!sender.hasPermission("uc.removeallcosmetics")) {
                this.plugin.informPlayerNoPermission(sender, this.plugin.getLang().getCommandNoPermission());
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /<command> <target>");
                return true;
            }
            Player p = General.getPlayerByName(args[0], true);
            if (p == null) {
                new ProfileFetcher(this.plugin).getByName(args[0], new CallbackHandler<PlayerProfile>() {
                    @Override
                    public void callback(PlayerProfile playerProfile) {
                        Commands.this.plugin.getDataLoader().updateQueue(playerProfile.getUuid().toString(), new CosmeticsQueue(Commands.this.plugin, Collections.<String>emptyList()));
                        sender.sendMessage(ChatColor.GREEN + "Successfully removed " + playerProfile.getUuid() + "'s Cosmetics!");
                    }
                });
            } else {
                Methods.removeCosmetics(p, this.plugin);
                this.plugin.getDataLoader().updateQueue(p.getUniqueId().toString(), new CosmeticsQueue(this.plugin, Collections.<String>emptyList()));
            }
            return true;
        }
        if ("Stacker".equalsIgnoreCase(cmd.getName())) {
            if (!this.plugin.getMisc().isStackerEnabled()) {
                this.plugin.informPlayerNotEnabled(sender);
                return true;
            }
            if (sender instanceof Player) {
                final Player p = (Player) sender;
                if (!p.hasPermission("uc.stacker")) {
                    this.plugin.informPlayerNoPermission(p, this.plugin.getLang().getCommandNoPermission());
                    return true;
                }
                loader.getStacker(p, new CallbackHandler<Boolean>() {
                    @Override
                    public void callback(Boolean b) {
                        String toggledStacker = Commands.this.plugin.getLang().getToggledStacker();
                        if(!toggledStacker.isEmpty()) p.sendMessage(toggledStacker.replace("{statuscolor}", (b ? ChatColor.RED : ChatColor.GREEN).toString()));
                        loader.setStacker(p, !b);
                    }
                });
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "You need to be a player to use this Command!");
                return true;
            }
        }
        if ("GiveBalloon".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.balloon", this.plugin.getBalloons().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new BalloonImpl(target, this.plugin.getApi().getBalloonByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveBanner".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.banner", this.plugin.getBanners().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new BannerImpl(target, this.plugin.getApi().getBannerByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveMorph".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.morph", this.plugin.getMorphs().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new MorphImpl(target, this.plugin.getApi().getMorphByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveFirework".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.firework", this.plugin.getFireworks().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new FireworkImpl(target, this.plugin.getApi().getFireworkByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveGadget".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.gadget", this.plugin.getGadgets().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new GadgetImpl(target, this.plugin.getApi().getGadgetByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveHat".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.hat", this.plugin.getHats().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new HatImpl(target, this.plugin.getApi().getHatByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveHearts".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.hearts", this.plugin.getHearts().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new HeartImpl(target, this.plugin.getApi().getHeartByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveMount".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.mount", this.plugin.getMounts().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new MountImpl(target, this.plugin.getApi().getMountByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveMusic".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.music", this.plugin.getMusic().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new MusicImpl(target, this.plugin.getApi().getMusicByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveParticles".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.particles", this.plugin.getParticles().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new ParticleImpl(target, this.plugin.getApi().getParticleByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GivePet".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.pet", this.plugin.getPets().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new PetImpl(target, this.plugin.getApi().getPetByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveTrail".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.trail", this.plugin.getTrails().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new TrailImpl(target, this.plugin.getApi().getTrailByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("GiveOutfit".equalsIgnoreCase(cmd.getName())) {
            if (checkGiveRequirements(sender, args, "uc.give.outfit", this.plugin.getWardrobe().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            new OutfitImpl(target, this.plugin.getApi().getOutfitByIdentifier(args[1])).give();
            sender.sendMessage(ChatColor.GREEN + "Successfully gave " + args[1] + " to " + args[0] + '!');
            return true;
        }
        if ("RemoveBalloon".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.balloon", this.plugin.getBalloons().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getBalloon(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Balloon!");
            return true;
        }
        if ("RemoveBanner".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.banner", this.plugin.getBanners().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getBanner(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Banner!");
            return true;
        }
        if ("RemoveMorph".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.morph", this.plugin.getMorphs().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getMorph(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Morph!");
            return true;
        }
        if ("RemoveGadget".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.gadget", this.plugin.getGadgets().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            if (api.hasGadget(target)) target.getInventory().setItem(this.plugin.getGadgets().getGadgetSlot(), null);
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Gadget!");
            return true;
        }
        if ("RemoveHat".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.hat", this.plugin.getHats().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getHat(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Hat!");
            return true;
        }
        if ("RemoveHearts".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.hearts", this.plugin.getHearts().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getHeart(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Hearts!");
            return true;
        }
        if ("RemoveMusic".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.music", this.plugin.getMusic().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getMusic(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Music!");
            return true;
        }
        if ("RemoveParticles".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.particles", this.plugin.getParticles().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getParticle(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Particles!");
            return true;
        }
        if ("RemovePet".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.pet", this.plugin.getPets().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getPet(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Pet!");
            return true;
        }
        if ("RemoveTrail".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.trail", this.plugin.getTrails().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getTrail(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Trail!");
            return true;
        }
        if ("RemoveOutfit".equalsIgnoreCase(cmd.getName())) {
            if (checkRemoveRequirements(sender, args, "uc.remove.outfit", this.plugin.getWardrobe().isEnabled())) {
                return true;
            }
            Player target = General.getPlayerByName(args[0], true);
            api.getOutfit(target).remove();
            sender.sendMessage(ChatColor.GREEN + "Successfully removed " + args[0] + "'s Outfit!");
            return true;
        }
        return false;
    }

    private boolean checkAmmoRequirements(CommandSender s, String[] args, String permission) {
        if (!s.hasPermission(permission)) {
            this.plugin.informPlayerNoPermission(s, this.plugin.getLang().getCommandNoPermission());
            return true;
        }
        if (args.length < 3) {
            s.sendMessage(ChatColor.RED + "Usage: /<command> <target> <gadgetidentifier> <amount>");
            return true;
        }
        if (General.getPlayerByName(args[0], true) == null) {
            s.sendMessage(ChatColor.RED + "Unknown Player!");
            return true;
        }
        if (this.plugin.getApi().getGadgetByIdentifier(args[1]) == null) {
            s.sendMessage(ChatColor.RED + "Unknown Gadget!");
            return true;
        }
        if (Parsing.parseInt(args[2]) <= 0) {
            s.sendMessage(ChatColor.RED + "Invalid Number!");
            return true;
        }
        return false;
    }

    private boolean checkGiveRequirements(CommandSender s, String[] args, String permission, boolean enabled) {
        if (!enabled) {
            this.plugin.informPlayerNotEnabled(s);
            return true;
        }
        if (!s.hasPermission(permission)) {
            this.plugin.informPlayerNoPermission(s, this.plugin.getLang().getCommandNoPermission());
            return true;
        }
        if (args.length < 2) {
            s.sendMessage(ChatColor.RED + "Usage: /<command> <target> <cosmetic>");
            return true;
        }
        if (General.getPlayerByName(args[0], true) == null) {
            s.sendMessage(ChatColor.RED + "Unknown Player!");
            return true;
        }
        return false;
    }

    private boolean checkRemoveRequirements(CommandSender s, String[] args, String permission, boolean enabled) {
        if (!enabled) {
            this.plugin.informPlayerNotEnabled(s);
            return true;
        }
        if (!s.hasPermission(permission)) {
            this.plugin.informPlayerNoPermission(s, this.plugin.getLang().getCommandNoPermission());
            return true;
        }
        if (args.length < 1) {
            s.sendMessage(ChatColor.RED + "Usage: /<command> <target>");
            return true;
        }
        if (General.getPlayerByName(args[0], true) == null) {
            s.sendMessage(ChatColor.RED + "Unknown Player!");
            return true;
        }
        return false;
    }
}
