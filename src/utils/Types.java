package utils;

import java.util.Random;

public class Types {
  public static final int INTEGER_BYTES = 4;

  public static String randomString(int length) {
    String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
    StringBuilder stringBuilder = new StringBuilder(length);
    Random random = new Random();
    for (int i = 0; i < length; ++i) {
      stringBuilder.append(chars.charAt(random.nextInt(chars.length())));
    }
    return stringBuilder.toString();
  }
}
