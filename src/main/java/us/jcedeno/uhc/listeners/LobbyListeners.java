package us.jcedeno.uhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Getter;
import lombok.Setter;
import us.jcedeno.uhc.UHCPlugin;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import java.util.List;

public class LobbyListeners implements Listener {

    private Integer gameTime = 0;


    @Getter
    @Setter
    private static boolean shouldTick = false;

    public LobbyListeners() {

        Bukkit.getScheduler().runTaskTimer(UHCPlugin.getPlugin(), () -> {
            if (shouldTick)
                ++gameTime;
        }, 0, 20);

    }

    public static String formatTime(int seconds) {
        if (seconds < 3600) {
            return String.format("%02d:%02d",
                    (seconds % 3600) / 60,
                    seconds % 60);
        }
        return String.format("%02d:%02d:%02d",
                seconds / 3600,
                (seconds % 3600) / 60,
                seconds % 60);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();

        player.sendMessage(miniMessage().deserialize("<rainbow><hover:show_text:\"<red>test:TEST\">hello world!"));

        var board = new FastBoard(player);

        board.updateTitle(miniMessage().deserialize("  <bold><white>UHC <rainbow>Mundial  "));

        Bukkit.getScheduler().runTaskTimer(UHCPlugin.getPlugin(), () -> {


            board.updateLines(List.of(
                    miniMessage().deserialize("<white>Time: <#f2c08e>" + formatTime(gameTime)),
                    miniMessage().deserialize(""),
                    miniMessage().deserialize("<white>Your kills: <#f2c08e>" +  player.getStatistic(Statistic.KILL_ENTITY, EntityType.ZOMBIE)),
                    miniMessage().deserialize(String.format("<white>(X,Z): <#f2c08e>(%.2f, %.2f)",
                            player.getLocation().getX(), player.getLocation().getZ())),
                    miniMessage().deserialize(""),
                    miniMessage().deserialize("<white>World Border: <#f2c08e>"
                            + (String.format("%.2f", Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2))),
                    miniMessage().deserialize(""),
                    miniMessage().deserialize("<#60097a>hynix.io")

            ));

        }, 0, 1l);

    }


}
