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
package cn.escheduler.server.worker.task;


import cn.escheduler.common.enums.TaskType;
import cn.escheduler.server.worker.task.common.dependent.DependentTask;
import cn.escheduler.server.worker.task.common.mr.MapReduceTask;
import cn.escheduler.server.worker.task.common.processdure.ProcedureTask;
import cn.escheduler.server.worker.task.common.python.PythonTask;
import cn.escheduler.server.worker.task.common.shell.ShellTask;
import cn.escheduler.server.worker.task.common.spark.Spark2Task;
import cn.escheduler.server.worker.task.common.spark.SparkTask;
import cn.escheduler.server.worker.task.common.sql.SqlTask;
import cn.escheduler.server.worker.task.deepLearning.CNNTask;
import cn.escheduler.server.worker.task.deepLearning.DNNTask;
import cn.escheduler.server.worker.task.deepLearning.LSTMForecastTask;
import cn.escheduler.server.worker.task.deepLearning.LSTMTask;
import cn.escheduler.server.worker.task.featureEngineering.*;
import cn.escheduler.server.worker.task.io.*;
import cn.escheduler.server.worker.task.machineLearning.*;
import cn.escheduler.server.worker.task.preprocess.*;
import cn.escheduler.server.worker.task.statisticAnalysis.*;
import cn.escheduler.server.worker.task.TrafficMetrics.*;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;



/**
 *  task manaster
 */
public class TaskManager {


