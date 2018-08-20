package com.licola.drawable.generate;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Drawable资源构建类
 * 示例：指定相同shape形状的不同资源形式
 * DrawableGenerate.newBuilder(dir, "oval", "circle", true)
 * .addSolid(new String[]{"black_A87", "black_A54", "black_A32"})
 * .build();
 *
 * 生成：多个类似drawable资源
 * circle_solid_black_a32.xml，circle_solid_black_a54.xml，circle_solid_black_a87.xml
 *
 * 说明：
 * 生成的xml文件名反映内部drawable信息，可以直观的通过名称了解内部数据形式,
 * 同时为了能够文件名有直接具体的含义，只能通过{android:color="@color/black_A32"}形式引用现有资源。
 *
 * 构造过程add过程，类似多叉树（有重复节点）的构造过程，每一层的叶子节点都包含上一级全部信息。
 * 最后的叶子节点是具有完备的节点信息，build就是遍历多叉树叶子节点过程，根据叶子生成对应的drawable文件
 *
 * @author LiCola
 * @date 2018/8/16
 */
public class DrawableGenerate {

  private Builder builder;

  private DrawableGenerate(Builder builder) {
    this.builder = builder;
  }

  /**
   * @see Builder#Builder(File, String, String, boolean)
   */
  public static Builder newBuilder(File outDir, String shape, String shapeAlias,
      boolean replace) {
    return new Builder(outDir, shape, shapeAlias, replace);
  }

  /**
   * @see Builder#Builder(File, String, String, boolean)
   */
  public static Builder newBuilder(File outDir, String shape, boolean replace) {
    return new Builder(outDir, shape, shape, replace);
  }

  public int generate() throws IOException {

    DrawableNode rootNode = builder.rootNode;
    File outDir = builder.outDir;
    boolean replace = builder.replace;

    List<DrawableNode> leafsNodes = leafsTraversal(rootNode, new ArrayList<DrawableNode>());

    int fileSum = 0;
    for (DrawableNode leafsNode : leafsNodes) {
      File fileOut = DrawableFileHelper
          .checkFile(DrawableFileHelper.makeFile(outDir, leafsNode.name), replace);
      if (fileOut == null) {
        continue;
      }
      DrawableFileHelper.generateXmlFile(fileOut, leafsNode.content.getBytes());
      fileSum++;
    }

    return fileSum;
  }

  /**
   * 遍历多叉树的叶子节点
   *
   * @param node 父节点
   * @param leafs 叶子节点集合
   * @return leafs 返回叶子节点集合
   */
  private static List<DrawableNode> leafsTraversal(DrawableNode node, List<DrawableNode> leafs) {
    if (node == null) {
      return leafs;
    }

    List<DrawableNode> childNodes = node.getChildNodes();
    if (childNodes == null || childNodes.isEmpty()) {
      leafs.add(node);
      return leafs;
    }

    for (DrawableNode drawableNode : childNodes) {
      leafsTraversal(drawableNode, leafs);
    }
    return leafs;
  }

  static final class Builder {

    private File outDir;
    private boolean replace;

    private String shapeAlias;

    private DrawableNode rootNode;
    private List<DrawableNode> curLevelNodes;

    /**
     * @param outDir 输出目录
     * @param shape android:shape="形状"
     * @param shapeAlias 形状别名
     * @param replace 是否替换已经存在的drawable
     */
    Builder(File outDir, String shape, String shapeAlias, boolean replace) {
      this.outDir = outDir;
      this.replace = replace;

      this.shapeAlias = shapeAlias.isEmpty() ? shape : shapeAlias;
      String rootContent = String
          .format("<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
              + "  android:shape=\"%s\">", shape);
      rootNode = new DrawableNode(shapeAlias, rootContent);
      curLevelNodes = Collections.singletonList(rootNode);
    }

