package dev.lumix.astatine.ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import dev.lumix.astatine.ecs.components.*
import dev.lumix.astatine.ecs.entities.Box
import dev.lumix.astatine.ecs.entities.Item
import dev.lumix.astatine.ecs.entities.inventory.InventoryItem
import dev.lumix.astatine.engine.*
import dev.lumix.astatine.world.World
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has

// used for updating velocities and collision checking
class PhysicsSystem(val world: World) : IteratingSystem(
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
        if (world.physicsWorld.getRect(item.item) == null) {
            return
        }
        val player = entity[PlayerComponent.mapper]

        val playerCollisionFilter = PlayerCollisionFilter()

        // update y vel
        physics.velocity.y = Utils.approach(physics.velocity.y, -MAX_VERTICAL_SPEED, GRAVITY * delta)

        // update x vel
        physics.velocity.x = Utils.approach(physics.velocity.x, 0f, FRICTION * delta)

        // update pos based on velocities
        transform.position.add(physics.velocity.x * delta, physics.velocity.y * delta)

        // collision check
        val result = world.physicsWorld.move(item.item, transform.position.x, transform.position.y, playerCollisionFilter)
        for (i in 0 until result.projectedCollisions.size()) {
            val collision = result.projectedCollisions.get(i)

            if (collision.other.userData is Entity) {
                if ((collision.other.userData as Entity).has(BlockComponent.mapper)) {
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

                        if (collision.item.userData is Box) {
                            if (world.physicsWorld.getRect(item.item) == null) {
                                return
                            }
                            world.explode((transform.position.x / 8f).toInt(), (transform.position.y / 8f).toInt())
                            world.physicsWorld.remove(item.item)
                            Static.engine.removeEntity(entity)
                        }
                    }
                }
            }

            // if colliding with item then remove the item
            if (collision.other.userData is Item) {
                val itemEntity = (collision.other.userData as Entity)
                val itemItem = itemEntity[ItemComponent.mapper] ?: return Utils.expectComponent("item", "item")

                player?.let {
                    entity[InventoryComponent.mapper]!!.inventory.addItem(InventoryItem((collision.other.userData as Item).block, 1))
                    Static.assets[SoundAssets.Pop].play(0.1f)
                }

                world.physicsWorld.remove(itemItem.item)
                Static.engine.removeEntity(itemEntity)
            }
        }

        // update pos based on collisions
        val rect = world.physicsWorld.getRect(item.item)
        rect?.let {
            transform.position.set(rect.x, rect.y)
        }
    }
}