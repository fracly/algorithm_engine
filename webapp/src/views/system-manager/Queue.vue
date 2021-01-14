<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入队列名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建队列</a-button>
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
      :dataSource="queueList"
      showPagination="auto"
      :pagination="pagination">
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
          <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该队列吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        <span v-if="text === null"></span>
        <span v-else>{{ text | moment }}</span>
      </span>
    </a-table>
    </a-spin>
    <create-queue ref="createQueueModal" @refresh="handleSearch"></create-queue>
  </a-card>
</template>

<script>

  import { queryQueueList, queueDelete } from '@/api/system'

  import CreateQueue from './modules/CreateQueue'
  import Confirm from '@/components/Notification/Confirm'

    export default {
        name: 'Queue',
        components: {
            CreateQueue,
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
                        title: '名称',
                        dataIndex: 'queueName'
                    },
                    {
                        title: '队列值',
                        dataIndex: 'queue'
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
                        width: '15%'
                    }
                ],
                pagination: {
                    total: 0,
                    pageSize: 10,
                    showTotal: (total) => `共${total}条`,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    onChange: (page, pageSize) => {
                        this.queryParam.pageNo = page
                        this.queryParam.pageSize = pageSize
                        this.$set(this.pagination, 'current', page)
                        this.$set(this.pagination, 'pageSize', pageSize)
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
                queueList: [],
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
                this.$refs.createQueueModal.show()
            },
            handleEdit (record) {
                this.$refs.createQueueModal.edit(record)
            },
            handleDelete (record) {
                this.loading = true
                const that = this
                queueDelete({ 'id': record.id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                        that.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                    that.loading = false
                })
            },
            handleCancel () {
                this.visible = false
            },
            list () {
                this.loading = true
                const that = this
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                queryQueueList(this.queryParam).then(res => {
                    that.queueList = res.data.totalList
                    that.pagination.total = res.data.total
                    that.loading = false
                })
            }
        }
    }
</script>
