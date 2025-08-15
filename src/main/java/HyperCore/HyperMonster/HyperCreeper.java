package HyperCore.HyperMonster;

import HyperCore.HyperCore;
import HyperCore.Listener.Hyper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class HyperCreeper extends Hyper {

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            Location center = creeper.getLocation();
            World world = center.getWorld();
            if (world == null) return;

            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks++ >= 100) {
                        this.cancel();
                        return;
                    }
                    double radiusPerTick = 0.5;
                    double pointPerCircum = 6.0;
                    double radius = radiusPerTick * ticks;
                    double circum = 2 * Math.PI * radius;
                    int pointsCount = (int) (circum / pointPerCircum);
                    if (pointsCount == 0) return;

                    double angle = 360.0 / pointsCount;
                    double y = center.getY();

                    for (int i = 0; i < pointsCount; i++) {
                        double currentAngle = Math.toRadians(i * angle);
                        double x = -Math.sin(currentAngle);
                        double z = Math.cos(currentAngle);

                        world.createExplosion(center.getX() + x * radius, y, center.getZ() + z * radius, 4F, false, true);
                    }
                }
            }.runTaskTimer(HyperCore.getInstance(), 0L, 1L);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Creeper) {
            event.setDamage(0.0);
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Creeper creeper) {
            creeper.setPowered(true);
        }
    }
}