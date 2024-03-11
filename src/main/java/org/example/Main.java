package org.example;

import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;
import org.example.util.Utils;

public class Main {
  public static void main(String[] args) {
    try (Socket socket = new Socket(Utils.COMMANDER_IP_ADDR, 8555);) {
      // send HTTP PUT req 5 time in 1 sec.
      // delay == 200 + (random between minus 50 ~ zero) ms
      int delayTimeMs = 200 + ThreadLocalRandom.current().nextInt(-50, 0);

      // make tcp connection to Commander.
      socket.getOutputStream().write(Utils.CONTAINER_CLIENT_NAME.getBytes());

      // wait for test start command
      InputStream inputStream = socket.getInputStream();
      byte[] resArr = new byte[10];
      while(true){
        if(inputStream.read(resArr) > 0){
          System.out.println("Req Send Command Received!!");
          break;
        }
      }

      // send PUT request to Kafka Cluster 25 times.
      for (int i = 0; i < 25; i++) {
        System.out.println("req Sent!!");
        /*
        need a response waiting time limit equal to delayTimeMs value.
        * */
        Thread.sleep(delayTimeMs);
      }

      System.out.println("Container Client " + Utils.CONTAINER_CLIENT_NAME + " finished.");
    } catch (Exception e){
      System.out.println("Error Occurred!!!");
      e.printStackTrace();
    }
  }//main
}//Main class