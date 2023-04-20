package korlibs.korge.tiled

import korlibs.datastructure.Extra
import korlibs.datastructure.iterators.*
import korlibs.datastructure.linkedHashMapOf
import korlibs.korge.view.*
import korlibs.korge.view.tiles.*
import korlibs.image.bitmap.*
import korlibs.image.color.*
import korlibs.image.tiles.tiled.TiledMap
import korlibs.image.tiles.tiled.toTileSet
import korlibs.math.geom.*
import korlibs.math.geom.shape.*
import kotlin.math.*

inline fun Container.tiledMapView(tiledMap: TiledMap, showShapes: Boolean = true, smoothing: Boolean = true, callback: TiledMapView.() -> Unit = {}) =
	TiledMapView(tiledMap, showShapes, smoothing).addTo(this, callback)

class TiledMapView(val tiledMap: TiledMap, showShapes: Boolean = true, smoothing: Boolean = true) : Container() {
    val tileset = tiledMap.tilesets.toTileSet()

    override fun hitTest(p: Point, direction: HitTestDirection): View? {
        //return super.hitTest(x, y, direction)
        return globalPixelHitTest(p, direction)
    }

    override val customHitShape get() = true
    protected fun hitTestShapeInternal(shape: Shape2D, matrix: MMatrix, direction: HitTestDirection): View? {
        // @TODO: Use shape
        val p = matrix.transform(shape.center)
        return globalPixelHitTest(p, direction)
        //println("TiledMapView.hitTestShapeInternal: $shape, $matrix")
        //return super.hitTestShapeInternal(shape, matrix, direction)
    }

    //protected override fun hitTestInternal(x: Double, y: Double, direction: HitTestDirection): View? = globalPixelHitTest(x, y, direction)

    //fun globalPixelHitTest(globalXY: IPoint, direction: HitTestDirection = HitTestDirection.ANY): View? = globalPixelHitTest(globalXY.x, globalXY.y, direction)

    fun globalPixelHitTest(p: Point, direction: HitTestDirection = HitTestDirection.ANY): View? {
        return pixelHitTest(
            (globalToLocal(p) / scaleX).toIntRound(),
            direction
        )
    }

    fun pixelHitTest(p: PointInt, direction: HitTestDirection = HitTestDirection.ANY): View? {
        fastForEachChild { child ->
            when (child) {
                is TileMapEx -> {
                    if (child.pixelHitTest(p, direction)) return child
                }
            }
        }
        return null
    }

    init {
		tiledMap.allLayers.fastForEachWithIndex { index, layer ->
            val view: View = when (layer) {
                is TiledMap.Layer.Tiles -> tileMapEx(
                    map = layer.map,
                    tileset = tileset,
                    smoothing = smoothing,
                    orientation = tiledMap.data.orientation,
                    staggerAxis = tiledMap.data.staggerAxis,
                    staggerIndex = tiledMap.data.staggerIndex,
                    tileSize = Size(tiledMap.tilewidth.toDouble(), tiledMap.tileheight.toDouble()),
                )
                //is TiledMap.Layer.Image -> image(layer.image)
                is TiledMap.Layer.Objects -> {
                    container {
                        for (obj in layer.objects) {
                            val bounds = obj.bounds
                            val gid = obj.gid
                            //println("ID:${obj.id} : ${obj::class}")
                            var shouldShow = showShapes
                            val view: View = when (val type = obj.objectShape) {
                                is TiledMap.Object.Shape.PPoint -> {
                                    solidRect(1.0, 1.0, Colors.WHITE)
                                }
                                is TiledMap.Object.Shape.Ellipse -> {
                                    ellipse(Size(bounds.width.toDouble() / 2, bounds.height.toDouble() / 2))
                                    //solidRect(bounds.width, bounds.width, Colors.RED)
                                }
                                is TiledMap.Object.Shape.Rectangle -> {
                                    if (gid != null) {
                                        val tileTex = tileset[gid] ?: Bitmaps.transparent
                                        //println("tileTex[gid=$gid]: $tileTex!")
                                        shouldShow = true
                                        image(tileTex)
                                    } else {
                                        //println("tileTex[gid=$gid]!")
                                        solidRect(bounds.width.toDouble(), bounds.height.toDouble(), Colors.WHITE)
                                    }
                                }
                                is TiledMap.Object.Shape.Polygon -> cpuGraphics {
                                    fill(Colors.WHITE) {
                                        var first = true
                                        var firstPoint: Point? = null
                                        type.points.fastForEach { point ->
                                            if (first) {
                                                first = false
                                                firstPoint = point
                                                moveTo(point)
                                            } else {
                                                lineTo(point)
                                            }                                        }
                                        firstPoint?.let { lineTo(it) }
                                        close()
                                    }
                                }
                                is TiledMap.Object.Shape.Polyline -> cpuGraphics {
                                    fill(Colors.WHITE) {
                                        var first = true
                                        type.points.fastForEach { point ->
                                            if (first) {
                                                first = false
                                                moveTo(point)
                                            } else {
                                                lineTo(point)
                                            }
                                        }
                                        close()
                                    }
                                }
                                is TiledMap.Object.Shape.Text -> {
                                    TODO("Unsupported tiled object $obj")
                                }
                            }
                            view
                                .visible(shouldShow)
                                .name(obj.name.takeIf { it.isNotEmpty() })
                                .xy(bounds.x, bounds.y)
                                .rotation(obj.rotation.degrees)
                                .also { it.addTiledProp("type", obj.type) }
                                .also { it.addTiledProps(obj.properties) }
                        }
                    }
                }
                else -> dummyView()
            }
            view
                .visible(layer.visible)
                .name(layer.name.takeIf { it.isNotEmpty() })
                .xy(layer.offsetx, layer.offsety)
                .alpha(layer.opacity)
                .also { it.addTiledProps(layer.properties) }
		}
	}
}

fun View.getTiledPropString(key: String): String? {
    return tiledProps?.get(key)?.toString()
}
fun View.addTiledProp(key: String, value: Any?) {
    tiledPropsSure[key] = value
}
fun View.addTiledProps(map: Map<String, TiledMap.Property>) {
    tiledPropsSure.putAll(map)
}
var View.tiledProps: MutableMap<String, Any?>? by Extra.PropertyThis { null }
val View.tiledPropsSure: MutableMap<String, Any?> get() {
    if (tiledProps == null) tiledProps = linkedHashMapOf()
    return tiledProps!!
}

fun TiledMap.createView() = TiledMapView(this)
