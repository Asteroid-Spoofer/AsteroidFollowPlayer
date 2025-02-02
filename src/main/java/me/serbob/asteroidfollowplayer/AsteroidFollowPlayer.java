package me.serbob.asteroidfollowplayer;

import me.serbob.asteroidapi.commands.AsteroidCommandManager;
import me.serbob.asteroidapi.enums.MinecraftVersion;
import me.serbob.asteroidapi.extension.ExtensionLifecycle;
import me.serbob.asteroidapi.interfaces.Version;
import me.serbob.asteroidfollowplayer.commands.FollowCommand;

@Version(MinecraftVersion.ALL)
public final class AsteroidFollowPlayer extends ExtensionLifecycle {
    private static AsteroidFollowPlayer instance;

    @Override
    public void onEnable() {
        instance = this;

        AsteroidCommandManager.registerCommand("follow", new FollowCommand());
    }

    @Override
    public void onDisable() {}

    public static AsteroidFollowPlayer getInstance() {
        return instance;
    }
}
