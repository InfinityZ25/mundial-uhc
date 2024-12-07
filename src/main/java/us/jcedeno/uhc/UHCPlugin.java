package us.jcedeno.uhc;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import us.jcedeno.uhc.commands.GameManagementCommands;
import us.jcedeno.uhc.listeners.LobbyListeners;

public class UHCPlugin extends JavaPlugin {

    @Getter
    private static UHCPlugin plugin;

    @Getter
    private CommandManager<CommandSourceStack> commandManager;

    @Getter
    private AnnotationParser<CommandSourceStack> annotationParser;

    @Override
    public void onEnable() {
        UHCPlugin.plugin = this;

        this.commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
                .buildOnEnable(plugin);


        annotationParser = new AnnotationParser<>(commandManager, CommandSourceStack.class);

        try {
            annotationParser.parse(this);
            annotationParser.parseContainers();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Hello world!");

        Bukkit.getPluginManager().registerEvents(new LobbyListeners(), this);

    }

    @Override
    public void onDisable() {
        System.out.println("Bye cruel world!");
    }

}