    /**
     * 添加corners圆角
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addCorners(String[] dimenIds) {
      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("corners_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<corners android:radius=\"%s\" />", formatResource(TYPE_DIMEN, resId));
          }
        });
      }
      this.curLevelNodes = process(curLevelNodes, onProcessResources);

      return this;
    }

    /**
     * 添加solid填充色
     *
     * @param colorIds color资源颜色id数组
     */
    public Builder addSolid(String[] colorIds) {

      List<OnProcessResources> onProcessResources = new ArrayList<>(colorIds.length);
      for (final String resId : colorIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("solid_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<solid android:color=\"%s\" />", formatResource(TYPE_COLOR, resId));
          }
        });
      }
      this.curLevelNodes = process(curLevelNodes, onProcessResources);
      return this;
    }

    /**
     * 添加size大小
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addSize(String[] dimenIds) {

      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("size_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<size\n"
                        + "    android:height=\"%s\"\n"
                        + "    android:width=\"%s\" />",
                    formatResource(TYPE_DIMEN, resId),
                    formatResource(TYPE_DIMEN, resId)
                );
          }
        });
      }
      this.curLevelNodes = process(curLevelNodes, onProcessResources);
      return this;
    }


    /**
     * 添加stroke线条
     *
     * @param dimenIds dimen资源id数组
     * @param colorIds color资源颜色id数组
     */
    public Builder addStroke(String[] dimenIds, String[] colorIds) {

      List<OnProcessResources> onProcessResources = new ArrayList<>(
          dimenIds.length * colorIds.length);
      for (final String dimenId : dimenIds) {
        for (final String colorId : colorIds) {
          onProcessResources.add(new OnProcessResources() {
            @Override
            public String onProcessName() {
              return String.format("stroke_%s_%s", dimenId.toLowerCase(), colorId.toLowerCase());
            }

            @Override
            public String onProcessContent() {
              return String.format("\t<stroke\n"
                      + "    android:width=\"%s\"\n"
                      + "    android:color=\"%s\" />",
                  formatResource(TYPE_DIMEN, dimenId),
                  formatResource(TYPE_COLOR, colorId)
              );
            }
          });
        }
      }
      this.curLevelNodes = process(curLevelNodes, onProcessResources);
      return this;
    }

    /**
     * 构造各种参数配置的多个drawable
     */
    public void build() {
      try {
        int sum = new DrawableGenerate(this).generate();
        String outFileInfo = String.format(Locale.CHINA, "生成%d个%s类型drawable文件", sum, shapeAlias);
        System.out.println(outFileInfo);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("生成drawable文件失败"+e.toString());
      }

    }
  }

  private interface OnProcessResources {

    /**
     * @return 处理名称
     */
    String onProcessName();

    /**
     * @return 处理内容
     */
    String onProcessContent();
  }

  private static List<DrawableNode> process(List<DrawableNode> curLevelNodes,
      List<OnProcessResources> onProcessResources) {

    List<DrawableNode> newCurLevelNodes = new ArrayList<>();
    for (DrawableNode curDepthNode : curLevelNodes) {
      for (OnProcessResources process : onProcessResources) {
        String childName = appendFileNameOrEmpty(curDepthNode.getName(), process.onProcessName());
        String childContent = appendContent(curDepthNode.getContent(), process.onProcessContent());
        DrawableNode childNode = new DrawableNode(childName, childContent);
        curDepthNode.addChildNode(childNode);
        newCurLevelNodes.add(childNode);
      }
    }
    return newCurLevelNodes;
  }

  private static String appendFileNameOrEmpty(String fileName, String appendName) {
    if (fileName == null || fileName.isEmpty()) {
      return appendName;
    }
    return fileName + "_" + appendName;
  }

  private static String appendContent(String fileContent, String appendContent) {
    return fileContent + '\n' + appendContent;
  }

  private static final String TYPE_DIMEN = "@dimen/";
  private static final String TYPE_COLOR = "@color/";

  private static String formatResource(String type, String value) {
    return type + value;
  }

}
