import { axios } from '@/utils/request'

const api = {
  // 业务主题操作URL
  createTopic: '/modelcatalog/createTopic',
  deleteTopic: '/modelcatalog/deleteTopic',
  updateTopic: '/modelcatalog/updateTopic',
  queryTopic: '/modelcatalog/queryTopic',

  // 表操作
  createTable: '/modelcatalog/createTable',
  deleteTable: '/modelcatalog/deleteTable',
  updateTable: '/modelcatalog/updateTable',
  queryTable: '/modelcatalog/queryTable',
  queryModel: '/modelcatalog/queryModel',

  // 表字段操作
  addTableColumn: '/modelcatalog/table/add-column',
  updateTableColumn: '/modelcatalog/table/update-column',
  queryTableColumn: '/modelcatalog/table/query-column',

  // 表规则操作
  cudTableRule: '/modelcatalog/table/cud-rule',
  queryTableRuleType: '/modelcatalog/table-rule/query-type',
  queryTableRuleTypeCondi: '/modelcatalog/table-rule/query-condi',
  queryTableRuleTypeCondiParam: '/modelcatalog/table-rule/query-param',
  runTableRule: '/runRule/runTblRule',

  // 表字段规则操作
  cudColumnRule: '/modelcatalog/table-column/cud-rule',
  queryColumnRule: '/modelcatalog/column-rule/query',
  queryColumnRuleType: '/modelcatalog/column-rule/query-type',
  queryColumnRuleTypeCondi: '/modelcatalog/column-rule/query-condi',
  queryColumnRuleTypeCondiParam: '/modelcatalog/column-rule/query-param',
  runColumnRule: '/runRule/runColRule',

  // 表内容操作
  previewTable: '/modelcatalog/preview',

  // 定时管理
  scheduleBase: '/projects/',
  scheduleList: '/projects/scheduled-project',
  projectList: '/projects/list',

  // 日志查询
  taskInstanceList: '/projects/task-instance/list-paging',
  processInstanceList: '/projects/instance/list-paging',
  instanceTaskList: '/projects/task-instance/query-by-process-instance'
}

export default api

// Topic Functions
export function createTopic (parameter) {
  return axios({
    url: api.createTopic,
    method: 'get',
    params: parameter
  })
}

export function deleteTopic (parameter) {
  return axios({
    url: api.deleteTopic,
    method: 'get',
    params: parameter
  })
}

export function updateTopic (parameter) {
  return axios({
    url: api.updateTopic,
    method: 'get',
    params: parameter
  })
}

export function queryTopic (parameter) {
  return axios({
    url: api.queryTopic,
    method: 'get',
    params: parameter
  })
}

// Table Functions
export function createTable (parameter) {
  return axios({
    url: api.createTable,
    method: 'post',
    data: parameter,
    timeout: 0
  })
}

export function deleteTable (parameter) {
  return axios({
    url: api.deleteTable,
    method: 'get',
    params: parameter
  })
}

export function updateTable (parameter) {
  return axios({
    url: api.updateTable,
    method: 'get',
    params: parameter
  })
}

export function queryTable (parameter) {
  return axios({
    url: api.queryTable,
    method: 'get',
    params: parameter
  })
}

export function queryModel (parameter) {
  return axios({
    url: api.queryModel,
    method: 'get',
    params: parameter
  })
}

// Table Column Functions
export function addTableColumn (parameter) {
  return axios({
    url: api.addTableColumn,
    method: 'post',
    params: parameter
  })
}

export function updateTableColumn (parameter) {
  return axios({
    url: api.updateTableColumn,
    method: 'post',
    params: parameter
  })
}

export function queryTableColumn (parameter) {
  return axios({
    url: api.queryTableColumn,
    method: 'get',
    params: parameter
  })
}

// Table Rule Function
export function cudTableRule (parameter) {
  return axios({
    url: api.cudTableRule,
    method: 'post',
    params: parameter
  })
}

