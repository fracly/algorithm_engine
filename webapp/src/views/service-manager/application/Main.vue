<template>
  <a-card :bordered="false">
    <div class="application">
      <a-form layout="inline">
        <a-row>
          <a-col :md="6" :sm="12">
            <div class="chart-title">
              <span>应用</span>
            </div>
            <div class="row">
              <div class="app">
                <div style="margin: 60px auto;text-align: center">
                  <h4 style="font-size: 16px">已创建应用：<span>{{ size }}</span>个</h4>
                  <a-button type="primary" size="large" @click="appList" style="margin-top: 20px;">应用列表</a-button>
                  <a-button type="primary" size="large" style="margin-left: 10px"
                            @click="() => { this.$router.push('/resource/application/add') }">新建应用
                  </a-button>
                </div>
              </div>
            </div>
          </a-col>
          <a-col :md="18" :sm="12">
            <div class="chart-title">
              <span>调用统计</span>
            </div>
            <div class="row">
              <a-table
                ref="table"
                size="middle"
                :columns="columns"
                rowKey="service_id"
                :dataSource="logList"
                :pagination="pagination"
                :alert="{ show: true, clear: true }"
              >
                <span slot="serial" slot-scope="text, record, index">
                  {{ index + 1 }}
                </span>
                <span slot="Rate" slot-scope="text, record">
                  {{ text | rateFilter }}
                </span>
                <span slot="action" slot-scope="text, record">
                  <template>
                    <a @click="view(record)">查看</a>
                  </template>
                </span>

              </a-table>
            </div>
          </a-col>
        </a-row>
        <a-row>
          <div class="chart-title">
            <span class="title">可用服务列表</span>
            <div>
              <a-button @click="callback(item.code)" :type="queryParam2.type==item.code?'primary':''" v-for="(item) in typeList" :key="item.id">
                {{item.name}}
              </a-button>
            </div>
            <!--            <a-tabs type="card" :default-active-key="queryParam2.type" @change="callback">-->
            <!--            </a-tabs>-->
            <div class="row">
              <a-table
                ref="table"
                size="middle"
                :columns="columns2"
                rowKey="serviceId"
                :dataSource="serviceList"
                :pagination="pagination2"
                :alert="{ show: true, clear: true }"
                @change="tableChange"
              >
                <span slot="serial" slot-scope="text, record, index">
                  {{ index + 1 }}
                </span>
                <span slot="ip" slot-scope="text,record">
                  {{ url + 'ms-consumer/encapsula/v1/service/'+record.serviceCode +'?token='}}
                </span>
                <span slot="type" slot-scope="text">{{ getTypeList(text) }}</span>
              </a-table>
            </div>
          </div>
        </a-row>
      </a-form>
    </div>
  </a-card>
</template>

