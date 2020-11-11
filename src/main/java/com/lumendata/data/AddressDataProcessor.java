package com.lumendata.data;

import com.lumendata.model.Address;
import com.lumendata.model.PartyUidData;
import com.lumendata.model.Phone;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddressDataProcessor {

    private PreparedStatement preparedStatement;

    public AddressDataProcessor(String sql, DataSource dataSource) {
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Address> readAddressData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet emilData=preparedStatement.executeQuery();
            return getAddressData(emilData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Address> getAddressData(ResultSet resultSet) throws SQLException {
        List<Address> addresses=new ArrayList<>();
        while (resultSet.next()){
            Address address=new Address();
            address.setState(resultSet.getString("STATE"));
            address.setCity(resultSet.getString("CITY"));
            address.setCountry(resultSet.getString("COUNTRY"));
            address.setPostalCode(resultSet.getString("ZIPCODE"));
            address.setAddressType(resultSet.getString("RELATION_TYPE_CD"));
            address.setRecordActiveFlag(resultSet.getString("ACTIVE_FLG"));
            address.setStreetAddress(resultSet.getString("ADDR_NAME"));
            address.setStreetAddress2(resultSet.getString("ADDR_LINE_2"));
            addresses.add(address);
        }
        return addresses;
    }
}
