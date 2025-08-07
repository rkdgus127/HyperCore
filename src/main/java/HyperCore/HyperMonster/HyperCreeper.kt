package HyperCore.HyperMonster

import HyperCore.Listener.Hyper
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityExplodeEvent
import kotlin.math.cos
import kotlin.math.sin

class HyperCreeper : Hyper() {

    val radius = 3.0

    val pointPerCircum = 6.0

    val circum = 2.0 * Math.PI * radius

    val pointsCount = (circum / pointPerCircum).toInt()

    @EventHandler
    fun onCreeperExplode(event: EntityExplodeEvent) {
        val entity = event.entity
        if (entity.type.name != "CREEPER") return

        val center = entity.location
        if (pointsCount == 0) return

        val angle = 360.0 / pointsCount
        val world = center.world
        val y = center.y

        for (i in 0 until pointsCount) {
            val currentAngle = Math.toRadians(i * angle)
            val x = -sin(currentAngle)
            val z = cos(currentAngle)

            world.createExplosion(center.x + x * radius, y, center.z + z * radius, 2.0f, false, true)
        }
    }

}
