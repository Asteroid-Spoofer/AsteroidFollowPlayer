package me.serbob.asteroidfollowplayer.commands;

import me.serbob.asteroidapi.AsteroidAPI;
import me.serbob.asteroidapi.actions.example.PathingAction;
import me.serbob.asteroidapi.commands.AsteroidCommand;
import me.serbob.asteroidapi.registries.FakePlayerEntity;
import me.serbob.asteroidapi.registries.FakePlayerRegistry;
import me.serbob.asteroidfollowplayer.actions.FolllowPlayerAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * You don't need to check for any permissions because the people who can use this command
 * already have UUID access to /asteroid
 */
public class FollowCommand implements AsteroidCommand {
    private final Map<UUID, FolllowPlayerAction> activeFollows = new HashMap<>();

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Invalid args. Must specify two valid players.");
            return false;
        }

        if (args[0].equals(args[1])) {
            commandSender.sendMessage(ChatColor.RED + "You cannot specify the same player twice!");
            return false;
        }

        Player fakePlayer = Bukkit.getPlayer(args[0]);
        Player target = Bukkit.getPlayer(args[1]);

        if (fakePlayer == null && target == null) {
            commandSender.sendMessage(ChatColor.RED + "Both players '" + args[0] + "' and '" + args[1] + "' were not found!");
            return false;
        }

        if (fakePlayer == null) {
            commandSender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' was not found!");
            return false;
        }

        if (target == null) {
            commandSender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' was not found!");
            return false;
        }

        if (!FakePlayerRegistry.INSTANCE.isAFakePlayer(fakePlayer.getUniqueId())) {
            commandSender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not a fake player!");
            return false;
        }

        // I know, I know, bad way to get a fake player
        // Will update the api soon...
        FakePlayerEntity fakePlayerEntity = (FakePlayerEntity)
                FakePlayerRegistry.INSTANCE.getFakePlayers().get(fakePlayer.getUniqueId());

        if (activeFollows.containsKey(fakePlayer.getUniqueId())) {
            FolllowPlayerAction existingAction = activeFollows.get(fakePlayer.getUniqueId());
            fakePlayerEntity.getFBrain().getActionManager().forceUnregister(existingAction);
            activeFollows.remove(fakePlayer.getUniqueId());

            commandSender.sendMessage(ChatColor.GREEN + "Stopped following " + target.getName());
            return true;
        }

        FolllowPlayerAction newAction = new FolllowPlayerAction(target);
        fakePlayerEntity.getFBrain().getActionManager().register(newAction);
        activeFollows.put(fakePlayer.getUniqueId(), newAction);

        commandSender.sendMessage(ChatColor.GREEN + "Started following " + target.getName());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (FakePlayerRegistry.INSTANCE.isAFakePlayer(player.getUniqueId())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 2) {
            Player firstPlayer = Bukkit.getPlayer(args[0]);
            if (firstPlayer != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.getName().equals(firstPlayer.getName())) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;
    }
}
