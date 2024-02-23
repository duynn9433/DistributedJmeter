package com.viettel.vtnet.distributedjmeter.common;

import lombok.Getter;

@Getter
public enum JmeterScript {
  RUN_JMETER("jmeter.sh"),
  STOP_TEST_JMETER("stoptest.sh"),
  SHUTDOWN_TEST_JMETER("shutdown.sh"),
  RUN_JMETER_NON_GUI("non-GUI.sh"),
  ;


  private final String value;
  JmeterScript(String value) {
    this.value = value;
  }
}
