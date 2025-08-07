package HyperCore.HyperMonster;

import HyperCore.Listener.Hyper;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

public class HyperZombie extends Hyper {

    public double zombie_size = 0.01;

    @EventHandler
    public void EntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            zombie.getAttribute(Attribute.SCALE).setBaseValue(zombie_size);
        }
    }
}
