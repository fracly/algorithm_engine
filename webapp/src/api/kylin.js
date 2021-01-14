import { axios } from '@/utils/request'

const api = {
  // cube调度
  queryCubeList: '/kylin/getCubesList',
  // cube历史日志
  queryLogList: '/kylin/getLogsList',
  //
  queryStepLog: '/kylin/getStepLog',
  //
  onlineSchedule: '/projects/kylinSchedule/online',
  offlineSchedule: '/projects/kylinSchedule/offline',
  createSchedule: '/projects/kylinSchedule/create',
  updateSchedule: '/projects/kylinSchedule/update',
  deleteSchedule: '/projects/kylinSchedule/delete'
}

export default api

//  Functions
export function queryCubeList (parameter) {
  return axios({
    url: api.queryCubeList,
    method: 'get',
    params: parameter
  })
}

export function queryLogList (parameter) {
  return axios({
    url: api.queryLogList,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}
export function queryStepLog (parameter) {
  return axios({
    url: api.queryStepLog,
    method: 'get',
    params: parameter
  })
}

export function onlineSchedule (parameter) {
  return axios({
    url: api.onlineSchedule,
    method: 'post',
    params: parameter
  })
}

export function offlineSchedule (parameter) {
  return axios({
    url: api.offlineSchedule,
    method: 'post',
    params: parameter
  })
}

export function createSchedule (parameter) {
  return axios({
    url: api.createSchedule,
    method: 'post',
    params: parameter
  })
}
export function updateSchedule (parameter) {
  return axios({
    url: api.updateSchedule,
    method: 'post',
    params: parameter
  })
}

export function deleteSchedule (parameter) {
  return axios({
    url: api.deleteSchedule,
    method: 'get',
    params: parameter
  })
}