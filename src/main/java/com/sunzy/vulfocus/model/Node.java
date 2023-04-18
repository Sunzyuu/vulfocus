package com.sunzy.vulfocus.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Node {
    String id;
    String name;
    String type;
    ArrayList<String> containNodes;
    int x;
    int y;

    String icon;
    String width;
    String height;
    String initW;
    String initH;
    String classType;
    String isLeftConnectShow;
    String isRightConnectShow;
    String isSelect;

//    Image attrs;


}
