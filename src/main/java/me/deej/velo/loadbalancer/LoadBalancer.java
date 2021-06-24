package me.deej.velo.loadbalancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.deej.velo.loadbalancer.api.LoadBalancerAPI;
import me.deej.velo.loadbalancer.command.LobbyCommand;
import me.deej.velo.loadbalancer.command.ReloadCommand;
import me.deej.velo.loadbalancer.listeners.PlayerListener;
import me.deej.velo.loadbalancer.listeners.PluginMessageListener;
import me.deej.velo.loadbalancer.managers.LobbyManager;
import me.deej.velo.loadbalancer.utils.Config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(
        id = "LoadBalancer",
        name = "LoadBalancer",
        version = "1.0-SNAPSHOT",
        description = "To balance players across the Velocity network",
        url = "https://github.com/Mr-Deej",
        authors = {"Deej"}
)
public class LoadBalancer {

    @Getter
    private final java.util.logging.Logger logger;
    @Getter private final ProxyServer server;
    @Getter private final Path folderPath;

    @Getter private Config config;

    @Getter private LobbyManager lobbyManager;

    @Inject
    public LoadBalancer(Logger logger, ProxyServer server, @DataDirectory Path folderPath) {
        this.logger = logger;
        this.server = server;
        this.folderPath = folderPath;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        if (!registerConfigs())
            return;

        registerInstances();
        registerListeners();
        registerCommands();

        LoadBalancerAPI.init(this);
    }

    /**
     * This method registers the Config files
     * @return True if loaded successfully
     */
    private boolean registerConfigs() {
        try {
            config = new Config(this, folderPath, "config");
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while loading the config file");
            return false;
        }
    }

    /**
     * This method registers the instances
     */
    private void registerInstances() {
        lobbyManager = new LobbyManager(this);
        lobbyManager.loadLobbies();
    }

    /**
     * This method registers the listeners
     */
    private void registerListeners() {
        server.getEventManager().register(this, new PlayerListener(this));
        server.getEventManager().register(this, new PluginMessageListener(this));
    }

    /**
     * This method registers the commands
     */
    private void registerCommands() {
        server.getCommandManager().register(new ReloadCommand(this), "velohub-reload");

        JsonObject commandInfo = config.getObject().getAsJsonObject("hub-command");
        if (!commandInfo.get("enabled").getAsBoolean())
            return;

        List<String> aliases = new ArrayList<>();
        for (JsonElement element : commandInfo.getAsJsonArray("aliases"))
            aliases.add(element.getAsString());

        server.getCommandManager().register(new LobbyCommand(this), aliases.toArray(new String[0]));
    }

}