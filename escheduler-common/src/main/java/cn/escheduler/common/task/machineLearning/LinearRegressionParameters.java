package cn.escheduler.common.task.machineLearning;

import cn.escheduler.common.enums.ProgramType;
import cn.escheduler.common.process.ResourceInfo;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2019/7/25.
 */

public class LinearRegressionParameters extends SparkParameters {
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
//        if(characterColumns.size() == 0 || StringUtils.isEmpty(labelColumn) || StringUtils.isEmpty(hiveTable)){
//            return false;
//        }
        return true;
    }

    @Override
    public String toString() {
        return "LinearRegressionParameters{" +
                "characterColumns=" + characterColumns +
                ", labelColumn='" + labelColumn + '\'' +
                ", table='" + table + '\'' +
                ", modelName='" + modelName + '\'' +
                ", datasource=" + datasource +
                '}';
    }
}
