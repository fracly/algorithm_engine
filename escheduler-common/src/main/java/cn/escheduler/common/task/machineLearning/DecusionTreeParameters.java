package cn.escheduler.common.task.machineLearning;

import cn.escheduler.common.task.common.spark.SparkParameters;

import java.util.List;

/**
 * Created by Administrator on 2019/7/25.
 */

public class DecusionTreeParameters extends SparkParameters {
    /**
     * character columns
     */
    private List<String> characterColumns;

    /**
     * label column
     */
    private String labelColumn;

    /**
     * hive table
     */
    private String table;

    /**
     * model file name
     */
    private String modelName;

    /**
     * datasource
     */
    private int datasource;

    private int depthMax;
    private int branchMax;
    private String method;


    public int getDepthMax() {
        return depthMax;
    }

    public void setDepthMax(int depthMax) {
        this.depthMax = depthMax;
    }

    public int getBranchMax() {
        return branchMax;
    }

    public void setBranchMax(int branchMax) {
        this.branchMax = branchMax;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public List<String> getCharacterColumns() {
        return characterColumns;
    }

    public void setCharacterColumns(List<String> characterColumns) {
        this.characterColumns = characterColumns;
    }

    public String getLabelColumn() {
        return labelColumn;
    }

    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public boolean checkParameters() {
        return true;
    }

    @Override
    public String toString() {
        return "DecusionTreeParameters{" +
                "characterColumns=" + characterColumns +
                ", labelColumn='" + labelColumn + '\'' +
                ", table='" + table + '\'' +
                ", modelName='" + modelName + '\'' +
                ", datasource=" + datasource +
                ", depthMax=" + depthMax +
                ", branchMax=" + branchMax +
                ", method='" + method + '\'' +
                '}';
    }
}
