package com.licola.drawable.generate;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

  @Test
  public void useAppContext() throws IOException {
    // Context of the app under test.
    Context appContext = InstrumentationRegistry.getTargetContext();

    assertEquals("com.licola.drawable.generate.test", appContext.getPackageName());

    Resources resources = appContext.getResources();
    String resourceName = resources.getResourceName(R.color.black_normal);
  }
}
