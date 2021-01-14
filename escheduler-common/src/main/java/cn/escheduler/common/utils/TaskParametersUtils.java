/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.escheduler.common.utils;

import cn.escheduler.common.enums.TaskType;
import cn.escheduler.common.task.AbstractParameters;
import cn.escheduler.common.task.TrafficMetrics.*;
import cn.escheduler.common.task.common.spark.Spark2Parameters;
import cn.escheduler.common.task.deepLearning.CNNParameters;
import cn.escheduler.common.task.deepLearning.DNNParameters;
import cn.escheduler.common.task.deepLearning.LSTMForecastParameters;
import cn.escheduler.common.task.deepLearning.LSTMParameters;
import cn.escheduler.common.task.featureEngineering.*;
import cn.escheduler.common.task.io.*;
import cn.escheduler.common.task.machineLearning.*;
import cn.escheduler.common.task.common.dependent.DependentParameters;
import cn.escheduler.common.task.common.mr.MapreduceParameters;
import cn.escheduler.common.task.common.procedure.ProcedureParameters;
import cn.escheduler.common.task.common.python.PythonParameters;
import cn.escheduler.common.task.common.shell.ShellParameters;
import cn.escheduler.common.task.common.spark.SparkParameters;
import cn.escheduler.common.task.common.sql.SqlParameters;
import cn.escheduler.common.task.common.subprocess.SubProcessParameters;
import cn.escheduler.common.task.preprocess.*;
import cn.escheduler.common.task.statisticAnalysis.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * task parameters utils
 */
public class TaskParametersUtils {

  private static Logger logger = LoggerFactory.getLogger(TaskParametersUtils.class);

