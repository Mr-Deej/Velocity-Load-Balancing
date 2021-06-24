package me.deej.velo.loadbalancer.command;

import com.velocitypowered.api.command.SimpleCommand;
import lombok.RequiredArgsConstructor;
import me.deej.velo.loadbalancer.LoadBalancer;


import java.io.FileNotFoundException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ReloadCommand implements SimpleCommand {

    private final LoadBalancer plugin;

    @Override
    public void execute(Invocation invocation) {
        try {
            plugin.getConfig().reload();
            plugin.getLobbyManager().loadLobbies();

            invocation.source().sendMessage(plugin.getConfig().getMessage("successReload"));
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while reload", e);
            invocation.source().sendMessage(plugin.getConfig().getMessage("errorReload"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("velohub.reload");
    }
}
