package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PlayerComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.PlayerCollisionFilter
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.abs

class PhysicsSystem(val world: World<Entity>) : IteratingSystem(
    allOf(TransformComponent::class, PhysicsComponent::class, ItemComponent::class).get()
) {
    private val GRAVITY = 300f
    private val FRICTION = 50f
    private val MAX_VERTICAL_SPEED = 250f
    private val MAX_JUMP_TIME = 0.25f

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper]
        val physics = entity[PhysicsComponent.mapper]
        val item = entity[ItemComponent.mapper]
        val player = entity[PlayerComponent.mapper]

        val playerCollisionFilter = PlayerCollisionFilter()

        // update y vel
        physics!!.velocity.y = approach(physics.velocity.y, -MAX_VERTICAL_SPEED, GRAVITY * delta)

        // update x vel
        physics.velocity.x = approach(physics.velocity.x, 0f, FRICTION * delta)

        // update pos based on velocities
        transform!!.position.add(physics.velocity.x * delta, physics.velocity.y * delta)

        // collision check
        val result = world.move(item!!.item, transform.position.x, transform.position.y, playerCollisionFilter)
        for (i in 0 until result.projectedCollisions.size()) {
            val collision = result.projectedCollisions.get(i)

            val hitWall = collision.normal.x != 0
            if (hitWall) {
                physics.velocity.x = 0f
            }

            val hitFloorOrCeiling = collision.normal.y != 0
            if (hitFloorOrCeiling) {
                physics.velocity.y = 0f

                // todo: ultra scuffed (pls fix)
                player?.let {
                    it.jumpTime = MAX_JUMP_TIME

                    val hitFloor = collision.normal.y == 1
                    if (hitFloor) {
                        it.jumpTime = 0f
                        it.isJumping = false
                    }
                }
            }
        }

        // update pos based on collisions
        val rect = world.getRect(item.item)
        rect?.let {
            transform.position.set(rect.x, rect.y)
        }
    }

    private fun approach(start: Float, target: Float, increment: Float): Float {
        var start = start
        var increment = increment
        increment = abs(increment)

        if (start < target) {
            start += increment
            if (start > target) {
                start = target
            }
        } else {
            start -= increment
            if (start < target) {
                start = target
            }
        }
        return start
    }
}