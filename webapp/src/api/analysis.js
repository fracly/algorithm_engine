import { axios } from '@/utils/request'

const api = {
    getProcessDefinitionCount: '/projects/analysis/process/statistic',
    getProcessInstance30AvgCount: '/projects/analysis/process/statistic/avg-run-times',
    getProcessInstanceTodayCount: '/projects/analysis/process/statistic/today-invoke-times',
    getProcessInstanceSuccessCount: '/projects/analysis/process/statistic/today-invoke/success-times',
    getProcessInstanceFailedCount: '/projects/analysis/process/statistic/today-invoke/failed-times',

    getReportCount: '/projects/analysis/report/statistic',
    getReportInvoke30AvgCount: '/projects/analysis/report/statistic/avg-invoke-times',

    getServiceCount: '/projects/analysis/service/statistic',
    getServiceInvokeCount: '/projects/analysis/service/statistic/invoke-times',
    getServiceInvoke30AvgCount: '/projects/analysis/service/statistic/avg-invoke-times',

    getServiceInvokeTimesByDay: '/projects/analysis/service/statistic/invoke-times/by-day',
    getHiveRecordRankTop10: '/projects/analysis/hive/record-rank/top10',
    getHiveRecordTotal: '/projects/analysis/hive/record-total',
    getHiveTableTotal: '/projects/analysis/hive/table-total',

    getDepartmentUsage: '/homePage/getTopZD',
    getDetachmentUsage: '/homePage/getTopPV'
}
export default api

export function getProcessDefinitionCount (parameter) {
    return axios({
        url: api.getProcessDefinitionCount,
        method: 'get',
        params: parameter
    })
}

export function getProcessInstance30AvgCount (parameter) {
  return axios({
    url: api.getProcessInstance30AvgCount,
    method: 'get',
    params: parameter
  })
}

export function getProcessInstanceTodayCount (parameter) {
  return axios({
    url: api.getProcessInstanceTodayCount,
    method: 'get',
    params: parameter
  })
}
export function getProcessInstanceSuccessCount (parameter) {
  return axios({
    url: api.getProcessInstanceSuccessCount,
    method: 'get',
    params: parameter
  })
}

export function getProcessInstanceFailedCount (parameter) {
  return axios({
    url: api.getProcessInstanceFailedCount,
    method: 'get',
    params: parameter
  })
}

export function getReportCount (parameter) {
  return axios({
    url: api.getReportCount,
    method: 'get',
    params: parameter
  })
}

export function getReportInvoke30AvgCount (parameter) {
  return axios({
    url: api.getReportInvoke30AvgCount,
    method: 'get',
    params: parameter
  })
}

export function getServiceCount (parameter) {
  return axios({
    url: api.getServiceCount,
    method: 'get',
    params: parameter
  })
}

export function getServiceInvokeCount (parameter) {
  return axios({
    url: api.getServiceInvokeCount,
    method: 'get',
    params: parameter
  })
}

export function getServiceInvoke30AvgCount (parameter) {
  return axios({
    url: api.getServiceInvoke30AvgCount,
    method: 'get',
    params: parameter
  })
}

export function getServiceInvokeTimesByDay (parameter) {
  return axios({
    url: api.getServiceInvokeTimesByDay,
    method: 'get',
    params: parameter
  })
}

export function getHiveRecordRankTop10 (parameter) {
  return axios({
    url: api.getHiveRecordRankTop10,
    method: 'get',
    params: parameter
  })
}

export function getHiveRecordTotal (parameter) {
  return axios({
    url: api.getHiveRecordTotal,
    method: 'get',
    params: parameter
  })
}

export function getHiveTableTotal (parameter) {
  return axios({
    url: api.getHiveTableTotal,
    method: 'get',
    params: parameter
  })
}

export function getDepartmentUsage (parameter) {
  return axios({
    url: api.getDepartmentUsage,
    method: 'get',
    params: parameter
  })
}

export function getDetachmentUsage (parameter) {
  return axios({
    url: api.getDetachmentUsage,
    method: 'get',
    params: parameter
  })
}
