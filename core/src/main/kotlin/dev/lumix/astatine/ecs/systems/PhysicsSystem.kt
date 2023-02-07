package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.dongbat.jbump.Collisions
import com.dongbat.jbump.World
import dev.lumix.astatine.ecs.components.ItemComponent
import dev.lumix.astatine.ecs.components.PhysicsComponent
import dev.lumix.astatine.ecs.components.TransformComponent
import dev.lumix.astatine.engine.PlayerCollisionFilter
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.abs

class PhysicsSystem(val world: World<Entity>) : IteratingSystem(
    allOf(TransformComponent::class, PhysicsComponent::class, ItemComponent::class).get()
) {
    private val GRAVITY = -100f
    private val WALK_SPEED = 80f
    private val JUMP_FORCE = 100f
    private val WALK_ACCELERATION = 200f
    private val FALL_ACCELERATION = 150f
    private val FRICTION = 50f
    private val MAX_VERTICAL_SPEED = 250f
    private val MAX_JUMP_TIME = 0.25f
    private var isJumping = false
    private var jumpTime = 0f

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper]
        val physics = entity[PhysicsComponent.mapper]
        val item = entity[ItemComponent.mapper]

        val playerCollisionFilter = PlayerCollisionFilter()
        val collisions = Collisions()

        // update y vel
        physics!!.velocity.y = approach(physics.velocity.y, -MAX_VERTICAL_SPEED, FALL_ACCELERATION * delta)

        val isJumpPressed = Gdx.input.isKeyPressed(Input.Keys.W)
        if (isJumpPressed) {
            isJumping = false
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            world.project(item!!.item, transform!!.position.x, transform.position.y, physics.bounds.x, physics.bounds.y, transform.position.x, transform.position.y - 0.1f,
                playerCollisionFilter, collisions);

            val isGrounded = collisions.size() > 0
            if (isGrounded) {
                isJumping = true
            }
        }

        if (isJumpPressed && isJumping && jumpTime < MAX_JUMP_TIME) {
            physics.velocity.y = JUMP_FORCE
            jumpTime += delta
        }

        // update x vel
        physics.velocity.x = approach(physics.velocity.x, 0f, FRICTION * delta)

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            physics.velocity.x = approach(physics.velocity.x, -WALK_SPEED, WALK_ACCELERATION * delta)
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            physics.velocity.x = approach(physics.velocity.x, WALK_SPEED, WALK_ACCELERATION * delta)
        } else {
            physics.velocity.x = approach(physics.velocity.x, 0f, WALK_ACCELERATION * delta)
        }

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
                jumpTime = MAX_JUMP_TIME

                val hitFloor = collision.normal.y == 1
                if (hitFloor) {
                    jumpTime = 0f
                    isJumping = false
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