package cn.escheduler.common.task.machineLearning;

import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2019/7/25.
 */

public class LogisticRegressionParameters extends SparkParameters {
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

    private Boolean isSaveModel;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getDatasource() {
        return datasource;
    }

    public void setDatasource(int datasource) {
        this.datasource = datasource;
    }

    public Boolean getSaveModel() {
        return isSaveModel;
    }

    public void setSaveModel(Boolean saveModel) {
        isSaveModel = saveModel;
    }

    @Override
    public boolean checkParameters() {

        return true;
    }

    @Override
    public String toString() {
        return "LogisticRegressionParameters{" +
                "characterColumns=" + characterColumns +
                ", labelColumn='" + labelColumn + '\'' +
                ", table='" + table + '\'' +
                ", modelName='" + modelName + '\'' +
                ", datasource=" + datasource +
                '}';
    }
}
