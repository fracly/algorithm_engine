import Vue from 'vue'

Vue.filter('execution_status', function (value) {
  if (value === 'SUBMITTED_SUCCESS') return '提交成功'
  else if (value === 'RUNNING_EXEUTION') return '执行中'
  else if (value === 'READY_PAUSE') return '等待暂停'
  else if (value === 'PAUSE') return '暂停'
  else if (value === 'READY_STOP') return '等待停止'
  else if (value === 'STOP') return '停止'
  else if (value === 'FAILURE') return '失败'
  else if (value === 'SUCCESS') return '成功'
  else if (value === 'NEED_FAULT_TOLERANCE') return '需要容错'
  else if (value === 'KILL') return '杀死'
  else if (value === 'WAITTING_THREAD') return '线程等待'
  else if (value === 'WAITTING_DEPEND') return '依赖等待'
  else if (value === 'PARAMS_ERROR') return '参数错误'
  else return '未知'
})

Vue.filter('process_execute_type', function (value) {
  if (value === 'START_PROCESS') return '启动模型'
  else if (value === 'START_CURRENT_TASK_PROCESS') return '启动当前模型'
  else if (value === 'RECOVER_TOLERANCE_FAULT_PROCESS') return '恢复容错模型'
  else if (value === 'RECOVER_SUSPENDED_PROCESS') return '恢复挂起模型'
  else if (value === 'START_FAILURE_TASK_PROCESS') return '恢复出错模型'
  else if (value === 'COMPLEMENT_DATA') return '补数据'
  else if (value === 'SCHEDULER') return '调度执行'
  else if (value === 'REPEAT_RUNNING') return '重复执行'
  else if (value === 'PAUSE') return '暂停'
  else if (value === 'STOP') return '停止'
  else if (value === 'RECOVER_WAITTING_THREAD') return '线程恢复执行'
  else return value
})

Vue.filter('yes_or_no', function (value) {
  if (value.upperCase === 'YES') return '是'
  else return '否'
})

Vue.filter('task_type', function (value) {
  if (value === 'SHELL') return 'SHELL工具'
  else if (value === 'SQL') return 'SQL工具'
  else if (value === 'SPARK') return 'SPARK'
  else if (value === 'PYTHON') return 'PYTHON'
  else if (value === 'INPUT') return '数据源输入'
  else if (value === 'OUTPUT') return '数据输出'
  else if (value === 'SPLIT') return '比列拆分'
  else if (value === 'DEFAULT_FILLING') return '缺省值填充'
  else if (value === 'DATA_FILTER') return '数据过滤'
  else if (value === 'COLUMN_FILTER') return '列过滤'
  else if (value === 'GROUP_AGGREGATION') return '分组聚合'
  else if (value === 'COLUMN_ALTER') return '列名修改'
  else if (value === 'TIME_SHIFT') return '时间格式转化'
  else if (value === 'TIME_DIFFER') return '时间差'
  else if (value === 'CHARACTER_MERGING') return '字符串拼接'
  else if (value === 'ACCUMULATION_CALCULATOR') return '累加器计算'
  else if (value === 'TABLE_JOIN') return '表关联'
  else if (value === 'DATA_OFFSET') return '数据偏移'
  else if (value === 'DATA_DISCRETIZATION') return '数据离散化'
  else if (value === 'SUBSTRING') return '字符串截取'
  else if (value === 'TIME_FUNCTION') return '时间函数'
  else if (value === 'ADD_COLUMN') return '增加新列'
  else if (value === 'CHARACTER_REPLACE') return '字符串替换'
  else if (value === 'KERNEL_DENSITY_ESTIMATION') return '核密度估计'
  else if (value === 'LINEAR_REGRESSION') return '线性回归'
  else if (value === 'REGRESSION_MODEL_ASSESSMENT') return '回归模型评估'
  else if (value === 'FORECAST') return '预测'
  else if (value === 'DECISION_TREE') return '决策树-分类'
  else if (value === 'MULTI_CLASSIFICATION_ASSESSMENT') return '多分类评估'
  else if (value === 'DATA_DEDUPLICATION') return '数据去重'
  else if (value === 'DATA_NORMALIZATION') return '归一化'
  else if (value === 'BASIC_STATISTICS') return '基本统计信息'
  else if (value === 'TRAFFIC_FLOW') return '交通流量'
  else if (value === 'ENTRY_LEAVE_CITY') return '出入城流量'
  else if (value === 'TRAVEL_TIME') return '行程时间'
  else if (value === 'MYSQL_INPUT') return 'Mysql输入'
  else if (value === 'MYSQL_OUTPUT') return 'Mysql输出'
  else if (value === 'HIVE_INPUT') return 'Hive输入'
  else if (value === 'HIVE_OUTPUT') return 'Hive输出'
  else if (value === 'TRAVEL_SPEED') return '行程速度'
  else if (value === 'CONGESTION_DELAY_INDEX') return '拥堵延时指数'
  else if (value === 'BASE_PASS_TIME') return '历史通行时长参数'
  else if (value === 'ONROAD_URBAN') return '在途量'
  else if (value === 'CORRELATION_ANALYSIS') return '相关性分析'
  else if (value === 'PASS_EFFICIENCY') return '通行效率指数'
  else if (value === 'QUEUE_LENGTH') return '排队长度'
  else if (value === 'PARKING_URBAN') return '城区驻车量'
  else if (value === 'LOGISTIC_REGRESSION') return '逻辑回归'
  else if (value === 'RID_IMBALANCE') return '路段失衡指数'
  else if (value === 'OVER_FLOW') return '溢流'
  else if (value === 'CONGESTION_MILEAGE') return '道路路况里程占比'



  else return value
})