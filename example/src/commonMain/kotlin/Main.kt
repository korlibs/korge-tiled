import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*

val DEFAULT_KORGE_BG_COLOR = Colors.DARKCYAN.mix(Colors.BLACK, 0.8)

suspend fun main() = Korge(
    bgcolor = DEFAULT_KORGE_BG_COLOR,
    //bgcolor = Colors.WHITE,
    clipBorders = false,
    //scaleMode = ScaleMode.EXACT,
    //debug = true,
    debug = false,
    multithreaded = true,
    forceRenderEveryFrame = false // Newly added optimization!

) {
    sceneContainer().changeTo({ MainTiledMapWithScrollScene() })
    //sceneContainer().changeTo({ MainRpgScene() })
}
