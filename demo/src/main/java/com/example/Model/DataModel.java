package com.example.Model;



public class DataModel {

    private String columnName;

    private String dataType;

    private String isNull;

    private String columnKey;

    private String extra;

    public void setDataModel(String columnname, String datatype, String isnull, String columnkey, String extra){
        
        this.setColumnName(columnname);
        this.setDataType(datatype);
        this.setIsNull(isnull);
        this.setColumnKey(columnkey);
        this.setExtra(extra);
    }
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }
    public String getColumnKey() {
        return columnKey;
    }
    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }
    public String getIsNull() {
        return isNull;
    }
    public void setIsNull(String isNull) {
        this.isNull = isNull;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
