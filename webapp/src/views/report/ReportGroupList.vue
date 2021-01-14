<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入分组名称"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="16">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" icon="search" style="width: 120px;margin-left: 20px;" @click="handleSearch()">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
            </span>
          </a-col>
          <a-col :md="3" :sm="8" style="float: right;">
            <a-form-item label="">
              <a-button type="primary" style="margin-left: 10px;" icon="plus" @click="addGroup">新建分组</a-button>
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
      :dataSource="groupList"
      :pagination="pagination"
      :alert="{ show: true, clear: true }"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
         <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该分组吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    <a-modal
      :title="title"
      class="data-modal"
      :width="500"
      :visible="visible"
      :maskClosable="false"
      @ok="onOk"
      @cancel="close">
      <a-form-model :model="group" :rules="rules" ref="groupForm">
        <a-form-model-item
          label="分组名称"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
          prop="name"
        >
          <a-input
            placeholder="请输入分组名称"
            v-model="group.name"
          />
        </a-form-model-item>
        <a-form-model-item
          label="描述"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          class="stepFormText"
        >
          <a-textarea
            placeholder="请输入描述"
            v-model="group.desc"
            :auto-size="{ minRows: 2 }"/>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </a-card>
</template>

<script>
  import { getGroupsListP, createGroup, updateGroup, deleteGroup } from '@/api/report'
  import Confirm from '@/components/Notification/Confirm'
  import _ from 'lodash'
  export default {
    name: 'ReportList',
    components: { Confirm },
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
        },
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '分组名称',
            dataIndex: 'name'
          },
          {
            title: '分组描述',
            dataIndex: 'desc'
          },
          {
            title: '报表数',
            dataIndex: 'num'
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
        groupList: [],
        visible: false,
        loading: false,
        pagination: {
          total: 20,
          pageSize: 10,
          current : 1,
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
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        group: {
          name: '',
          desc: '',
          status: 1
        },
        rules: {
          name: [{ required: true, message: '分组名称不能为空', whitespace: true }]
        },
        title: '',
        isAdd: true
      }
    },
    created () {
      this.list()
    },
    methods: {
      // 删除
      handleDelete (record) {
        const self = this
        deleteGroup({ 'id': record.id }).then(res => {
          if (res.code === 0) {
            self.$message.success('删除成功')
            self.list()
          } else {
            self.$message.success(res.msg)
          }
        })

        // this.$confirm({
        //   title: '警告',
        //   content: `真的要删除${record.name}吗?`,
        //   okText: '删除',
        //   okType: 'danger',
        //   cancelText: '取消',
        //   onOk () {
        //     console.log('OK')
        //     // 在这里调用删除接口
        //     deleteGroup({ 'id': record.id }).then(res => {
        //       if (res.code === 0) {
        //         self.$message.success('删除成功')
        //         self.list()
        //       } else {
        //         self.$message.success(res.msg)
        //       }
        //     })
        //   },
        //   onCancel () {
        //   }
        // })
      },
      // 查询
      handleSearch () {
        this.queryParam.pageNo = 1
        this.list()
      },
      // 编辑
      handleEdit (item) {
        this.visible = true
        this.title = '编辑分组'
        this.isAdd = false
        this.group = _.cloneDeep(item)
      },
      // 列表数据
      list () {
        this.queryParam.searchVal = this.queryParam.searchVal.trim()
        getGroupsListP(this.queryParam).then(res => {
          this.groupList = res.data.lists
          this.pagination.total = res.data.totalCount
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
          searchVal: ''
        }
        this.list()
      },

      close () {
        this.visible = false
      },
      onOk () {
        const self = this
        this.$refs.groupForm.validate(valid => {
          if (valid) {
            if (self.isAdd) {
              createGroup(self.group).then(res => {
                if (res.code === 0) {
                  self.$message.success('分组创建成功')
                  self.close()
                  self.list()
                } else {
                  self.$message.error(res.msg)
                }
              }).catch(e => {
                self.$message.error(e)
              })
            } else {
              updateGroup(self.group).then(res => {
                if (res.code === 0) {
                  self.$message.success('文件编辑成功')
                  self.close()
                  self.list()
                } else {
                  self.$message.error(res.msg)
                }
              }).catch(e => {
                self.$message.error(e)
              })
            }
          } else {
            console.log('error submit!!')
            return false
          }
        })
      },
      addGroup () {
        this.visible = true
        this.title = '新建分组'
        this.isAdd = true
        this.group = {
          name: '',
          desc: '',
          status: 1
        }
      }
    }
  }
</script>
