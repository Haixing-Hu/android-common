////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2017 - 2024.
//    Nanjing Smart Medical Investment Operation Service Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package cn.njzhyl.android.commons.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AssetUtils {

  private static final int BUFFER_SIZE = 16384;

  /**
   * Loads a properties file from the assets directory.
   * <p>
   * This function assumes that the charset of the properties file is UTF-8.
   *
   * @param context
   *     the context.
   * @param path
   *     the path of the properties file, relative to the `assets` directory.
   * @return
   *     the loaded properties.
   * @throws RuntimeException
   *     if any I/O error occurs.
   * @author Haixing Hu
   */
  public static Properties loadProperties(final Context context, final String path) {
    return loadProperties(context, path, UTF_8);
  }

  /**
   * Loads a properties file from the assets directory.
   *
   * @param context
   *     the context.
   * @param path
   *     the path of the properties file, relative to the `assets` directory.
   * @param charset
   *     the charset of the properties file.
   * @return
   *     the loaded properties.
   * @throws RuntimeException
   *     if any I/O error occurs.
   * @author Haixing Hu
   */
  public static Properties loadProperties(final Context context, final String path,
      final Charset charset) {
    final AssetManager assets = context.getAssets();
    final Properties properties = new Properties();
    try (final InputStream in = assets.open(path)) {
      final InputStreamReader reader = new InputStreamReader(in, charset);
      properties.load(reader);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  /**
   * Loads the content of a file in the assets directory as a string.
   * <p>
   * This function assumes that the charset of the file is UTF-8.
   *
   * @param context
   *     the context.
   * @param path
   *     the path of the file, relative to the `assets` directory.
   * @return
   *     the content of the file as a string.
   * @throws RuntimeException
   *     if any I/O error occurs.
   * @author Haixing Hu
   */
  public static String loadString(final Context context, final String path) {
    return loadString(context, path, UTF_8);
  }


  /**
   * Loads the content of a file in the assets directory as a string.
   *
   * @param context
   *     the context.
   * @param path
   *     the path of the file, relative to the `assets` directory.
   * @param charset
   *     the charset of the file.
   * @return
   *     the content of the file as a string.
   * @throws RuntimeException
   *     if any I/O error occurs.
   * @author Haixing Hu
   */
  public static String loadString(final Context context, final String path,
      final Charset charset) {
    final AssetManager assets = context.getAssets();
    try (final InputStream in = assets.open(path)) {
      final InputStreamReader reader = new InputStreamReader(in, charset);
      final StringBuilder builder = new StringBuilder();
      final char[] buffer = new char[BUFFER_SIZE];
      int n;
      while ((n = reader.read(buffer)) != -1) {
        builder.append(buffer, 0, n);
      }
      return builder.toString();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
