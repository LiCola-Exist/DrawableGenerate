# 自动批量生成Drawable资源

# 背景
开UI开发中，设计师会定义各自分割线、圆形、圆角矩形等UI元素，这些元素基本都是类似，只是在大小，颜色、圆角大小上有细微变化。

同时在UI开发中为了，尽可能的使用AS的preview实时预览功能，也为其他drawable资源的互相引用，没有采用直接构建ShapeDrawable对象的形式。

# 自动生成
为了生成的xml文件名有含义，且能够通过文件名直观的了解内部的drawable结构。
需要先行在values目标下的color/dimens等资源中定义名称有含义颜色的距离资源名

# 使用 
```java
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
        .addSolid(new String[]{"black_A87", "black_A54", "black_A32"})//各种分割线颜色
        .build();
  }
```
在某个类中定义mian方法，run直接运行在PC本机上，生成资源xml文件。
![效果图](https://github.com/LiCola/DrawableGenerate/blob/master/image/drawable-build.png)

# API
详见[DrawableGenerate](https://github.com/LiCola/DrawableGenerate/blob/master/drawable-generate/src/main/java/com/licola/drawable/generate/DrawableGenerate.java)

# 关联资源定义
[color](https://github.com/LiCola/DrawableGenerate/blob/master/drawable-generate/src/main/res/values/color.xml)
[dimens](https://github.com/LiCola/DrawableGenerate/blob/master/drawable-generate/src/main/res/values/dimens.xml)