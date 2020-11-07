package com.lumendata.model;

import lombok.Data;

import java.util.List;

@Data
public class Constituent {
    private PrimaryData primaryData;
    private List<Source> source;
}
