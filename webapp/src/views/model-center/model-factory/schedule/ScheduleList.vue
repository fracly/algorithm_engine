<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input style="width: 30%" placeholder="请输入模型名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建调度</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      :dataSource="scheduleList"
      :pagination="pagination"
      rowKey="id">
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
      <span slot="failureStrategy" slot-scope="text">
        <span v-if="text === 'END'"><a-tag color="#f50">结束</a-tag></span>
        <span v-else-if="text === 'CONTINUE'"><a-tag color="#108ee9">继续</a-tag></span>
        <span v-else>
          {{ text }}
        </span>
      </span>

      <span slot="releaseState" slot-scope="text">
        <span v-if="text === 'OFFLINE'"><a-tag color="#f50">未上线</a-tag></span>
        <span v-else-if="text === 'ONLINE'"><a-tag color="#33CC33">已上线</a-tag></span>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="action" slot-scope="text, record">
        <template>
          <a v-if="record.releaseState === 'ONLINE'" @click="handleOffline(record)">下线</a>
          <a v-else @click="handleOnline(record)">上线</a>
          <a-divider type="vertical"></a-divider>
          <a :disabled="record.releaseState === 'ONLINE'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <confirm :disabled="record.releaseState === 'ONLINE'" title="确认删除" @confirm="handleDelete(record)" content="确认删除该调度吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    <create-schedule-modal
      ref="createModal"
      :visible="visible"
      :loading="loading"
      @refresh="handleSearch"></create-schedule-modal>
  </a-card>
</template>

<script>
    import { querySchedule, onlineSchedule, offlineSchedule, deleteSchedule } from '@/api/model'
    import Moment from 'moment'

    import Confirm from '@/components/Notification/Confirm'
    import CreateScheduleModal from './modules/CreateScheduleModal'

    export default {
        name: 'ScheduleList',
        components: {
            Confirm,
            CreateScheduleModal
        },
        data () {
            return {
                // 查询参数
                queryParam: {
                    status: '0',
                    searchVal: '',
                    pageNo: 1,
                    pageSize: 10
                },
                // 表头
                columns: [
                    {
                        title: '模型名称',
                        dataIndex: 'processDefinitionName'
                    },
                    {
                        title: '开始时间',
                        dataIndex: 'startTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '结束时间',
                        dataIndex: 'endTime',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '定时表达式',
                        dataIndex: 'crontab'
                    },
                    {
                        title: '失败策略',
                        dataIndex: 'failureStrategy',
                        scopedSlots: { customRender: 'failureStrategy' }
                    },
                    {
                        title: '状态',
                        dataIndex: 'releaseState',
                        scopedSlots: { customRender: 'releaseState' }
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
                        width: '12%'
                    }
                ],
                scheduleList: [],
                visible: false,
                loading: false,
                pagination: {
                    total: 0,
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
                this.queryParam.searchVal = ''
                this.list()
            },
            handleOnline (item) {
                onlineSchedule(item).then(res => {
                    if (res.code === 0) {
                        this.$message.success('调度上线成功')
                        this.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },
            handleOffline (item) {
                offlineSchedule(item).then(res => {
                    if (res.code === 0) {
                        this.$message.success('调度下线成功')
                        this.list()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            },
            handleSearch () {
                this.queryParam.pageNo = 1
                this.$set(this.pagination, 'current', 1)
                this.list()
            },
            handleOk () {
            },
            handleAdd () {
                this.$refs.createModal.show()
            },
            handleEdit (record) {
                const editObj = {}
                editObj.projectName = record.projectName
                editObj.model = record.processDefinitionId
                editObj.id = record.id
                editObj.timeRange = [new Moment(record.startTime), new Moment(record.endTime)]
                editObj.crontab = record.crontab
                this.$refs.createModal.edit(editObj)
            },
            handleDelete (item) {
                const that = this
                deleteSchedule({ 'scheduleId': item.id, 'projectName': item.projectName }).then(res => {
                    if (res.code === 0) {
                        that.$message.success('删除调度成功')
                    } else {
                        that.$message.error(res.msg)
                    }
                    that.handleSearch()
                })
            },
            handleCancel () {
                this.visible = false
            },
            list () {
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                querySchedule(this.queryParam).then(res => {
                    this.scheduleList = res.data
                    this.pagination.total = res.dataMap.total
                })
            }
        }
    }
</script>
