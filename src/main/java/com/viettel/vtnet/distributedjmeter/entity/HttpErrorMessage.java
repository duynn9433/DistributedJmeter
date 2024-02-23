package com.viettel.vtnet.distributedjmeter.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
public class HttpErrorMessage {
  private String message;
  private HttpStatusCode statusCode;
  private String Url;
}