  /**
   *  create new task
   * @param taskType
   * @param props
   * @param logger
   * @return
   * @throws IllegalArgumentException
   */
  public static AbstractTask newTask(String taskType, TaskProps props, Logger logger)
      throws IllegalArgumentException {
    switch (EnumUtils.getEnum(TaskType.class,taskType)) {
      //common component
      case SHELL:
        return new ShellTask(props, logger);
      case PROCEDURE:
        return new ProcedureTask(props, logger);
      case SQL:
        return new SqlTask(props, logger);
      case MR:
        return new MapReduceTask(props, logger);
      case SPARK:
        return new SparkTask(props, logger);
      case SPARK2:
        return new Spark2Task(props, logger);
      case PYTHON:
        return new PythonTask(props, logger);
      case DEPENDENT:
        return new DependentTask(props, logger);

      //input/output component
      case INPUT:
        return new InputTask(props, logger);
      case OUTPUT:
        return new OutputTask(props, logger);
      case HIVE_INPUT:
        return new HiveInputTask(props, logger);
      case HIVE_OUTPUT:
        return new HiveOutputTask(props, logger);
      case MYSQL_INPUT:
        return new MysqlInputTask(props, logger);
      case MYSQL_OUTPUT:
        return new MysqlOutputTask(props, logger);

      //preprocess component
      case WEIGHTED_SAMPLING:
        return new WeightedSamplingTask(props, logger);
      case RANDOM_SAMPLING:
        return new RandomSamplingTask(props, logger);
      case FILTER_AND_MAPPING:
        return new FilterAndMappingTask(props, logger);
      case STRATIFIED_SAMPLING:
        return new StratifiedSamplingTask(props, logger);
      case JOIN:
        return new JoinTask(props, logger);
      case MERGE_COLUMN:
        return new MergeColumnTask(props, logger);
      case UNION:
        return new UnionTask(props, logger);
      case ADD_SERIAL_NUMBER:
        return new AddSerialNumberTask(props, logger);
      case SPLIT:
        return new SplitTask(props, logger);
      case DEFAULT_FILLING:
        return new DefaultFillingTask(props, logger);
      case NORMALIZATION:
        return new NormalizationTask(props, logger);
      case STANDARDIZATION:
        return new StandardizationTask(props, logger);
      case TYPE_TRASFORMATION:
        return new TypeTransformationTask(props, logger);
      case DATA_FILTER:
        return new DataFilterTask(props, logger);
      case COLUMN_FILTER:
        return new ColumnFilterTask(props, logger);
      case GROUP_AGGREGATION:
        return new GroupbyAggTask(props, logger);
      case COLUMN_ALTER:
        return new ColumnnameAlterTask(props, logger);
      case TIME_SHIFT:
        return new TimeShiftTask(props, logger);
      case TIME_DIFFER:
        return new TimeDifferTask(props, logger);
      case CHARACTER_MERGING:
        return new CharactorMergingTask(props, logger);
      case ACCUMULATION_CALCULATOR:
        return new AccumulationCalculatorTask(props, logger);
      case TABLE_JOIN:
        return new TableJoinTask(props, logger);
      case DATA_OFFSET:
        return new DataOffsetTask(props, logger);
      case DATA_DISCRETIZATION:
        return new DataDiscretizationTask(props, logger);
      case SUBSTRING:
        return new CharactorInterceptTask(props, logger);
      case TIME_FUNCTION:
        return new TimeFunctionTask(props, logger);
      case ADD_COLUMN:
        return new AddColumnTask(props, logger);
      case CHARACTER_REPLACE:
        return new CharactorReplaceTask(props, logger);
      case DATA_DEDUPLICATION:
        return new DataDeduplicationTask(props, logger);
      case DATA_NORMALIZATION:
        return new DataNormalizationTask(props, logger);
      //feature engineering component
      case PRINCIPAL_COMPONENT_ANALYSIS:
        return new PrincipalComponentAnalysisTask(props, logger);
      case CHARACTERISTIC_SCALE_TRANSFORM:
        return new CharacteristicScaleTransformTask(props, logger);
      case CHARACTERISTIC_DISCRETENESS:
        return new CharacteristicDiscretenessTask(props, logger);
      case FEATURE_ANOMALY_SMOOTHING:
        return new FeatureAnomalySmoothingTask(props, logger);
      case STOCHASTIC_FOREST_CHARACTERISTICS_IMPORTANCE:
        return new StochasticForestCharacteristicsImportanceTask(props, logger);
      case GBDT_CHARACTERISTIC_IMPORTANCE:
        return new GBDTCharacteristicImportanceTask(props, logger);
      case LINEAR_MODELS_CHARACTERISTIC_IMPORTANCE:
        return new LinearModelsCharacteristicImportanceTask(props, logger);
      case PREFERENCE_CALCULATION:
        return new PreferenceCalculationTask(props, logger);
      case FILTERING_FEATURE_SELECTION:
        return new FilteringFeatureSelectionTask(props, logger);

      //statistic analysis component
      case PERCENTAGE:
        return new PercentageTask(props, logger);
      case FULL_TABLE_STATISTICS:
        return new FullTableStatisticsTask(props, logger);
      case PEARSON_COEFFICIENT:
        return new PearsonCoefficientTask(props, logger);
      case HISTOGRAM:
        return new HistogramTask(props, logger);
      case DISCRETE_VALUE_CHARACTERISTIC_ANALYSIS:
        return new DiscreteValueCharacteristicAnalysisTask(props, logger);
      case T_TEST:
        return new TTestTask(props, logger);
      case CHI_SQUARE_TEST:
        return new ChiSquareTestTask(props, logger);
      case CORRELATION_COEFFICIENT_MATRIX:
        return new CorrelationCoefficientMairixTask(props, logger);
      case KERNEL_DENSITY_ESTIMATION:
        return new KernelDensityEstimationTask(props, logger);
      case BASIC_STATISTICS:
        return new BasicStatisticsTask(props, logger);



      //machine learning component
      case LINEAR_SUPPORT_VECTOR_MACHINE:
        return new LinearSupportVectorMachineTask(props, logger);
      case GBDT_BI_CLASSIFY:
        return new GBDTBiClassifyTask(props, logger);
      case LOGISTIC_REGRESSION:
        return new LogisticRegressionTask(props, logger);
      case LINEAR_REGRESSION:
        return new LinearRegressionTask(props, logger);
      case K_NEAREST_NEIGHBORS:
        return new KNearestNeighborsTask(props, logger);
      case RANDOM_FOREST:
        return new RandomForestTask(props, logger);
      case NAIVE_BAYES:
        return new NaiveBayesTask(props, logger);
      case K_MEANS_CLUSTERING:
        return new KMeansClusteringTask(props, logger);
      case GBDT_REGRESSION:
        return new GBDTRegressionTask(props, logger);
      case XGBOOST_REGRESSION:
        return new XGBOOSTRegressionTask(props, logger);
      case CONFUSION_MATRIX:
        return new ConfusionMatrixTask(props, logger);
      case MULTI_CLASSIFICATION_ASSESSMENT:
        return new MultiClassificationAssessmentTask(props, logger);
      case BI_CLASSIFICATION_ASSESSMENT:
        return new BiClassificationAssessmentTask(props, logger);
      case REGRESSION_MODEL_ASSESSMENT:
        return new RegressionModelAssessmentTask(props, logger);
      case DECISION_TREE:
        return new DecusionTreeTask(props, logger);
      case FORECAST:
        return new ForecastTask(props, logger);
      case TIME_SERIES_ANALYSIS:
        return new TimeSeriesAnalysisTask(props, logger);

      //deep learning
      case CNN:
        return new CNNTask(props, logger);
      case DNN:
        return new DNNTask(props, logger);
      case LSTM:
        return new LSTMTask(props, logger);
      case LSTM_FORECAST:
        return new LSTMForecastTask(props, logger);

      case TRAFFIC_FLOW:
        return new TrafficFlowTask(props, logger);
        case ENTRY_LEAVE_CITY:
            return new EntryLeavecityTask(props, logger);
        case TRAVEL_TIME:
            return new TravelTimeTask(props, logger);
      case TRAVEL_SPEED:
        return new TravelSpeedTask(props, logger);
      case CONGESTION_DELAY_INDEX:
        return new CongestionDelayIndexTask(props, logger);
      case BASE_PASS_TIME:
        return new BasePasstimeTask(props, logger);
      case ONROAD_URBAN:
        return new OnroadUrbanTask(props, logger);
        case CORRELATION_ANALYSIS:
            return new CorrelationAnalysisTask(props, logger);
        case PASS_EFFICIENCY:
            return new PassEfficiencyTask(props, logger);
        case QUEUE_LENGTH:
            return new QueueLengthTask(props, logger);
        case PARKING_URBAN:
            return new ParkingUrbanTask(props, logger);
        case RID_IMBALANCE:
            return new RidImbalanceTask(props, logger);
        case OVER_FLOW:
            return new OverFlowTask(props, logger);
        case CONGESTION_MILEAGE:
            return new CongestionMileageTask(props, logger);
        case TRAFFIC_CAPACITY:
            return new CongestionMileageTask(props, logger);
        case DELAY_TIME:
            return new CongestionMileageTask(props, logger);
        case RID_SATURATION:
            return new CongestionMileageTask(props, logger);

      default:
        logger.error("unsupport task type: {}", taskType);
        throw new IllegalArgumentException("not support task type");
    }
  }
}
