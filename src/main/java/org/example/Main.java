package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import org.example.util.Utils;

public class Main {
  public static void main(String[] args) {
    try {
      // send HTTP PUT req 5 time in 1 sec.
      // delay == 200 + (random between minus 50 ~ zero) ms
      int delayTimeMs = 200 + ThreadLocalRandom.current().nextInt(-50, 0);
      System.out.println("delayTimeMs = " + delayTimeMs);

      // make tcp connection to Commander.
      Socket socket = new Socket(Utils.COMMANDER_IP_ADDR, 8555);
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
      URL url = new URL(Utils.KAFKA_PUBLISHER_ALB_DNS);
      long finalStart = System.nanoTime();
      for (int i = 0; i < 25; i++) {
        long start = 0L;
        long end = 0L;
        try {
          System.out.println("\nreq Sent!!");
          //need a response waiting time limit equal to delayTimeMs value.

          start = System.nanoTime();
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setConnectTimeout(1000); // 1000 milliseconds = 1 sec
          conn.setReadTimeout(delayTimeMs); // 100 milliseconds
          conn.setRequestMethod("PUT");

          int responseCode = conn.getResponseCode();
          System.out.println("Response Code: " + responseCode);
          end = System.nanoTime();

          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String inputLine;
          StringBuilder response = new StringBuilder();
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          System.out.println("Response: " + response);

          conn.disconnect();

          System.out.println("end-start ms = " + (end - start)/1_000_000);

          // sleep before sending next req.
          Thread.sleep(delayTimeMs - ( (end-start)/1_000_000 ));
        } catch (SocketTimeoutException socketTimeoutException){
          System.out.println("Req " + i + " time out!!");
        }
      }//for
      long finalEnd = System.nanoTime();

      System.out.println();
      System.out.println("Total Req Send Delay Time ms : " + ((finalEnd-finalStart)/1_000_000));
      socket.getOutputStream().write(String.valueOf( (finalEnd-finalStart)/1_000_000).getBytes() );
      socket.close();
    } catch (Exception e){
      System.out.println("Error Occurred!!!");
      e.printStackTrace();
    }
  }//main
}//Main class