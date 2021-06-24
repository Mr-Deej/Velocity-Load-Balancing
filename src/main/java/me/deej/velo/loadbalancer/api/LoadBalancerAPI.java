package me.deej.velo.loadbalancer.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import me.deej.velo.loadbalancer.LoadBalancer;
import me.deej.velo.loadbalancer.objects.Lobby;

import java.util.Optional;

public final class LoadBalancerAPI {
    private LoadBalancerAPI() {
    }

    private static LoadBalancer plugin;

    public static void init(LoadBalancer pl) {
        plugin = pl;
    }

    /**
     * This method gets the Lobby where a Player is currently
     * @param player The Player
     * @return The Lobby, can be null
     */
    public static Lobby getLobby(Player player) {
        Optional<ServerConnection> server = player.getCurrentServer();
        return server.map(serverConnection -> plugin.getLobbyManager().getLobby(serverConnection.getServer().getServerInfo().getName())).orElse(null);

    }
}
