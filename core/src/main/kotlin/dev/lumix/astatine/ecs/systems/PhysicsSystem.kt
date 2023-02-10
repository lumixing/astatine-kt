package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PlayerComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.PlayerCollisionFilter
import dev.lumix.astatine.engine.Utils
import ktx.ashley.allOf
import ktx.ashley.get

class PhysicsSystem(val world: World<Entity>) : IteratingSystem(
    allOf(TransformComponent::class, PhysicsComponent::class, ItemComponent::class).get()
) {
    companion object {
        const val GRAVITY = 300f
        const val FRICTION = 50f
        const val MAX_VERTICAL_SPEED = 250f
        const val MAX_JUMP_TIME = 0.25f
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper] ?: return Utils.expectComponent("entity", "transform")
        val physics = entity[PhysicsComponent.mapper] ?: return Utils.expectComponent("entity", "physics")
        val item = entity[ItemComponent.mapper] ?: return Utils.expectComponent("entity", "item")
        val player = entity[PlayerComponent.mapper]

        val playerCollisionFilter = PlayerCollisionFilter()

        // update y vel
        physics.velocity.y = Utils.approach(physics.velocity.y, -MAX_VERTICAL_SPEED, GRAVITY * delta)

        // update x vel
        physics.velocity.x = Utils.approach(physics.velocity.x, 0f, FRICTION * delta)

        // update pos based on velocities
        transform.position.add(physics.velocity.x * delta, physics.velocity.y * delta)

        // collision check
        val result = world.move(item.item, transform.position.x, transform.position.y, playerCollisionFilter)
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
}