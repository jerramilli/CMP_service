package com.wf.cmp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CMPRequestBody {

  @JsonProperty("payload")
  private List<Object> payload;

  public List<Object> getPayload() {
    return payload;
  }

  public void setPayload(List<Object> payload) {
    this.payload = payload;
  }
}
