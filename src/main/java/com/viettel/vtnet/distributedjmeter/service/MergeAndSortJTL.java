package com.viettel.vtnet.distributedjmeter.service;

import java.io.*;
import java.util.*;

public class MergeAndSortJTL {
  public static void main(String[] args) {
    // Paths to input .jtl files
    String file1Path = "/home/vtn-duynn22/apache-jmeter-5.6.2/bin/results/result_20240220140114.jtl";
    String file2Path = "/home/vtn-duynn22/apache-jmeter-5.6.2/bin/results_113/result_20240220135955.jtl";

    // Path to output .jtl file
    String outputPath = "merged_sorted.jtl";

    try {
      // Read data from both .jtl files
      List<String> file1Data = readJTL(file1Path);
      List<String> file2Data = readJTL(file2Path);

      // Merge data
      List<String> mergedData = new ArrayList<>(file1Data);
      mergedData.addAll(file2Data);

      // Sort merged data by timestamp
      Collections.sort(mergedData, new Comparator<String>() {
        @Override
        public int compare(String line1, String line2) {
          String[] parts1 = line1.split(",");
          String[] parts2 = line2.split(",");
          return parts1[0].compareTo(parts2[0]);
        }
      });

      // Write sorted data to output .jtl file
      writeJTL(outputPath, mergedData);

      System.out.println("Merged and sorted data written to " + outputPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Function to read .jtl file and return list of lines
  public static List<String> readJTL(String filePath) throws IOException {
    List<String> data = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      // Skip the header row
      br.readLine();

      String line;
      while ((line = br.readLine()) != null) {
        data.add(line);
      }
    }
    return data;
  }

  // Function to write list of lines to a .jtl file
  public static void writeJTL(String filePath, List<String> data) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
      bw.write("timeStamp,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,sentBytes,grpThreads,allThreads,URL,Latency,IdleTime,Connect,\"ulp_buffer_fill\",\"ulp_lag_time\",\"ulp_play_time\",\"ulp_lag_ratio\",\"ulp_lag_ratio_wo_bf\",\"ulp_dwn_time\",\"ulp_speed_rate\",\"ulp_actual_live_offset\",\"ulp_cdns_hit\",\"ulp_cdns_miss\"");
      bw.newLine();

      for (String line : data) {
        bw.write(line);
        bw.newLine();
      }
    }
  }
}
