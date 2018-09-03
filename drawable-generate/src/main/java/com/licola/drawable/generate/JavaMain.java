package com.licola.drawable.generate;

import java.io.File;
import java.io.IOException;

/**
 * @author LiCola
 * @date 2018/8/17
 */
public class JavaMain {


  public static void main(String[] args) throws IOException {
    File dir = new File(
        "./drawable-generate/src/main/res/drawable");

    DrawableGenerate.newBuilder(dir, "rectangle", true)//生成矩形
        .addCorners(new String[]{"x1dp"})//圆角
        .addSolid(new String[]{"black_A87", "orange"})//填充色
        .addStroke(new String[]{"x1dp"}, new String[]{"gray_deep"})//外边线条
        .build();

    DrawableGenerate.newBuilder(dir, "oval", "circle", true)//生成圆（oval） 文件别名circle
        .addSolid(new String[]{"black_A87", "black_A54", "black_A32"})
        .build();

    DrawableGenerate.newBuilder(dir, "rectangle", "line", true)//生成线条（rectangle） 别名line 即常见的分割线
        .addSize(new String[]{"x1dp", "x10dp"})//各种分割线大小
        .addSolid(new String[]{"black_A87", "black_A54", "black_A32"})//各种分割线
        .build();

    String[] baseCornersColor = new String[]{"orange", "white"};
    DrawableGenerate.newBuilder(dir, "rectangle", "", true)
        .addCornersBottom(new String[]{"x10dp"})
        .addSolid(baseCornersColor)
        .build();
    DrawableGenerate.newBuilder(dir, "rectangle", "", true)
        .addCornersLeft(new String[]{"x10dp"})
        .addSolid(baseCornersColor)
        .build();

  }
}
