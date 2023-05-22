import korlibs.event.*
import korlibs.image.tiles.tiled.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.tiled.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.memory.*
import korlibs.time.*
import kotlin.math.*

class MainTiledMapWithScrollScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val tiledMap = resourcesVfs["gfx/sample.tmx"].readTiledMap()
        fixedSizeContainer(Size(512, 256), clip = true) {
            position(128, 128)
            val camera = camera {
                tiledMapView(tiledMap) {
                }
            }
            var dx = 0f
            var dy = 0f
            //this.keys.apply {
            //	down { key ->
            //		when (key) {
            //			Key.RIGHT -> dx -= 1.0
            //			Key.LEFT -> dx += 1.0
            //			Key.DOWN -> dy -= 1.0
            //			Key.UP -> dy += 1.0
            //		}
            //	}
            //}

            onMouseDrag {
                if (!it.start && !it.end) {
                    dx += it.deltaDx
                    dy += it.deltaDy
                }
            }

            addUpdater {
                //val scale = 1.0 / (it / 16.666666.hrMilliseconds)
                val scale: Float = if (it == 0.0.milliseconds) 0f else (it / 16.666666.milliseconds)
                if (views.input.keys[Key.RIGHT]) dx -= 1f
                if (views.input.keys[Key.LEFT]) dx += 1f
                if (views.input.keys[Key.UP]) dy += 1f
                if (views.input.keys[Key.DOWN]) dy -= 1f
                dx = dx.clamp(-10f, +10f)
                dy = dy.clamp(-10f, +10f)
                camera.x += dx * scale
                camera.y += dy * scale
                dx *= 0.9f.pow(scale)
                dy *= 0.9f.pow(scale)
            }
        }
    }
}
