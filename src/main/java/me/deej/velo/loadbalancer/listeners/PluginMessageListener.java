package me.deej.velo.loadbalancer.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.RequiredArgsConstructor;
import me.deej.velo.loadbalancer.LoadBalancer;
import me.deej.velo.loadbalancer.objects.Lobby;

@RequiredArgsConstructor
public class PluginMessageListener {

    private static final LegacyChannelIdentifier LEGACY_CHANNEL = new LegacyChannelIdentifier("VeloHub");
    private static final MinecraftChannelIdentifier MODERN_CHANNEL = MinecraftChannelIdentifier.create("velohub", "main");

    private final LoadBalancer plugin;

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getIdentifier().equals(LEGACY_CHANNEL) && !event.getIdentifier().equals(MODERN_CHANNEL))
            return;

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (!(event.getSource() instanceof ServerConnection))
            return;

        ServerConnection connection = (ServerConnection) event.getSource();
        ByteArrayDataInput input = event.dataAsDataStream();

        String subChannel = input.readUTF();
        if (subChannel.equalsIgnoreCase("ConnectHub")) {
            Lobby lobby = plugin.getLobbyManager().getLobby();
            if (lobby == null)
                return;

            connection.getPlayer().createConnectionRequest(lobby.getServer()).fireAndForget();
        }
    }

}
