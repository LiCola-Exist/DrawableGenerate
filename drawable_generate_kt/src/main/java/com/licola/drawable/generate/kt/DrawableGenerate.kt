package com.licola.drawable.generate.kt


import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Drawable资源构建类 示例：指定相同shape形状的不同资源形式 DrawableGenerate.newBuilder(dir, "oval", "circle", true)
 * .addSolid(new String[]{"black_A87", "black_A54", "black_A32"}) .build();
 *
 * 生成：多个类似drawable资源 circle_solid_black_a32.xml，circle_solid_black_a54.xml，circle_solid_black_a87.xml
 *
 * 说明： 生成的xml文件名反映内部drawable信息，可以直观的通过名称了解内部数据形式, 同时为了能够文件名有直接具体的含义，只能通过{android:color="@color/black_A32"}形式引用现有资源。
 *
 * 构造过程add过程，类似多叉树（有重复节点）的构造过程，每一层的叶子节点都包含上一级全部信息。 最后的叶子节点是具有完备的节点信息，build就是使用多叉树叶子节点过程，根据叶子生成对应的drawable文件
 *
 * 更新记录： 2018/12/11：精简类，优化成一个类文件处理。并开放外部
 *
 * @author LiCola
 * @date 2018/8/16
 */
class DrawableGenerate private constructor(private val builder: Builder) {

    @Throws(IOException::class)
    fun generate(): Int {

        val outDir = builder.outDir
        val replace = builder.replace

        val leafsNodes = builder.curLevelNodes

        var fileSum = 0
        for (leafsNode in leafsNodes!!) {
            val outFile = makeFile(outDir, leafsNode.name)
            if (!replace && outDir.exists()) {
                continue
            }
            generateXmlFile(outFile, leafsNode.content.toByteArray())
            fileSum++
        }

        return fileSum
    }


