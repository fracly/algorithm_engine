<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-input
                style="width: 30%"
                placeholder="请输入cube名称查询"
                v-model="queryParam.searchVal"
                @keyup.enter="handleSearch"></a-input>
              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询
              </a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
<!--              <a-button type="primary" style="width: 160px;float:right" icon="plus" @click="handleAdd">新建调度</a-button>-->
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      :dataSource="cubeList"
      rowKey="name"
      :pagination="false">
      <span slot="description" slot-scope="text">
        <a-popover title="" :content="text" v-if="text.length > 10">
          {{ text.substr(0, 10) + '...' }}
        </a-popover>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="status" slot-scope="text">
        <span v-if="text === 'DISABLED'"><a-tag color="#d2d6de">DISABLED</a-tag></span>
        <span v-else-if="text === 'READY'"><a-tag color="#00a65a">READY</a-tag></span>
        <span v-else>
          {{ text }}
        </span>
      </span>
      <span slot="size" slot-scope="text">
        <span v-if="text>1024">{{ (text/1024).toFixed(2) }}MB</span>
        <span v-else>
          {{ text }}KB
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
          <a v-else @click="handleOnline(record)" :disabled="record.crontab===null">上线</a>
          <a-divider type="vertical"></a-divider>
          <a :disabled="record.releaseState === 'ONLINE'" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"></a-divider>
          <a  @click="handleLog(record)">日志</a>
          <a-divider type="vertical"></a-divider>
          <confirm
            :disabled="record.releaseState === 'ONLINE'||record.crontab===null"
            title="确认删除"
            @confirm="handleDelete(record)"
            content="确认删除该调度吗？"></confirm>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        <span v-if="text===null">{{ text }}</span>
        <span v-else>{{ text| moment }}</span>
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
  import { queryCubeList, onlineSchedule, offlineSchedule, deleteSchedule } from '@/api/kylin'
  import Moment from 'moment'
  import Confirm from '@/components/Notification/Confirm'
  import CreateScheduleModal from './modules/CreateScheduleModal'

  export default {
    name: 'KylinList',
    components: {
      Confirm,
      CreateScheduleModal
    },
    data () {
      return {
        // 查询参数
        queryParam: {
          searchVal: ''
        },
        // 表头
        columns: [
          {
            title: 'CUBE名称',
            dataIndex: 'name'
          },
          {
            title: 'CUBE状态',
            dataIndex: 'status',
            scopedSlots: { customRender: 'status' }
          },
          {
            title: '所属项目',
            dataIndex: 'project'
          },
          {
            title: '大小',
            dataIndex: 'size_kb',
            scopedSlots: { customRender: 'size' }
          },
          {
            title: '数据条数',
            dataIndex: 'input_records_count'
          },
          {
            title: '上次构建时间',
            dataIndex: 'last_modified',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '定时表达式',
            dataIndex: 'crontab'
          },
          {
            title: '定时状态',
            dataIndex: 'releaseState',
            scopedSlots: { customRender: 'releaseState' }
          },
          {
            title: '定时开始时间',
            dataIndex: 'cronStartTime',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '定时结束时间',
            dataIndex: 'cronEndTime',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            width: '12%'
          }
        ],
        cubeList: [],
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
        this.queryParam.searchVal = ''
        this.list()
      },
      handleOnline (item) {
        onlineSchedule({ cubeName: item.name }).then(res => {
            if (res.code === 0) {
                this.$message.success('CUBE调度上线成功')
                this.list()
            } else {
                this.$message.error(res.msg)
            }
        })
      },
      handleOffline (item) {
        offlineSchedule({ cubeName: item.name }).then(res => {
            if (res.code === 0) {
                this.$message.success('CUBE调度下线成功')
                this.list()
            } else {
                this.$message.error(res.msg)
            }
        })
      },
      handleSearch () {
        this.list()
      },
      handleOk () {
      },
      handleAdd () {
        this.$refs.createModal.show()
      },
      handleEdit (record) {
        const editObj = {}
        editObj.cubeName = record.name
        editObj.id = record.id
        if (record.crontab) {
          editObj.timeRange = [new Moment(record.cronStartTime), new Moment(record.cronEndTime)]
          editObj.crontab = record.crontab
          editObj.time = Moment(record.time, 'HH:mm:ss')
          editObj.type = record.type
        }
        this.$refs.createModal.edit(editObj)
      },
      handleLog (record) {
        this.$router.push({ path: `/manager/kylin/log/` + record.name })
      },
      handleDelete (item) {
        const that = this
        deleteSchedule({ 'scheduleId': item.id }).then(res => {
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
        queryCubeList(this.queryParam).then(res => {
          this.cubeList = res.data
        })
      }
    }
  }
</script>
