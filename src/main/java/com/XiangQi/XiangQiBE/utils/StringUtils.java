package com.XiangQi.XiangQiBE.utils;

public class StringUtils {
  public static String replaceCharAt(String str, String replace, int index) {
    return str.substring(0, index) + replace + str.substring(index + 1);
  }

  public static boolean isStringEmpty(String str) {
    return str == null || str.isEmpty();
  }
}
