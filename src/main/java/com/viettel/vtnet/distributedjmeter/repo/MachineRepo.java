package com.viettel.vtnet.distributedjmeter.repo;

import com.viettel.vtnet.distributedjmeter.entity.Machine;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

@Getter
@Repository
public class MachineRepo {

  private final Environment env;
  private final String masterBinPath;
  private Map<String, Machine> mapMachine;
  public MachineRepo(Environment env) {
    this.env = env;
    masterBinPath = "/home/vtn-duynn22/apache-jmeter-5.6.2/bin";
    mapMachine = Map.of(
        "192.168.122.32", Machine.builder()
            .username("ats")
            .ip("192.168.122.32")
            .jmeterPath("/home/ats/apache-jmeter-5.6.2")
            .password("1")
            .sshPort("22")
            .endpoint("http://192.168.122.32:8888")
            .build(),
        "117.1.157.113", Machine.builder()
            .username("vt_admin")
            .ip("117.1.157.113")
            .jmeterPath("/duynnopt/apache-jmeter-5.6.2")
            .password(env.getProperty("jmeter.pass113"))
            .sshPort("9022")
            .endpoint("http://1117.1.157.113:8888")
            .build(),
        "117.1.157.114", Machine.builder()
            .username("vt_admin")
            .ip("117.1.157.114")
            .jmeterPath("/duynnopt/apache-jmeter-5.6.2")
            .password(env.getProperty("jmeter.pass114"))
            .sshPort("9022")
            .endpoint("http://1117.1.157.114:8888")
            .build(),
        "117.1.157.115", Machine.builder()
            .username("vt_admin")
            .ip("117.1.157.115")
            .jmeterPath("/duynnopt/apache-jmeter-5.6.2")
            .password(env.getProperty("jmeter.pass115"))
            .sshPort("9022")
            .endpoint("http://1117.1.157.115:8888")
            .build()
    );
  }

  public void addListMachine(List<String> listIP) {
    for(String ip : listIP) {
      mapMachine.put(ip, Machine.builder()
          .username("ubuntu")
          .ip(ip)
          .jmeterPath("/home/ubuntu/apache-jmeter-5.6.2")
          .password("pemfile")
          .sshPort("9022")
          .build());
    }
  }
}
