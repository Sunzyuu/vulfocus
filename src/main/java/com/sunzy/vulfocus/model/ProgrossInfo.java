package com.sunzy.vulfocus.model;

import lombok.Data;

@Data
public class ProgrossInfo {
    String id;
    String status;
    double progress;
    double total;

    @Override
    public String toString() {
        return "ProgrossInfo{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                ", total=" + total +
                '}';
    }
}
