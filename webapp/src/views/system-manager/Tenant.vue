<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入租户名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建租户</a-button>
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
      :dataSource="tenantList"
      showPagination="auto"
      :pagination="pagination"
    >
      <span slot="description" slot-scope="text">
        <span v-if="text === null"></span>
        <a-popover title="" :content="text" v-else-if="text.length > 15">
          {{ text.substr(0, 15) + '...' }}
        </a-popover>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <confirm title="确认删除" @confirm="handleDelete(record.id)" content="确认删除该租户吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
    </a-spin>
    <create-tenant ref="createTenantModal" @refresh="handleSearch"></create-tenant>
  </a-card>
</template>

<script>

  import { queryTenantList, tenantDelete } from '@/api/system'

  import CreateTenant from './modules/CreateTenant'
  import Confirm from '@/components/Notification/Confirm'

    export default {
        name: 'Tenant',
        components: {
            CreateTenant,
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
                        title: '租户名称',
                        dataIndex: 'tenantName'
                    },
                    {
                        title: '租户编码',
                        dataIndex: 'tenantCode'
                    },
                    {
                        title: '描述',
                        dataIndex: 'desc',
                        scopedSlots: { customRender: 'description' }
                    },
                    {
                        title: '队列',
                        dataIndex: 'queueName'
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
                    total: 20,
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
                tenantList: [],
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
            handleAdd () {
                this.$refs.createTenantModal.show()
            },
            handleEdit (record) {
                this.$refs.createTenantModal.edit(record)
            },
            handleDelete (id) {
                const formData = new FormData()
                formData.append('id', id)
                tenantDelete(formData).then(res => {
                    if (res.code === 0) {
                        this.$message.success('删除租户成功')
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
                this.loading = true
                const that = this
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                queryTenantList(this.queryParam).then(res => {
                    that.tenantList = res.data.totalList
                    that.pagination.total = res.data.total
                    that.loading = false
                })
            }
        }
    }
</script>
