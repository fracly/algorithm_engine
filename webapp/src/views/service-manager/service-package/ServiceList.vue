<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入服务名称"></a-input>
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
              <a-button type="primary" style="margin-left: 10px" icon="plus" @click="() => { this.$router.push('/resource/service/add') }">新建服务</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      rowKey="serviceId"
      :dataSource="serviceList"
      :pagination="pagination"
      @change="tableChange"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="status" slot-scope="text">
        {{ text | statusFilter }}
      </span>
      <span slot="type" slot-scope="text">{{ getTypeList(text) }}</span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a v-if="record.status === '1'" @click="handleDisable(record.serviceId)">下线</a>
          <a v-if="record.status === '0'" @click="handleEnable(record.serviceId)">上线</a>
          <a-divider type="vertical"></a-divider>
          <a v-if="record.status === '1'" @click="handleShow(record)">查看</a>
          <a v-if="record.status === '0'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <!--          <a @click="handleDelete(record)">删除</a>-->
          <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该服务吗？"></confirm>

        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
  </a-card>
</template>

<script>
  import { getListP, editServiceState, deleteService, getTypeList } from '@/api/service'
  import Confirm from '@/components/Notification/Confirm'

  export default {
    name: 'ServicePackage',
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
          searchVal: '',
          //
          status: '',
          //
          type: ''
        },
        // 表头
        columns: [],
        serviceList: [],
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
            // this.list()
          },
          onShowSizeChange: (current, size) => {
            this.queryParam.pageNo = current
            this.queryParam.pageSize = size
            // this.list()
          }
        },
        stateList: [{ name: '上线', code: '1' }, { name: '下线', code: '0' }]
      }
    },
    filters: {
      statusFilter (type) {
        if (type === '0') {
          return '下线'
        } else {
          return '上线'
        }
      }
    },
    created () {
      this.getType()
      this.list()
    },
    methods: {
      // 下线
      handleDisable (id) {
        editServiceState({ 'id': id, status: 0 }).then(res => {
          if (res.code === 0) {
            this.$message.success('下线成功')
            this.list()
          } else {
            this.$message.error(res.msg)
          }
        })
      },
      // 上线
      handleEnable (id) {
        editServiceState({ 'id': id, status: 1 }).then(res => {
          if (res.code === 0) {
            this.$message.success('上线成功')
            this.list()
          } else {
            this.$message.error(res.msg)
          }
        })
      },
      // 删除
      handleDelete (record) {
        const self = this
        deleteService({ 'id': record.serviceId }).then(res => {
          if (res.code === 0) {
            self.$message.success('删除成功')
            self.list()
          } else {
            self.$message.success(res.msg)
          }
        })
      },
      // 查询
      handleSearch () {
        this.queryParam.pageNo = 1
        this.list()
      },
      //  编辑
      handleEdit (item) {
        this.$router.push({ path: `/resource/service/edit/` + item.serviceId })
      },
      //  查看
      handleShow (item) {
        this.$router.push({ path: `/resource/service/view/` + item.serviceId })
      },
      // 列表数据
      list () {
        this.queryParam.searchVal = this.queryParam.searchVal.trim()
        getListP(this.queryParam).then(res => {
          this.serviceList = res.data.totalList
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
          status: '',
          //
          type: ''
        }
        this.list()
      },
      getTypeList (type) {
        for (let i = 0; i < this.typeList.length; i++) {
          if (this.typeList[i].code === type) {
            return this.typeList[i].name
          }
        }
      },
      getType () {
        getTypeList({ code: '01' }).then(res => {
          this.typeList = res.data
          const serviceTypeList = []
          for (let i = 0; i < this.typeList.length; i++) {
            serviceTypeList.push({
              'text': this.typeList[i].name,
              'value': this.typeList[i].code
            })
          }
          this.columns = [
            {
              title: '编号',
              scopedSlots: { customRender: 'serial' }
            },
            {
              title: '服务名称',
              dataIndex: 'serviceName'
            },
            {
              title: '服务编码',
              dataIndex: 'serviceCode'
            },
            {
              title: '服务类型',
              dataIndex: 'service_type',
              scopedSlots: { customRender: 'type' },
              filters: serviceTypeList
            },
            {
              title: '创建人',
              dataIndex: 'createUser'
            },
            {
              title: '创建时间',
              dataIndex: 'createTime',
              scopedSlots: { customRender: 'time' }
            },
            {
              title: '状态',
              dataIndex: 'status',
              scopedSlots: { customRender: 'status' },
              filters: [
                { 'text': '上线', 'value': '1' },
                { 'text': '下线', 'value': '0' }
              ]
            },
            {
              title: '操作',
              dataIndex: 'action',
              scopedSlots: { customRender: 'action' }
            }
          ]
        })
      },
      tableChange (pagination, filters) {
        if (filters.status) {
          this.queryParam.status = filters.status.length === 1 ? filters.status.toString() : ''
        }
        if (filters.service_type) {
          this.queryParam.type = filters.service_type.length >= 1 ? filters.service_type.toString() : ''
        }
        this.queryParam.pageSize = pagination.pageSize
        this.list()
      }
    }
  }
</script>
<style lang="less">
  .ant-table-thead > tr > th .anticon-filter{
    right: unset;
  }
</style>
