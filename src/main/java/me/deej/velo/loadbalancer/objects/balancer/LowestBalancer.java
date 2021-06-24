package me.deej.velo.loadbalancer.objects.balancer;

import me.deej.velo.loadbalancer.objects.Balancer;
import me.deej.velo.loadbalancer.objects.Lobby;

import java.util.List;

public class LowestBalancer implements Balancer {

    @Override
    public Lobby getLobby(List<Lobby> lobbies) {
        Lobby lowestLobby = null;

        for (Lobby lobby : lobbies) {
            if (lowestLobby == null || lowestLobby.getServer().getPlayersConnected().size() > lobby.getServer().getPlayersConnected().size())
                lowestLobby = lobby;
        }
        return lowestLobby;
    }
}
