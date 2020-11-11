package com.lumendata.data;

import com.lumendata.model.Email;
import com.lumendata.model.PartyUidData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailDataProcessor {

    private PreparedStatement preparedStatement;

    public EmailDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Email> readEmailData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet emilData=preparedStatement.executeQuery();
            return getEmailData(emilData,partyUidData.getPrimaryEmailId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Email> getEmailData(ResultSet resultSet,String primaryEmailId)
            throws SQLException {
        List<Email> emailData=new ArrayList<>();
        while (resultSet.next()){
            Email email=new Email();
            if(primaryEmailId.equalsIgnoreCase(resultSet.getString("ROW_ID"))) {
                email.setIsPrimary("Y");
            }else{
                email.setIsPrimary("N");
            }
            email.setEmail(resultSet.getString("ADDR"));
            email.setUseType(resultSet.getString("NAME"));
            email.setLogicalDeleteFlg(resultSet.getString("X_LOG_DEL_FLG"));
            emailData.add(email);
        }
        return emailData;
    }
}
