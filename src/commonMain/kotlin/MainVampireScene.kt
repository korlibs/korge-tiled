import korlibs.datastructure.iterators.*
import korlibs.event.*
import korlibs.image.atlas.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.image.tiles.tiled.*
import korlibs.io.file.std.*
import korlibs.korge.scene.*
import korlibs.korge.tiled.*
import korlibs.korge.view.*
import korlibs.korge.view.animation.*
import korlibs.korge.view.collision.*
import korlibs.math.geom.*
import korlibs.math.geom.shape.*
import korlibs.math.geom.slice.*
import korlibs.time.*

class MainVampireScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val atlas = MutableAtlasUnit(1024, 512, border = 2)

        val sw = Stopwatch().start()

        resourcesVfs["korim.png"].readBitmapSlice().splitInRows(32, 32).toAtlas(atlas = atlas)
        val korim = resourcesVfs["korim.png"].readBitmapSlice(atlas = atlas)
        val characters = resourcesVfs["characters.ase"].readImageDataContainer(ASE.toProps(), atlas = atlas)
        val slices = resourcesVfs["slice-example.ase"].readImageDataContainer(ASE.toProps(), atlas = atlas)
        val tiledMap = resourcesVfs["Tilemap/untitled.tmx"].readTiledMap(atlas = atlas)

        println(sw.elapsed)

        //image(korim)

        //image(atlas.bitmap);return

        lateinit var tiledMapView: TiledMapView

        container {
            scale(2.0)
            //tiledMapView(tiledMap, smoothing = false)
            tiledMapView = tiledMapView(tiledMap, smoothing = true)
        }

        container {
            scale = 2.0
            imageDataView(slices["wizHat"]).xy(0, 50)
            imageDataView(slices["giantHilt"]).xy(32, 50)
            imageDataView(slices["pumpkin"]).xy(64, 50)
        }

        //image(tiledMapView.collisionToBitmap()).scale(2.0)

        //val ase2 = resourcesVfs["vampire.ase"].readImageData(ASE, atlas = atlas)
        //val ase3 = resourcesVfs["vampire.ase"].readImageData(ASE, atlas = atlas)
        //for (bitmap in atlas.allBitmaps) image(bitmap) // atlas generation

        //val gg = buildPath {
        //    rect(300, 0, 100, 100)
        //    circle(400, 400, 50)
        //    star(5, 30.0, 100.0, x = 400.0, y = 300.0)
        //    //star(400, 400, 50)
        //}

        val gg = cpuGraphics {
            fill(Colors.RED) {
                rect(300, 0, 100, 100)
            }
            fill(Colors.RED) {
                circle(Point(400, 400), 50.0)
            }
            fill(Colors.BLUE) {
                star(5, 30.0, 100.0, x = 400.0, y = 300.0)
                //star(400, 400, 50)
            }
        }

        container {
            keepChildrenSortedByY()

            val character1 = imageDataView(characters["vampire"], "down") {
                stop()
                xy(200, 200)
                hitShape2d = Rectangle.fromBounds(-8.0, -3.0, +8.0, +3.0).toShape2D()
            }

            val character2 = imageDataView(characters["vamp"], "down") {
                stop()
                xy(160, 110)
            }

            //val hitTestable = listOf(tiledMapView, gg).toHitTestable()
            val hitTestable = listOf(gg)

            controlWithKeyboard(
                character1,
                hitTestable,
                up = Key.UP,
                right = Key.RIGHT,
                down = Key.DOWN,
                left = Key.LEFT,
            )
            controlWithKeyboard(character2, hitTestable, up = Key.W, right = Key.D, down = Key.S, left = Key.A)
        }
    }

    private fun Container.keepChildrenSortedByY() {
        addUpdater {
            children.fastForEach { it.zIndex = y }
        }
    }


    fun Container.controlWithKeyboard(
        char: ImageDataView,
        collider: List<View>,
        up: Key = Key.UP,
        right: Key = Key.RIGHT,
        down: Key = Key.DOWN,
        left: Key = Key.LEFT,
    ) {
        this@MainVampireScene.stage
        addUpdater { dt ->
            val speed = 5.0 * (dt / 16.0.milliseconds)
            val dx = stage!!.keys.getDeltaAxis(left, right)
            val dy = stage!!.keys.getDeltaAxis(up, down)
            if (dx != 0.0 || dy != 0.0) {
                val dpos = Point(dx, dy).normalized * speed
                char.moveWithCollisions(collider, dpos.x.toFloat(), dpos.y.toFloat())
            }
            char.animation = when {
                dx < 0.0 -> "left"
                dx > 0.0 -> "right"
                dy < 0.0 -> "up"
                dy > 0.0 -> "down"
                else -> char.animation
            }
            if (dx != 0.0 || dy != 0.0) {
                char.play()
            } else {
                char.stop()
                char.rewind()
            }
        }
    }


    fun TiledMapView.collisionToBitmap(): Bitmap {
        val bmp = Bitmap32(this.width.toInt(), this.height.toInt())
        for (y in 0 until bmp.height) for (x in 0 until bmp.width) {
            bmp[x, y] = if (pixelHitTest(PointInt(x, y)) != null) Colors.WHITE else Colors.TRANSPARENT
        }
        return bmp
    }
}
