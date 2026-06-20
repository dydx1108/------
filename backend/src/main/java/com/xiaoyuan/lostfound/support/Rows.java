package com.xiaoyuan.lostfound.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

public final class Rows {
  private Rows() {
  }

  public static RowMapper<Map<String, Object>> mapRow() {
    return new RowMapper<Map<String, Object>>() {
      @Override
      public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<String, Object>();
        int count = rs.getMetaData().getColumnCount();
        for (int index = 1; index <= count; index++) {
          row.put(rs.getMetaData().getColumnLabel(index), rs.getObject(index));
        }
        return row;
      }
    };
  }
}