  /**
   * get task parameters
   * @param taskType
   * @param parameter
   * @return task parameters
   */
  public static AbstractParameters getParameters(String taskType, String parameter) {
    try {
      switch (EnumUtils.getEnum(TaskType.class,taskType)) {
        case SUB_PROCESS:
          return JSONUtils.parseObject(parameter, SubProcessParameters.class);
        case SHELL:
          return JSONUtils.parseObject(parameter, ShellParameters.class);
        case PROCEDURE:
          return JSONUtils.parseObject(parameter, ProcedureParameters.class);
        case SQL:
          return JSONUtils.parseObject(parameter, SqlParameters.class);
        case MR:
          return JSONUtils.parseObject(parameter, MapreduceParameters.class);
        case SPARK:
          return JSONUtils.parseObject(parameter, SparkParameters.class);
        case SPARK2:
          return JSONUtils.parseObject(parameter, Spark2Parameters.class);
        case PYTHON:
          return JSONUtils.parseObject(parameter, PythonParameters.class);
        case DEPENDENT:
          return JSONUtils.parseObject(parameter, DependentParameters.class);

        //input/output component
        case INPUT:
          return JSONUtils.parseObject(parameter, InputParameters.class);
        case OUTPUT:
          return JSONUtils.parseObject(parameter, OutputParameters.class);
        case HIVE_INPUT:
          return JSONUtils.parseObject(parameter, HiveInputParameters.class);
        case HIVE_OUTPUT:
          return JSONUtils.parseObject(parameter, HiveOutputParameters.class);
        case MYSQL_INPUT:
          return JSONUtils.parseObject(parameter, MysqlInputParameters.class);
        case MYSQL_OUTPUT:
          return JSONUtils.parseObject(parameter, MysqlOutputParameters.class);




        //preprocess component
        case WEIGHTED_SAMPLING:
          return JSONUtils.parseObject(parameter, WeightedSamplingParameters.class);
        case RANDOM_SAMPLING:
          return JSONUtils.parseObject(parameter, RandomSamplingParameters.class);
        case FILTER_AND_MAPPING:
          return JSONUtils.parseObject(parameter, FilterAndMappingParameters.class);
        case STRATIFIED_SAMPLING:
          return JSONUtils.parseObject(parameter, StratifiedSamplingParameters.class);
        case JOIN:
          return JSONUtils.parseObject(parameter, JoinParameters.class);
        case MERGE_COLUMN:
          return JSONUtils.parseObject(parameter, MergeColumnParameters.class);
        case UNION:
          return JSONUtils.parseObject(parameter, UnionParameters.class);
        case ADD_SERIAL_NUMBER:
          return JSONUtils.parseObject(parameter, AddSerialNumberParameters.class);
        case SPLIT:
          return JSONUtils.parseObject(parameter, SplitParameters.class);
        case DEFAULT_FILLING:
          return JSONUtils.parseObject(parameter, DefaultFillingParameters.class);
        case NORMALIZATION:
          return JSONUtils.parseObject(parameter, NormalizationParameters.class);
        case STANDARDIZATION:
          return JSONUtils.parseObject(parameter, StandardizationParameters.class);
        case TYPE_TRASFORMATION:
          return JSONUtils.parseObject(parameter, TypeTransformationParameters.class);
        case DATA_FILTER:
          return JSONUtils.parseObject(parameter, DataFilterParameters.class);
        case COLUMN_FILTER:
          return JSONUtils.parseObject(parameter, ColumnFilterParameters.class);
        case GROUP_AGGREGATION:
          return JSONUtils.parseObject(parameter, GroupbyAggParameters.class);
        case COLUMN_ALTER:
          return JSONUtils.parseObject(parameter, ColumnnameAlterParameters.class);
        case TIME_SHIFT:
          return JSONUtils.parseObject(parameter, TimeShiftParameters.class);
        case TIME_DIFFER:
          return JSONUtils.parseObject(parameter, TimeDifferParameters.class);
        case CHARACTER_MERGING:
          return JSONUtils.parseObject(parameter, CharacterMergingParameters.class);
        case ACCUMULATION_CALCULATOR:
          return JSONUtils.parseObject(parameter, AccumulationCalculatorParameters.class);
        case TABLE_JOIN:
          return JSONUtils.parseObject(parameter, TableJoinParameters.class);
        case DATA_OFFSET:
          return JSONUtils.parseObject(parameter, DataOffsetParameters.class);
        case DATA_DISCRETIZATION:
          return JSONUtils.parseObject(parameter, DataDiscretizationParameters.class);
        case SUBSTRING:
          return JSONUtils.parseObject(parameter, CharacterInterceptParameters.class);
        case TIME_FUNCTION:
          return JSONUtils.parseObject(parameter, TimeFunctionParameters.class);
        case ADD_COLUMN:
          return JSONUtils.parseObject(parameter, AddColumnParameters.class);
        case CHARACTER_REPLACE:
          return JSONUtils.parseObject(parameter, CharactorReplaceParameters.class);
        case DATA_DEDUPLICATION:
          return JSONUtils.parseObject(parameter, CharactorReplaceParameters.class);
        case DATA_NORMALIZATION:
          return JSONUtils.parseObject(parameter, CharactorReplaceParameters.class);

          //feature engineering component
        case PRINCIPAL_COMPONENT_ANALYSIS:
          return JSONUtils.parseObject(parameter, PrincipalComponentAnalysisParameters.class);
        case CHARACTERISTIC_SCALE_TRANSFORM:
          return JSONUtils.parseObject(parameter, CharacteristicScaleTransformParameters.class);
        case CHARACTERISTIC_DISCRETENESS:
          return JSONUtils.parseObject(parameter, CharacteristicDiscretenessParameters.class);
        case FEATURE_ANOMALY_SMOOTHING:
          return JSONUtils.parseObject(parameter, FeatureAnomalySmoothingParameters.class);
        case STOCHASTIC_FOREST_CHARACTERISTICS_IMPORTANCE:
          return JSONUtils.parseObject(parameter, StochasticForestCharacteristicsImportanceParameters.class);
        case GBDT_CHARACTERISTIC_IMPORTANCE:
          return JSONUtils.parseObject(parameter, GBDTCharacteristicImportanceParameters.class);
        case LINEAR_MODELS_CHARACTERISTIC_IMPORTANCE:
          return JSONUtils.parseObject(parameter, LinearModelsCharacteristicImportanceParameters.class);
        case PREFERENCE_CALCULATION:
          return JSONUtils.parseObject(parameter, PreferenceCalculationParameters.class);
        case FILTERING_FEATURE_SELECTION:
          return JSONUtils.parseObject(parameter, FilteringFeatureSelectionParameters.class);

        //statistic analysis component
        case PERCENTAGE:
          return JSONUtils.parseObject(parameter, PercentageParameters.class);
        case FULL_TABLE_STATISTICS:
          return JSONUtils.parseObject(parameter, FullTableStatisticsParameters.class);
        case PEARSON_COEFFICIENT:
          return JSONUtils.parseObject(parameter, PearsonCoefficientParameters.class);
        case HISTOGRAM:
          return JSONUtils.parseObject(parameter, HistogramParameters.class);
        case DISCRETE_VALUE_CHARACTERISTIC_ANALYSIS:
          return JSONUtils.parseObject(parameter, DiscreteValueCharacteristicAnalysisParameters.class);
        case T_TEST:
          return JSONUtils.parseObject(parameter, TTestParameters.class);
        case CHI_SQUARE_TEST:
          return JSONUtils.parseObject(parameter, ChiSquareTestParameters.class);
        case CORRELATION_COEFFICIENT_MATRIX:
          return JSONUtils.parseObject(parameter, CorrelationCoefficientMairixParameters.class);
        case KERNEL_DENSITY_ESTIMATION:
          return JSONUtils.parseObject(parameter, KernelDensityEstimationParameters.class);
        case BASIC_STATISTICS:
          return JSONUtils.parseObject(parameter, BasicStatisticsParameters.class);



        //machine learning component
        case LINEAR_SUPPORT_VECTOR_MACHINE:
          return JSONUtils.parseObject(parameter, LinearSupportVectorMachineParameters.class);
        case GBDT_BI_CLASSIFY:
          return JSONUtils.parseObject(parameter, GBDTBiClassifyParameters.class);
        case LOGISTIC_REGRESSION:
          return JSONUtils.parseObject(parameter, LogisticRegressionParameters.class);
        case LINEAR_REGRESSION:
          return JSONUtils.parseObject(parameter, LinearRegressionParameters.class);
        case K_NEAREST_NEIGHBORS:
          return JSONUtils.parseObject(parameter, KNearestNeighborsParameters.class);
        case RANDOM_FOREST:
          return JSONUtils.parseObject(parameter, RandomForestParameters.class);
        case NAIVE_BAYES:
          return JSONUtils.parseObject(parameter, NaiveBayesParameters.class);
        case K_MEANS_CLUSTERING:
          return JSONUtils.parseObject(parameter, KMeansClusteringParameters.class);
        case GBDT_REGRESSION:
          return JSONUtils.parseObject(parameter, GBDTRegressionParameters.class);
        case XGBOOST_REGRESSION:
          return JSONUtils.parseObject(parameter, XGBOOSTRegressionParameters.class);
        case CONFUSION_MATRIX:
          return JSONUtils.parseObject(parameter, ConfusionMatrixParameters.class);
        case MULTI_CLASSIFICATION_ASSESSMENT:
          return JSONUtils.parseObject(parameter, MultiClassificationAssessmentParameters.class);
        case BI_CLASSIFICATION_ASSESSMENT:
          return JSONUtils.parseObject(parameter, BiClassificationAssessmentParameters.class);
        case REGRESSION_MODEL_ASSESSMENT:
          return JSONUtils.parseObject(parameter, RegressionModelAssessmentParameters.class);
        case FORECAST:
          return JSONUtils.parseObject(parameter, ForecastParameters.class);
        case DECISION_TREE:
          return JSONUtils.parseObject(parameter, DecusionTreeParameters.class);
        case TIME_SERIES_ANALYSIS:
          return JSONUtils.parseObject(parameter, TimeSeriesAnalysisParameters.class);

        //deep learning
        case CNN:
          return JSONUtils.parseObject(parameter, CNNParameters.class);
        case DNN:
          return JSONUtils.parseObject(parameter, DNNParameters.class);
        case LSTM:
          return JSONUtils.parseObject(parameter, LSTMParameters.class);
        case LSTM_FORECAST:
          return JSONUtils.parseObject(parameter, LSTMForecastParameters.class);

        // traffic metrics
          case TRAFFIC_FLOW:
          return JSONUtils.parseObject(parameter, TrafficFlowParameters.class);
          case ENTRY_LEAVE_CITY:
              return JSONUtils.parseObject(parameter, EntryLeavecityParameters.class);
          case TRAVEL_TIME:
              return JSONUtils.parseObject(parameter, TravelTimeParameters.class);
        case TRAVEL_SPEED:
          return JSONUtils.parseObject(parameter, TravelSpeedParameters.class);
        case CONGESTION_DELAY_INDEX:
          return JSONUtils.parseObject(parameter, CongestionDelayIndexParameters.class);
        case BASE_PASS_TIME:
          return JSONUtils.parseObject(parameter, BasePasstimeParameters.class);
        case ONROAD_URBAN:
          return JSONUtils.parseObject(parameter, OnroadUrbanParameters.class);
        case CORRELATION_ANALYSIS:
          return JSONUtils.parseObject(parameter, CorrelationAnalysisParameters.class);
        case PASS_EFFICIENCY:
          return JSONUtils.parseObject(parameter, PassEfficiencyParameters.class);
        case QUEUE_LENGTH:
          return JSONUtils.parseObject(parameter, QueueLengthParameters.class);
        case PARKING_URBAN:
          return JSONUtils.parseObject(parameter, ParkingUrbanParameters.class);
        case RID_IMBALANCE:
          return JSONUtils.parseObject(parameter, RidImbalanceParameters.class);
        case OVER_FLOW:
          return JSONUtils.parseObject(parameter, OverFlowParameters.class);
        case CONGESTION_MILEAGE:
          return JSONUtils.parseObject(parameter, CongestionMileageParameters.class);
        case TRAFFIC_CAPACITY:
          return JSONUtils.parseObject(parameter, TrafficCapacityParameters.class);
        case DELAY_TIME:
          return JSONUtils.parseObject(parameter, DelayTimeParameters.class);
        case RID_SATURATION:
          return JSONUtils.parseObject(parameter, RidSaturationParameters.class);
        default:
          return null;
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }
}
