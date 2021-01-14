import { axios } from '@/utils/request'

const api = {
  getListP: 'serverManager/list-paging',
  editServiceState: 'serverManager/onLine',
  deleteService: 'serverManager/delete',
  getAnalysisSql: 'serverManager/analysisSql',
  addServer: 'serverManager/createServer',
  addParam: 'serverManager/createParam',
  updateServer: 'serverManager/updateServer',
  getParam: 'serverManager/getParam',
  getServerInfo: 'serverManager/getInfo',
  applicationList: 'service/application/list',
  getAppLog: 'service/callStatList',
  deleteApp: 'service/application/delete',
  interList: 'service/interface/list',
  applicationInfo: 'service/getApplicationInfo',
  applicationFailInfo: 'service/getApplicationFailInfo',
  createApp: 'service/application/create',
  getAppInfo: '/service/application/detail',
  getTypeList: '/service/getTypeList',
  getListAllP: 'serverManager/list-paging-all'

}

export default api

export function getListP (parameter) {
  return axios({
    url: api.getListP,
    method: 'get',
    params: parameter
  })
}
export function getListAllP (parameter) {
  return axios({
    url: api.getListAllP,
    method: 'get',
    params: parameter
  })
}
export function editServiceState (parameter) {
  return axios({
    url: api.editServiceState,
    method: 'get',
    params: parameter
  })
}
export function deleteService (parameter) {
  return axios({
    url: api.deleteService,
    method: 'get',
    params: parameter
  })
}
export function getAnalysisSql (parameter) {
  return axios({
    url: api.getAnalysisSql,
    method: 'get',
    params: parameter
  })
}
export function addServer (parameter) {
  return axios({
    url: api.addServer,
    method: 'post',
    data: parameter
  })
}
export function addParam (parameter) {
  return axios({
    url: api.addParam,
    method: 'post',
    data: parameter
  })
}
export function updateServer (parameter) {
  return axios({
    url: api.updateServer,
    method: 'post',
    data: parameter
  })
}
export function getServerInfo (parameter) {
  return axios({
    url: api.getServerInfo,
    method: 'get',
    params: parameter
  })
}
export function getParam (parameter) {
  return axios({
    url: api.getParam,
    method: 'get',
    params: parameter
  })
}
export function getToken (parameter) {
  return axios({
    url: Glod.token + 'oauth/token?grant_type=client_credentials&client_id=user-client&client_secret=123456',
    method: 'post',
    params: parameter
  })
}
export function testServer (parameter) {
  return axios({
    url: Glod.token + `ms-consumer/encapsula/v1/service/` + parameter.code + '?token=' + parameter.token,
    method: 'post',
    data: parameter,
    timeout: 20000
  })
}
export function getRuleList (parameter) {
  return axios({
    url: Glod.token + `ms-consumer/sentinel/v1/flow/list?token=` + parameter.token,
    method: 'post',
    data: parameter
  })
}
export function deleteRule (parameter) {
  return axios({
    url: Glod.token + `ms-consumer/sentinel/v1/flow/delete?token=` + parameter.token,
    method: 'post',
    data: parameter
  })
}
export function addRule (parameter) {
  return axios({
    url: Glod.token + `ms-consumer/sentinel/v1/flow/add?token=` + parameter.token,
    method: 'post',
    data: parameter
  })
}
export function updateRule (parameter) {
  return axios({
    url: Glod.token + `ms-consumer/sentinel/v1/flow/update?token=` + parameter.token,
    method: 'post',
    data: parameter
  })
}
export function applicationList (parameter) {
  return axios({
    url: api.applicationList,
    method: 'get',
    params: parameter
  })
}
export function getAppLog (parameter) {
  return axios({
    url: api.getAppLog,
    method: 'get',
    params: parameter
  })
}
export function interList (parameter) {
  return axios({
    url: api.interList,
    method: 'get',
    params: parameter
  })
}
export function applicationInfo (parameter) {
  return axios({
    url: api.applicationInfo,
    method: 'get',
    params: parameter
  })
}
export function applicationFailInfo (parameter) {
  return axios({
    url: api.applicationFailInfo,
    method: 'get',
    params: parameter
  })
}
export function createApp (parameter) {
  return axios({
    url: api.createApp,
    method: 'post',
    params: parameter
  })
}
export function getAppInfo (parameter) {
  return axios({
    url: api.getAppInfo,
    method: 'get',
    params: parameter
  })
}
export function getTypeList (parameter) {
  return axios({
    url: api.getTypeList,
    method: 'get',
    params: parameter
  })
}
export function deleteApp (parameter) {
  return axios({
    url: api.deleteApp,
    method: 'get',
    params: parameter
  })
}