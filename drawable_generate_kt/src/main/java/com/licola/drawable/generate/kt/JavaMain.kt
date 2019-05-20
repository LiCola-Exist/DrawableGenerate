package com.licola.drawable.generate.kt

import com.licola.llogger.LLogger
import java.io.File
import java.io.IOException

/**
 * @author LiCola
 * @date 2019-05-20
 */
object JavaMain {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        LLogger.init()

        val dir = File("./drawable_generate_kt/src/main/res/drawable")

        val msg = DrawableGenerate
                .newBuilder(dir, true, "oval", "circle")//生成圆（oval） 文件别名circle
                .addSolid(arrayOf("black_A87", "black_A54", "black_A32"))
                .build()

        LLogger.d(msg)

    }
}
