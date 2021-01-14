<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入角色名称查询" v-model="queryParam.name" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建角色</a-button>
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
      :dataSource="roleList"
      :pagination="pagination"
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
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <a-dropdown>
            <a class="ant-dropdown-link" @click="e => e.preventDefault()">
              更多<a-icon type="down" />
            </a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="assignMenu(record)">权限分配</a>
              </a-menu-item>
              <a-menu-item>
                <confirm title="确认删除" @confirm="handleDelete(record.id)" content="确认删除该角色吗？"></confirm>
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
    <create-role-modal
      ref="createModal"
      :visible="visible"
      :loading="loading"
      @refresh="handleSearch"></create-role-modal>
    <assign-menu-modal
      ref="assignMenuModal"
      :visible="visible2"
      :loading="loading2"
      :selectedRowKeys="selectedRowKeys"
      :list="menuList"
      @cancel="handleCancel2"
      @ok="handleOk2"></assign-menu-modal>
  </a-card>
</template>

<script>
    import { systemRoleList, systemRoleDelete, systemTreeMenuList, roleMenuList, roleMenuUpdate } from '@/api/system'
    import CreateRoleModal from './modules/CreateRoleModal'
    import AssignMenuModal from './modules/AssignMenuModal'
    import Confirm from '@/components/Notification/Confirm'

    export default {
        name: 'SystemRole',
        components: {
            AssignMenuModal,
            CreateRoleModal,
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
                        title: '角色名称',
                        dataIndex: 'name'
                    },
                    {
                        title: '角色代码',
                        dataIndex: 'code'
                    },
                    {
                        title: '描述',
                        dataIndex: 'desc',
                        scopedSlots: { customRender: 'description' }
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        scopedSlots: { customRender: 'action' },
                        width: '12%'
                    }
                ],
                roleList: [],
                menuList: [],
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
            handleAdd () {
                this.$refs.createModal.show()
            },
            handleEdit (record) {
                this.$refs.createModal.edit(record)
            },
            handleDelete (id) {
                systemRoleDelete({ 'id': id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                    } else {
                        this.$message.error(res.msg)
                    }
                    this.list()
                })
            },
            // 权限分配
            assignMenu (record) {
                this.currentId = record.id
                this.visible2 = true
                this.loading2 = true
                this.menuList = []
                this.selectedRowKeys = []
                systemTreeMenuList({ name: '', status: 1 }).then(res => {
                    this.menuList = res.data
                    roleMenuList({
                        'roleId': record.id
                    }).then(res => {
                        this.selectedRowKeys = res.data
                        this.loading2 = false
                    })
                }).catch(() => {
                    this.loading2 = false
                })
            },
            handleSearch () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.list()
            },
            handleOk2 (rowKeys) {
                if (rowKeys.length === 0) {
                    this.$message.warn('请至少为角色分配一个菜单')
                }
                roleMenuUpdate({
                    'roleId': this.currentId,
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
                systemRoleList(this.queryParam).then(res => {
                    if (res.code === 0) {
                        that.roleList = res.data
                        that.pagination.total = res.dataMap.total
                    } else {
                        that.$message.error(res.msg)
                    }
                    that.loading = false
                })
            }
        }
    }
</script>
