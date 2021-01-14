<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline"  >
        <a-row :gutter="48">
          <a-col :span="6" >
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入数据源名称"/>
            </a-form-item>
          </a-col>
          <a-col :span="8" >
            <span class="table-page-search-submitButtons">
              <a-button type="primary" icon="search" style="width: 120px;margin-left: 20px;" @click="handleSearch()">查询</a-button>
              <a-button  @click="resetSearchForm" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
          <a-col :span="10" :push="7" v-show="userInfo.administrator">
            <a-form-item label="">
              <a-button type="primary" icon="plus" @click="add()"  >创建数据源</a-button>
            </a-form-item >
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      rowKey="name"
      :dataSource="jobList"
      :pagination="pagination"
    >
      <!--showPagination="auto" 自然分页的方法，直接查出所有的数据-->
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="action" slot-scope="text, record">
        <template v-if="userInfo.administrator">
          <a @click="edit(record)">编辑</a>
           <a-divider type="vertical" />
          <confirm title="确认删除" @confirm="handleDeleteRecord(record.id)" content="确认删除该表吗？"></confirm>
        </template>
        <template v-else>
          <span>无</span>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
      <span slot="description" slot-scope="text">
              <a-popover title="" :content="text" v-if="text.length > 10">
                {{ text.substr(0, 10) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>

      <span slot="getConnection" slot-scope="record">
        <template>
          <a-popover title="详细参数" trigger="click" @click="getConnectionDetail(record)">
            <template slot="content">
              <div class="text">user: <span class="inner_text">{{ record_user }}</span></div>
              <div class="text">password: <span class="inner_text">{{ record_password }}</span></div>
              <div class="text">address: <span class="inner_text">{{ record_address }}</span></div>
              <div class="text">database: <span class="inner_text">{{ record_database }}</span></div>
              <div class="text">dbcUrl: <span class="inner_text">{{ record_jdbcUrl }}</span></div>
              <div class="text">other: <span class="inner_text">{{ record_other }}</span></div>
              <div class="text">hostname: <span class="inner_text">{{ record_hostname }}</span></div>
              <div class="text">port: <span class="inner_text">{{ record_port }}</span></div><br>
            </template>
            <a-button type="link" style="height: auto;font-size: 12px">
              查看内容
            </a-button>
          </a-popover>
        </template>
      </span>
    </a-table>
    <DatasourceEdit ref="modal" @refresh="handleSaveOk"  />
  </a-card>
</template>

<script>
  import { getAiDataSourceList, deleteAiDataSource } from '@/api/datasource'
  import ACol from 'ant-design-vue/es/grid/Col'
  import DatasourceEdit from './modules/DatasourceEdit'
  import Confirm from '@/components/Notification/Confirm'

  import { mapGetters } from 'vuex'

  export default {
    name: 'DataSource',
    components: {
      ACol,
      // STable
      DatasourceEdit,
      Confirm
    },
    data () {
      return {
        // 查询参数
        queryParam: {
          searchVal: '',
          pageSize: 10,
          pageNo: 1
        },
        i: 0,
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '数据源名称',

            dataIndex: 'name'
          },
          {
            title: '数据源类型',
            dataIndex: 'type'
          },
          {
            title: '数据源参数',
            scopedSlots: { customRender: 'getConnection' }
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
            title: '描述',
            dataIndex: 'note',
            scopedSlots: { customRender: 'description' }
          },
          {
            title: '操作',
            scopedSlots: { customRender: 'action' }
          }
        ],
        jobList: [],
        pagination: {
          total: 20,
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
        // visible: false,
        record_user: '',
        record_password: '',
        record_address: '',
        record_database: '',
        record_jdbcUrl: '',
        record_other: '',
        record_hostname: '',
        record_port: '',
        // 删除对话框的属性
        ModalText: '确认删除吗？',
        visible: false
        // confirmLoading: false,
      }
    },
    // filters: {
    // },
    created () {
      this.list()
    },
    computed: {
        ...mapGetters(['userInfo'])
    },
    methods: {
      handleDelete (id) {
        const self = this
        deleteAiDataSource({ 'id': id }).then(res => {
          this.$message.success('删除成功')
          self.list()
        })
      },

      handleDeleteRecord: function (id) {
        const _this = this
        deleteAiDataSource({ 'id': id }).then(res => {
          if (res.code === 0) {
            _this.$message.success('删除表成功')
            _this.list()
          } else {
            _this.$message.error('删除表失败')
          }

        }).catch(err => {
          _this.$message.error(err)

        })
      },
      handleSearch () {
        this.queryParam.pageNo = 1
        this.$set(this.pagination, 'current', 1)
        this.queryParam.pageNo=1
          this.list()
      },
      list () {
        this.queryParam.searchVal = this.queryParam.searchVal.trim()
        getAiDataSourceList(this.queryParam).then(res => {
          this.jobList = res.data.totalList
          this.pagination.total = res.data.total
        }).catch(err => {
          this.$message.error('获取数据源的数据失败' + err)
        })
      },
      getConnectionDetail (record) {
        const a = record.connectionParams
        const b = JSON.parse(a)
        this.record_user = b.user
        this.record_password = b.password
        this.record_address = b.address
        this.record_database = b.database
        this.record_jdbcUrl = b.jdbcUrl
        this.record_other = b.other
        this.record_hostname = b.hostname
        this.record_port = b.port
      },
      resetSearchForm () {
        this.$set(this.pagination, 'current', 1)
        this.queryParam.pageNo = 1
        this.queryParam.searchVal = ''
        this.list()
      },
      add () {
        this.$refs.modal.add()
      },
      /**
       * model模块引入，不用dialog
       * @param record
       */
      edit (record) {
        this.$refs.modal.edit(record)
      },
      handleSaveOk () {
        // 刷新当前数据
        this.list()
      },
      // handleSaveClose () {
      // },
      refresh () {
        this.list()
      }
    }
  }
</script>
<style lang="less">
  .text{
    color: red;
  }
  .inner_text{
    color: black;
  }
  .test {
    & /deep/ .ant-table-tbody > tr > td {
      padding: 10px!important;
    }
  }
</style>
