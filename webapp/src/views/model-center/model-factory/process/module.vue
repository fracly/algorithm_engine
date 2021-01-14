<template>
  <div>
    <a-form-model layout="vertical" class="ant-advanced-search-form" :model="tasks">
      <a-form-model-item label="节点名称:">
        <a-input v-model="tasks.name"/>
      </a-form-model-item>
      <a-form-item label="描述:">
        <a-input v-model="tasks.desc" type="textarea"/>
      </a-form-item>
    </a-form-model>
    <components :is="node.moduleId" ref="child" :tasks="tasks.params"></components>
  </div>
</template>
<script>
    import { clone } from '@/utils/global'
  /* 通用工具 */
  import ShellTools from '@/views/model-center/model-factory/module/GeneralTools/ShellTools'
  import SqlTools from '@/views/model-center/model-factory/module/GeneralTools/SqlTools'
  import SparkTools from '@/views/model-center/model-factory/module/GeneralTools/SparkTools'
  import PythonTolls from '@/views/model-center/model-factory/module/GeneralTools/PythonTolls'
  import Dependent from '@/views/model-center/model-factory/module/GeneralTools/Dependent'
  /* 输入输出 */
  import DataSourceInput from '@/views/model-center/model-factory/module/InputOutput/DataSourceInput'
  import DataOutput from '@/views/model-center/model-factory/module/InputOutput/DataOutput'
  import SQLDataOutput from '@/views/model-center/model-factory/module/InputOutput/SQLDataOutput'
  import MysqlInput from '@/views/model-center/model-factory/module/InputOutput/MysqlInput'
  import MysqlOutput from '@/views/model-center/model-factory/module/InputOutput/MysqlOutput'
    import HiveInput from '@/views/model-center/model-factory/module/InputOutput/HiveInput'
    import HiveOutput from '@/views/model-center/model-factory/module/InputOutput/HiveOutput'
  /* 预处理组件 */
  import ColumnFilter from '@/views/model-center/model-factory/module/Preprocess/ColumnFilter'
  import TableJoin from '@/views/model-center/model-factory/module/Preprocess/TableJoin'
  import ColumnAlter from '@/views/model-center/model-factory/module/Preprocess/ColumnAlter'
  import DataFilter from '@/views/model-center/model-factory/module/Preprocess/DataFilter'
  import PacketAggregation from '@/views/model-center/model-factory/module/Preprocess/PacketAggregation'
  import TimeShift from '@/views/model-center/model-factory/module/Preprocess/TimeShift'
  import TimeDiffer from '@/views/model-center/model-factory/module/Preprocess/TimeDiffer'
  import CharacterMerging from '@/views/model-center/model-factory/module/Preprocess/CharacterMerging'
  import AccumulationCalculator from '@/views/model-center/model-factory/module/Preprocess/AccumulationCalculator'
  import DataOffset from '@/views/model-center/model-factory/module/Preprocess/DataOffset'
  import DataDiscretization from '@/views/model-center/model-factory/module/Preprocess/DataDiscretization'
  import SubString from '@/views/model-center/model-factory/module/Preprocess/SubString'
  import TimeFunction from '@/views/model-center/model-factory/module/Preprocess/TimeFunction'
  import AddColumn from '@/views/model-center/model-factory/module/Preprocess/AddColumn'
  import Split from '@/views/model-center/model-factory/module/Preprocess/Split'
  import DefaultFilling from '@/views/model-center/model-factory/module/Preprocess/DefaultFilling'
  import CharacterReplace from '@/views/model-center/model-factory/module/Preprocess/CharacterReplace'
  import DataDeduplication from '@/views/model-center/model-factory/module/Preprocess/DataDeduplication'
  import DataNormalization from '@/views/model-center/model-factory/module/Preprocess/DataNormalization'

    /* 统计分析 */
  import NuclearDensityAnalysis
    from '@/views/model-center/model-factory/module/StatisticalAnalysis/NuclearDensityAnalysis'
  import CorrelationMatrix from '@/views/model-center/model-factory/module/StatisticalAnalysis/CorrelationMatrix'
  import BasicStatistics from '@/views/model-center/model-factory/module/StatisticalAnalysis/BasicStatistics'
    /* 机器学习 */
  import RegressionModelEvaluation from '@/views/model-center/model-factory/module/MachineLearning/RegressionModelEvaluation'
  import TimeSeriesAnalysis from '@/views/model-center/model-factory/module/MachineLearning/TimeSeriesAnalysis'
  import DecisionTree from '@/views/model-center/model-factory/module/MachineLearning/DecisionTree'
  import forecast from '@/views/model-center/model-factory/module/MachineLearning/forecast'
  import LinearRegression from '@/views/model-center/model-factory/module/MachineLearning/LinearRegression'
  import MultiClassFicationAssessment from '@/views/model-center/model-factory/module/MachineLearning/MultiClassFicationAssessment'
  import LogisticRegression from '@/views/model-center/model-factory/module/MachineLearning/LogisticRegression'

    /* 深度学习 */
  import LSTMAlgorithm from '@/views/model-center/model-factory/module/DeepLearning/LSTMAlgorithm'
  import LSTMPrediction from '@/views/model-center/model-factory/module/DeepLearning/LSTMPrediction'
  /* 交通指标 */
  import TrafficFlow from '@/views/model-center/model-factory/module/TrafficMetrics/TrafficFlow'
  import EntryLeavecity from '@/views/model-center/model-factory/module/TrafficMetrics/EntryLeavecity'
  import TravelTime from '@/views/model-center/model-factory/module/TrafficMetrics/TravelTime'
  import TravelSpeed from '@/views/model-center/model-factory/module/TrafficMetrics/TravelSpeed'
  import CongestionDelayIndex from '@/views/model-center/model-factory/module/TrafficMetrics/CongestionDelayIndex'
  import BasePasstime from '@/views/model-center/model-factory/module/TrafficMetrics/BasePasstime'
  import OnroadUrban from '@/views/model-center/model-factory/module/TrafficMetrics/OnroadUrban'
  import CorrelationAnalysis from '@/views/model-center/model-factory/module/TrafficMetrics/CorrelationAnalysis'
  import PassEfficiency from '@/views/model-center/model-factory/module/TrafficMetrics/PassEfficiency'
  import QueueLength from '@/views/model-center/model-factory/module/TrafficMetrics/QueueLength'
  import ParkingUrban from '@/views/model-center/model-factory/module/TrafficMetrics/ParkingUrban'
  import RidImbalance from '@/views/model-center/model-factory/module/TrafficMetrics/RidImbalance'
  import OverFlow from '@/views/model-center/model-factory/module/TrafficMetrics/OverFlow'
  import CongestionMileage from '@/views/model-center/model-factory/module/TrafficMetrics/CongestionMileage'

    export default {
    data () {
      return {
        menuKey: 1,
        tasks: {
          type: '',
          name: '',
          desc: '',
          params: {},
          dependence: {},
          preTasks: []
        }
      }
    },
    props: {
      node: {
        type: Object,
        default: () => {
        }
      },
      graph: {
        type: Object,
        default: () => {
        }
      },
      module: {
        type: Object,
        default: () => {
        }
      }
    },
    created () {
      this.initChildPage()
    },
    components: {
      /* 通用工具 */
      ShellTools,
      SparkTools,
      PythonTolls,
      Dependent,
      SqlTools,
      /* 输入输出 */
      DataSourceInput,
      DataOutput,
      SQLDataOutput,
      MysqlInput,
      MysqlOutput,
      HiveInput,
      HiveOutput,
      /* 预处理组件 */
      ColumnFilter,
      TableJoin,
      ColumnAlter,
      DataFilter,
      PacketAggregation,
      TimeShift,
      TimeDiffer,
      CharacterMerging,
      AccumulationCalculator,
      DataOffset,
      DataDiscretization,
      SubString,
      TimeFunction,
      AddColumn,
      Split,
      DefaultFilling,
      CharacterReplace,
      DataDeduplication,
      DataNormalization,
      /* 统计分析 */
      NuclearDensityAnalysis,
      CorrelationMatrix,
      BasicStatistics,
      /* 机器学习 */
      RegressionModelEvaluation,
      TimeSeriesAnalysis,
      DecisionTree,
      forecast,
      LinearRegression,
      MultiClassFicationAssessment,
      LogisticRegression,
      /* 深度学习 */
      LSTMAlgorithm,
      LSTMPrediction,
      /* 交通指标 */
      TrafficFlow,
      EntryLeavecity,
      TravelTime,
      TravelSpeed,
      CongestionDelayIndex,
      BasePasstime,
      OnroadUrban,
      CorrelationAnalysis,
      PassEfficiency,
      QueueLength,
      ParkingUrban,
      RidImbalance,
      OverFlow,
      CongestionMileage
    },
    watch: {
      'tasks.name': {
        handler (newVal, oldVal) {
            const name = this.createNodeName(newVal, newVal, 0)
            if (name !== this.tasks.name) {
              this.$message.error('组件名称重复，系统已重新生成')
            }
            this.tasks.name = name
        }
      }
    },

    methods: {
      /* 节点名称必须唯一 且必须为下一个节点的preTaskName赋值 */
      createNodeName (NodeName, NodeNameTemp, i) {
        if (!this.findNodeName(NodeNameTemp)) {
          ++i
          NodeNameTemp = this.createNodeName(NodeName, NodeName + '-' + i, i)
        }
        return NodeNameTemp
      },
      /* 查询组件名称是否重复 */
      findNodeName (NodeName) {
        const nodes = this.graph.getNodes()
        for (var i = 0; i < nodes.length; i++) {
          if (nodes[i].getModel().label === NodeName && nodes[i].getModel().id !== this.node.id) {
            return false
          }
        }
        return true
      },
      /* 获取子组件的参数并与公共参数进行封装组合 */
      getChildParams () {
        this.tasks.params = this.$refs.child.params
        return this.tasks
      },
      getPreNodeDataSourceIds () {
        return this.getPreNodeParams('dataStorage', 'String', 'datasource')
      },
      /* 获取上个节点的模型信息 预测组件专用 只能获取当前节点第一个锚点的数据 */
      getPreNodeModel () {
        const nodeTemp = this.graph.findById(this.node.id)
        let inEdges = nodeTemp.getInEdges()
        if (inEdges.length === 0) { // 找不到输入连线
          return { 'modelName': '', 'characterColumns': [] }
        }
        inEdges = this.sortEdges(inEdges)
        for (var i = 0; i < inEdges.length; i++) {
          const endPointIndex = inEdges[i].getModel().endPointIndex
          if (endPointIndex === 0) {
            const source = inEdges[i].getSource().getModel()
            if ('modelName' in source.tasks.params) {
              return { 'modelName': source.tasks.params.modelName, 'characterColumns': source.tasks.params.characterColumns }
            }
          }
        }
        return { 'modelName': '', 'characterColumns': [] }
      },
      // 获取上游节点字段信息
      getPreNodeColumList () {
        const preNodeParams = this.getPreNodeParams('columnList', 'Array', 'List')
        return preNodeParams
      },
      /* 获取上游节点表名 */
      getPreNodeTableName () {
        const preNodeParam = this.getPreNodeParam(this.node, 'newTable', 'String')
        return preNodeParam
      },
      /* 获取上游节点的数据源Id */
      getPreNodeDataSourceId () {
        return this.getPreNodeParam(this.node, 'dataStorage', 'String')
      },
      /* 获取当前节点需要生成的表名 */
      getLocalNodeTableName () {
        return this.module.workId + '_' + this.node.id
      },
      /* 获取上游节点参数 传入参数为节点和参数名 注意 此方法是固定获取params里的参数 */
      getPreNodeParam (node, cloumn, type) {
        var cloumTemp = clone(cloumn)
        const nodeTemp = this.graph.findById(node.id)
        const inEdges = nodeTemp.getInEdges()
        if (inEdges.length == 0) { // 找不到输入连线
          return this.returnDefalutValue(type)
        }
        // 获取上游节点的数据 默认认为只有一个节点
        const source = inEdges[0].getSource()
        let param
        // 如果上游节点是比例拆分 则进行判断
        if (source.getModel().nodeType === 'SPLIT' && cloumn === 'newTable') {
          let sourcePointIndex = inEdges[0].getModel().sourcePointIndex
          sourcePointIndex = parseInt(sourcePointIndex) + 1
          cloumTemp = cloumn + sourcePointIndex
        }
        if ('tasks' in source.getModel()) {
           param = source.getModel().tasks.params[cloumTemp]
        } else {
          return this.returnDefalutValue(type)
        }
        if (param == null || param == '' || param == undefined) {
          return this.getPreNodeParam(source.getModel(), cloumn, type)
        }
        return param
      },
      /* 默认根据数据类型返回默认参数 */
      returnDefalutValue (type) {
        if (type === 'String') {
          return ''
        }
        if (type === 'Array') {
          return []
        }
        if (type === 'Boolean') {
          return false
        }
        if (type === 'Number') {
          return 0
        }
      },
      /* 获取上游节点参数 支持多锚点查询 默认命名规则columnList0 1 2.... */
      getPreNodeParams (cloumn, type, name) {
        const nodeTemp = this.graph.findById(this.node.id)
        let inEdges = nodeTemp.getInEdges()
        if (inEdges.length === 0) { // 找不到输入连线
          var name10 = 'point0' + name
          var name20 = 'point1' + name
          var map = {}
          map[name10] = this.returnDefalutValue(type)
          map[name20] = this.returnDefalutValue(type)
          return map
        }
        inEdges = this.sortEdges(inEdges)
        var columnList = {}
        for (var i = 0; i < inEdges.length; i++) {
          var cloumTemp = clone(cloumn)
          const endPointIndex = inEdges[i].getModel().endPointIndex
          // 获取锚点的下标位置 并根据下标来命名参数
          var Name = 'point' + endPointIndex + name
          // point1List
          const source = inEdges[i].getSource()
          let param
          // 如果上游节点是比例拆分 则进行判断
          if (source.getModel().nodeType === 'SPLIT' && cloumn === 'newTable') {
            let sourcePointIndex = inEdges[i].getModel().sourcePointIndex
            sourcePointIndex = parseInt(sourcePointIndex) + 1
            cloumTemp = cloumn + sourcePointIndex
          }
          // 如果上个节点没有初始化的时候 返回空值
          if ('tasks' in source.getModel()) {
            param = source.getModel().tasks.params[cloumTemp]
          } else {
            columnList[Name] = this.returnDefalutValue(type)
            return columnList
          }
          if (param == null || param === '' || param === undefined) {
            columnList[Name] = this.getPreNodeParam(source.getModel(), cloumn, type)
          } else {
            columnList[Name] = param
          }
        }
        return columnList
      },
      /* 通过锚点下标对连线升序 */
      sortEdges (edges) {
        const sort = edges.sort(function (a, b) {
          return a.getModel().endPointIndex - b.getModel().endPointIndex
        })
        return edges
      },
      /* 子组件修改父组件tasks参数 */
      updateLocalNodeParam (name, value) {
        this.tasks[name] = value
      },
      /* 初始化子组件参数 */
      initChildPage () {
        if (this.node.tasks) {
          this.tasks = this.node.tasks
        }
      }
    }
  }

</script>
<style scoped>
  .ant-form-item {
    margin-bottom: 4px !important;
  }

  /deep/ .ant-modal-body {
    padding: 10px !important;
  }
</style>