<script>
  import { applicationList, getAppLog, getListAllP, getTypeList } from '@/api/service'

  export default {
    name: 'Application',
    components: {},
    data () {
      return {
        // 查询参数
        queryParam: {
          // Number of pages per page
          pageSize: 4,
          // Number of pages
          pageNo: 1,
          status: 1,
          searchVal: ''
        },
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '接口名称',
            dataIndex: 'serviceName'
          },
          {
            title: '调用量',
            dataIndex: 'total'
          },
          {
            title: '调用失败次数',
            dataIndex: 'fail'
          },
          {
            title: '失败率',
            dataIndex: 'rate',
            scopedSlots: { customRender: 'Rate' }
          },
          {
            title: '详情',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        logList: [],
        serviceList: [],
        pagination: {
          total: 20,
          pageSize: 4,
          showTotal: (total) => `共${total}条`,
          showSizeChanger: false,
          onChange: (page, pageSize) => {
            this.queryParam.pageNo = page
            this.queryParam.pageSize = pageSize
            this.getLogList()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam.pageNo = current
            this.queryParam.pageSize = size
            this.getLogList()
          }
        },
        pagination2: {
          total: 20,
          pageSize: 10,
          current: 1,
          showTotal: (total) => `共${total}条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          onChange: (page, pageSize) => {
            this.queryParam2.pageNo = page
            this.queryParam2.pageSize = pageSize
            this.getList()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam2.pageNo = current
            this.queryParam2.pageSize = size
            this.getList()
          }
        },
        // 查询参数
        queryParam2: {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNo: 1,
          //
          type: ''
        },
        // 表头
        columns2: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '服务名称',
            dataIndex: 'serviceName'
          },
          {
            title: '服务编码',
            dataIndex: 'serviceCode'
          },
          {
            title: '服务地址',
            dataIndex: 'serviceId',
            scopedSlots: { customRender: 'ip' }
          },
          // {
          //   title: '服务类型',
          //   dataIndex: 'service_type',
          //   scopedSlots: { customRender: 'type' },
          //   filters: serviceTypeList
          // },
          {
            title: '服务描述',
            dataIndex: 'des'
          }
        ],
        size: 0,
        url: Glod.token,
        typeList: []
      }
    },
    created () {
      this.getAppSize()
      this.getLogList()
      // this.getType()
      this.getList1()
    },
    filters: {
      rateFilter (rate) {
        return (rate * 100).toFixed(2) + '%'
      }
    },
    methods: {
      // 获取应用总数
      getAppSize () {
        applicationList(this.queryParam).then(res => {
          this.size = res.data.totalCount
        })
      },
      // 跳转应用列表
      appList () {
        this.$router.push('/resource/application/list')
      },

      // 获取调用统计
      getLogList () {
        getAppLog(this.queryParam).then(res => {
          this.logList = res.data.totalList
          this.pagination.total = res.data.total
        })
      },
      // 获取服务列表
      getList1 () {
        getTypeList({ code: '01' }).then(res => {
          this.typeList = res.data
          if (this.queryParam2.type === '') {
            this.queryParam2.type = this.typeList[0].code
          }
          getListAllP(this.queryParam2).then(res => {
            this.serviceList = res.data.totalList
            this.pagination2.total = res.data.total
          })
        })
      },
      // 获取服务列表
      getList () {
        getListAllP(this.queryParam2).then(res => {
          this.serviceList = res.data.totalList
          this.pagination2.total = res.data.total
          this.pagination2.current = this.queryParam2.pageNo
          this.pagination2.pageSize = this.queryParam2.pageSize
        })
      },
      getTypeList (type) {
        for (let i = 0; i < this.typeList.length; i++) {
          if (this.typeList[i].code === type) {
            return this.typeList[i].name
          }
        }
      },
      getType () {
        getTypeList({ code: '01' }).then(res => {
          this.typeList = res.data
          this.queryParam2.type = this.typeList[0].code
        })
      },
      view (item) {
        this.$router.push({ name: 'AppLog', params: { serviceName: item.service_id } })
      },
      tableChange (pagination, filters) {
        // if (filters.service_type) {
        //   this.queryParam2.type = filters.service_type.length >= 1 ? filters.service_type.toString() : ''
        // }
        // this.queryParam2.pageSize = pagination.pageSize
        // this.getList()
      },
      callback (key) {
        this.queryParam2.type = key
        this.queryParam2.pageNo = 1
        this.queryParam2.pageSize = 10
        this.getList()
      }
    },
    mounted () {

    }
  }
</script>
<style lang="less" scoped>
  .application {
    width: 100%;
    height: 100%;

    .row {
      margin-right: 15px;
      margin-left: 15px;
    }

    .chart-title {
      text-align: left;
      height: 60px;
      line-height: 60px;

      span.title {
        font-size: 22px;
        color: #333;
        font-weight: bold;
        margin-left: 20px;
      }

      button span {
        font-size: 18px;
      }

    }

    .app {
      border: 1px solid #D3D3D3;
      /*box-shadow: 2px 1px 2px #888888;*/
      /*border-radius: 5px;*/
    }
  }
</style>
