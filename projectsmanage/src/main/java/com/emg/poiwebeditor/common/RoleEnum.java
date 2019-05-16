package com.emg.poiwebeditor.common;

import java.io.Serializable;

public enum RoleEnum implements Serializable {
  none {
    public String getRoleInCN() {
      return "尚未设定模式";
    }
  },
  edit {
    public String getRoleInCN() {
      return "编辑模式";
    }
  },
  cace {

    public String getRoleInCN() {
      return "改错模式";
    }
  },
  cmce {
    public String getRoleInCN() {
      return "改错模式";
    }
  },
  view {
    public String getRoleInCN() {
      return "浏览模式";
    }
  };

  public abstract String getRoleInCN();
}
