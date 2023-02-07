package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.dongbat.jbump.Item
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PlayerComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.abs

class PlayerSystem : IteratingSystem(
    allOf(PlayerComponent::class).get()
) {
    companion object {
        const val FRICTION = 250f
        const val RUN_ACCELERATION = 1800f
        const val RUN_SPEED = 800f
        const val JUMP_SPEED = 1200f
        const val BOUNCE_SPEED = 800f
        const val GRAVITY = 3000f
        const val JUMP_MAX_TIME = .25f
//        const val PLAYER_COLLISION_FILTER = PlayerCollision
    }

    var jumping = false

    override fun processEntity(entity: Entity, delta: Float) {
//        entity[TransformComponent.mapper]?.let { transform ->
//            entity[PhysicsComponent.mapper]?.let { physics ->
////                if (physics.item == null) {
////                    physics.item = Item(entity)
////                }
//
//                physics.delta.x = approach(physics.delta.x, 0f, FRICTION * delta)
//
//                val isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT)
//                val isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT)
//                val isUpPressed = Gdx.input.isKeyPressed(Input.Keys.UP)
//                val isUpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.UP)
//
//                if (isRightPressed) {
//                    physics.delta.x = approach(physics.delta.x, RUN_SPEED, RUN_ACCELERATION * delta)
//                } else if (isLeftPressed) {
//                    physics.delta.x = approach(physics.delta.x, -RUN_SPEED, RUN_ACCELERATION * delta)
//                } else {
//                    physics.delta.x = approach(physics.delta.x, 0f, RUN_ACCELERATION * delta)
//                }
//
//                if (!isUpPressed) jumping = false
//                if (isUpJustPressed) {
////                    physics.world.project(physics.item,
////                        transform.position.x + physics.boxPosition.x,
////                        transform.position.y + physics.boxPosition.y,
////                        physics.boxScale.x,
////                        physics.boxScale.y,
////                        transform.position.x + physics.boxPosition.x,
////                        transform.position.y + physics.boxPosition.y - .1f,
////                    )
//                }
//            }
//        }
    }

    private fun approach(start: Float, target: Float, increment: Float): Float {
        var _increment = increment
        var _start = start

        _increment = abs(increment)
        if (_start < target) {
            _start += _increment
            if (_start > target) _start = target
        } else {
            _start -= _increment
            if (_start < target) _start = target
        }
        return _start
    }
}