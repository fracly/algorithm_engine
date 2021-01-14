import { axios } from '@/utils/request'

const api = {
  getResourcesListP: 'resources/list-paging',
  getResourcesList: 'resources/list',
  deleteResource: 'resources/delete',
  createFile: 'resources/create',
  createResourceFile: 'resources/online-create',
  resourceRename: 'resources/update',
  resourceVerifyName: 'resources/verify-name',
  updateContent: 'resources/update-content',
  getViewResources: 'resources/view',
  getUdfFuncListP: 'resources/udf-func/list-paging',
  deleteUdf: 'resources/udf-func/delete',
  createUdfFunc: 'resources/udf-func/create',
  updateUdfFunc: 'resources/udf-func/update'
}

export default api

export function getResourcesListP (parameter) {
  return axios({
    url: api.getResourcesListP,
    method: 'get',
    params: parameter
  })
}
export function getResourcesList (parameter) {
  return axios({
    url: api.getResourcesList,
    method: 'get',
    params: parameter
  })
}
export function deleteResource (parameter) {
  return axios({
    url: api.deleteResource,
    method: 'get',
    params: parameter
  })
}
export function createFile (parameter) {
  return axios({
    url: api.createFile,
    method: 'post',
    data: parameter,
    timeout: 0,
    emulateJSON: false,
    onUploadProgress (progressEvent) {
      debugger
      const loaded = progressEvent.loaded
      // Total attachment size
      const total = progressEvent.total
      // self.pre = Math.floor(100 * loaded / total)
    }
  })
}
export function createResourceFile (parameter) {
  return axios({
    url: api.createResourceFile,
    method: 'post',
    params: parameter
  })
}
export function resourceRename (parameter) {
  return axios({
    url: api.resourceRename,
    method: 'post',
    params: parameter
  })
}

export function resourceVerifyName (parameter) {
  return axios({
    url: api.resourceVerifyName,
    method: 'get',
    params: parameter
  })
}
export function getViewResources (parameter) {
  return axios({
    url: api.getViewResources,
    method: 'get',
    params: parameter
  })
}
export function updateContent (parameter) {
  return axios({
    url: api.updateContent,
    method: 'post',
    data: parameter
  })
}
export function getUdfFuncListP (parameter) {
  return axios({
    url: api.getUdfFuncListP,
    method: 'get',
    params: parameter
  })
}

export function updateUdfFunc (parameter) {
  return axios({
    url: api.updateUdfFunc,
    method: 'post',
    params: parameter
  })
}
export function createUdfFunc (parameter) {
  return axios({
    url: api.createUdfFunc,
    method: 'post',
    params: parameter
  })
}
export function deleteUdf (parameter) {
  return axios({
    url: api.deleteUdf,
    method: 'get',
    params: parameter
  })
}