package com.viettel.vtnet.distributedjmeter.service;

import com.viettel.vtnet.distributedjmeter.common.ShellScript;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class TerminalExecute {
  static Map<String, String> mapIPPassword = Map.of(
      "32","1",
      "113","Jgv_113H3J!",
      "115","Ora_11574J!");

//  public static void main(String[] args) {
//    runJmeter();
//  }
  public static int runJmeter() {
    return executeCommand("non-GUI.sh");
  }

  private static String getWorkdir(){
    return System.getProperty("user.home")+ "/apache-jmeter-5.6.2/bin";
  }
  private static int executeCommand(String commandS) {
//    String osPassword = System.getenv("OS_PASSWORD");
    if (!isLinux()) {
      throw new RuntimeException("Only support Linux");
    }
    ProcessBuilder builder = new ProcessBuilder();
//    builder.command("sh", "-c", commandS);
    builder.command(String.valueOf(ShellScript.BASH), commandS);
    builder.directory(new File(getWorkdir()));
    Process process = null;
    OutputStream outputStream = null;
    InputStream inputStream = null;
    try {
      process = builder.start();
      inputStream = process.getInputStream();
      outputStream = process.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    //print data
    StreamGobbler streamGobbler =
        new StreamGobbler(inputStream, processOutputConsole(inputStream,outputStream));
    Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);

    try {
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException("Exit code: " + exitCode + " when execute command: " + commandS);
      }
      return exitCode;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    finally {
      future.cancel(true);
    }
  }
  private static Consumer<String> processOutputConsole(InputStream inputStream,
      OutputStream outputStream) {
    //check each line and print to console
    return line -> {
      if(line != null && line.contains("end of run")) {
        System.out.println("Call to master: " + line);
      } else if (line != null && line.contains("ats@192.168.122.32's password:")) {
        //send password to console
        String password = "1"; // Replace with your actual password
        try {
          outputStream.write((password + "\n").getBytes());
          outputStream.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

      }
//      else if (line != null && line.contains("vt_admin@117.1.157.113's password:")) {
//        //send password to console
//        String password = "Jgv_113H3J!"; // Replace with your actual password
//        try {
//          outputStream.write((password + "\n").getBytes());
//          outputStream.flush();
//        } catch (IOException e) {
//          throw new RuntimeException(e);
//        }
//
//      } else if (line != null && line.contains("vt_admin@117.1.157.115's password")) {
//        System.out.println("Call to slave: " + line);
//        String password = "Ora_11574J!"; // Replace with your actual password
//        try {
//          outputStream.write((password + "\n").getBytes());
//          outputStream.flush();
//        } catch (IOException e) {
//          throw new RuntimeException(e);
//        }
//      }
      System.out.println(line);
    };

  }

  /**
   * Check OS is Linux
   * */
  private static boolean isLinux() {
    return System.getProperty("os.name")
        .toLowerCase().startsWith("linux");
  }

  private record StreamGobbler(InputStream inputStream, Consumer<String> consumer) implements
      Runnable {

    @Override
      public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
      }
    }
}
