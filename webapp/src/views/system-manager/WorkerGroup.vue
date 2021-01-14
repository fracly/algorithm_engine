<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入worker分组名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建Worker分组</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-spin :spinning="loading">
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      :dataSource="workerGroupList"
      showPagination="auto"
      :pagination="pagination"
    >
      <span slot="status" slot-scope="text">
        <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
      </span>
      <span slot="name" slot-scope="text">
        <a-popover title="" :content="text" v-if="text.length > 10">
          {{ text.substr(0, 10) + '...' }}
        </a-popover>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="description" slot-scope="text">
        <a-popover title="" :content="text" v-if="text.length > 10">
          {{ text.substr(0, 10) + '...' }}
        </a-popover>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <confirm title="确认删除" @confirm="handleDelete(record.id)" content="确认删除该worker分组吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
    </a-spin>
    <create-worker-group ref="createWorkerGroupModal" @refresh="list"></create-worker-group>
  </a-card>
</template>

<script>

  import { queryWorkerGroupList, workerGroupDelete } from '@/api/system'

  import CreateWorkerGroup from './modules/CreateWorkerGroup'
  import Confirm from '@/components/Notification/Confirm'

    export default {
        name: 'WorkerGroup',
        components: {
            CreateWorkerGroup,
            Confirm
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    searchVal: '',
                    pageNo: 1,
                    pageSize: 10
                },
                // 表头
                columns: [
                    {
                        title: '组名',
                        dataIndex: 'name',
                        scopedSlots: { customRender: 'name' }
                    },
                    {
                        title: 'IP列表',
                        dataIndex: 'ipList'
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '更新时间',
                        dataIndex: 'updateTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: { customRender: 'action' },
                        width: '10%'
                    }
                ],
                pagination: {
                    total: 0,
                    pageSize: 10,
                    showTotal: (total) => `共${total}条`,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    onChange: (page, pageSize) => {
                        this.$set(this.pagination, 'current', page)
                        this.$set(this.pagination, 'pageSize', pageSize)
                        this.queryParam.pageNo = page
                        this.queryParam.pageSize = pageSize
                        this.list()
                    },
                    onShowSizeChange: (current, size) => {
                        this.$set(this.pagination, 'current', current)
                        this.$set(this.pagination, 'pageSize', size)
                        this.queryParam.pageNo = current
                        this.queryParam.pageSize = size
                        this.list()
                    }
                },
                workerGroupList: [],
                visible: false,
                loading: false
            }
        },
        created () {
            this.list()
        },
        methods: {
            resetSearchForm () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.queryParam.searchVal = ''
                this.list()
            },
            handleSearch () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.list()
            },
            handleOk () {
            },
            handleAdd () {
                this.$refs.createWorkerGroupModal.show()
            },
            handleEdit (record) {
                this.$refs.createWorkerGroupModal.edit(record)
            },
            handleDelete (id) {
                workerGroupDelete({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success('删除worker分组成功')
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.list()
                })
            },
            handleCancel () {
                this.visible = false
            },
            list () {
                const that = this
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                queryWorkerGroupList(this.queryParam).then(res => {
                    that.workerGroupList = res.data.totalList
                    that.pagination.pageSize = this.queryParam.pageSize
                    that.pagination.total = res.data.total
                })
            }
        }
    }
</script>
