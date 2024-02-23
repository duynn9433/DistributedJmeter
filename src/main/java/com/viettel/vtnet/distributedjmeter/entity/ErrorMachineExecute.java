package com.viettel.vtnet.distributedjmeter.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMachineExecute
{
  private Machine machine;
  private String errorMessage;
  private String errorCode;
}
