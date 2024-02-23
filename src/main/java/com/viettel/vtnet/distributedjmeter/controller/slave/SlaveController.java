package com.viettel.vtnet.distributedjmeter.controller.slave;

import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import com.viettel.vtnet.distributedjmeter.service.ExecuteService;
import com.viettel.vtnet.distributedjmeter.service.TerminalExecute;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SlaveController {
  private final ExecuteService executeService;
  public SlaveController(ExecuteService executeService) {
    this.executeService = executeService;
  }

  @GetMapping("/run-slave")
  public ResponseEntity<?> run(@RequestParam("ip") String ip)
  {
    ErrorMachineExecute errorMachineExecute = executeService.runJmeter(ip);
    if (errorMachineExecute == null) {
      return ResponseEntity.ok("Jmeter is running");
    } else {
      return ResponseEntity.status(500).body("Jmeter is not running");
    }
  }
  @GetMapping("/stop-test")
  public ResponseEntity<?> stopTest() {
    ErrorMachineExecute errorMachineExecute = executeService.stopJmeter();
    if (errorMachineExecute == null) {
      return ResponseEntity.ok("Jmeter is stopped");
    } else {
      return ResponseEntity.status(500).body("Jmeter is not stopped");
    }
  }
  @GetMapping("/shutdown-test")
  public ResponseEntity<?> shutdownTest() {
    ErrorMachineExecute errorMachineExecute = executeService.shutdownJmeter();
    if (errorMachineExecute == null) {
      return ResponseEntity.ok("Jmeter is shutdown");
    } else {
      return ResponseEntity.status(500).body("Jmeter is not shutdown");
    }
  }
}
