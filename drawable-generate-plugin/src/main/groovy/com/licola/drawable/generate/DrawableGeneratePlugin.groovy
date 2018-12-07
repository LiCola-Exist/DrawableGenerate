package com.licola.drawable.generate

import org.gradle.api.Plugin
import org.gradle.api.Project

class DrawableGeneratePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.extensions.create("generateExt",GenerateExt)

        //after 在gradle构建完成后自动执行
        project.afterEvaluate {
            println("Hello plugin drawable!")
            println("project.generateExt:"+project.generateExt.srcDir)

//            def file = new File("namefile.txt")
//            if (file.exists()) {
//                println("has exists:" + file.getAbsolutePath())
//            } else {
//                file.write("date")
//            }
        }
        //这里是添加一个task 需要手动调起
        project.task("drawable") << {
            println("new task")
        }
    }
}

