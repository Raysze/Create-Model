package com.example.Model;



public class keyModel {

    private String table_name;

    private String column_name;

    private String referenced_table_name;

    private String referenced_column_name;

    private String constraint_name;

    public void KeyModel(){

    }
    public void KeyModel(String tablename, String column, String ref_tablename, String ref_column, String constraint){

        this.setTable_name(tablename);
        this.setColumn_name(column);
        this.setReferenced_table_name(ref_tablename);
        this.setReferenced_column_name(ref_column);
        this.setConstraint_name(constraint);
    }

    public String getConstraint_name() {
        return constraint_name;
    }

    public void setConstraint_name(String constraint_name) {
        this.constraint_name = constraint_name;
    }

    public String getReferenced_column_name() {
        return referenced_column_name;
    }

    public void setReferenced_column_name(String referenced_column_name) {
        this.referenced_column_name = referenced_column_name;
    }

    public String getReferenced_table_name() {
        return referenced_table_name;
    }

    public void setReferenced_table_name(String referenced_table_name) {
        this.referenced_table_name = referenced_table_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
}
