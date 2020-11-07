package com.lumendata.data;

import com.lumendata.model.PartyUidData;
import com.lumendata.model.PrimaryData;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrimaryDataProcessor {
    private String readPrimaryData;
    private PreparedStatement preparedStatement;

    public PrimaryDataProcessor(String sql,DataSource dataSource){
        this.readPrimaryData=sql;
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(readPrimaryData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public PrimaryData readPrimaryData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
           ResultSet primaryData=preparedStatement.executeQuery();
           return getPrimaryData(primaryData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private PrimaryData getPrimaryData(ResultSet resultSet) throws SQLException {
        PrimaryData primaryData=new PrimaryData();
        while (resultSet.next()){
            primaryData.setFirstName(resultSet.getString("FST_NAME"));
            primaryData.setMiddleName(resultSet.getString("MID_NAME"));
            primaryData.setLastName(resultSet.getString("LAST_NAME"));
            primaryData.setDob(resultSet.getString("BIRTH_DT"));
            primaryData.setEmail(resultSet.getString("EMAIL_ADDR"));
            primaryData.setHomePhone(resultSet.getString("HOME_PH_NUM"));
            primaryData.setMaritalStatus(resultSet.getString("MARITAL_STAT_CD"));
            primaryData.setDeathDate(resultSet.getString("DEATH_DT"));
            primaryData.setSsn(resultSet.getString("SOC_SECURITY_NUM"));
            primaryData.setGender(resultSet.getString("SEX_MF"));
            primaryData.setPlaceOfBirth(resultSet.getString("PLACE_OF_BIRTH"));
            return primaryData;
        }
        return null;
    }
}
