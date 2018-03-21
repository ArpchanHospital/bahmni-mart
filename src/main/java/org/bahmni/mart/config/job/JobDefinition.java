package org.bahmni.mart.config.job;

import java.util.List;

public class JobDefinition {
    private String name;
    private String type;
    private String readerSql;
    private int chunkSizeToRead;
    private String tableName;
    private String conceptReferenceSource;
    private List<ColumnsToIgnore> columnsToIgnore;

    class ColumnsToIgnore {
        private String tableName;
        private List<String> columns;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public List<String> getColumns() {
            return columns;
        }

        public void setColumns(List<String> columns) {
            this.columns = columns;
        }
    }

    public int getChunkSizeToRead() {
        return chunkSizeToRead;
    }

    public void setChunkSizeToRead(int chunkSizeToRead) {
        this.chunkSizeToRead = chunkSizeToRead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReaderSql() {
        return readerSql;
    }

    public void setReaderSql(String readerSql) {
        this.readerSql = readerSql;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getConceptReferenceSource() {
        return conceptReferenceSource;
    }

    public void setConceptReferenceSource(String conceptReferenceSource) {
        this.conceptReferenceSource = conceptReferenceSource;
    }

    public List<ColumnsToIgnore> getColumnsToIgnore() {
        return columnsToIgnore;
    }

    public void setColumnsToIgnore(List<ColumnsToIgnore> columnsToIgnore) {
        this.columnsToIgnore = columnsToIgnore;
    }
}
