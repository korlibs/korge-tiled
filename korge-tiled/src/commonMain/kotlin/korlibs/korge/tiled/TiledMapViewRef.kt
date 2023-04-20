package korlibs.korge.tiled

import korlibs.image.tiles.tiled.readTiledMap
import korlibs.io.file.VfsFile
import korlibs.korge.render.*
import korlibs.korge.view.*
import korlibs.korge.view.property.*

class TiledMapViewRef() : Container(), ViewLeaf, ViewFileRef by ViewFileRef.Mixin() {
    override suspend fun forceLoadSourceFile(views: Views, currentVfs: VfsFile, sourceFile: String?) {
        baseForceLoadSourceFile(views, currentVfs, sourceFile)
        removeChildren()
        addChild(currentVfs["$sourceFile"].readTiledMap().createView())
    }

    override fun renderInternal(ctx: RenderContext) {
        this.lazyLoadRenderInternal(ctx, this)
        super.renderInternal(ctx)
    }

    @Suppress("unused")
    @ViewProperty
    @ViewPropertyFileRef(["tmx"])
    private var tilemapSourceFile: String? by this::sourceFile
}
