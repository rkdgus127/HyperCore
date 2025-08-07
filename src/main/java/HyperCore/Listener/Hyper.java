package HyperCore.Listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Hyper implements Listener {
    protected Hyper() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HyperCore");
        if (plugin == null) throw new IllegalStateException("HyperCore 플러그인 못 찾음");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
