package com.lumendata.model;

import lombok.Data;

@Data
public class Email {
    private String email;
    private String logicalDeleteFlg;
    private String useType;
    private String isPrimary;
    private String source;
    private String sourceId;
}
