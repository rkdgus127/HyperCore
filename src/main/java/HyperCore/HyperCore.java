package HyperCore;

import HyperCore.Listener.HyperLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class HyperCore extends JavaPlugin {

    @Override
    public void onEnable() {
        HyperLoader.loadAll(this);
    }
}
