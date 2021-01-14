package cn.escheduler.common.task.statisticAnalysis;

import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2019/7/25.
 */

public class HistogramParameters extends SparkParameters {
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
    private String hiveTable;

    /**
     * model file name
     */
    private String modelFileName;

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


    public String getModelFileName() {
        return modelFileName;
    }

    public void setModelFileName(String modelFileName) {
        this.modelFileName = modelFileName;
    }

    public String getHiveTable() {
        return hiveTable;
    }

    public void setHiveTable(String hiveTable) {
        this.hiveTable = hiveTable;
    }

    @Override
    public boolean checkParameters() {
        if(characterColumns.size() == 0 || StringUtils.isEmpty(labelColumn) || StringUtils.isEmpty(hiveTable)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LinearRegressionParameters{" +
                "characterColumns=" + characterColumns +
                ", labelColumn='" + labelColumn + '\'' +
                ", modelFileName='" + modelFileName + '\'' +
                '}';
    }
}
