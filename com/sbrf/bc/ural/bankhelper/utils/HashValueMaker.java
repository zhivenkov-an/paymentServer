package com.sbrf.bc.ural.bankhelper.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import sun.misc.BASE64Encoder;

public class HashValueMaker
{
  private static Random random = new Random(System.currentTimeMillis());
  private static char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
  private static HashValueMaker instance = null;
  private BASE64Encoder encoder;

  private String makePasswordHashValue(String salt, String login, String clearTextPassword)
  {
    try
    {
      String composed = salt + login + clearTextPassword;
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] buf = digest.digest(composed.getBytes("ISO8859-1"));
      String result = salt + this.encoder.encode(buf);
      return result;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }return null;
  }

  public HashValueMaker()
  {
    this.encoder = new BASE64Encoder();
  }

  public String makeRandomSalt() {
    char a = chars[random.nextInt(chars.length)];
    char b = chars[random.nextInt(chars.length)];
    String result = "" + a;
    result = result + b;
    return result;
  }

  public String makePasswordHashValue(String login, String password) {
    return makePasswordHashValue(makeRandomSalt(), login, password);
  }

  public boolean checkPassword(String login, String clearTextPassword, String encodedPassword) {
    String salt = encodedPassword.substring(0, 2);
    return makePasswordHashValue(salt, login, clearTextPassword).equals(encodedPassword);
  }

  public static HashValueMaker getInstance() {
    if (instance == null) {
      instance = new HashValueMaker();
    }
    return instance;
  }
}