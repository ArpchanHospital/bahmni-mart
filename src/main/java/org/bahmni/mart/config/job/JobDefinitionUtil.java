package org.bahmni.mart.config.job;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class JobDefinitionUtil {

    private static Logger logger = LoggerFactory.getLogger(JobDefinitionUtil.class);
    public static final String TO_SPLIT_FROM = "(?i)from";
    public static final String TO_SPLIT_SELECT = "(?i)select";
    public static final String OBS_JOB_TYPE = "obs";

    public static String getReaderSQLByIgnoringColumns(List<String> columnsToIgnore, String readerSQL) {
        if (StringUtils.isEmpty(readerSQL) || columnsToIgnore == null || columnsToIgnore.isEmpty()) {
            return readerSQL;
        }
        String[] sqlSubstrings = readerSQL.split(TO_SPLIT_FROM, 2);
        String[] readerSQLColumns = sqlSubstrings[0].trim().split(TO_SPLIT_SELECT)[1].trim().split(",");

        List<String> updatedColumns = getUpdatedColumns(readerSQLColumns, columnsToIgnore);

        return getUpdatedSQL(updatedColumns, sqlSubstrings[1]);
    }

    private static String getUpdatedSQL(List<String> updatedColumns, String query) {
        String finalColumns = "";
        if (updatedColumns.isEmpty())
            return finalColumns;
        finalColumns = updatedColumns.toString();
        return String.format("select %s from%s", finalColumns.substring(1, finalColumns.length() - 1), query);
    }

    private static String getTrimmedSql(String trimSql) {
        String finalTrimSql = trimSql.trim();
        String[] splitBy = {"\\.", " as ", " AS ", " aS ", " As "};
        for (String splitToken : splitBy) {
            finalTrimSql = (finalTrimSql.split(splitToken).length > 1) ?
                    finalTrimSql.split(splitToken)[1] : finalTrimSql;
        }
        return finalTrimSql.contains("`") ? finalTrimSql.substring(1, finalTrimSql.length() - 1) : finalTrimSql;
    }

    private static List<String> getUpdatedColumns(String[] readerSQLColumns, List<String> columnsToIgnore) {
        Set<String> ignoredColumns = new HashSet<>();
        ignoredColumns.addAll(columnsToIgnore);
        List<String> updatedColumns = new ArrayList<>();

        Arrays.asList(readerSQLColumns).forEach((String readerSQLColumn) -> {
            String trimmedReaderSQLColumn = getTrimmedSql(readerSQLColumn);

            boolean isIgnored = ignoredColumns.stream().anyMatch(trimmedReaderSQLColumn::equals);
            if (!isIgnored) {
                updatedColumns.add(readerSQLColumn);
            }
        });
        return updatedColumns;
    }

    public static List<String> getIgnoreConceptNamesForObsJob(List<JobDefinition> jobDefinitions) {
        List<String> columnsToIgnore = getObsJobDefinition(jobDefinitions).getColumnsToIgnore();
        return columnsToIgnore == null ? new ArrayList<>() : columnsToIgnore;
    }

    public static List<String> getSeparateTableNamesForObsJob(List<JobDefinition> jobDefinitions) {
        List<String> separateTables = getObsJobDefinition(jobDefinitions).getSeparateTables();
        return separateTables == null ? new ArrayList<>() : separateTables;
    }

    public static JobDefinition getObsJobDefinition(List<JobDefinition> jobDefinitions) {
        try {
            Optional<JobDefinition> optionalObsJobDefinition = jobDefinitions.stream()
                    .filter(jobDefinition -> jobDefinition.getType().equals(OBS_JOB_TYPE)).findFirst();
            return optionalObsJobDefinition.get();
        } catch (NoSuchElementException e) {
            logger.info("No obs job definition found");
        }
        return new JobDefinition();
    }
}
