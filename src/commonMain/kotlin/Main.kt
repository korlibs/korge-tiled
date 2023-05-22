import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.image.color.*
import korlibs.math.interpolation.*

val DEFAULT_KORGE_BG_COLOR = Colors.DARKCYAN.mix(Colors.BLACK, 0.8.toRatio())

suspend fun main() = Korge(
    backgroundColor = DEFAULT_KORGE_BG_COLOR,
    displayMode = KorgeDisplayMode.DEFAULT.copy(clipBorders = false),
    //bgcolor = Colors.WHITE,
    //clipBorders = false,
    //scaleMode = ScaleMode.EXACT,
    //debug = true,
    debug = false,
    multithreaded = true,
    forceRenderEveryFrame = false // Newly added optimization!
).start {
    sceneContainer().changeTo({ MainVampireScene() })

    //sceneContainer().changeTo({ MainTiledMapWithScrollScene() })
    //sceneContainer().changeTo({ MainRpgScene() })
}
