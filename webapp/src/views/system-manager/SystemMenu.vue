<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入菜单名称查询" v-model="queryParam.name" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建菜单</a-button>
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
      :dataSource="menuList"
      :pagination="false">
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="status" slot-scope="text">
        <a-badge :status="text | statusTypeFilter" :text="text | statusFilter" />
      </span>
      <span slot="description" slot-scope="text">
        <ellipsis :length="4" tooltip>{{ text }}</ellipsis>
      </span>

      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该菜单吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    </a-spin>
    <create-menu-modal
      ref="createModal"
      @refresh="handleSearch"></create-menu-modal>
  </a-card>
</template>

<script>
    import { systemTreeMenuList, systemMenuDelete } from '@/api/system'

    import CreateMenuModal from './modules/CreateMenuModal'
    import Confirm from '@/components/Notification/Confirm'

    export default {
        name: 'SystemMenu',
        components: {
            Confirm,
            CreateMenuModal
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    status: '0',
                    name: ''
                },
                // 表头
                columns: [
                    {
                        title: '序号',
                        dataIndex: 'sort',
                        width: '8%',
                        align: 'center'
                    },
                    {
                        title: '名称',
                        dataIndex: 'name',
                        align: 'center'
                    },
                    {
                        title: '代码',
                        dataIndex: 'code',
                        align: 'center'
                    },
                    {
                        title: '路径',
                        dataIndex: 'url',
                        align: 'center'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        width: '12%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                menuList: [],
                pidList: [],
                visible: false,
                loading: false
            }
        },
        created () {
            this.list()
        },
        methods: {
            resetSearchForm () {
                this.queryParam.name = ''
                this.list()
            },
            handleDelete (record) {
                systemMenuDelete({ 'id': record.id }).then(res => {
                    if (res.code === 0) {
                        this.$message.success(res.msg)
                        this.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },
            handleSearch () {
                this.list()
            },
            handleAdd () {
                this.$refs.createModal.show()
            },
            handleEdit (record) {
                this.$refs.createModal.edit(record)
            },
            list () {
                this.loading = true
                this.queryParam.name = this.queryParam.name.trim()
                systemTreeMenuList(this.queryParam).then(res => {
                    this.menuList = res.data
                    this.loading = false
                })
            }
        }
    }
</script>
