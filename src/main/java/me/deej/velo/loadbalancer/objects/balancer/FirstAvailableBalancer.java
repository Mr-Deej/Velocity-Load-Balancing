package me.deej.velo.loadbalancer.objects.balancer;

import me.deej.velo.loadbalancer.objects.Balancer;
import me.deej.velo.loadbalancer.objects.Lobby;

import java.util.List;

public class FirstAvailableBalancer implements Balancer {

    @Override
    public Lobby getLobby(List<Lobby> lobbies) {
        for (Lobby lobby : lobbies) {
            int onlinePlayers = lobby.getServer().getPlayersConnected().size();
            if (onlinePlayers < lobby.getMaxPlayers())
                return lobby;
        }
        return null;
    }
}
