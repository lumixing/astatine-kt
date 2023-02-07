package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use

class RenderSystem : SortedIteratingSystem(
    allOf(TransformComponent::class, SpriteComponent::class).get(),
    compareBy { entity: Entity -> entity[SpriteComponent.mapper]?.z }
) {
    override fun update(delta: Float) {
        forceSort()
        super.update(delta)
    }

    override fun processEntity(entity: Entity, delta: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            entity[SpriteComponent.mapper]?.let { sprite ->
                Static.batch.use { batch ->
                    sprite.sprite.setPosition(transform.position.x + sprite.offset.x, transform.position.y + sprite.offset.y)
                    sprite.sprite.rotation = transform.rotation * MathUtils.radiansToDegrees
                    sprite.sprite.draw(batch)
                }
            }
        }
    }
}