package dev.lumix.astatine.engine

import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.CollisionFilter
import com.dongbat.jbump.Item
import com.dongbat.jbump.Response
import dev.lumix.astatine.ecs.components.BlockComponent
import dev.lumix.astatine.ecs.components.ExplosiveComponent
import dev.lumix.astatine.ecs.components.PickableComponent
import ktx.ashley.has

class PlayerCollisionFilter : CollisionFilter {
    override fun filter(item: Item<*>?, other: Item<*>): Response? {
        if ((other.userData as Entity).has(BlockComponent.mapper)) {
            return Response.slide
        }
        if ((other.userData as Entity).has(ExplosiveComponent.mapper)) {
            return Response.slide
        }
        if ((other.userData as Entity).has(PickableComponent.mapper)) {
            return Response.cross
        }
        return null
    }
}