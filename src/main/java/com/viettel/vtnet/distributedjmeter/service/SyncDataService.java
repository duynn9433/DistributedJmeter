package com.viettel.vtnet.distributedjmeter.service;

import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import com.viettel.vtnet.distributedjmeter.entity.Machine;
import com.viettel.vtnet.distributedjmeter.repo.MachineRepo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SyncDataService {
  MachineRepo machineRepo;

  public SyncDataService(MachineRepo machineRepo) {
    this.machineRepo = machineRepo;
  }

  /**
   * for general use case
   * */
  public List<ErrorMachineExecute> syncFileFromMasterToSlaveUsePassword(
      Map<String, String> listSlaveIPFilePath, String masterFilePath) {
    List<ErrorMachineExecute> errorList = new ArrayList<>();
    //for all map
    for (Map.Entry<String, String> entry : listSlaveIPFilePath.entrySet()) {
      log.debug("Syncing file to slave: " + entry.getKey() + " " + entry.getValue());
      String ip = entry.getKey();
      String slaveFilePath = entry.getValue();
      ErrorMachineExecute err = syncFileFromMasterToSlaveUsePassword(ip, masterFilePath, slaveFilePath);
      if(err != null) {
        log.error("Error when syncing file to slave: " + ip);
        errorList.add(err);
      }
    }
    return errorList;
  }

  /**
   * for sync jmeter test file
   * */
  public List<ErrorMachineExecute> syncFileFromMasterToSlaveUsePassword(
      List<String> listSlaveIp, String masterFilePath) {
    List<ErrorMachineExecute> errorList = new ArrayList<>();
    //for all map
    for (String ip : listSlaveIp) {
      log.debug("Syncing file to slave: " + ip);
      Machine machine = machineRepo.getMapMachine().get(ip);
      ErrorMachineExecute err = syncFileFromMasterToSlaveUsePassword(
          ip, masterFilePath,
          machine.getJmeterSlaveBinPath());
      if(err != null) {
        log.error("Error when syncing file to slave: " + ip);
        errorList.add(err);
      }
    }
    return errorList;
  }


  /**
   * Sync file from master to slave with password like: non-GUI.sh
   * @param ip slave ip: 192.168.122.32
   * @param masterFilePath master file path: /home/vtn-duynn22/apache-jmeter-5.6.2/bin/non-GUI.sh
   * @param slaveFilePath slave file path: /home/ats/apache-jmeter-5.6.2/bin/non-GUI.sh
   * */
  public ErrorMachineExecute syncFileFromMasterToSlaveUsePassword(
      String ip, String masterFilePath, String slaveFilePath)
  {
    Machine machine = machineRepo.getMapMachine().get(ip);
    if(machine == null) {
      throw new RuntimeException("Machine not found");
    }
    //copy with scp
    ProcessBuilder processBuilder = new ProcessBuilder("sshpass",
        "-p", machine.getPassword(),
        "scp", "-P", machine.getSshPort(),
        masterFilePath,
        slaveFilePath);
    log.debug("CMD: " + processBuilder.command().toString());
    try {
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      log.debug("CONSOLE OUTPUT sync file " + ip);
      String line;
      while ((line = reader.readLine())  != null) {
        log.debug(line);
      }

      int exitCode = process.waitFor();
      log.debug("\nExited with error code : " + exitCode);
      if (exitCode != 0) {
        return ErrorMachineExecute.builder()
            .machine(machine)
            .errorCode(String.valueOf(exitCode))
            .errorMessage("Error when syncing file from master to slave with password")
            .build();
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * file from slave to master
   * */
  public List<String> syncJmeterDataUsePassword(List<String> listSlaveIP) {
    List<String> res = new ArrayList<>();
    for (String ip : listSlaveIP) {
      log.debug("Syncing jmeter data from slave: " + ip);
      List<String> temp = syncJmeterDataUsePassword(ip);
      res.addAll(temp);
    }
    return res;
  }

  /**
   * file jmeter in results from slave to master
   * @param ip slave ip:
   * */
  public List<String> syncJmeterDataUsePassword(String ip){
    Machine machine = machineRepo.getMapMachine().get(ip);
    if(machine == null) {
      throw new RuntimeException("Machine not found");
    }
    List<String> res = new ArrayList<>();
    ProcessBuilder processBuilder = new ProcessBuilder(
        "sshpass",
        "-p",
        machine.getPassword(),
        "rsync",
        "-e",
        "ssh -p " + machine.getSshPort(),
        "-rvc",
        "--delete",
        machine.getJmeterSlaveResultPath(),
        machineRepo.getMasterBinPath()
    );
//    ProcessBuilder processBuilder = new ProcessBuilder("sshpass " +
//        "-p " + machine.getPassword() +
//        " rsync " + "-e " +
//        "\"ssh -p " + machine.getSshPort() + "\" -rvc --delete " +
//        machine.getJmeterSlaveResultPath() + " " +
//        machineRepo.getMasterResultPath())
//        ;
    log.debug("CMD: " + processBuilder.command().toString());
    try {
      Process process = processBuilder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      log.debug("CONSOLE OUTPUT sync jmeter data " + ip);
      while ((line = reader.readLine())  != null) {
        log.debug(line);
        if(line.contains(".jtl")) {
          res.add(line);
        }
      }

      int exitCode = process.waitFor();
      log.debug("\nExited with error code : " + exitCode);
      if(exitCode == 0) {
        return res;
      } else {
        return null;
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

}
