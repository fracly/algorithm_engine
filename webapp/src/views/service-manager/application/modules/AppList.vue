<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入应用名称"></a-input>
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
              <a-button
                type="primary"
                style="margin-left: 10px"
                icon="plus"
                @click="() => { this.$router.push('/resource/application/add') }">新建应用</a-button>
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
      :dataSource="appList"
      :pagination="pagination"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="type" slot-scope="text">
        {{ getTypeName(text) }}
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a @click="log(record)">查看日志</a>
          <a-divider type="vertical"></a-divider>
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <!--          <a @click="handleDelete(record)">删除</a>-->
          <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该应用吗？"></confirm>

        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
  </a-card>
</template>

<script>
  import { applicationList, deleteApp, getTypeList } from '@/api/service'
  import Confirm from '@/components/Notification/Confirm'

  export default {
    name: 'AppList',
    components: { Confirm },
    data () {
      return {
        // 查询参数
        queryParam: {
          // Number of pages per page
          pageSize: 10,
          // Number of pages
          pageNo: 1,
          searchVal: ''
        },
        appList: [],
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
            this.getList()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam.pageNo = current
            this.queryParam.pageSize = size
            this.getList()
          }
        },
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '应用名称',
            dataIndex: 'name'
          },
          {
            title: '应用类型',
            dataIndex: 'type',
            scopedSlots: { customRender: 'type' }
          },
          {
            title: 'API_Key',
            dataIndex: 'apikey'
          },
          {
            title: 'Secret_Key',
            dataIndex: 'secretKey'
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
        typeList: []
      }
    },
    created () {
      this.getType()
    },
    methods: {
      getType () {
        getTypeList({ code: '02' }).then(res => {
          this.typeList = res.data
        })
      },
      getTypeName (type) {
        for (let i = 0; i < this.typeList.length; i++) {
          if (this.typeList[i].code === type) {
            return this.typeList[i].name
          }
        }
      },
      // 获取服务列表
      getList () {
        const self = this
        this.queryParam.searchVal = this.queryParam.searchVal.trim()
        applicationList(this.queryParam).then(res => {
          self.appList = res.data.lists
          self.pagination.total = res.data.totalCount
          self.pagination.current = this.queryParam.pageNo
          self.pagination.pageSize = this.queryParam.pageSize
        })
      },

      // 查询
      handleSearch () {
        this.queryParam.pageNo = 1
        this.getList()
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
        this.getList()
      },
      //  编辑
      handleEdit (item) {
        this.$router.push({ path: `/resource/application/edit/` + item.id })
      },
      // 删除
      handleDelete (record) {
        const self = this
        deleteApp({ 'applicationId': record.id }).then(res => {
          if (res.code === 0) {
            self.$message.success('删除成功')
            self.getList()
          } else {
            self.$message.success(res.msg)
          }
        })
      },
      // 查看日志
      log (item) {
        this.$router.push({ name: 'AppLog', params: { applicationName: item } })
      }
    },
    mounted () {
      this.getList()
    },
    watch: {
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
      span {
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
      border: 1px solid #909090;
      /*box-shadow: 2px 1px 2px #888888;*/
      /*border-radius: 5px;*/
    }
  }
</style>
