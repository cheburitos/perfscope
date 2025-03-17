package com.perfscope;

import com.perfscope.db.DatabaseLoader;
import com.perfscope.model.CommData;

import java.sql.SQLException;
import java.util.List;

public class TestJooq {

    public static void main(String[] args) throws SQLException {
        DatabaseLoader databaseLoader = new DatabaseLoader();
        List<CommData> commData = databaseLoader.loadCommsWithCalls("examples/pt_example");
        System.out.println(commData);
    }
}
