package com.soywiz.korge.view.tiles

import com.soywiz.korim.tiles.tiled.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlin.test.*

class TiledMapTest {
    val vfs = localCurrentDirVfs["src/commonTest/resources"]

    //val vfs = resourcesVfs
    @Test
    fun test() = suspendTest {
        vfs["tmxbug1/lv1.tmx"].readTiledMap()
    }

    @Test
    fun test2() = suspendTest {
        resourcesVfs["tiled/Abstract_Platformer.tmx"].readTiledMap()
        resourcesVfs["tiled/platformer.tmx"].readTiledMap()
    }
}
