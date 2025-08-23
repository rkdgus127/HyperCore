package HyperCore.HyperEntity;

import HyperCore.Listener.Hyper;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class HyperIronGolem extends Hyper {

    @EventHandler
    public void onIronGolemSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof IronGolem irongolem) {
            irongolem.setMaxHealth(1000);
            irongolem.heal(1000);
            irongolem.setMaximumNoDamageTicks(0);
        }
    }

    @EventHandler
    public void onIronGolemMelee(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof IronGolem irongolem) {
            var damager = event.getDamager();

            if (!(damager instanceof Projectile)) {
                var loc = damager.getLocation();
                var direction = loc.getDirection().normalize().multiply(-1);
                var behindLoc = loc.clone().add(direction);
                irongolem.teleport(behindLoc);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onIronGolemProjectile(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(event.getHitEntity() instanceof IronGolem irongolem)) return;
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

        projectile.remove();

        Location golemLoc = irongolem.getEyeLocation();
        Location shooterLoc = shooter.getEyeLocation();

        Vector direction = shooterLoc.toVector().subtract(golemLoc.toVector());
        if (direction.lengthSquared() == 0) direction = irongolem.getLocation().getDirection();
        direction = direction.normalize().multiply(3);

        Projectile newProjectile = irongolem.launchProjectile(projectile.getClass(), direction);
        newProjectile.setShooter(irongolem);

        if (projectile instanceof ThrownPotion oldPotion && newProjectile instanceof ThrownPotion newPotion) {
            newPotion.setItem(oldPotion.getItem().clone());
        }
    }
}
