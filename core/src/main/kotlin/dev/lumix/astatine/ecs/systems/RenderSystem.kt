package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.MathUtils
import dev.lumix.astatine.ecs.components.SpriteComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.Utils
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.debug

// used for rendering sprites at given location (requires phd in theoretical quantum physics)
class RenderSystem : SortedIteratingSystem(
    allOf(TransformComponent::class, SpriteComponent::class).get(),
    compareBy { entity: Entity -> entity[SpriteComponent.mapper]?.z }
) {
    override fun update(delta: Float) {
        forceSort()
        super.update(delta)
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")
        val sprite = entity[SpriteComponent.mapper] ?: return Utils.expectComponent("player", "sprite")

        Static.batch.use { batch ->
            sprite.sprite.setPosition(transform.position.x + sprite.offset.x, transform.position.y + sprite.offset.y)
            sprite.sprite.setScale(transform.scale.x, transform.scale.y)
            sprite.sprite.rotation = transform.rotation * MathUtils.radiansToDegrees
            sprite.sprite.setFlip(sprite.flipX, false)
            sprite.sprite.draw(batch)
        }
    }
}