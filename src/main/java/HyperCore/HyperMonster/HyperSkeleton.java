package HyperCore.HyperMonster;

import HyperCore.HyperCore;
import HyperCore.Listener.Hyper;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyperSkeleton extends Hyper {

    private final Map<UUID, BukkitRunnable> shootTasks = new ConcurrentHashMap<>();

    @EventHandler
    public void onSkeletonShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Skeleton skeleton) || !(event.getProjectile() instanceof Arrow)) return;

        event.setCancelled(true);
        final UUID id = skeleton.getUniqueId();

        BukkitRunnable oldTask = shootTasks.remove(id);
        if (oldTask != null) oldTask.cancel();

        if (skeleton.getTarget() == null) return;

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (skeleton.isDead() || skeleton.getTarget() == null) {
                    this.cancel();
                    shootTasks.remove(id);
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    if (skeleton.getTarget() instanceof LivingEntity player) {
                        org.bukkit.util.Vector direction = player.getLocation().toVector()
                                .subtract(skeleton.getLocation().toVector())
                                .normalize();
                        Arrow arrow = skeleton.launchProjectile(Arrow.class);
                        arrow.setVelocity(direction.multiply(2)); // 속도 조절
                    }
                }
            }
        };
        task.runTaskTimer(HyperCore.getInstance(), 0L, 1L);
        shootTasks.put(id, task);
    }

    @EventHandler
    public void onSkeletonSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton skeleton)) return;
        skeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40.0);
        skeleton.setHealth(40.0);
    }
}