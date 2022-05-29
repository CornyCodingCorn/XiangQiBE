package com.XiangQi.XiangQiBE.utils;

import java.util.LinkedList;

public class LinkedListUtils {
  public static <T> void AddIfNotNull(LinkedList<T> list, T object) {
    if (object != null) list.add(object);
  }
}
