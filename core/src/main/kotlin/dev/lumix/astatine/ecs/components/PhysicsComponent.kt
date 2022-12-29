package dev.lumix.astatine.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import ktx.ashley.mapperFor

class PhysicsComponent : Component {
    companion object {
        val mapper = mapperFor<PhysicsComponent>()
    }

    var body: Body? = null
    var type = BodyType.DynamicBody
}