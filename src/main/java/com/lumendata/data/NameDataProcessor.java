package com.lumendata.data;

import com.lumendata.model.Email;
import com.lumendata.model.Name;
import com.lumendata.model.PartyUidData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NameDataProcessor {

    private PreparedStatement preparedStatement;

    public NameDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Name> readNameData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet emilData=preparedStatement.executeQuery();
            return geNameData(emilData,partyUidData.getPrimaryNameId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Name> geNameData(ResultSet resultSet,String primaryNameId)
            throws SQLException {
        List<Name> nameData=new ArrayList<>();
        while (resultSet.next()){
            Name name=new Name();
            if(primaryNameId.equalsIgnoreCase(resultSet.getString("ROW_ID"))) {
                name.setIsPrimary("Y");
            }else{
                name.setIsPrimary("N");
            }
            name.setFirstName(resultSet.getString("FST_NAME"));
            name.setLastName(resultSet.getString("LAST_NAME"));
            name.setFullName(resultSet.getString("FULL_NAME"));
            name.setNameType(resultSet.getString("TYPE_CD"));
            name.setEffectiveStartDate(resultSet.getString("EFF_START_DT"));
            name.setLogicalDeleteFlg(resultSet.getString("X_LOG_DEL_FLG"));
            name.setRecordActiveFlag(resultSet.getString("X_REC_ACTIVE_FLG"));
            nameData.add(name);
        }
        return nameData;
    }
}
