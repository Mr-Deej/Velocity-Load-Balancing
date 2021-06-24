package me.deej.velo.loadbalancer.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.deej.velo.loadbalancer.LoadBalancer;
import me.deej.velo.loadbalancer.objects.Balancer;
import me.deej.velo.loadbalancer.objects.Lobby;
import me.deej.velo.loadbalancer.objects.balancer.FirstAvailableBalancer;
import me.deej.velo.loadbalancer.objects.balancer.LowestBalancer;
import me.deej.velo.loadbalancer.objects.balancer.RandomBalancer;
import me.deej.velo.loadbalancer.objects.balancer.SequentialBalancer;

import java.util.*;
import java.util.logging.Level;

@RequiredArgsConstructor
public class LobbyManager {

    private final LoadBalancer plugin;
    private final List<Lobby> lobbies = Collections.synchronizedList(new ArrayList<>());

    private Balancer balancer;
    @Getter private boolean sendPlayersToHubOnCLose;

    /**
     * This method loads all the lobbies from the config file
     */
    public void loadLobbies(){
        lobbies.clear();

        for (JsonElement element : plugin.getConfig().getObject().getAsJsonArray("lobby-servers")) {
            JsonObject object = element.getAsJsonObject();
            String name = object.get("name").getAsString();
            int protocolVersion = object.get("lowest-protocol-version").getAsInt();

            Optional<RegisteredServer> optionalServer = plugin.getServer().getServer(name);
            if (!optionalServer.isPresent()) {
                plugin.getLogger().info("Invalid server name: "+ name);
                continue;
            }

            RegisteredServer server = optionalServer.get();
            Lobby lobby = new Lobby(name, protocolVersion, server);

            server.ping().whenComplete((ping, exception) -> {
                if (ping == null) {
                    plugin.getLogger().log(Level.WARNING, "An error occurred while pinging " + lobby.getName(), exception);
                    return;
                }

                Optional<ServerPing.Players> players = ping.getPlayers();
                if (!players.isPresent())
                    return;

                lobby.setMaxPlayers(players.get().getMax());
            });
            lobbies.add(lobby);
        }

        plugin.getLogger().info("Loaded " + lobbies.size() + "lobbies.");
        balancer = getBalancer(plugin.getConfig().getObject().get("load-balancing-method").getAsString());
        sendPlayersToHubOnCLose = plugin.getConfig().getObject().get("send-players-to-hub-on-server-close").getAsBoolean();
    }

    /**
     * This method gets the first available lobby
     * @return The lobby, can be null
     */
    public Lobby getLobby() {
        return balancer.getLobby(lobbies);
    }

    /**
     * This method gets the lobby based on the server name
     * @param name the server name
     * @return The lobby, can be null
     */
    public Lobby getLobby(String name) {
        for (Lobby lobby : lobbies) {
            if (lobby.getName().equalsIgnoreCase(name))
                return lobby;
        }

        return null;
    }

    private Balancer getBalancer(String type) {
        switch (type.toUpperCase()) {
            case "LOWEST":
                return new LowestBalancer();
            case "FIRSTAVAILABLE":
                return new FirstAvailableBalancer();
            case "RANDOM":
                return new RandomBalancer();
            case "SEQUENTIAL":
                return new SequentialBalancer();
            default:
                return null;
        }
    }
}
