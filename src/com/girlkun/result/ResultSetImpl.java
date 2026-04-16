package com.girlkun.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ResultSet implementation that copies data from a forward-only cursor so we
 * can safely iterate without requiring scrollable result sets.
 */
public class ResultSetImpl implements GirlkunResultSet {

    private final List<Map<String, Object>> rows;
    private final List<Object[]> values;
    private final String[] columnNames;
    private int index = -1;

    public ResultSetImpl(ResultSet rs) throws SQLException {
        this.rows = new ArrayList<>();
        this.values = new ArrayList<>();

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        this.columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            this.columnNames[i - 1] = meta.getColumnLabel(i).toLowerCase();
        }

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            Object[] valueRow = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                Object val = rs.getObject(i);
                valueRow[i - 1] = val;
                row.put(this.columnNames[i - 1], val);
            }
            this.rows.add(row);
            this.values.add(valueRow);
        }
    }

    @Override
    public void dispose() {
        if (!rows.isEmpty()) {
            rows.clear();
        }
        if (!values.isEmpty()) {
            values.clear();
        }
        index = -1;
    }

    @Override
    public boolean next() throws Exception {
        ensureData();
        if (index + 1 < rows.size()) {
            index++;
            return true;
        }
        return false;
    }

    @Override
    public boolean first() throws Exception {
        ensureData();
        if (rows.isEmpty()) {
            return false;
        }
        index = 0;
        return true;
    }

    @Override
    public boolean gotoResult(int idx) throws Exception {
        ensureData();
        if (idx < 0 || idx >= rows.size()) {
            throw new Exception("Index out of bound");
        }
        index = idx;
        return true;
    }

    @Override
    public boolean gotoFirst() throws Exception {
        return first();
    }

    @Override
    public void gotoBeforeFirst() {
        index = -1;
    }

    @Override
    public boolean gotoLast() throws Exception {
        ensureData();
        if (rows.isEmpty()) {
            return false;
        }
        index = rows.size() - 1;
        return true;
    }

    @Override
    public int getRows() throws Exception {
        ensureData();
        return rows.size();
    }

    @Override
    public byte getByte(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).byteValue();
        }
        return Byte.parseByte(String.valueOf(val));
    }

    @Override
    public byte getByte(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).byteValue();
        }
        return Byte.parseByte(String.valueOf(val));
    }

    @Override
    public int getInt(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return Integer.parseInt(String.valueOf(val));
    }

    @Override
    public int getInt(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return Integer.parseInt(String.valueOf(val));
    }

    @Override
    public short getShort(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }
        return Short.parseShort(String.valueOf(val));
    }

    @Override
    public short getShort(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }
        return Short.parseShort(String.valueOf(val));
    }

    @Override
    public float getFloat(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).floatValue();
        }
        return Float.parseFloat(String.valueOf(val));
    }

    @Override
    public float getFloat(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).floatValue();
        }
        return Float.parseFloat(String.valueOf(val));
    }

    @Override
    public double getDouble(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return Double.parseDouble(String.valueOf(val));
    }

    @Override
    public double getDouble(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return Double.parseDouble(String.valueOf(val));
    }

    @Override
    public long getLong(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return Long.parseLong(String.valueOf(val));
    }

    @Override
    public long getLong(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        return Long.parseLong(String.valueOf(val));
    }

    @Override
    public String getString(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        return String.valueOf(val);
    }

    @Override
    public String getString(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        return String.valueOf(val);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        if (val instanceof Number) {
            return ((Number) val).intValue() == 1;
        }
        return Boolean.parseBoolean(String.valueOf(val));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        if (val instanceof Number) {
            return ((Number) val).intValue() == 1;
        }
        return Boolean.parseBoolean(String.valueOf(val));
    }

    @Override
    public Object getObject(int columnIndex) throws Exception {
        return valueAt(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws Exception {
        return valueAt(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws Exception {
        Object val = valueAt(columnIndex);
        if (val instanceof Timestamp) {
            return (Timestamp) val;
        }
        return Timestamp.valueOf(String.valueOf(val));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws Exception {
        Object val = valueAt(columnLabel);
        if (val instanceof Timestamp) {
            return (Timestamp) val;
        }
        return Timestamp.valueOf(String.valueOf(val));
    }

    private void ensureData() throws Exception {
        if (rows == null || values == null) {
            throw new Exception("No data available");
        }
    }

    private void ensurePrepared() throws Exception {
        ensureData();
        if (index < 0 || index >= rows.size()) {
            throw new Exception("Results need to be prepared in advance");
        }
    }

    private Object valueAt(int columnIndex) throws Exception {
        ensurePrepared();
        if (columnIndex < 1 || columnIndex > columnNames.length) {
            throw new Exception("Index out of bound");
        }
        return values.get(index)[columnIndex - 1];
    }

    private Object valueAt(String columnLabel) throws Exception {
        ensurePrepared();
        String key = columnLabel.toLowerCase();
        Map<String, Object> row = rows.get(index);
        if (!row.containsKey(key)) {
            throw new Exception("Column not found");
        }
        return row.get(key);
    }
}
