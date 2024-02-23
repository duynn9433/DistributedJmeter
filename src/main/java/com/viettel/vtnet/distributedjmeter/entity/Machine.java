package com.viettel.vtnet.distributedjmeter.entity;

import com.viettel.vtnet.distributedjmeter.common.ShellScript;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Machine {
  private String username;
  private String ip;
  private String password;
  private String sshPort;
  private String jmeterPath;
  private String endpoint;
  private String pathToKeyFile;
  private ShellScript shellScript = ShellScript.BASH;

  public String getJmeterSlaveResultPath(){
    return this.getSshSlavePrefix() + jmeterPath + "/bin/results";
  }
  public String getJmeterSlaveBinPath(){
    return this.getSshSlavePrefix() + jmeterPath + "/bin";
  }
  public String getSshSlavePrefix(){
    return username + "@" + ip + ":";
  }


}
