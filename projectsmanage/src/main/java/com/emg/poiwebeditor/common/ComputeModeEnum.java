package com.emg.poiwebeditor.common;

import java.io.Serializable;

public enum ComputeModeEnum implements Serializable {
  none {
    @Override
    public String getDesc() {
      return "不存在该模式";
    }

    @Override
    public int getCode() {
      return -1;
    }
  },
  line {
    @Override
    public String getDesc() {
      return "Line模式";
    }

    @Override
    public int getCode() {
      return 0;
    }
  },
  polygon {
    @Override
    public String getDesc() {
      return "Polygon模式";
    }

    @Override
    public int getCode() {
      return 1;
    }
  };

  public abstract String getDesc();

  public abstract int getCode();
}
