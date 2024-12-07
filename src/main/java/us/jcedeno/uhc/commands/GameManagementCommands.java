package us.jcedeno.uhc.commands;

import java.util.concurrent.CompletableFuture;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public class GameManagementCommands {

    public GameManagementCommands() {
    }

    @Command("uhc <input> [number]")
    public CompletableFuture<Void> command(CommandSender sender, String input, int number) {

        return CompletableFuture.supplyAsync(()->{
            sender.sendMessage("Hello world! " + input + " " + number);

            return null;
        });
    }

}
