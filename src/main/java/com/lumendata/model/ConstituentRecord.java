package com.lumendata.model;

import lombok.Data;

import java.util.List;

@Data
public class ConstituentRecord {
    private PrimaryData primaryData;
    private List<Name> names;
    private List<Email> emails;
    private List<Phone> phones;
    private List<Address> addresses;
    private List<Affiliation> affiliations;
    private List<Identification> identifications;
    private Source source;
    private String guid;
}
