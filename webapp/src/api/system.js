import { axios } from '@/utils/request'

const api = {

  systemTreeMenuList: '/system/search-menu/tree',
  systemFlatMenuList: '/system/search-menu/flat',
  systemMenuDisable: '/system/disable-menu',
  systemMenuEnable: '/system/enable-menu',
  systemMenuCreate: '/system/create-menu',
  systemMenuUpdate: '/system/update-menu',
  systemMenuDelete: '/system/delete-menu',
  systemRoleList: '/system/search-role',
  systemRoleDisable: '/system/disable-role',
  systemRoleEnable: '/system/enable-role',
  systemRoleCreate: '/system/create-role',
  systemRoleDelete: '/system/delete-role',
  systemRoleUpdate: '/system/update-role',
  systemUserList: '/system/search-user',
  systemUserDisable: '/system/disable-user',
  systemUserEnable: '/system/enable-user',
  systemUserCreate: '/system/create-user',
  systemUserDelete: '/system/delete-user',
  systemUserUpdate: '/system/update-user',
  systemUserModifyPassword: '/system/user/modify-password',
  roleMenuList: '/system/role-menu/list',
  roleMenuUpdate: '/system/role-menu/update',
  userRoleList: '/system/user-role/list',
  userRoleUpdate: '/system/user-role/update',

  queryWorkerGroupList: '/worker-group/list-paging',
  workerGroupCreate: '/worker-group/save',
  workerGroupUpdate: '/worker-group/update',
  workerGroupDelete: '/worker-group/delete-by-id',

  queryTenantList: '/tenant/list-paging',
  tenantCreate: '/tenant/create',
  tenantUpdate: '/tenant/update',
  tenantDelete: '/tenant/delete',
  queueList: '/queue/list',

  queryQueueList: '/queue/list-paging',
  queueCreate: '/queue/create',
  queueUpdate: '/queue/update',
  verifyQueue: '/queue/verify-queue',
  queueDelete: '/queue/delete',

  conf: '/system/conf'
}

export default api

export function systemTreeMenuList (parameter) {
    return axios({
        url: api.systemTreeMenuList,
        method: 'get',
        params: parameter
    })
}

export function systemFlatMenuList (parameter) {
  return axios({
    url: api.systemFlatMenuList,
    method: 'get',
    params: parameter
  })
}

export function systemMenuDisable (parameter) {
  return axios({
    url: api.systemMenuDisable,
    method: 'get',
    params: parameter
  })
}

export function systemMenuDelete (parameter) {
  return axios({
    url: api.systemMenuDelete,
    method: 'get',
    params: parameter
  })
}

export function systemMenuEnable (parameter) {
  return axios({
    url: api.systemMenuEnable,
    method: 'get',
    params: parameter
  })
}

export function systemMenuCreate (parameter) {
  return axios({
    url: api.systemMenuCreate,
    method: 'post',
    params: parameter
  })
}

export function systemMenuUpdate (parameter) {
  return axios({
    url: api.systemMenuUpdate,
    method: 'post',
    params: parameter
  })
}

export function systemRoleList (parameter) {
  return axios({
    url: api.systemRoleList,
    method: 'get',
    params: parameter
  })
}

export function systemRoleDisable (parameter) {
  return axios({
    url: api.systemRoleDisable,
    method: 'get',
    params: parameter
  })
}

export function systemRoleEnable (parameter) {
  return axios({
    url: api.systemRoleEnable,
    method: 'get',
    params: parameter
  })
}

export function systemRoleDelete (parameter) {
  return axios({
    url: api.systemRoleDelete,
    method: 'get',
    params: parameter
  })
}

export function systemRoleCreate (parameter) {
  return axios({
    url: api.systemRoleCreate,
    method: 'post',
    params: parameter
  })
}

export function systemRoleUpdate (parameter) {
  return axios({
    url: api.systemRoleUpdate,
    method: 'post',
    params: parameter
  })
}

