package com.viettel.vtnet.distributedjmeter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.viettel.vtnet.distributedjmeter.entity.ErrorMachineExecute;
import com.viettel.vtnet.distributedjmeter.entity.Machine;
import com.viettel.vtnet.distributedjmeter.repo.MachineRepo;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class SyncDataServiceTest {

  @Autowired
  private MachineRepo machineRepo;

  @Autowired
  private SyncDataService syncDataServiceUnderTest;


//  @Test
  void testSyncFileFromMasterToSlaveUsePassword1() {


//    // Run the test
//    final int result = syncDataServiceUnderTest.syncFileFromMasterToSlaveUsePassword(
//        listSlaveIPFilePath, "masterFilePath");
//
//    // Verify the results
//    assertThat(result).isEqualTo(0);
  }

//  @Test
  void testSyncFileFromMasterToSlaveUsePassword2() {


    // Run the test
    ErrorMachineExecute result = syncDataServiceUnderTest.syncFileFromMasterToSlaveUsePassword("192.168.122.32",
        "/home/vtn-duynn22/apache-jmeter-5.6.2/bin/hls_test.jmx",
        "/home/ats/apache-jmeter-5.6.2/bin");

    // Verify the results
    assertThat(result).isEqualTo(null);
  }

//  @Test
  void testSyncJmeterDataUsePassword1() {
    // Setup


    // Run the test
    final List<String> result = syncDataServiceUnderTest.syncJmeterDataUsePassword(
        List.of("value"));

    // Verify the results
    assertThat(result).isEqualTo(List.of("value"));
  }

//  @Test
  void testSyncJmeterDataUsePassword2() {
    // Setup


    // Run the test
    final List<String> result = syncDataServiceUnderTest.syncJmeterDataUsePassword("192.168.122.32");

    // Verify the results
//    assertThat(result).isEqualTo(List.of("value"));
  }

//  @Test
  void syncJmeterDataUsePassword() {
//    ListErrorMachineExecute err = syncDataServiceUnderTest.syncJmeterDataUsePassword("192.168.122.32");
  }
}
