import net.minecraft.server.v1_16_R3.*
import org.bukkit.entity.LivingEntity

class Pathfind(
    var creature: EntityInsentient,
    var player: EntityPlayer,
    var tprange: Double,
    var speed: Double
    ) : PathfinderGoal() {
    private val navigation: NavigationAbstract = creature.navigation as NavigationAbstract
    override fun a(): Boolean {
        if (!creature.passengers.isEmpty()) {
            return false
        }
        if (creature.getWorld().world.name != player.world.world.name) {
            creature.goalTarget = null
            tp()
            return false
        }
        val dist = creature.bukkitEntity.location.distance(player.bukkitEntity.location)
        if (dist >= tprange) {
            creature.goalTarget = null
            tp()
            return false
        }
        if (creature.goalTarget == null) {
            creature.navigation.a(player, 0)
        }
        return false
    }

    fun tp() {
        (creature.bukkitEntity as LivingEntity).teleport(player.bukkitEntity.location)
    }

    override fun e() {
        speed = creature.bukkitEntity.getMetadata("Speed")[0].asDouble()
        creature.a(player, 30f, 30f)
        navigation.a(player, speed)
        creature.a(player, 30f, 30f)
    }

}