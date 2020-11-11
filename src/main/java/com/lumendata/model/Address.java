package com.lumendata.model;

import lombok.Data;

@Data
public class Address {
    private String city;
    private String country;
    private String addressType;
    private String postalCode;
    private String state;
    private String logicalDeleteFlg;
    private String isPrimary;
    private String streetAddress;
    private String streetAddress2;
    private String streetAddress3;
    private String streetAddress4;
    private String asmCountry;
    private String recordActiveFlag;
}