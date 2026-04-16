package com.girlkun.result;

import java.sql.Timestamp;

public interface GirlkunResultSet {
    byte getByte(int columnIndex) throws Exception;
    byte getByte(String columnLabel) throws Exception;

    int getInt(int columnIndex) throws Exception;
    int getInt(String columnLabel) throws Exception;

    short getShort(int columnIndex) throws Exception;
    short getShort(String columnLabel) throws Exception;

    float getFloat(int columnIndex) throws Exception;
    float getFloat(String columnLabel) throws Exception;

    double getDouble(int columnIndex) throws Exception;
    double getDouble(String columnLabel) throws Exception;

    long getLong(int columnIndex) throws Exception;
    long getLong(String columnLabel) throws Exception;

    String getString(int columnIndex) throws Exception;
    String getString(String columnLabel) throws Exception;

    boolean getBoolean(int columnIndex) throws Exception;
    boolean getBoolean(String columnLabel) throws Exception;

    Object getObject(int columnIndex) throws Exception;
    Object getObject(String columnLabel) throws Exception;

    Timestamp getTimestamp(int columnIndex) throws Exception;
    Timestamp getTimestamp(String columnLabel) throws Exception;

    void dispose();
    boolean next() throws Exception;
    boolean first() throws Exception;
    boolean gotoResult(int index) throws Exception;
    boolean gotoFirst() throws Exception;
    void gotoBeforeFirst();
    boolean gotoLast() throws Exception;
    int getRows() throws Exception;
}
