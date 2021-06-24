package me.deej.velo.loadbalancer.objects.balancer;

import me.deej.velo.loadbalancer.objects.Balancer;
import me.deej.velo.loadbalancer.objects.Lobby;

import java.util.List;
import java.util.Random;

public class RandomBalancer implements Balancer {

    private final Random random = new Random();

    @Override
    public Lobby getLobby(List<Lobby> lobbies) {
        return lobbies.get(random.nextInt(lobbies.size()));
    }
}
