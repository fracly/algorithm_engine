import { axios } from '@/utils/request'

const api = {
  getlabelList: '/hybqLebal/list-paging',
}


export function getLeabalList (parameter) {
  return axios({
    url: api.getlabelList,
    method: 'get',
    params: parameter
  })
}