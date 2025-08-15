package HyperCore;

import HyperCore.Listener.HyperLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class HyperCore extends JavaPlugin {

    private static HyperCore instance;

    @Override
    public void onEnable() {
        instance = this;
        HyperLoader.loadAll(this);
    }

    public static HyperCore getInstance() {
        return instance;
    }
}
