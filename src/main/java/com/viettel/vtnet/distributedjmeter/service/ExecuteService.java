package com.viettel.vtnet.distributedjmeter.service;

import static com.viettel.vtnet.distributedjmeter.common.GetLinuxSystemInfo.*;

import com.viettel.vtnet.distributedjmeter.common.JmeterScript;
import com.viettel.vtnet.distributedjmeter.common.ShellScript;
import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import com.viettel.vtnet.distributedjmeter.entity.Machine;
import com.viettel.vtnet.distributedjmeter.repo.MachineRepo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * for slave
 */
@Service
@Log4j2
public class ExecuteService {

  private Machine localMachine;

  public ExecuteService(MachineRepo machineRepo) {
    //get local machine information
    List<String> ipAddresses = getListSystemIP();
    for (String ip : ipAddresses) {
      Machine machine = machineRepo.getMapMachine().get(ip);
      if (machine != null) {
        localMachine = machine;
        break;
      }
    }

  }

  public ErrorMachineExecute runJmeterScript(JmeterScript jmeterScript, String... args) {
    if (!isLinux()) {
      throw new RuntimeException("Only support Linux");
    }
    ProcessBuilder builder = new ProcessBuilder(ShellScript.BASH.getValue(),
        jmeterScript.getValue());
    //add args to builder command
    for (String arg : args) {
      builder.command().add(arg);
    }

    builder.directory(new File(getDefaultSystemJmeterBin()));
    try {
      Process process = builder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        log.debug(line);
      }

      int exitCode = process.waitFor();
      log.debug("\nExited with error code : " + exitCode);
      if (exitCode != 0) {
        return ErrorMachineExecute.builder()
            .errorCode(String.valueOf(exitCode))
            .machine(localMachine)
            .errorMessage("Error when running jmeter script:" + jmeterScript)
            .build();
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public ErrorMachineExecute runJmeter(String ip) {
    return runJmeterScript(JmeterScript.RUN_JMETER_NON_GUI, ip);
  }

  public ErrorMachineExecute stopJmeter() {
    return runJmeterScript(JmeterScript.STOP_TEST_JMETER);
  }

  public ErrorMachineExecute shutdownJmeter() {
    return runJmeterScript(JmeterScript.SHUTDOWN_TEST_JMETER);
  }

}
