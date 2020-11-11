package com.lumendata.model;

import lombok.Data;

@Data
public class Phone {
    private String phone;
    private String logicalDeleteFlg;
    private String useType;
    private String isPrimary;
}
