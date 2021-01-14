import { axios } from '@/utils/request'

const api = {
  createDataSource: '/datasource/create',
  updateDataSource: '/datasource/update',
  deleteDataSource: '/datasource/delete',
  queryDataSourceById: '/datasource/queryById',
  queryDataSource: '/datasource/query',
  statisticDataSource: '/datasource/statistic',
  testDataSource: '/datasource/test',
  getDataSourceList: 'datasources/list',
  getAiDataSourceList: '/datasources/list-paging',
  testAiDataSource: '/datasources/connect',
  updateAiDataSource: '/datasources/update',
  createAiDataSource: '/datasources/create',
  deleteAiDataSource: '/datasources/delete',
  getTablesById: '/datasources/table-list-by-id',
  getColumnById: '/datasources/column-list-by-id',
  getUpdateById: '/datasources/update-ui'
}

export default api

export function createDataSource (parameter) {
  return axios({
    url: api.createDataSource,
    method: 'post',
    params: parameter
  })
}

export function updateDataSource (parameter) {
  return axios({
    url: api.updateDataSource,
    method: 'post',
    params: parameter
  })
}

export function deleteDataSource (parameter) {
  return axios({
    url: api.deleteDataSource,
    method: 'get',
    params: parameter
  })
}

export function queryDataSourceById (parameter) {
  return axios({
    url: api.queryDataSourceById,
    method: 'get',
    params: parameter
  })
}

export function queryDataSource (parameter) {
  return axios({
    url: api.queryDataSource,
    method: 'get',
    params: parameter
  })
}

export function statisticDataSource (parameter) {
  return axios({
    url: api.statisticDataSource,
    method: 'get',
    params: parameter
  })
}

export function testDataSource (parameter) {
  return axios({
    url: api.testDataSource,
    method: 'post',
    params: parameter
  })
}
export function getDataSourceList (parameter) {
  return axios({
    url: api.getDataSourceList,
    method: 'get',
    params: parameter
  })
}

export function getAiDataSourceList (parameter) {
  return axios({
    url: api.getAiDataSourceList,
    method: 'get',
    params: parameter
  })
}

export function testAiDataSource (parameter) {
  return axios({
    url: api.testAiDataSource,
    method: 'post',
    params: parameter
  })
}
export function updateAiDataSource (parameter) {
  return axios({
    url: api.updateAiDataSource,
    method: 'post',
    params: parameter
  })
}
export function createAiDataSource (parameter) {
  return axios({
    url: api.createAiDataSource,
    method: 'post',
    params: parameter
  })
}
export function deleteAiDataSource (parameter) {
  return axios({
    url: api.deleteAiDataSource,
    method: 'get',
    params: parameter
  })
}
export function dataSourceTypeList (parameter) {
  const dataSourceTypeList = [
    {
      id: 0,
      code: 'MYSQL',
      disabled: false
    },
    // {
    //   id: 1,
    //   code: 'POSTGRESQL',
    //   disabled: false
    // },
    {
      id: 2,
      code: 'HIVE',
      disabled: false
    },
    // {
    //   id: 3,
    //   code: 'SPARK',
    //   disabled: false
    // },
    // {
    //   id: 4,
    //   code: 'CLICKHOUSE',
    //   disabled: false
    // },
    // {
    //   id: 5,
    //   code: 'ORACLE',
    //   disabled: false
    // },
    // {
    //   id: 6,
    //   code: 'SQLSERVER',
    //   disabled: false
    // },
    {
      id: 7,
      code: 'KAFKA',
      disabled: false
    },
    {
      id: 7,
      code: 'ELASTICSEARCH',
      disabled: false
    },
    {
      id: 8,
      code: 'KYLIN',
      disabled: false
    }
  ]
  return dataSourceTypeList
}
export function getTablesById (parameter) {
  return axios({
    url: api.getTablesById,
    method: 'post',
    params: parameter
  })
}
export function getColumnById (parameter) {
  return axios({
    url: api.getColumnById,
    method: 'post',
    params: parameter
  })
}

export function getUpdateById (parameter) {
  return axios({
    url: api.getUpdateById,
    method: 'post',
    params: parameter
  })
}
