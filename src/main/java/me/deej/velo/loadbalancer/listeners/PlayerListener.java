package me.deej.velo.loadbalancer.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import lombok.RequiredArgsConstructor;
import me.deej.velo.loadbalancer.LoadBalancer;
import me.deej.velo.loadbalancer.objects.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.Optional;

@RequiredArgsConstructor
public class PlayerListener {

    private final LoadBalancer plugin;

    @Subscribe
    public void onInitialServerSelect(PlayerChooseInitialServerEvent event) {
        Lobby lobby = plugin.getLobbyManager().getLobby();
        if (lobby == null) {
            event.getPlayer().disconnect(plugin.getConfig().getMessage("kickNoLobbyAvailable"));
            return;
        }

        event.setInitialServer(lobby.getServer());
    }

    @Subscribe
    public void onKick(KickedFromServerEvent event) {
        if (!plugin.getLobbyManager().isSendPlayersToHubOnCLose())
            return;

        Optional<Component> kickReason = event.getServerKickReason();
        if (!kickReason.isPresent())
            return;

        String json = GsonComponentSerializer.gson().serialize(kickReason.get());
        if (!json.toLowerCase().contains("server closed"))
            return;

        Lobby lobby = plugin.getLobbyManager().getLobby();
        if (lobby == null) {
            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(plugin.getConfig().getMessage("kickNoLobbyAvailable")));
            return;
        }

        event.setResult(KickedFromServerEvent.RedirectPlayer.create(lobby.getServer()));
    }

}
