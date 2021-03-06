package cn.escheduler.common.task.statisticAnalysis;

import cn.escheduler.common.model.TaskNode;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2019/7/25.
 */

public class CorrelationCoefficientMairixParameters extends AbstractParameters {


    /**
     * shell script
     */
    private String rawScript;

    /**
     * resource list
     */
    private List<ResourceInfo> resourceList;

    public String getRawScript() {
        return rawScript;
    }

    public void setRawScript(String rawScript) {
        this.rawScript = rawScript;
    }

    public List<ResourceInfo> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceInfo> resourceList) {
        this.resourceList = resourceList;
    }

    /**
     * task id
     */
    private String taskId;

    /**
     * character columns
     */
    private List<String> relatedColumns ;

    /**
     * datasource id
     */
    private int datasource;

    /**
     * databases
     */
    private String databases;

    /**
     * hive table
     */
    private String Table;



    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public String getTable() {
        return Table;
    }

    public void setTable(String table) {
        Table = table;
    }

    public List<String> getRelatedColumns() {
        return relatedColumns;
    }

    public void setRelatedColumns(List<String> relatedColumns) {
        this.relatedColumns = relatedColumns;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public boolean checkParameters() {
        return true;
    }

    @Override
    public List<String> getResourceFilesList() {
        if (resourceList != null) {
            return resourceList.stream()
                    .map(p -> p.getRes()).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public String getResultTable(){
        return databases + "." + taskId.replace("-", "_");
    }

}
