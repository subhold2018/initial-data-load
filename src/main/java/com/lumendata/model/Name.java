package com.lumendata.model;

import lombok.Data;

@Data
public class Name {
    private String firstName;
    private String lastName;
    private String fullName;
    private String recordActiveFlag;
    private String logicalDeleteFlg;
    private String effectiveStartDate;
    private String isPrimary;
    private String nameType;
}
