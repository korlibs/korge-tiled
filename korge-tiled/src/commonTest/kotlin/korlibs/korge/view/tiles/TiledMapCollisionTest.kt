package korlibs.korge.view.tiles

import korlibs.korge.tiled.*
import korlibs.image.tiles.tiled.*
import korlibs.io.async.*
import korlibs.io.file.std.*
import korlibs.math.geom.*
import korlibs.math.geom.collider.*
import kotlin.test.*

class TiledMapCollisionTest {
    @Test
    @Ignore
    fun test() = suspendTest {
        val tiledMap = resourcesVfs["tilecollision/untitled.tmx"].readTiledMap()
        val tiledMapView = TiledMapView(tiledMap)
        assertTrue(tiledMapView.pixelHitTest(PointInt(-16, -16)) != null, "outside bounds")
        assertTrue(tiledMapView.pixelHitTest(PointInt(16, 16)) == null, "empty tile")
        assertTrue(tiledMapView.pixelHitTest(PointInt(48, 16)) != null, "block tile")
    }

    @Test
    @Ignore
    fun testCollisionOffset() = suspendTest {
        val tiledSet = resourcesVfs["tilecollision/offset/Offset Test.tsx"].readTiledSet()
        val tiles = (0..2).map { tiledSet.data.tilesById[it]!! }
        val collisions = (0..2).map { tiledSet.tileset.tilesMap[it]!!.collision!! }

        assertEquals(
            """
                M0,0 L16,0 L16,16 L0,16 Z
                M8,8 L16,8 L16,16 L8,16 Z
                M16,16 L0,16 L8,8 L16,8 Z
            """.trimIndent(),
            tiles.joinToString("\n") { it.objectGroup!!.objects.joinToString(",") { it.toVectorPath().toSvgString() } }
        )

        assertEquals(true, collisions[0].hitTestAny(Point(1, 1), HitTestDirection.ANY), "0: 0,0")
        assertEquals(false, collisions[0].hitTestAny(Point(8, 17), HitTestDirection.ANY), "0: 8,17")
        assertEquals(false, collisions[1].hitTestAny(Point(1, 1), HitTestDirection.ANY), "1: 1,1")
        assertEquals(true, collisions[1].hitTestAny(Point(9, 9), HitTestDirection.ANY), "1: 9,9")
        assertEquals(false, collisions[2].hitTestAny(Point(1, 13), HitTestDirection.ANY), "2: 1,13")
        assertEquals(false, collisions[2].hitTestAny(Point(7, 7), HitTestDirection.ANY), "2: 7,7")
        assertEquals(true, collisions[2].hitTestAny(Point(12, 12), HitTestDirection.ANY), "2: 12,12")

        //println(collisions)
    }
}
