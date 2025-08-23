package HyperCore.HyperEntity;

import HyperCore.HyperCore;
import HyperCore.Listener.Hyper;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyperSpider extends Hyper {

    private final Map<UUID, Double> originalMaxHealth = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> restoreTasks = new ConcurrentHashMap<>();

    @EventHandler
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Spider)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        UUID id = victim.getUniqueId();
        AttributeInstance attr = victim.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;

        double currentMax = attr.getBaseValue();
        double newMax = currentMax - event.getDamage();
        if (newMax < 0.5) newMax = 0.5;

        if (!originalMaxHealth.containsKey(id)) {
            originalMaxHealth.put(id, currentMax);
        }
        attr.setBaseValue(newMax);

        int addTicks = 100;
        int remain = 0;
        PotionEffect nausea = victim.getPotionEffect(PotionEffectType.NAUSEA);
        if (nausea != null) {
            remain = nausea.getDuration();
        }
        int newDuration = remain + addTicks;

        victim.addPotionEffect(new PotionEffect(
                PotionEffectType.NAUSEA,
                newDuration,
                0,
                true,
                true,
                false
        ));

        BukkitRunnable oldTask = restoreTasks.get(id);
        if (oldTask != null) oldTask.cancel();

        BukkitRunnable newTask = new BukkitRunnable() {
            @Override
            public void run() {
                LivingEntity entity = victim;
                if (!entity.isValid() || entity.isDead() || !entity.hasPotionEffect(PotionEffectType.NAUSEA)) {
                    Double origin = originalMaxHealth.remove(id);
                    restoreTasks.remove(id);
                    AttributeInstance a = entity.getAttribute(Attribute.MAX_HEALTH);
                    if (origin != null && a != null) {
                        a.setBaseValue(origin);
                        if (entity.getHealth() > origin) entity.setHealth(origin);
                    }
                    cancel();
                }
            }
        };
        restoreTasks.put(id, newTask);
        newTask.runTaskTimer(HyperCore.getInstance(), 0L, 1L);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Spider spider)) return;
        AttributeInstance attr = spider.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) attr.setBaseValue(50.0);
        spider.setHealth(50.0);
        spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false, false));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        UUID id = entity.getUniqueId();
        Double origin = originalMaxHealth.remove(id);
        BukkitRunnable task = restoreTasks.remove(id);
        if (task != null) task.cancel();
        AttributeInstance attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (origin != null && attr != null) {
            attr.setBaseValue(origin);
        }
    }
}