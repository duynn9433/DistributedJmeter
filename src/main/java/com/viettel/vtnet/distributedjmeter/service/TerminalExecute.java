package com.viettel.vtnet.distributedjmeter.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class TerminalExecute {

  public static void main(String[] args) {
    runJmeter();
  }
  public static int runJmeter() {
    executeCommand("non-GUI.sh");
    return 0;
  }

  private static String getWorkdir(){
    return System.getProperty("user.home")+ "/apache-jmeter-5.6.2/bin";
  }
  private static void executeCommand(String commandS) {
//    String osPassword = System.getenv("OS_PASSWORD");
    if (!isLinux()) {
      throw new RuntimeException("Only support Linux");
    }
    ProcessBuilder builder = new ProcessBuilder();
//    builder.command("sh", "-c", commandS);
    builder.command("bash", commandS);
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
        new StreamGobbler(inputStream, processOutputConsole(inputStream));
    Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
    try {
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException("Exit code: " + exitCode + " when execute command: " + commandS);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    finally {
      future.cancel(true);
    }
  }
  private static Consumer<String> processOutputConsole(InputStream inputStream) {
    //check each line and print to console
    return line -> {
      if(line != null && !line.isEmpty() && line.contains("end of run")) {
        System.out.println("Call to master: " + line);
      }
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
  /**
   * @Dedicated for print data from terminal
   * */
  private static class StreamGobbler implements Runnable {

    private InputStream inputStream;
    private Consumer<String> consumer;
    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> {
        consumer.accept(line);
      });
    }
  }
}
