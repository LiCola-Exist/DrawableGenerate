package com.licola.drawable.generate;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
 * 更新记录：
 * 2018/12/11：精简类，优化成一个类文件处理。并开放外部
 * @author LiCola
 * @date 2018/8/16
 */
public class DrawableGenerate {

  private static final String FILE_SUFFIX = ".xml";

  private static final byte[] HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes();

  private Builder builder;

  private DrawableGenerate(Builder builder) {
    this.builder = builder;
  }

  public static Builder newBuilder(File outDir, boolean replace, String shape, String shapeAlias) {
    return new Builder(outDir, replace, shape, shapeAlias);
  }

  public static Builder newBuilder(File outDir, boolean replace, String shape) {
    return new Builder(outDir, replace, shape, shape);
  }

  public int generate() throws IOException {

    File outDir = builder.outDir;
    boolean replace = builder.replace;

    List<DrawableNode> leafsNodes = builder.curLevelNodes;

    int fileSum = 0;
    for (DrawableNode leafsNode : leafsNodes) {
      File outFile = makeFile(outDir, leafsNode.name);
      if (!replace && outDir.exists()) {
        continue;
      }
      generateXmlFile(outFile, leafsNode.content.getBytes());
      fileSum++;
    }

    return fileSum;
  }


  static final class Builder {

    private File outDir;
    private boolean replace;

    private String shapeAlias;

    private DrawableNode rootNode;
    private List<DrawableNode> curLevelNodes;

    /**
     * @param outDir 输出目录
     * @param replace 是否替换已经存在的drawable
     * @param shape android:shape="形状"
     * @param shapeAlias 形状别名
     */
    Builder(File outDir, boolean replace, String shape, String shapeAlias) {
      this.outDir = outDir;
      this.replace = replace;

      this.shapeAlias = shapeAlias.isEmpty() ? shape : shapeAlias;
      String rootContent = String
          .format("<shape xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
              + "  android:shape=\"%s\">", shape);
      rootNode = new DrawableNode(shapeAlias, rootContent);
      curLevelNodes = Collections.singletonList(rootNode);
    }

    public Builder addNode(OnProcessResources[] resources){
      return addNode(Arrays.asList(resources));
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
      return addNode(onProcessResources);
    }

    /**
     * 添加corners圆角(只有上边Top)
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addCornersTop(String[] dimenIds) {
      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("corners_top_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<corners\n"
                        + "    android:topLeftRadius=\"%s\"\n"
                        + "    android:topRightRadius=\"%s\" />",
                    formatResource(TYPE_DIMEN, resId),
                    formatResource(TYPE_DIMEN, resId));
          }
        });
      }
      return addNode(onProcessResources);
    }

    /**
     * 添加corners圆角(只有底边Bottom)
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addCornersBottom(String[] dimenIds) {
      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("corners_bottom_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<corners\n"
                        + "    android:bottomLeftRadius=\"%s\"\n"
                        + "    android:bottomRightRadius=\"%s\" />",
                    formatResource(TYPE_DIMEN, resId),
                    formatResource(TYPE_DIMEN, resId));
          }
        });
      }
      return addNode(onProcessResources);
    }

    /**
     * 添加corners圆角(只有左边Left)
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addCornersLeft(String[] dimenIds) {
      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("corners_left_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<corners\n"
                        + "    android:bottomLeftRadius=\"%s\"\n"
                        + "    android:topLeftRadius=\"%s\" />",
                    formatResource(TYPE_DIMEN, resId),
                    formatResource(TYPE_DIMEN, resId));
          }
        });
      }
      return addNode(onProcessResources);

    }

    /**
     * 添加corners圆角(只有左边Left)
     *
     * @param dimenIds dimen资源id数组
     */
    public Builder addCornersRight(String[] dimenIds) {
      List<OnProcessResources> onProcessResources = new ArrayList<>(dimenIds.length);
      for (final String resId : dimenIds) {
        onProcessResources.add(new OnProcessResources() {
          @Override
          public String onProcessName() {
            return String.format("corners_right_%s", resId.toLowerCase());
          }

          @Override
          public String onProcessContent() {
            return String
                .format("\t<corners\n"
                        + "    android:bottomRightRadius=\"%s\"\n"
                        + "    android:topRightRadius=\"%s\" />",
                    formatResource(TYPE_DIMEN, resId),
                    formatResource(TYPE_DIMEN, resId));
          }
        });
      }
      return addNode(onProcessResources);

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
      return addNode(onProcessResources);

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
      return addNode(onProcessResources);

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
      return addNode(onProcessResources);

    }

    private Builder close() {

      return addNode(Collections.singletonList(new OnProcessResources() {
        @Override
        public String onProcessName() {
          return null;
        }

        @Override
        public String onProcessContent() {
          return "</shape>";
        }
      }));
    }

    /**
     * 构造各种参数配置的多个drawable
     */
    public String build() {

      String msg;
      try {
        DrawableGenerate drawableGenerate = new DrawableGenerate(close());
        int sum = drawableGenerate.generate();
        msg = String.format(Locale.CHINA, "生成%d个%s类型drawable文件", sum, shapeAlias);
      } catch (IOException e) {
        msg = "生成drawable文件失败:" + e.toString();
      }
      return msg;
    }

    private Builder addNode(List<OnProcessResources> onProcessResources) {

      List<DrawableNode> newCurLevelNodes = new ArrayList<>();
      for (DrawableNode curDepthNode : curLevelNodes) {
        for (OnProcessResources process : onProcessResources) {
          String childName = appendFileNameOrEmpty(curDepthNode.name, process.onProcessName());
          String childContent = appendContent(curDepthNode.content,
              process.onProcessContent());

          DrawableNode childNode = new DrawableNode(childName, childContent);

          curDepthNode.addChildNode(childNode);
          newCurLevelNodes.add(childNode);
        }
      }

      this.curLevelNodes = newCurLevelNodes;
      return this;
    }

  }

  public static class DrawableNode {

    String name;
    String content;
    List<DrawableNode> childNodes;

    DrawableNode(String name, String content) {
      this.name = name;
      this.content = content;
    }

    void addChildNode(DrawableNode childNode){
      if (childNodes==null){
        childNodes=new ArrayList<>();
      }
      childNodes.add(childNode);
    }
  }

  public interface OnProcessResources {

    /**
     * @return 处理名称
     */
    String onProcessName();

    /**
     * @return 处理内容
     */
    String onProcessContent();
  }

  private static String appendFileNameOrEmpty(String fileName, String appendName) {
    if (fileName == null || fileName.isEmpty()) {
      return appendName;
    }

    if (appendName == null || appendName.isEmpty()) {
      return fileName;
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

  private static File makeFile(File outDir,String fileName) {
    return new File(outDir, fileName + FILE_SUFFIX);
  }

  private static void generateXmlFile(File outFile, byte[] outBytes) throws IOException {

    BufferedOutputStream outputStream = new BufferedOutputStream(
        new FileOutputStream(outFile));
    try {
      outputStream.write(HEAD, 0, HEAD.length);
      outputStream.write(outBytes, 0, outBytes.length);
    } finally {
      outputStream.flush();
      outputStream.close();
    }
  }
}
