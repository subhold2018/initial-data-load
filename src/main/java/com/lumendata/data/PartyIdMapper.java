package com.lumendata.data;

import com.lumendata.model.PartyUidData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class PartyIdMapper implements RowMapper<PartyUidData> {

    @Override
    public PartyUidData mapRow(ResultSet rs, int rowNum) throws SQLException {
        PartyUidData partyUidData=new PartyUidData();
        partyUidData.setGuidId(rs.getString("guidId"));
        log.info("Reader-mapping partyId={}",partyUidData.getGuidId());
        return partyUidData;
    }
}
