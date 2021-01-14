package cn.escheduler.common.task.machineLearning;

import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2019/7/25.
 */

public class RegressionModelAssessmentParameters extends SparkParameters {
    /**
     * character columns
     */
    private String predictionColumn;

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
    private int datasource;

    private String databases;

    private String modelName;


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDatabases() {
        return databases;
    }

    public void setDatabases(String databases) {
        this.databases = databases;
    }

    public String getPredictionColumn() {
        return predictionColumn;
    }

    public void setPredictionColumn(String predictionColumn) {
        this.predictionColumn = predictionColumn;
    }

    public String getLabelColumn() {
        return labelColumn;
    }

    public void setLabelColumn(String labelColumn) {
        this.labelColumn = labelColumn;
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

    @Override
    public boolean checkParameters() {
        return true;
    }

    @Override
    public String getResultTable(){
        return getDatabases()+"."+table+"_pg";
    }

    @Override
    public String toString() {
        return "RegressionModelAssessmentParameters{" +
                "predictionColumn='" + predictionColumn + '\'' +
                ", labelColumn='" + labelColumn + '\'' +
                ", table='" + table + '\'' +
                ", datasource=" + datasource +
                ", databases='" + databases + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
