<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入函数名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="6">
            <span class="table-page-search-submitButtons">
              <a-button type="primary"icon="search"  style="width: 120px;margin-left: 20px;" @click="handleSearch()">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
            </span>
          </a-col>
          <a-col :md="6" :sm="6" :push="9">
            <a-form-item label="">
              <a-button type="primary" style="margin-left: 10px;" icon="plus" @click="create">创建函数</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      rowKey="id"
      :dataSource="funcList"
      :pagination="pagination"
      :alert="{ show: true, clear: true }"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="description" slot-scope="text">
              <a-popover title="" :content="text" v-if="text?text.length > 10:text">
                {{ text.substr(0, 10) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>

      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)"> 编辑</a>
          <a-divider type="vertical"></a-divider>
<!--          <a @click="handleDelete(record)">删除</a>-->
       <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该函数吗？"></confirm>

        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    <func-create ref="modal" @refresh="handleSearch"></func-create>

  </a-card>
</template>

<script>
  import { getUdfFuncListP, deleteUdf } from '@/api/resources'
  import FuncCreate from './modules/FuncCreate'
  import Confirm from '@/components/Notification/Confirm'

  export default {
    name: 'ReportList',
    components: { FuncCreate, Confirm },
    data () {
      return {
        // 查询参数
        queryParam: {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNo: 1,
          // Search value
          searchVal: ''
          //
          // type: 'ALL'
        },
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: 'UDF函数名称',
            dataIndex: 'funcName',
            scopedSlots: { customRender: 'description' }

          },
          {
            title: '包类名',
            dataIndex: 'className',
            scopedSlots: { customRender: 'description' }
          },
          {
            title: '参数',
            dataIndex: 'argTypes',
            scopedSlots: { customRender: 'description' }
          },
          {
            title: '类型',
            dataIndex: 'type'
          },
          {
            title: '使用说明',
            dataIndex: 'desc',
            scopedSlots: { customRender: 'description' }
          },
          {
            title: 'UDF资源jar包',
            dataIndex: 'resourceName'
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        funcList: [],
        visible: false,
        loading: false,
        pagination: {
          total: 20,
          pageSize: 10,
          current: 1,
          showTotal: (total) => `共${total}条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          onChange: (page, pageSize) => {
            this.queryParam.pageNo = page
            this.queryParam.pageSize = pageSize
            this.list()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam.pageNo = current
            this.queryParam.pageSize = size
            this.list()
          }
        },
        fillTypeArr: ['txt', 'log', 'sh', 'conf', 'cfg', 'py', 'java', 'sql', 'xml', 'hql']

      }
    },
    created () {
      this.list()
    },
    methods: {
      // 删除
      handleDelete (record) {
        const self = this
        deleteUdf({ 'id': record.id }).then(res => {
          if (res.code === 0) {
            self.$message.success('删除成功')
            self.list()
          } else {
            self.$message.success(res.msg)
          }
        })
      },
      // 创建
      create () {
        this.$refs.modal.add()
      },
      // 查询
      handleSearch () {
        this.queryParam.pageNo = 1
        this.list()
      },
      // 编辑
      handleEdit (item) {
        this.$refs.modal.edit(item)
      },
      // 列表数据
      list () {
        this.queryParam.searchVal = this.queryParam.searchVal.trim()
        getUdfFuncListP(this.queryParam).then(res => {
          this.funcList = res.data.totalList
          this.pagination.total = res.data.total
          this.pagination.current = this.queryParam.pageNo
          this.pagination.pageSize = this.queryParam.pageSize
        })
      },
      // 重置
      resetSearchForm () {
        this.queryParam = {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNo: 1,
          // Search value
          searchVal: '',
          //
          type: 'ALL'
        }
        this.list()
      }
    }
  }
</script>
