package korlibs.korge.tiled

import korlibs.korge.tests.*
import korlibs.korge.view.tiles.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.tiles.*
import korlibs.image.tiles.tiled.*
import korlibs.io.async.*
import korlibs.io.file.std.*
import korlibs.memory.extract
import korlibs.number.*
import kotlin.test.*

internal object DefaultViewport {
    const val WIDTH = 1280
    const val HEIGHT = 720
}

class TiledMapTest : ViewsForTesting() {
    //@Test
    //// @Path not supported anymore
    //fun name() = viewsTest {
    //	disableNativeImageLoading {
    //		//class Demo(@Path("sample.tmx") val map: TiledMap)
    //		class Demo(val map: TiledMap)
    //		val demo = injector.get<Demo>()
    //		val map = demo.map
    //		assertEquals(1, map.tilesets.size)
    //		assertEquals(1, map.tilesets.first().firstgid)
    //		assertEquals(256, map.tilesets.first().tileset.textures.size)
    //		assertEquals(3, map.allLayers.size)
    //		assertEquals(1, map.imageLayers.size)
    //		assertEquals(1, map.objectLayers.size)
    //		//assertEquals(1, map.patternLayers.size)
    //		//println(map)
    //		//println(demo.map)
    //	}
    //}

    @Test
    @Ignore
    fun testRenderInBounds() {
        val renderTilesCounter = views.stats.counter("renderedTiles")
        val tileset = TileSet(Bitmap32(32, 32, premultiplied = true).slice(), 32, 32)
        val map = TileMap(TileMapData(200, 200), tileset)
        views.stage += map
        views.frameUpdateAndRender()
        assertEquals(DefaultViewport.WIDTH, views.actualVirtualWidth)
        assertEquals(DefaultViewport.HEIGHT, views.actualVirtualHeight)
        views.render()
        //assertEquals(300, count)
        //assertEquals(336, renderTilesCounter.countThisFrame) // Update if optimized when no decimal scrolling
        assertEquals(943, renderTilesCounter.countThisFrame) // Update if optimized when no decimal scrolling
    }

    @Test
    fun testObjProps() = suspendTestNoJs {
        val data = resourcesVfs["tiled/library1.tmx"].readTiledMapData()
        val librarian = data.getObjectByName("librarian")!!
        assertEquals("hair-girl1", librarian.properties["hair"]?.string)
        assertTrue(librarian.properties["script"]?.string?.isNotBlank() == true)
        assertTrue(librarian.properties["script"]?.string?.contains("wait(1.5.seconds)") == true)
        assertTrue(librarian.properties["script"]?.string?.contains("move(\"librarian\")") == true)
    }

    @Test
    fun testUnsignedIntUid() = suspendTestNoJs {
        resourcesVfs["tiled/Spaceship 3.tmx"].readTiledMapData()
        resourcesVfs["tiled/Spaceship 3b.tmx"].readTiledMapData()
        resourcesVfs["tiled/Spaceship 3c.tmx"].readTiledMapData()
        resourcesVfs["tiled/Spaceship 3d.tmx"].readTiledMapData()
        resourcesVfs["tiled/Spaceship 3e.tmx"].readTiledMapData()
        resourcesVfs["tiled/Spaceship 3f.tmx"].readTiledMapData()
    }

    @Test
    fun testMultiTexture() = suspendTestNoJs {
        val tileSet = TileSet(
            listOf(
                TileSetTileInfo(1, Bitmap32(32, 32, Colors.RED.premultiplied).slice()),
                TileSetTileInfo(2, Bitmap32(32, 32, Colors.BLUE.premultiplied).slice())
            ), 32, 32
        )
        val tileMap = TileMap(TileMapData(32, 32), tileSet)
        tileMap.map.data[0, 0, 0] = Int53(0.0)
        tileMap.map.data[1, 0, 0] = Int53(1.0)
        tileMap.render(views.renderContext)
    }

    @Test
    fun testTileMapFlipRotateIndices() {
        assertEquals(
            "0123, 0321, 3210, 1230, 1032, 3012, 2301, 2103",
            (0 until 8).joinToString(", ") {
                BaseTileMapEx.computeIndices(flipX = it.extract(2), flipY = it.extract(1), rotate = it.extract(0))
                    .joinToString("")
            }
        )
    }

    @Test
    fun testTileMapWithTileSetFromOutsideFolder() = suspendTestNoJs {
        // The demo.tmx tilemap file is using a tileset from its parent folder -> ../wood_tileset_3.tsx
        resourcesVfs["tiled/demo.tmx"].readTiledMapData()
    }
}
