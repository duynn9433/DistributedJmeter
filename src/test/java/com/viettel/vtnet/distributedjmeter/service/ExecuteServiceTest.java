package com.viettel.vtnet.distributedjmeter.service;

import static org.junit.jupiter.api.Assertions.*;

import com.viettel.vtnet.distributedjmeter.common.JmeterScript;
import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExecuteServiceTest {
  @Autowired
  ExecuteService executeService;

//  @Test
  void testRunJmeterScript() {
    // Setup

    // Run the test
    // ErrorMachineExecute result = ExecuteService.runJmeterScript(jmeterScript);
    ErrorMachineExecute err = executeService.runJmeterScript(JmeterScript.RUN_JMETER_NON_GUI);

    // Verify the results
    assertNull(err);
  }

}