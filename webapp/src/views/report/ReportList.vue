<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="12">
            <a-form-item label="">
              <a-input v-model="queryParam.searchVal" placeholder="请输入报表名称"></a-input>
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
              <a-button type="primary" style="margin-left: 10px;" icon="plus" @click="() => { this.$router.push('/report/custom/add') }">新建报表</a-button>
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
      :dataSource="reportList"
      :pagination="pagination"
      :alert="{ show: true, clear: true }"
    >
      <span slot="serial" slot-scope="text, record, index">
        {{ index + 1 }}
      </span>
      <span slot="status" slot-scope="text">
        {{ text | statusFilter }}
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a v-if="record.status === '1'" @click="handleDisable(record.id)">下线</a>
          <a v-if="record.status === '0'" @click="handleEnable(record.id)">上线</a>
          <a-divider type="vertical"></a-divider>
          <a v-if="record.status === '1'" @click="handleShow(record)">查看</a>
          <a v-if="record.status === '0'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
<!--          <a @click="handleDelete(record)">删除</a>-->
         <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该报表吗？"></confirm>

        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
  </a-card>
</template>

<script>
    import { getReportListP, editStatusReport, deleteReport } from '@/api/report'
    import Confirm from '@/components/Notification/Confirm'

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
                    searchVal: '',
                    //
                    status: ''
                },
                // 表头
                columns: [
                    {
                        title: '编号',
                        scopedSlots: { customRender: 'serial' }
                    },
                    {
                        title: '报表名称',
                        dataIndex: 'reportName'
                    },
                    {
                        title: '报表编码',
                        dataIndex: 'reportCode'
                    },
                    {
                        title: '创建人',
                        dataIndex: 'createUser'
                    },
                    {
                        title: '状态',
                        dataIndex: 'status',
                        scopedSlots: { customRender: 'status' }
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
                reportList: [],
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
                }
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
            this.list()
        },
        methods: {
            // 下线
            handleDisable (id) {
                editStatusReport({ 'id': id, status: 0 }).then(res => {
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
                editStatusReport({ 'id': id, status: 1 }).then(res => {
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
              // 在这里调用删除接口
              deleteReport({ 'id': record.id }).then(res => {
                if (res.code === 0) {
                  self.$message.success('删除成功')
                  self.list()
                } else {
                  self.$message.success(res.msg)
                }
              })
                // this.$confirm({
                //     title: '警告',
                //     content: `真的要删除${record.reportName}吗?`,
                //     okText: '删除',
                //     okType: 'danger',
                //     cancelText: '取消',
                //     onOk () {
                //         console.log('OK')
                //
                //     },
                //     onCancel () {
                //     }
                // })
            },
            // 查询
            handleSearch () {
              this.queryParam.pageNo = 1
              this.list()
            },
            // 编辑
            handleEdit (item) {
              this.$router.push({ path: `/report/custom/edit/` + item.id })
            },
            handleShow (item) {
              this.$router.push({ path: `/report/custom/view/` + item.id })
            },
            // 列表数据
            list () {
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                getReportListP(this.queryParam).then(res => {
                    this.reportList = res.data.totalList
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
                        status: ''
                }
                this.list()
            }
        }
    }
</script>
