package dev.lumix.astatine.ecs.entities

import com.badlogic.ashley.core.Entity
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.ecs.components.TransformComponent

class PlayerEntity : Entity() {
    var transform = TransformComponent()
    var physics = PhysicsComponent()
    var sprite = SpriteComponent()

    init {
        add(transform)
        add(physics)
        add(sprite)
    }
}