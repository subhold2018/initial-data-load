package com.lumendata.model;

import lombok.Data;


@Data
public class Affiliation {
    private String id;
    private String updated;
    private String updatedBy;
    private String affiliationCode;
    private String effectiveEndDate;
    private String institutionId;
    private String institution;
    private String personId;
    private String effectiveStartDate;
    private String status;
    private String statusDescription;
    private String affiliationRank;
    private String institutionName;
    private String logicalDeleteFlg;
    private String isPrimary;
    private String searchspec;
    private String operation;
}
