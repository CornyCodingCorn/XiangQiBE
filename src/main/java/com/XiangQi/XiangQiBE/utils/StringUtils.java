package com.XiangQi.XiangQiBE.utils;

public class StringUtils {
  public static String replaceCharAt(String str, char replace, int index) {
    char[] arr = str.toCharArray();
    arr[index] = replace;
    return String.valueOf(arr);
  }

  public static boolean isStringEmpty(String str) {
    return str == null || str.isEmpty();
  }
}
