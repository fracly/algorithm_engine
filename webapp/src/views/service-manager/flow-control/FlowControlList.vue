<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.resource" placeholder="请输入服务编码"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" icon="search" style="width: 120px;margin-left: 20px;" @click="handleSearch()">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
            </span>
          </a-col>
          <a-col :md="3" :sm="8" style="float: right">
            <a-form-item label="">
              <a-button type="primary" style="margin-left: 10px" icon="plus" @click="handleAdd">新建流控规则</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      rowKey="resource"
      :dataSource="ruleList"
      :pagination="pagination"
      :alert="{ show: true, clear: true }"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="type" slot-scope="text">
        {{ text | typeFilter }}
      </span>
      <span slot="behavior" slot-scope="text">
        {{ text | behaviorFilter }}
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
<!--          <a @click="handleDelete(record)">删除</a>-->
          <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该规则吗？"></confirm>

        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    <flow-control-edit ref="modal" @refresh="handleSearch"></flow-control-edit>

  </a-card>
</template>

<script>
  import { getToken, getRuleList, deleteRule } from '@/api/service'
  import FlowControlEdit from './modules/FlowControlEdit'
  import Confirm from '@/components/Notification/Confirm'

  export default {
    name: 'ServicePackage',
    components: { FlowControlEdit, Confirm },
    data () {
      return {
        // 查询参数
        queryParam: {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNum: 1,
          // Search value
          resource: ''
        },
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '服务编码',
            dataIndex: 'resource'
          },
          {
            title: '阈值类型',
            dataIndex: 'grade',
            scopedSlots: { customRender: 'type' }
          },
          {
            title: '限流阈值',
            dataIndex: 'count'
          },
          {
            title: '流控效果',
            dataIndex: 'controlBehavior',
            scopedSlots: { customRender: 'behavior' }
          },
          {
            title: '创建时间',
            dataIndex: 'gmtCreate',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        ruleList: [],
        pagination: {
          total: 20,
          pageSize: 10,
          current: 1,
          showTotal: (total) => `共${total}条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          onChange: (page, pageSize) => {
            this.queryParam.pageNum = page
            this.queryParam.pageSize = pageSize
            this.getFlowRuleList()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam.pageNum = current
            this.queryParam.pageSize = size
            this.getFlowRuleList()
          }
        }
      }
    },
    filters: {
      typeFilter (type) {
        if (type === 1) {
          return 'QPS'
        } else {
          return '线程数'
        }
      },
      behaviorFilter (b) {
        if (b === 0) {
          return '快速失败'
        } else if (b === 1) {
          return 'Warm Up'
        } else {
          return '排队等待'
        }
      }
    },
    created () {
      this.getFlowRuleList()
    },
    methods: {
      // 查询所有的流量规则列表
      getFlowRuleList () {
        this.queryParam.resource = this.queryParam.resource.trim()
        this.pagination.current = this.queryParam.pageNum
        this.pagination.pageSize = this.queryParam.pageSize
        getToken().then(res => {
          if (res) {
            const params = {}
            params.seqId = Math.floor(Math.random() * 10000)
            params.timeStamp = new Date().getTime()
            params.reqData = this.queryParam
            params.token = res.access_token
            getRuleList(params).then(res => {
              this.ruleList = res.data.data ? res.data.data : []
              this.pagination.total = res.data.totalCount
            }).catch(e => {
              if (e.code === 200) {

              } else {
              }
            })
          } else {
            this.$message.error(res.msg)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 删除
      handleDelete (record) {
        const self = this
        getToken().then(res => {
          if (res) {
            const params = {}
            params.seqId = Math.floor(Math.random() * 10000)
            params.timeStamp = new Date().getTime()
            params.reqData = {}
            params.reqData.resource = record.resource
            params.token = res.access_token
            deleteRule(params).then(res => {
              if (res.code === 0) {
                self.$message.success(res.msg)
                self.getFlowRuleList()
              } else {
                self.$message.success(res.msg)
              }
            })
          } else {
            self.$message.error(res.msg)
          }
        }).catch(e => {
          self.$message.error(e.msg || '')
        })
        // this.$confirm({
        //   title: '警告',
        //   content: `真的要删除${record.resource}吗?`,
        //   okText: '删除',
        //   okType: 'danger',
        //   cancelText: '取消',
        //   onOk () {
        //     console.log('OK')
        //     // 在这里调用删除接口
        //     getToken().then(res => {
        //       if (res) {
        //         const params = {}
        //         params.seqId = Math.floor(Math.random() * 10000)
        //         params.timeStamp = new Date().getTime()
        //         params.reqData = {}
        //         params.reqData.resource = record.resource
        //         params.token = res.access_token
        //         deleteRule(params).then(res => {
        //           if (res.code === 0) {
        //             self.$message.success(res.msg)
        //             self.getFlowRuleList()
        //           } else {
        //             self.$message.success(res.msg)
        //           }
        //         })
        //       } else {
        //         self.$message.error(res.msg)
        //       }
        //     }).catch(e => {
        //       self.$message.error(e.msg || '')
        //     })
        //   },
        //   onCancel () {
        //   }
        // })
      },
      // 查询
      handleSearch () {
        this.queryParam.pageNo = 1
        this.getFlowRuleList()
      },
      // 编辑
      handleEdit (item) {
        this.$refs.modal.edit(item)
      },
      // 重置
      resetSearchForm () {
        this.queryParam = {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNum: 1,
          // Search value
          resource: ''
        }
        this.getFlowRuleList()
      },
      handleAdd () {
        this.$refs.modal.add()
      }
    }
  }
</script>
