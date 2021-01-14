import { axios } from '@/utils/request'
const api = {
  getInfo: '/cloudera/getIO',
  getHostList: 'cloudera/getHosts',
  getCpu: '/cloudera/getCpu',
  getVc: '/cloudera/getVc',
  getMem: '/cloudera/getMem'

}
export default api

export function getInfo (parameter) {
  return axios({
    url: api.getInfo,
    method: 'get',
    params: parameter
  })
}
export function getHostList (parameter) {
  return axios({
    url: api.getHostList,
    method: 'get',
    params: parameter
  })
}
export function getCpu (parameter) {
  return axios({
    url: api.getCpu,
    method: 'get',
    params: parameter
  })
}

export function getVc (parameter) {
  return axios({
    url: api.getVc,
    method: 'get',
    params: parameter
  })
}

export function getMem (parameter) {
  return axios({
    url: api.getMem,
    method: 'get',
    params: parameter
  })
}