export function systemUserList (parameter) {
  return axios({
    url: api.systemUserList,
    method: 'get',
    params: parameter
  })
}

export function systemUserDisable (parameter) {
  return axios({
    url: api.systemUserDisable,
    method: 'get',
    params: parameter
  })
}

export function systemUserEnable (parameter) {
  return axios({
    url: api.systemUserEnable,
    method: 'get',
    params: parameter
  })
}

export function systemUserCreate (parameter) {
  return axios({
    url: api.systemUserCreate,
    method: 'post',
    params: parameter
  })
}

export function systemUserDelete (parameter) {
  return axios({
    url: api.systemUserDelete,
    method: 'get',
    params: parameter
  })
}

/**
 * 更新用户信息
 */
export function systemUserUpdate (parameter) {
  return axios({
    url: api.systemUserUpdate,
    method: 'post',
    params: parameter
  })
}

/**
 * 重设用户密码
 */
export function systemUserModifyPassword (parameter) {
  return axios({
    url: api.systemUserModifyPassword,
    method: 'post',
    params: parameter
  })
}

/**
 * 获取角色权限列表
 */
export function roleMenuList (parameter) {
  return axios({
    url: api.roleMenuList,
    method: 'get',
    params: parameter
  })
}

/**
 * 更新角色的权限
 */
export function roleMenuUpdate (parameter) {
  return axios({
      url: api.roleMenuUpdate,
      method: 'post',
      params: parameter
   })
}

/**
 * 获取用户角色列表
 */
export function userRoleList (parameter) {
  return axios({
    url: api.userRoleList,
    method: 'get',
    params: parameter
  })
}

/**
 * 更新用户的角色
 */
export function userRoleUpdate (parameter) {
  return axios({
    url: api.userRoleUpdate,
    method: 'post',
    params: parameter
  })
}

export function queryWorkerGroupList (parameter) {
  return axios({
    url: api.queryWorkerGroupList,
    method: 'get',
    params: parameter
  })
}

export function workerGroupCreate (parameter) {
  return axios({
    url: api.workerGroupCreate,
    method: 'post',
    params: parameter
  })
}

export function workerGroupUpdate (parameter) {
  return axios({
    url: api.workerGroupUpdate,
    method: 'post',
    params: parameter
  })
}

export function workerGroupDelete (parameter) {
  return axios({
    url: api.workerGroupDelete,
    method: 'get',
    params: parameter
  })
}

export function queryTenantList (parameter) {
  return axios({
    url: api.queryTenantList,
    method: 'get',
    params: parameter
  })
}

export function queueList (parameter) {
  return axios({
    url: api.queueList,
    method: 'get',
    params: parameter
  })
}

export function tenantCreate (parameter) {
  return axios({
    url: api.tenantCreate,
    method: 'post',
    params: parameter
  })
}

export function tenantUpdate (parameter) {
  return axios({
    url: api.tenantUpdate,
    method: 'post',
    params: parameter
  })
}

export function tenantDelete (parameter) {
  return axios({
    url: api.tenantDelete,
    method: 'post',
    data: parameter
  })
}

export function queryQueueList (parameter) {
  return axios({
    url: api.queryQueueList,
    method: 'get',
    params: parameter
  })
}

export function queueCreate (parameter) {
  return axios({
    url: api.queueCreate,
    method: 'post',
    params: parameter
  })
}

export function queueUpdate (parameter) {
  return axios({
    url: api.queueUpdate,
    method: 'post',
    params: parameter
  })
}

export function verifyQueue (parameter) {
  return axios({
    url: api.verifyQueue,
    method: 'post',
    params: parameter
  })
}

export function queueDelete (parameter) {
  return axios({
    url: api.queueDelete,
    method: 'post',
    params: parameter
  })
}
export function conf (parameter) {
  return axios({
    url: api.conf,
    method: 'get',
    params: parameter
  })
}