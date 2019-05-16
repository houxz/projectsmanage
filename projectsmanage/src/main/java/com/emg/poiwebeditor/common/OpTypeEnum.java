package com.emg.poiwebeditor.common;

import java.io.Serializable;

public enum OpTypeEnum implements Serializable {
  init, //初始化
  submit, //修改中
  ready, //可发布
  published; //已发布
}
