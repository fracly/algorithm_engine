import { axios } from '@/utils/request'

const api = {
  getReportListP: 'reportFrom/list-paging',
  deleteReport: 'reportFrom/delete',
  editStatusReport: 'reportFrom/onLine',
  getReportInfo: 'reportFrom/getReportInfo',
  getGroupsList: 'reportFrom/getGroup',
  getDictList: 'reportFrom/getDict',
  getRolesList: 'reportFrom/getRoles',
  uploadFile: 'reportFrom/create',
  createReport: 'reportFrom/createReport',
  reportExcel: 'reportFrom/parseSqlAndExcle',
  createGroup: 'reportFrom/createGroup',
  updateGroup: 'reportFrom/updateGroup',
  deleteGroup: 'reportFrom/deleteGroup',
  getGroupsListP: 'reportFrom/getGroupList'

}

export default api

export function getReportListP (parameter) {
  return axios({
    url: api.getReportListP,
    method: 'get',
    params: parameter
  })
}

export function deleteReport (parameter) {
  return axios({
    url: api.deleteReport,
    method: 'get',
    params: parameter
  })
}

export function editStatusReport (parameter) {
  return axios({
    url: api.editStatusReport,
    method: 'get',
    params: parameter
  })
}

export function getGroupsList (parameter) {
  return axios({
    url: api.getGroupsList,
    method: 'get',
    params: parameter
  })
}

export function getDictList (parameter) {
  return axios({
    url: api.getDictList,
    method: 'get',
    params: parameter
  })
}

export function getRolesList (parameter) {
  return axios({
    url: api.getRolesList,
    method: 'get',
    params: parameter
  })
}

export function uploadFile (parameter) {
  return axios({
    url: api.uploadFile,
    method: 'post',
    data: parameter
  })
}

export function createReport (parameter) {
  return axios({
    url: api.createReport,
    method: 'post',
    data: parameter
  })
}

export function getReportInfo (parameter) {
  return axios({
    method: 'get',
    url: api.getReportInfo,
    params: parameter
  })
}

export function reportExcel (parameter) {
  return axios({
    method: 'get',
    params: parameter,
    url: api.reportExcel
  })
}

export function createGroup (parameter) {
  return axios({
    method: 'get',
    params: parameter,
    url: api.createGroup
  })
}

export function updateGroup (parameter) {
  return axios({
    method: 'get',
    params: parameter,
    url: api.updateGroup
  })
}

export function deleteGroup (parameter) {
  return axios({
    method: 'get',
    params: parameter,
    url: api.deleteGroup
  })
}

export function getGroupsListP (parameter) {
  return axios({
    method: 'get',
    params: parameter,
    url: api.getGroupsListP
  })
}

export function loadReport (parameter) {
  return axios({
    url: '/customReport/view/' + parameter.reportCode,
    method: 'post',
    data: parameter,
    timeout: 20000
  })
}

export function queryList (parameter) {
  return axios({
    url: '/customReport/queryList/' + parameter.reportCode,
    method: 'post',
    data: parameter,
    timeout: 20000
  })
}

export function exportExcel (parameter) {
  return axios({
    url: '/customReport/exportExcel/' + parameter.reportCode,
    method: 'post',
    data: parameter,
    responseType: 'blob',
    timeout: 20000
  })
}

export function exportExcelNoPagination (parameter) {
  return axios({
    url: '/customReport/exportExcelNoPagination/' + parameter.reportCode,
    method: 'post',
    data: parameter,
    responseType: 'blob',
    timeout: 20000
  })
}
