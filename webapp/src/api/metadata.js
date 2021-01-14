import { axios } from '@/utils/request'

const api = {
  metadataList: '/schemas/querySchema',
  getBaseLayerList: '/schemas/getSchemaBusinessTopic',
  cleanAndInit: '/schemas/clean2init',
  metadataDownload: '/schemas/download',
  getBranchAndVersionList: '/schemas/querySchemaBranchAndVersion'
}

export default api

export function metadataList (parameter) {
  return axios({
    url: api.metadataList,
    method: 'get',
    params: parameter
  })
}

export function getBaseLayerList (parameter) {
  return axios({
    url: api.getBaseLayerList,
    method: 'get',
    params: parameter
  })
}

export function cleanAndInit (parameter) {
  return axios({
    url: api.cleanAndInit,
    method: 'get',
    params: parameter,
    timeout: 0
  })
}

export function metadataDownload (parameter) {
  const a = document.createElement('a')
  a.setAttribute('download', '')
  a.setAttribute('href', '/escheduler' + api.metadataDownload + '?topic=' + parameter.topic + '&layer=' + parameter.layer)
  console.log(a)
  a.click()
}

export function getBranchAndVersionList (parameter) {
  return axios({
    url: api.getBranchAndVersionList,
    method: 'get',
    params: parameter
  })
}
