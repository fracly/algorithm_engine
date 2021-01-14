<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入用户名称查询" v-model="queryParam.name" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建用户</a-button>
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
      :dataSource="userList"
      :pagination="pagination"
      showPagination="auto"
    >
      <span slot="status" slot-scope="text">
        <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
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
          <a v-if="record.status === 1" @click="handleDisable(record.id)">禁用</a>
          <a v-else @click="handleEnable(record.id)">启用</a>
          <a-divider type="vertical"></a-divider>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <a-dropdown>
            <a class="ant-dropdown-link" @click="e => e.preventDefault()">
              更多<a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="roleAssign(record)">角色分配</a>
              </a-menu-item>
              <a-menu-item>
                <confirm title="确认删除" @confirm="handleDelete(record.id)" content="确认删除该用户吗？"></confirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </span>
      <span slot="time" slot-scope="text">
          {{ text | moment }}
      </span>
    </a-table>
    </a-spin>
    <create-user-modal
      ref="createModal"
      :visible="visible"
      :loading="loading"
      @refresh="handleSearch"></create-user-modal>
    <assign-roles-modal
      ref="assignPermissionModal"
      :visible="visible2"
      :loading="loading2"
      :selectedRowKeys="selectedRowKeys"
      :list="permissionList"
      @cancel="handleCancel2"
      @ok="handleOk2"></assign-roles-modal>
  </a-card>
</template>

<script>
    import { systemUserList, systemRoleList, systemUserDisable, systemUserDelete, systemUserEnable, userRoleList, userRoleUpdate } from '@/api/system'

    import CreateUserModal from './modules/CreateUserModal'
    import AssignRolesModal from './modules/AssignRolesModal'
    import Confirm from '@/components/Notification/Confirm'
    const statusMap = {
        1: {
            status: 'success',
            text: '正常'
        },
        9: {
            status: 'error',
            text: '禁用'
        },
        8: {
            status: 'warning',
            text: '锁定'
        }
    }
    export default {
        name: 'SystemRole',
        components: {
            AssignRolesModal,
            CreateUserModal,
            Confirm
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    status: '0',
                    name: '',
                    pageNo: 1,
                    pageSize: 10
                },
                // 表头
                columns: [
                    {
                        title: '姓名',
                        dataIndex: 'userCNName'
                    },
                    {
                        title: '账号',
                        dataIndex: 'userName'
                    },
                    {
                        title: '手机',
                        dataIndex: 'phone'
                    },
                    {
                        title: '邮箱',
                        dataIndex: 'email'
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '描述',
                        dataIndex: 'desc',
                        scopedSlots: { customRender: 'description' }
                    },
                    {
                        title: '状态',
                        dataIndex: 'status',
                        scopedSlots: { customRender: 'status' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: { customRender: 'action' },
                        width: '12%'
                    }
                ],
                userList: [],
                permissionList: [],
                selectedRowKeys: [],
                visible: false,
                loading: false,
                visible2: false,
                loading2: false,
                currentId: null,
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
                }
            }
        },
        filters: {
            statusFilter (type) {
                return statusMap[type].text
            },
            statusTypeFilter (type) {
                return statusMap[type].status
            }
        },
        created () {
            this.list()
        },
        methods: {
            resetSearchForm () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.queryParam.name = ''
                this.list()
            },
            handleDisable (id) {
                systemUserDisable({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                        this.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },
            handleEnable (id) {
                systemUserEnable({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                        this.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },
            roleAssign (record) {
                this.currentId = record.id
                this.visible2 = true
                this.loading2 = true
                this.permissionList = []
                this.selectedRowKeys = []
                systemRoleList({
                    name: '',
                    status: 1,
                    pageSize: 10000,
                    pageNo: 1
                }).then(res => {
                    this.permissionList = res.data
                    userRoleList({
                        'userId': record.id
                    }).then(res => {
                        this.selectedRowKeys = res.data
                        this.loading2 = false
                    })
                }).catch(() => {
                    this.loading2 = true
                })
            },
            handleSearch () {
                this.pagination.current = this.queryParam.pageNo
                this.queryParam.pageNo = 1
                this.list()
            },
            handleOk () {
            },
            handleOk2 (rowKeys) {
                if (rowKeys.length === 0) {
                    this.$message.warn('请至少为用户选定一个角色')
                }
                userRoleUpdate({
                    'userId': this.currentId,
                    'idArray': rowKeys.join()
                }).then((res) => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.visible2 = false
                })
            },

            handleAdd () {
                this.$refs.createModal.show()
            },
            handleEdit (record) {
                this.$refs.createModal.edit(record)
            },
            handleDelete (id) {
                const that = this
                systemUserDelete({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        that.$message.success(res.msg)
                    } else {
                        that.$message.error(res.msg)
                    }
                    that.handleSearch()
                })
            },
            handleCancel () {
                this.visible = false
            },
            handleCancel2 () {
                this.visible2 = false
            },
            list () {
                this.loading = true
                const that = this
                this.queryParam.name = this.queryParam.name.trim()
                systemUserList(this.queryParam).then(res => {
                    that.userList = res.data
                    that.pagination.total = res.dataMap.total
                    that.loading = false
                })
            }
        }
    }
</script>
