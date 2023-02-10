package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.dongbat.jbump.Collisions
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.PlayerComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.PlayerCollisionFilter
import dev.lumix.astatine.engine.Utils
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerSystem(val world: World<Entity>) : IteratingSystem(
    allOf(PlayerComponent::class).get()
) {
    companion object {
        const val WALK_SPEED = 80f
        const val JUMP_FORCE = 80f
        const val WALK_ACCELERATION = 200f
        const val MAX_JUMP_TIME = 0.25f
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper] ?: return Utils.expectComponent("player", "transform")
        val physics = entity[PhysicsComponent.mapper] ?: return Utils.expectComponent("player", "physics")
        val item = entity[ItemComponent.mapper] ?: return Utils.expectComponent("player", "item")
        val player = entity[PlayerComponent.mapper] ?: return Utils.expectComponent("player", "player")

        val playerCollisionFilter = PlayerCollisionFilter()
        val collisions = Collisions()

        // update y vel
        val isJumpPressed = Gdx.input.isKeyPressed(Input.Keys.W)
        if (!isJumpPressed) {
            player.isJumping = false
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            world.project(item.item,
                transform.position.x, transform.position.y,
                physics.bounds.x, physics.bounds.y,
                transform.position.x, transform.position.y - .1f,
                playerCollisionFilter, collisions);

            val isGrounded = collisions.size() > 0
            if (isGrounded) {
                player.isJumping = true
            }
        }

        if (isJumpPressed && player.isJumping && player.jumpTime < MAX_JUMP_TIME) {
            physics.velocity.y = JUMP_FORCE
            player.jumpTime += delta
        }

        // update x vel
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            physics.velocity.x = Utils.approach(physics.velocity.x, -WALK_SPEED, WALK_ACCELERATION * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            physics.velocity.x = Utils.approach(physics.velocity.x, WALK_SPEED, WALK_ACCELERATION * delta)
        } else {
            physics.velocity.x = Utils.approach(physics.velocity.x, 0f, WALK_ACCELERATION * delta)
        }
    }
}