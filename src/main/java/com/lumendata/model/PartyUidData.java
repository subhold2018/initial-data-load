package com.lumendata.model;


import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class PartyUidData {
    private String rowId;
    private String guidId;
    private String primaryEmailId;
    private String primaryNameId;
    private String primaryAffId;
    private String primaryAddressId;
    private String primaryPhoneId;
    private String primaryIdentityId;
}
