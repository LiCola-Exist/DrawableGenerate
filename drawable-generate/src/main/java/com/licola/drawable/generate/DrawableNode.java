package com.licola.drawable.generate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiCola
 * @date 2018/8/17
 */
public class DrawableNode {

  String name;
  String content;
  List<DrawableNode> childNodes;

  public DrawableNode(String name, String content) {
    this.name = name;
    this.content = content;
  }

  public void addChildNode(DrawableNode childNode){
    if (childNodes==null){
      childNodes=new ArrayList<>();
    }
    childNodes.add(childNode);
  }

  public List<DrawableNode> getChildNodes() {
    return childNodes;
  }

  public String getName() {
    return name;
  }

  public String getContent() {
    return content;
  }
}
