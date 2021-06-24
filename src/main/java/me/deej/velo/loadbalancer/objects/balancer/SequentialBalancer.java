package me.deej.velo.loadbalancer.objects.balancer;

import me.deej.velo.loadbalancer.objects.Balancer;
import me.deej.velo.loadbalancer.objects.Lobby;

import java.util.List;

public class SequentialBalancer implements Balancer {

    private int lastServer = 0;

    @Override
    public Lobby getLobby(List<Lobby> lobbies) {
        lastServer++;
        if (lobbies.size() >= lastServer)
            lastServer = 0;

        return lobbies.get(lastServer);
    }
}
