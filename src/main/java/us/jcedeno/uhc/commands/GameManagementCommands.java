package us.jcedeno.uhc.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.context.CommandContext;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.uhc.UHCPlugin;
import us.jcedeno.uhc.listeners.LobbyListeners;

@CommandContainer
public class GameManagementCommands {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Random RANDOM = new Random();
    private static final int MAX_SCATTER_ATTEMPTS = 50;

    @Command("uhc <input> [number]")
    @CommandDescription("Main UHC command")
    @Permission("uhc.admin")
    public void command(
            final CommandContext<CommandSourceStack> context,
            @Argument("input") String input,
            @Argument("number") @Default("42") int number) {
        CommandSender sender = context.sender().getSender();
        sender.sendMessage(mm.deserialize("<yellow>UHC Command:</yellow> " + input + " " + number));
    }

    @Command("board tick <bool>")
    @CommandDescription("Toggle the board tick state")
    @Permission("uhc.admin.board")
    public void toggleBoardTick(
            final CommandContext<CommandSourceStack> context,
            @Argument("bool") @Default("false") boolean shouldTick) {
        CommandSender sender = context.sender().getSender();
        LobbyListeners.setShouldTick(shouldTick);
        Component message = mm.deserialize("<white>Board tick state updated to: <green>" + shouldTick);

        if (sender instanceof Player) {
            sender.sendMessage(message);
        } else {
            sender.sendMessage(mm.deserialize("<yellow>[Console]</yellow> ").append(message));
        }
    }

    @Command("game scatter <radius>")
    @CommandDescription("Scatter all players within the given radius")
    @Permission("uhc.admin.scatter")
    public void scatter(
            final CommandContext<CommandSourceStack> context,
            @Argument("radius") @Default("1000") int radius) {
        CommandSender sender = context.sender().getSender();
        try {
            World world = Bukkit.getWorlds().get(0);
            setupWorldBorder(world, radius);

            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            int successCount = scatterPlayers(players, world, radius);

            sendScatterSummary(sender, players.size(), successCount);
        } catch (Exception e) {
            sender.sendMessage(mm.deserialize("<red>An error occurred while scattering players: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void setupWorldBorder(World world, int radius) {
        Bukkit.getScheduler().runTask(UHCPlugin.getPlugin(), () -> {
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(radius * 2);
        });
    }

    private int scatterPlayers(List<Player> players, World world, int radius) {
        int successCount = 0;

        for (Player player : players) {
            Location safeLoc = findSafeLocation(world, radius);

            if (safeLoc != null) {
                teleportPlayer(player, safeLoc);
                successCount++;
            } else {
                player.sendMessage(mm.deserialize("<red>Could not find a safe location to scatter to!"));
            }
        }

        return successCount;
    }

    private Location findSafeLocation(World world, int radius) {
        for (int attempts = 0; attempts < MAX_SCATTER_ATTEMPTS; attempts++) {
            Location loc = generateRandomLocation(world, radius);
            if (isSafeLocation(loc)) {
                return loc;
            }
        }
        return null;
    }

    private Location generateRandomLocation(World world, int radius) {
        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double distance = RANDOM.nextDouble() * radius;

        int x = (int) (Math.cos(angle) * distance);
        int z = (int) (Math.sin(angle) * distance);
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x, y, z);
    }

    private boolean isSafeLocation(Location loc) {
        Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        return !block.isLiquid() && block.getType().isSolid() && loc.getY() > 0;
    }

    private void teleportPlayer(Player player, Location safeLoc) {
        safeLoc.getChunk().load(true);
        safeLoc.setY(safeLoc.getY() + 1);
        player.teleport(safeLoc);
        player.sendMessage(mm.deserialize("<green>You have been scattered!"));
    }

    private void sendScatterSummary(CommandSender sender, int totalPlayers, int successCount) {
        Component message = mm.deserialize(String.format(
                "<green>Scattered <white>%d/%d</white> players successfully!",
                successCount, totalPlayers));
        sender.sendMessage(message);
    }
}