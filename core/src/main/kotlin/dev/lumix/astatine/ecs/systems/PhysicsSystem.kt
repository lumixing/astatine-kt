package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.World
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PositionComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.box2d.body
import ktx.box2d.box

class PhysicsSystem(private val world: World) : IteratingSystem(
    allOf(PhysicsComponent::class, PositionComponent::class).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[PhysicsComponent.mapper]?.let { physics ->
            entity[PositionComponent.mapper]?.let { positionComponent ->
                if (physics.body == null) {
                    physics.body = world.body {
                        box(8f, 8f) {
                            density = 1f
                            restitution = 0.5f
                        }
                        position.set(positionComponent.position.cpy().add(4f, 4f))
                        type = physics.type
                    }
                }
                positionComponent.position.set(physics.body!!.position)
                positionComponent.angle = physics.body!!.angle
            }
        }
    }
}