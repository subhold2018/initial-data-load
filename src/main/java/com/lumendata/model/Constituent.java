package com.lumendata.model;

import lombok.Data;

import java.util.List;

@Data
public class Constituent {
    private PrimaryData primaryData;
    private List<Source> source;
    private List<Email> emails;
    private List<Phone> phones;
    private List<Address> addresses;
    private List<Identification> identifications;
    private List<Affiliation> affiliations;
    private List<Name> names;
    private String guid;
}
