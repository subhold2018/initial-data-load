package com.lumendata.data;

import com.lumendata.model.Address;
import com.lumendata.model.Identification;
import com.lumendata.model.PartyUidData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IdentityDataProcessor {

    private PreparedStatement preparedStatement;

    public IdentityDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Identification> reaIdentityData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet identityData=preparedStatement.executeQuery();
            return getIdentityData(identityData,partyUidData.getPrimaryIdentityId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Identification> getIdentityData(ResultSet resultSet,String primaryNid) throws SQLException {
        List<Identification> identifications=new ArrayList<>();
        while (resultSet.next()){
            Identification identification=new Identification();
            if(primaryNid.equalsIgnoreCase(resultSet.getString("ROW_ID"))) {
                identification.setIsPrimary("Y");
            }else{
                identification.setIsPrimary("N");
            }
            identification.setCountry(resultSet.getString("ISS_COUNTRY_CD"));
            identification.setNationalIDType(resultSet.getString("TYPE_CD"));
            identification.setNationalID(resultSet.getString("IDENTITY_DOC_NUM"));
            identification.setEffectiveStartDate(resultSet.getString("EFF_START_DT"));
            identification.setLogicalDeleteFlg(resultSet.getString("X_LOG_DEL_FLG"));
            identifications.add(identification);
        }
        return identifications;
    }
}
