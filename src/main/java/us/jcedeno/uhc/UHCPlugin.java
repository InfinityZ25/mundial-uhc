package us.jcedeno.uhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import us.jcedeno.uhc.listeners.LobbyListeners;

public class UHCPlugin extends JavaPlugin {

    @Getter
    private static UHCPlugin plugin;

    @Override
    public void onEnable() {
        UHCPlugin.plugin = this;
        System.out.println("Hello world!");

        Bukkit.getPluginManager().registerEvents(new LobbyListeners(), this);

    }

    @Override
    public void onDisable() {
        System.out.println("Bye cruel world!");
    }



}
