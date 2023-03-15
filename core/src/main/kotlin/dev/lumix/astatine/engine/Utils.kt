package dev.lumix.astatine.engine

import kotlin.math.abs

class Utils {
    companion object {
        // basically to get rid of .? and !! in components
        fun expectComponent(entityName: String, componentName: String) {
            throw Throwable("expected $entityName to have $componentName component!")
        }

        // acceleration magic
        fun approach(start: Float, target: Float, increment: Float): Float {
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
}