export function queryTableRuleType (parameter) {
  return axios({
    url: api.queryTableRuleType,
    method: 'get',
    params: parameter
  })
}

export function queryTableRuleTypeCondi (parameter) {
  return axios({
    url: api.queryTableRuleTypeCondi,
    method: 'get',
    params: parameter
  })
}

export function queryTableRuleTypeCondiParam (parameter) {
  return axios({
    url: api.queryTableRuleTypeCondiParam,
    method: 'get',
    params: parameter
  })
}

export function runTableRule (parameter) {
  return axios({
    url: api.runTableRule,
    method: 'get',
    params: parameter
  })
}

// Table Column Rule Function
export function cudColumnRule (parameter) {
  return axios({
    url: api.cudColumnRule,
    method: 'post',
    params: parameter
  })
}

export function queryColumnRule (parameter) {
  return axios({
    url: api.queryColumnRule, // 跟字段信息是一个接口，包含规则信息
    method: 'get',
    params: parameter
  })
}

export function queryColumnRuleType (parameter) {
  return axios({
    url: api.queryColumnRuleType,
    method: 'get',
    params: parameter
  })
}

export function queryColumnRuleTypeCondi (parameter) {
  return axios({
    url: api.queryColumnRuleTypeCondi,
    method: 'get',
    params: parameter
  })
}

export function queryColumnRuleTypeCondiParam (parameter) {
  return axios({
    url: api.queryColumnRuleTypeCondiParam,
    method: 'get',
    params: parameter
  })
}

export function runColumnRule (parameter) {
  return axios({
    url: api.runColumnRule,
    method: 'get',
    params: parameter
  })
}

// Table Content Function
export function previewTable (parameter) {
  return axios({
    url: api.previewTable,
    method: 'get',
    params: parameter
  })
}

// Schedule Functions
export function createSchedule (parameter) {
  return axios({
    url: api.scheduleBase + parameter.projectName + '/schedule/create',
    method: 'post',
    data: parameter
  })
}

export function deleteSchedule (parameter) {
  return axios({
    url: api.scheduleBase + parameter.projectName + '/schedule/delete',
    method: 'get',
    params: parameter
  })
}

export function updateSchedule (parameter) {
  return axios({
    url: api.scheduleBase + parameter.projectName + '/schedule/update',
    method: 'post',
    data: parameter
  })
}

export function querySchedule (parameter) {
  return axios({
    url: api.scheduleList,
    method: 'get',
    params: parameter
  })
}

export function onlineSchedule (parameter) {
  const formData = new FormData()
  formData.append('id', parameter.id)
  return axios({
    url: api.scheduleBase + parameter.projectName + '/schedule/online',
    method: 'post',
    data: formData
  })
}

export function offlineSchedule (parameter) {
  const formData = new FormData()
  formData.append('id', parameter.id)
  return axios({
    url: api.scheduleBase + parameter.projectName + '/schedule/offline',
    method: 'post',
    data: formData
  })
}

// Others
export function projectList (parameter) {
  return axios({
    url: api.projectList,
    method: 'get',
    params: parameter
  })
}

export function processDefinitionList (parameter) {
  return axios({
    url: api.scheduleBase + parameter.projectName + '/process/list',
    method: 'get',
    params: parameter
  })
}

export function taskInstanceList (parameter) {
  return axios({
    url: api.taskInstanceList,
    method: 'get',
    params: parameter
  })
}

export function processInstanceList (parameter) {
  return axios({
    url: api.processInstanceList,
    method: 'get',
    params: parameter
  })
}

export function instanceTaskList (parameter) {
  return axios({
    url: api.instanceTaskList,
    method: 'get',
    params: parameter
  })
}

export function executeInstance (parameter) {
  return axios({
    url: '/projects/' + parameter.processDefinitionName + '/executors/execute',
    method: 'post',
    params: parameter
  })
}

export function deleteInstance (parameter) {
  return axios({
    url: '/projects/' + parameter.processDefinitionName + '/instance/delete',
    method: 'get',
    params: parameter
  })
}
