package HyperCore.HyperMonster;

import HyperCore.Listener.Hyper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;

public class HyperCombust extends Hyper {

    @EventHandler
    public void onCombine(EntityCombustEvent event) {
        event.setCancelled(true);
    }
}
