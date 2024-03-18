package org.example.util;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {
  public static final String COMMANDER_IP_ADDR = "192.168.0.64";
  public static final String KAFKA_PUBLISHER_ALB_DNS = "http://192.168.0.244:8080/pub/";
  public static final String CONTAINER_CLIENT_NAME = getRandomAlphabets() + addRandomNumber();

  private static int addRandomNumber(){
    return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
  }

  private static String getRandomAlphabets(){
    return
        String.valueOf( (char)ThreadLocalRandom.current().nextInt('A', 'Z'+1) )
            + String.valueOf( (char)ThreadLocalRandom.current().nextInt('A', 'Z'+1) )
            + String.valueOf( (char)ThreadLocalRandom.current().nextInt('A', 'Z'+1) );
  }
}