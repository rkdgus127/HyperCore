package HyperCore.HyperEntity;

import HyperCore.Listener.Hyper;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

public class HyperZombie extends Hyper {

    @EventHandler
    public void EntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            zombie.getAttribute(Attribute.SCALE).setBaseValue(0.01);
            zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100);
            zombie.heal(100);
        }
    }
}
