package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.dongbat.jbump.Collisions
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.ecs.entities.Item
import dev.lumix.astatine.ecs.entities.inventory.InventoryItem
import dev.lumix.astatine.engine.*
import dev.lumix.astatine.world.World
import dev.lumix.astatine.world.block.BlockType
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerSystem(val world: World) : IteratingSystem(
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
            world.physicsWorld.project(item.item,
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

        // mouse input
        if (Gdx.input.isTouched) {
            val blockPos = Static.camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)).scl(1/8f)

            for (newBlockPosX in blockPos.x.toInt()-Static.digRadius..blockPos.x.toInt()+Static.digRadius) {
                for (newBlockPosY in blockPos.y.toInt()-Static.digRadius..blockPos.y.toInt()+Static.digRadius) {
                    val blockType = world.chunkManager.getBlockType(newBlockPosX, newBlockPosY)
                    if (blockType == BlockType.AIR || blockType == null) continue

                    Static.assets[SoundAssets.Stone].play(0.1f)
                    world.player[InventoryComponent.mapper]!!.inventory.addItem(InventoryItem(blockType, 1))
                    world.chunkManager.setBlockType(newBlockPosX, newBlockPosY, BlockType.AIR)

                    // spawn item
                    val itemPositionX = newBlockPosX * 8f - 4 + MathUtils.random(-4, 4)
                    val itemPositionY = newBlockPosY * 8f + MathUtils.random(-4, 4)
                    val itemEntity = Item(itemPositionX, itemPositionY, blockType)
                    itemEntity.addItemToEntity(world.physicsWorld)
                }
            }

        }
    }
}