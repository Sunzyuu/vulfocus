package com.sunzy.vulfocus.model.dto;

import com.sunzy.vulfocus.model.po.ImageInfo;
import lombok.Data;

import java.util.Map;

@Data
public class ImageDTO extends ImageInfo {
    private Map<String, Object> status;
}
