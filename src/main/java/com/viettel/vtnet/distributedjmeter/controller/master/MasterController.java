package com.viettel.vtnet.distributedjmeter.controller.master;

import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import com.viettel.vtnet.distributedjmeter.entity.HttpErrorMessage;
import com.viettel.vtnet.distributedjmeter.entity.Machine;
import com.viettel.vtnet.distributedjmeter.repo.MachineRepo;
import com.viettel.vtnet.distributedjmeter.service.SyncDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@Log4j2
public class MasterController {

  @Autowired
  SyncDataService syncDataService;
  @Autowired
  MachineRepo machineRepo;
  private final RestTemplate restTemplate = new RestTemplate();

  @PostMapping("/run-master-concurrently")
  public ResponseEntity<?> runConcurrently(@RequestBody List<String> listSlave) {
    List<HttpErrorMessage> errList = new ArrayList<>();
    ExecutorService executor = Executors.newFixedThreadPool(listSlave.size());

    List<Future<HttpErrorMessage>> futures = new ArrayList<>();

    for (String ip : listSlave) {
      Future<HttpErrorMessage> future = executor.submit(() -> {
        return sendRequest(machineRepo.getMapMachine().get(ip).getEndpoint() + "/run-slave");
      });
      futures.add(future);
    }

    for (Future<HttpErrorMessage> future : futures) {
      try {
        HttpErrorMessage httpErrorMessage = future.get();
        if (httpErrorMessage != null) {
          errList.add(httpErrorMessage);
        }
      } catch (Exception e) {
        // Handle exception
        e.printStackTrace();
      }
    }

    executor.shutdown();

    if (errList.isEmpty()) {
      // All responses were 200 OK, perform an action here
      System.out.println("All responses were 200 OK");
      syncData(listSlave);
      //TODO: merge data

      //TODO: generate html response
      //return html dir
      return ResponseEntity.ok("Success");
    } else {
      return ResponseEntity.badRequest().body(errList);
    }
  }

  @PostMapping("/run-master")
  public ResponseEntity<?> runSequentially(@RequestBody List<String> listSlave) {
    List<HttpErrorMessage> errList = new ArrayList<>();
    for (String ip : listSlave) {
      Machine machine = machineRepo.getMapMachine().get(ip);
      HttpErrorMessage httpErrorMessage = sendRequest(
          machine.getEndpoint() + "/run-slave?ip=" + machine.getIp());
      if (httpErrorMessage != null) {
        errList.add(httpErrorMessage);
      }
    }

    if (errList.isEmpty()) {
      // All responses were 200 OK, perform an action here
      System.out.println("All responses were 200 OK");
      syncData(listSlave);
      //TODO: merge data

      //TODO: generate html response
      //return html dir
      return ResponseEntity.ok("Success");
    } else {
      return ResponseEntity.badRequest().body(errList);
    }
  }

  @PostMapping("/stop-test")
  public ResponseEntity<?> stopTest(@RequestBody List<String> listSlave) {
    return ResponseEntity.ok("Requests sent");
  }

  @PostMapping("/shutdown-test")
  public ResponseEntity<?> shutdownTest(@RequestBody List<String> listSlave) {
    return ResponseEntity.ok("Requests sent");
  }

  /**
   * sync file from master to slave
   */
  @PostMapping("/sync-file-jmeter-bin")
  public ResponseEntity<?> syncFileFromMasterToSlave(@RequestBody List<String> listSlave,
      @RequestParam("file") String file) {
    String filePath = machineRepo.getMasterBinPath() + "/" + file;
    List<ErrorMachineExecute> err = syncDataService.syncFileFromMasterToSlaveUsePassword(listSlave,
        filePath);
    if (!err.isEmpty()) {
      return ResponseEntity.badRequest().body(err);
    } else {
      return ResponseEntity.ok("Sync file from master to slave successfully");
    }
  }


  @PostMapping("/sync-test-file")
  public ResponseEntity<?> syncTestFile(@RequestBody List<String> listSlave,
      @RequestParam("filename") String filename) {
    String filePath = machineRepo.getMasterBinPath() + "/" + filename;
    log.debug("Sync file: " + filePath + " to slaves: " + listSlave);
    List<ErrorMachineExecute> listErr
        = syncDataService.syncFileFromMasterToSlaveUsePassword(listSlave, filePath);

    if (!listErr.isEmpty()) {
      return ResponseEntity.badRequest().body(listErr);
    } else {
      return ResponseEntity.ok("Sync " + filename + " from master to slave successfully");
    }
  }

  private void syncData(List<String> listSlave) {
    syncDataService.syncJmeterDataUsePassword(listSlave);
  }

  private HttpErrorMessage sendRequest(String url) {
    log.debug(url);
    try {
      // Replace with your actual request
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null,
          String.class);
      HttpStatusCode statusCode = response.getStatusCode();
      //print status code
      if (statusCode == HttpStatus.OK) {
        log.debug("Url: " + url + " - Status code: " + statusCode);
        return null;
      } else {
        log.error("Url: " + url + " - Status code: " + statusCode);
        return HttpErrorMessage.builder()
            .Url(url)
            .statusCode(statusCode)
            .build();
      }
    } catch (HttpClientErrorException e) {
      return HttpErrorMessage.builder()
          .Url(url)
          .message(e.getMessage())
          .statusCode(e.getStatusCode())
          .build();
    } catch (Exception e) {
      log.error("Url: " + url + " - Status code: " + HttpStatus.INTERNAL_SERVER_ERROR);
      return HttpErrorMessage.builder()
          .Url(url)
          .message(e.getMessage())
          .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
    }
  }
}
