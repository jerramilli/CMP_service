package com.wf.cmp.model;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CMPRecords {
    @JsonProperty("count")
    private int count;
    @JsonProperty("payload")
    private List<Object> payload;

    public CMPRecords(){

    }

    public CMPRecords(List<Hit<Object>> payload) {
        this.payload = payload.stream().map(o -> (Object)o).collect(Collectors.toList());
        this.count = payload.size();
    }

    public CMPRecords(Object payload) {
        this.payload = new ArrayList<>();
        this.payload.add(payload);
        this.count = 1;
    }

    public List<Object> getPayload() {
        return payload;
    }

    public void setPayload(List<Object> payload) {
        this.payload = payload;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
