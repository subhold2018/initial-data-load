package com.lumendata.data;

import com.lumendata.model.PartyUidData;
import com.lumendata.model.Phone;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhoneDataProcessor {

    private PreparedStatement preparedStatement;

    public PhoneDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Phone> readPhoneData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet emilData=preparedStatement.executeQuery();
            return getEmailData(emilData,partyUidData.getPrimaryPhoneId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Phone> getEmailData(ResultSet resultSet,String primaryId) throws SQLException {
        List<Phone> phones=new ArrayList<>();
        while (resultSet.next()){
            Phone phone=new Phone();
            if(primaryId.equalsIgnoreCase(resultSet.getString("ROW_ID"))) {
                phone.setIsPrimary("Y");
            }else{
                phone.setIsPrimary("N");
            }
            phone.setPhone(resultSet.getString("ADDR"));
            phone.setUseType(resultSet.getString("NAME"));
            phone.setLogicalDeleteFlg(resultSet.getString("X_LOG_DEL_FLG"));
            phones.add(phone);
        }
        return phones;
    }
}
