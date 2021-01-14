package cn.escheduler.common.task.deepLearning;

import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2019/7/25.
 */

public class LSTMForecastParameters extends AbstractParameters {
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
     * character columns
     */
    private List<String> characterColumns;

    /**
     * databases
     */
    private String databases;

    /**
     * input hive table
     */
    private String table;

    /**
     * datasource
     */
    private int datasource;

    /**
     * modelName
     */
    private String modelName;

    public List<String> getCharacterColumns() {
        return characterColumns;
    }

    public void setCharacterColumns(List<String> characterColumns) {
        this.characterColumns = characterColumns;
    }

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public boolean checkParameters() {
        return true;
    }

    @Override
    public String getResultTable(){
        return databases + "." + modelName.replace("-", "_");
    }

    @Override
    public List<String> getResourceFilesList() {
        if (resourceList != null) {
            return resourceList.stream()
                    .map(p -> p.getRes()).collect(Collectors.toList());
        }

        return null;
    }

}
