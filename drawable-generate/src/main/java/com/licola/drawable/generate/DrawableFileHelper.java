package com.licola.drawable.generate;

import android.support.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author LiCola
 * @date 2018/8/16
 */
public class DrawableFileHelper {

  private static final String FILE_SUFFIX = ".xml";

  private static final byte[] HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes();
  private static final byte[] END="\n</shape>".getBytes();

  @Nullable
  static File checkFile(File outFile,boolean replace) {
    if (outFile.exists()) {
      return replace ? outFile : null;
    }
    return outFile;
  }

  static File makeFile(File outDir,String fileName) {
    return new File(outDir, fileName + DrawableFileHelper.FILE_SUFFIX);
  }

  static void generateXmlFile(File outFile, byte[] outBytes) throws IOException {

    BufferedOutputStream outputStream = new BufferedOutputStream(
        new FileOutputStream(outFile));
    try {
      outputStream.write(DrawableFileHelper.HEAD, 0, DrawableFileHelper.HEAD.length);
      outputStream.write(outBytes, 0, outBytes.length);
      outputStream.write(DrawableFileHelper.END, 0, DrawableFileHelper.END.length);
    } finally {
      outputStream.flush();
      outputStream.close();
    }
  }
}
