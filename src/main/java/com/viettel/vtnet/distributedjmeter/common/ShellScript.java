package com.viettel.vtnet.distributedjmeter.common;

import lombok.Getter;

@Getter
public enum ShellScript {
  BASH("bash"),
  ZSH("zsh"),
  SH("sh"),
  CSH("csh"),
  KSH("ksh"),
  ;
  private final String value;
  ShellScript(String value) {
    this.value = value;
  }
}
