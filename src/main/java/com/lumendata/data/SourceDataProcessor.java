package com.lumendata.data;

import com.lumendata.model.PartyUidData;
import com.lumendata.model.PrimaryData;
import com.lumendata.model.Source;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SourceDataProcessor {
    private String sourceData;
    private PreparedStatement preparedStatement;

    public SourceDataProcessor(String sql, DataSource dataSource){
        this.sourceData=sql;
        try {
            preparedStatement=dataSource.getConnection()
                    .prepareStatement(sourceData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public List<Source> readSourceData(PartyUidData partyUidData){
        try {
            preparedStatement.setString(1,partyUidData.getRowId());
            ResultSet primaryData=preparedStatement.executeQuery();
            return getSourceData(primaryData);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private List<Source> getSourceData(ResultSet resultSet) throws SQLException {
        List<Source> sources = new ArrayList<>();
        while (resultSet.next()) {
            Source source = new Source();
            source.setSourceId(resultSet.getString("EXT_CUST_ID1"));
            source.setSource(resultSet.getString("CIF_EXT_SYST_ID"));
            sources.add(source);
        }
        return sources;
    }
}
