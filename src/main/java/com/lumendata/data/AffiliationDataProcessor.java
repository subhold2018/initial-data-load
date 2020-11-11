package com.lumendata.data;

import com.lumendata.model.Address;
import com.lumendata.model.Affiliation;
import com.lumendata.model.PartyUidData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AffiliationDataProcessor {

    private PreparedStatement preparedStatement;

    public AffiliationDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Affiliation> readAffiliationData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet affiliationData=preparedStatement.executeQuery();
            return getAddressData(affiliationData,partyUidData.getPrimaryAffId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Affiliation> getAddressData(ResultSet resultSet,String primaryId) throws SQLException {
        List<Affiliation> affiliations=new ArrayList<>();
        while (resultSet.next()){
            Affiliation affiliation=new Affiliation();
            if(primaryId.equalsIgnoreCase(resultSet.getString("ROW_ID"))) {
                affiliation.setIsPrimary("Y");
            }else{
                affiliation.setIsPrimary("N");
            }
            affiliation.setEffectiveStartDate(resultSet.getString("START_DT"));
            affiliation.setEffectiveEndDate(resultSet.getString("END_DT"));
            affiliation.setAffiliationRank(resultSet.getString("AFFL_RANK"));
            affiliation.setAffiliationCode(resultSet.getString("AFFL_STATUS_CD"));
            affiliation.setStatusDescription(resultSet.getString("AFFL_STATUS_DESC"));
            //REL_TYPE_CD
            affiliations.add(affiliation);
        }
        return affiliations;
    }
}
