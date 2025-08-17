package HyperCore.HyperMonster;

import HyperCore.HyperCore;
import HyperCore.Listener.Hyper;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HyperSpider extends Hyper {

    private static class Debuff {
        double totalDamage;
        int remainingTicks;
        Debuff(double dmg, int ticks) {
            this.totalDamage = dmg;
            this.remainingTicks = ticks;
        }
    }

    private final Map<UUID, Debuff> debuffs = new ConcurrentHashMap<>();

    @EventHandler
    public void onSpiderAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Spider)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        UUID id = target.getUniqueId();
        double damage = event.getDamage();
        AttributeInstance attr = target.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return;

        Debuff debuff = debuffs.get(id);
        int addTicks = 200;
        if (debuff != null) {
            debuff.remainingTicks += addTicks;
            debuff.totalDamage += damage;
        } else {
            debuff = new Debuff(damage, addTicks);
            debuffs.put(id, debuff);
        }

        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, debuff.remainingTicks, 0, true, true, true));

        double newMax = Math.max(1.0, attr.getBaseValue() - damage);
        attr.setBaseValue(newMax);
        if (target.getHealth() > newMax) target.setHealth(newMax);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!target.isValid()) {
                    debuffs.remove(id);
                    cancel();
                    return;
                }
                if (!target.hasPotionEffect(PotionEffectType.NAUSEA)) {
                    Debuff d = debuffs.remove(id);
                    if (d != null && attr != null) {
                        attr.setBaseValue(attr.getDefaultValue());
                        if (target.getHealth() > attr.getBaseValue()) target.setHealth(attr.getBaseValue());
                    }
                    cancel();
                }
            }
        }.runTaskTimer(HyperCore.getInstance(), 0L, 1L);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Spider spider)) return;
        spider.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
        spider.setHealth(20.0);
        spider.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false, false));
    }
}