    class Builder
    /**
     * @param outDir 输出目录
     * @param replace 是否替换已经存在的drawable
     * @param shape android:shape="形状"
     * @param shapeAlias 形状别名
     */
    (val outDir: File, val replace: Boolean, shape: String, shapeAlias: String) {

        val shapeAlias: String

        val rootNode: DrawableNode
        var curLevelNodes: List<DrawableNode>? = null

        init {
            this.shapeAlias = if (shapeAlias.isNotEmpty()) shapeAlias else shape
            val rootContent = String
                    .format("<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" + "  android:shape=\"%s\">", shape)
            rootNode = DrawableNode(shapeAlias, rootContent)
            curLevelNodes = listOf(rootNode)
        }

        fun addNode(resources: Array<OnProcessResources>): Builder {
            return addNode(Arrays.asList(*resources))
        }

        /**
         * 添加corners圆角
         *
         * @param dimenIds dimen资源id数组
         */
        fun addCorners(dimenIds: Array<String>): Builder {
            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return String.format("corners_%s", resId.toLowerCase())
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<corners android:radius=\"%s\" />", formatResource(TYPE_DIMEN, resId))
                    }
                })
            }
            return addNode(onProcessResources)
        }

        /**
         * 添加corners圆角(只有上边Top)
         *
         * @param dimenIds dimen资源id数组
         */
        fun addCornersTop(dimenIds: Array<String>): Builder {
            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return String.format("corners_top_%s", resId.toLowerCase())
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<corners\n"
                                        + "    android:topLeftRadius=\"%s\"\n"
                                        + "    android:topRightRadius=\"%s\" />",
                                        formatResource(TYPE_DIMEN, resId),
                                        formatResource(TYPE_DIMEN, resId))
                    }
                })
            }
            return addNode(onProcessResources)
        }

        /**
         * 添加corners圆角(只有底边Bottom)
         *
         * @param dimenIds dimen资源id数组
         */
        fun addCornersBottom(dimenIds: Array<String>): Builder {
            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return String.format("corners_bottom_%s", resId.toLowerCase())
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<corners\n"
                                        + "    android:bottomLeftRadius=\"%s\"\n"
                                        + "    android:bottomRightRadius=\"%s\" />",
                                        formatResource(TYPE_DIMEN, resId),
                                        formatResource(TYPE_DIMEN, resId))
                    }
                })
            }
            return addNode(onProcessResources)
        }

        /**
         * 添加corners圆角(只有左边Left)
         *
         * @param dimenIds dimen资源id数组
         */
        fun addCornersLeft(dimenIds: Array<String>): Builder {
            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return String.format("corners_left_%s", resId.toLowerCase())
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<corners\n"
                                        + "    android:bottomLeftRadius=\"%s\"\n"
                                        + "    android:topLeftRadius=\"%s\" />",
                                        formatResource(TYPE_DIMEN, resId),
                                        formatResource(TYPE_DIMEN, resId))
                    }
                })
            }
            return addNode(onProcessResources)

        }

        /**
         * 添加corners圆角(只有左边Left)
         *
         * @param dimenIds dimen资源id数组
         */
        fun addCornersRight(dimenIds: Array<String>): Builder {
            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return String.format("corners_right_%s", resId.toLowerCase())
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<corners\n"
                                        + "    android:bottomRightRadius=\"%s\"\n"
                                        + "    android:topRightRadius=\"%s\" />",
                                        formatResource(TYPE_DIMEN, resId),
                                        formatResource(TYPE_DIMEN, resId))
                    }
                })
            }
            return addNode(onProcessResources)

        }

        /**
         * 添加solid填充色
         *
         * @param colorIds color资源颜色id数组
         */
        fun addSolid(colorIds: Array<String>): Builder {

            val onProcessResources = ArrayList<OnProcessResources>(colorIds.size)
            for (resId in colorIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return "solid_${resId.toLowerCase()}"
                    }

                    override fun onProcessContent(): String {
                        return "\t<solid android:color=\"${formatResource(TYPE_COLOR, resId)}\" />"

                    }
                })
            }
            return addNode(onProcessResources)

        }

        /**
         * 添加size大小
         *
         * @param dimenIds dimen资源id数组
         */
        fun addSize(dimenIds: Array<String>): Builder {

            val onProcessResources = ArrayList<OnProcessResources>(dimenIds.size)
            for (resId in dimenIds) {
                onProcessResources.add(object : OnProcessResources {
                    override fun onProcessName(): String {
                        return "size_$resId.toLowerCase()"
                    }

                    override fun onProcessContent(): String {
                        return String
                                .format("\t<size\n"
                                        + "    android:height=\"%s\"\n"
                                        + "    android:width=\"%s\" />",
                                        formatResource(TYPE_DIMEN, resId),
                                        formatResource(TYPE_DIMEN, resId)
                                )
                    }
                })
            }
            return addNode(onProcessResources)

        }


        /**
         * 添加stroke线条
         *
         * @param dimenIds dimen资源id数组
         * @param colorIds color资源颜色id数组
         */
        fun addStroke(dimenIds: Array<String>, colorIds: Array<String>): Builder {

            val onProcessResources = ArrayList<OnProcessResources>(
                    dimenIds.size * colorIds.size)
            for (dimenId in dimenIds) {
                for (colorId in colorIds) {
                    onProcessResources.add(object : OnProcessResources {
                        override fun onProcessName(): String {
                            return "stroke_${dimenId.toLowerCase()}_${colorId.toLowerCase()}"
                        }

                        override fun onProcessContent(): String {
                            return String.format("\t<stroke\n"
                                    + "    android:width=\"%s\"\n"
                                    + "    android:color=\"%s\" />",
                                    formatResource(TYPE_DIMEN, dimenId),
                                    formatResource(TYPE_COLOR, colorId)
                            )
                        }
                    })
                }
            }
            return addNode(onProcessResources)

        }

        private fun close(): Builder {

            return addNode(listOf<OnProcessResources>(object : OnProcessResources {
                override fun onProcessName(): String? {
                    return null
                }

                override fun onProcessContent(): String {
                    return "</shape>"
                }
            }))
        }

        /**
         * 构造各种参数配置的多个drawable
         */
        fun build(): String {

            var msg: String
            try {
                val drawableGenerate = DrawableGenerate(close())
                val sum = drawableGenerate.generate()
                msg = String.format(Locale.CHINA, "生成%d个%s类型drawable文件", sum, shapeAlias)
            } catch (e: IOException) {
                msg = "生成drawable文件失败:$e"
            }

            return msg
        }

        private fun addNode(onProcessResources: List<OnProcessResources>): Builder {

            val newCurLevelNodes = ArrayList<DrawableNode>()
            for (curDepthNode in curLevelNodes!!) {
                for (process in onProcessResources) {
                    val childName = appendFileNameOrEmpty(curDepthNode.name, process.onProcessName())
                    val childContent = appendContent(curDepthNode.content,
                            process.onProcessContent())

                    val childNode = DrawableNode(childName, childContent)

                    curDepthNode.addChildNode(childNode)
                    newCurLevelNodes.add(childNode)
                }
            }

            this.curLevelNodes = newCurLevelNodes
            return this
        }

    }

    class DrawableNode internal constructor(internal var name: String?, internal var content: String) {
        internal var childNodes: MutableList<DrawableNode>? = null

        internal fun addChildNode(childNode: DrawableNode) {
            if (childNodes == null) {
                childNodes = ArrayList()
            }
            childNodes!!.add(childNode)
        }
    }

    interface OnProcessResources {

        /**
         * @return 处理名称
         */
        fun onProcessName(): String?

        /**
         * @return 处理内容
         */
        fun onProcessContent(): String
    }

    companion object {

        private val FILE_SUFFIX = ".xml"

        private val HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".toByteArray()

        fun newBuilder(outDir: File, replace: Boolean, shape: String, shapeAlias: String): Builder {
            return Builder(outDir, replace, shape, shapeAlias)
        }

        fun newBuilder(outDir: File, replace: Boolean, shape: String): Builder {
            return Builder(outDir, replace, shape, shape)
        }

        private fun appendFileNameOrEmpty(fileName: String?, appendName: String?): String? {
            if (fileName == null || fileName.isEmpty()) {
                return appendName
            }

            return if (appendName == null || appendName.isEmpty()) {
                fileName
            } else fileName + "_" + appendName

        }

        private fun appendContent(fileContent: String, appendContent: String): String {
            return fileContent + '\n'.toString() + appendContent
        }

        private const val TYPE_DIMEN = "@dimen/"
        private const val TYPE_COLOR = "@color/"

        private fun formatResource(type: String, value: String): String {
            return type + value
        }

        private fun makeFile(outDir: File, fileName: String?): File {
            return File(outDir, fileName + FILE_SUFFIX)
        }

        @Throws(IOException::class)
        private fun generateXmlFile(outFile: File, outBytes: ByteArray) {

            val outputStream = BufferedOutputStream(
                    FileOutputStream(outFile))
            try {
                outputStream.write(HEAD, 0, HEAD.size)
                outputStream.write(outBytes, 0, outBytes.size)
            } finally {
                outputStream.flush()
                outputStream.close()
            }
        }
    }
}
