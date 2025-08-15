package HyperCore.HyperMonster;

import HyperCore.Listener.Hyper;
import HyperCore.HyperCore;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HyperSkeleton extends Hyper {

    private final Map<UUID, BukkitRunnable> shootTasks = new HashMap<>();

    @EventHandler
    public void onSkeletonShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Skeleton skeleton &&
                event.getProjectile() instanceof org.bukkit.entity.Arrow &&
                skeleton.getTarget() instanceof Player player) {

            event.setCancelled(true);

            UUID id = skeleton.getUniqueId();
            if (shootTasks.containsKey(id)) {
                shootTasks.get(id).cancel();
                shootTasks.remove(id);
            }

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (skeleton.isDead() || skeleton.getTarget() != player) {
                        this.cancel();
                        shootTasks.remove(id);
                        return;
                    }
                    for (int i = 0; i < 10; i++) {
                        skeleton.launchProjectile(org.bukkit.entity.Arrow.class);
                    }
                }
            };
            task.runTaskTimer(HyperCore.getInstance(), 0L, 1L);
            shootTasks.put(id, task);
        }
    }

    @EventHandler
    public void onSkeletonSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Skeleton skeleton) {
            skeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
            skeleton.heal(100.0);
        }
    }
}