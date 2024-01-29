////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ActivityUtils {

  /**
   * Sets the activity to fullscreen mode.
   *
   * @param activity
   *     the activity to be set.
   * @author Haixing Hu
   */
  public static void setFullscreen(final Activity activity) {
    // Hide title bar
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    // Hide status bar
    final Window window = activity.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    // Immersive mode allows apps to remain full screen even when the user interacts with the screen
    final View decorView = window.getDecorView();
    decorView.setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN          // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }

  public static boolean checkPermission(final Context context,
      final String permission) {
    final int result = ContextCompat.checkSelfPermission(context, permission);
    return (result == PackageManager.PERMISSION_GRANTED);
  }

  // public static boolean checkPermission(final Context context,
  //     final String permission, final boolean tryRequest) {
  //   final int result = ContextCompat.checkSelfPermission(context, permission);
  //   if (result == PackageManager.PERMISSION_GRANTED) {
  //     return true;
  //   }
  //   if (!tryRequest) {
  //     return false;
  //   }
  //   // try to request the missing permission
  //
  // }

  public static void shutdownApplication(final Activity activity) {
    final Intent intent = new Intent (activity, QuitApplicationActivity.class);
    intent.setFlags(FLAG_ACTIVITY_NEW_TASK
        | FLAG_ACTIVITY_CLEAR_TASK
        | FLAG_ACTIVITY_CLEAR_TOP);
    activity.startActivity(intent);
    activity.finish();
  }

  private static class QuitApplicationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      finish();
    }
  }
}
