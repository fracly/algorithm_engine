import { axios } from '@/utils/request'

const api = {
  /* 数据源相关接口 */
  getDataBaseData: '/datasources/list',
  getTableData: '/datasources/table-list-by-id',
  getColumData: 'datasources/column-list-by-id',
  /* 工作组相关接口 */
  getAllGroup: '/worker-group/all-groups',
  /* 模型组相关接口 */
  createProject: '/projects/create',
  updateProject: '/projects/update',
  deleteProject: '/projects/delete',
  getAllWorkGroup: '/projects/instance/workGroupList',
  getAllInstance: '/projects/instance/search',
  /* 流程任务运行参数 */
  getWorkInfo: 'projects/process/getWorkId',
  updateWorkInfo: 'projects/process/updateWorkId',
  /* 资源相关信息 */
  getUdfs: '/resources/udf-func/list',
  getResources: '/resources/list',
  /* 模型相关操作 */
  processUrl: '/projects',
  /* 日志相关 */
  logdetail: '/log/detail',
  getAllFlagInstance: '/projects/instance/searchFlag'

}

export default api

export function logdetail (parameter) {
  return axios({
    url: api.logdetail,
    method: 'get',
    params: parameter
  })
}
/* 节点运行结果 */
export function opreaResult (parameter) {
  return axios({
    url: `/projects/${parameter.projectName}/instance/result`,
    method: 'get',
    params: parameter
  })
}

export function startProcess (parameter) {
  return axios({
    url: `/projects/${parameter.projectName}/executors/start-process-instance`,
    method: 'post',
    params: parameter
  })
}

export function stopProcess (parameter) {
  return axios({
    url: `/projects/${parameter.projectName}/executors/execute`,
    method: 'post',
    params: parameter
  })
}

export function queryStatus (parameter) {
  return axios({
    url: `/projects/${parameter.projectName}/instance/task-list-by-process-id`,
    method: 'get',
    params: parameter
  })
}
export function getWorkInfo (parameter) {
  return axios({
    url: api.getWorkInfo,
    method: 'get',
    params: parameter
  })
}
export function updateWorkInfo (parameter) {
  return axios({
    url: api.updateWorkInfo,
    method: 'get',
    params: parameter
  })
}
export function deleteProcess (parameter) {
  return axios({
    url: `/projects/${parameter.workGroup}/process/delete`,
    method: 'get',
    params: parameter
  })
}

export function releaseProcess (parameter) {
  return axios({
    url: `/projects/${parameter.workGroup}/process/release`,
    method: 'post',
    params: parameter
  })
}
export function editProcess (parameter) {
  return axios({
    url: `/projects/${parameter.workGroup}/process/edit`,
    method: 'post',
    data: parameter
  })
}
export function sharedProcess (parameter) {
  return axios({
    url: `/projects/${parameter.workGroup}/process/share`,
    method: 'post',
    params: parameter
  })
}
// 修改为同步方法
export async function createProcess (parameter) {
  const url = api.processUrl + '/' + parameter.workGroup + '/process/save'
  return await axios({
    url: url,
    method: 'post',
    data: parameter
  })
}
// 修改为同步方法
export async function updateProcess (parameter) {
  const url = api.processUrl + '/' + parameter.workGroup + '/process/update'
  return await axios({
    url: url,
    method: 'post',
    data: parameter
  })
}
export function loadTopo (parameter) {
  const url = api.processUrl + '/' + parameter.workGroup + '/process/select-by-id'
  return axios({
    url: url,
    method: 'get',
    params: parameter
  })
}
export function getResources (parameter) {
  return axios({
    url: api.getResources,
    method: 'get',
    params: parameter
  })
}
/* 获取UDF */
export function getUdfs (parameter) {
  return axios({
    url: api.getUdfs,
    method: 'get',
    params: parameter
  })
}
/* 获取数据库 */
export function getDataBaseData (parameter) {
  return axios({
    url: api.getDataBaseData,
    method: 'get',
    params: parameter
  })
}
/* 获取表名 */
export function getTableData (parameter) {
  return axios({
    url: api.getTableData,
    method: 'post',
    params: parameter
  })
}
export function getColumData (parameter) {
  return axios({
    url: api.getColumData,
    method: 'post',
    params: parameter
  })
}
export function createProject (parameter) {
  return axios({
    url: api.createProject,
    method: 'post',
    params: parameter
  })
}
export function updateProject (parameter) {
  return axios({
    url: api.updateProject,
    method: 'post',
    params: parameter
  })
}
export function deleteProject (parameter) {
  return axios({
    url: api.deleteProject,
    method: 'get',
    params: parameter
  })
}
export function getAllGroup (parameter) {
  return axios({
    url: api.getAllGroup,
    method: 'get',
    params: parameter
  })
}
export function getAllWorkGroup (parameter) {
  return axios({
    url: api.getAllWorkGroup,
    method: 'get',
    params: parameter
  })
}
export function getAllInstance (parameter) {
  return axios({
    url: api.getAllInstance,
    method: 'get',
    params: parameter
  })
}
export function getAllFlagInstance (parameter) {
  return axios({
    url: api.getAllFlagInstance,
    method: 'get',
    params: parameter
  })
}