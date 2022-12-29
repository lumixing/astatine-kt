package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.ecs.components.PositionComponent
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.engine.Static
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use

class RenderSystem : SortedIteratingSystem(
    allOf(PositionComponent::class, SpriteComponent::class).get(),
    compareBy { entity: Entity -> entity[SpriteComponent.mapper]?.z }
) {
    override fun update(deltaTime: Float) {
        forceSort()
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[PositionComponent.mapper]?.let { position ->
            entity[SpriteComponent.mapper]?.let { sprite ->
                Static.batch.use { batch ->
                    sprite.sprite.setPosition(position.position.x + sprite.offset.x, position.position.y + sprite.offset.y)
                    sprite.sprite.rotation = position.angle * MathUtils.radiansToDegrees
                    sprite.sprite.draw(batch)
                }
            }
        }
    }
}