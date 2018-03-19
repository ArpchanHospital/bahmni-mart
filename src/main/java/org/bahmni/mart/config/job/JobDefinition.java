package org.bahmni.mart.config.job;

public class JobDefinition {
    private String name;
    private String type;
    private String readerSql;
    private int chunkSizeToRead;
    private String tableName;
    private String conceptReferenceSource;

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
}
