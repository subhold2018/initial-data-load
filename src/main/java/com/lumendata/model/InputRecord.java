package com.lumendata.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InputRecord {
    private List<Constituent> customers;
    private Map<String,List<String>> guids;
}
