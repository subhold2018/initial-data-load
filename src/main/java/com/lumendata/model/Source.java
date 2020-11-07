package com.lumendata.model;

import lombok.Data;

@Data
public class Source {
    private String source;
    private String sourceId;
    private String createdDate;
    private String updatedDate;
    private String operation;
    private String guid;
}
