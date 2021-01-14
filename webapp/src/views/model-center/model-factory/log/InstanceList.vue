<template>
  <div>
    <a-card>
      <div style="margin-bottom: 10px;">
        <a-form layout="inline">
          <a-row>
            <a-col>
              <div class="operate" style="margin-bottom: 20px">
                <a-input style="width: 30%" placeholder="请输入模型名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
                <a-button type="primary" style="width: 100px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
                <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
                <a @click="toggleAdvanced" style="margin-left: 8px">
                  {{ advanced ? '收起' : '展开' }}
                  <a-icon :type="advanced ? 'up' : 'down'"/>
                </a>
              </div>
            </a-col>
            <a-col v-show="advanced">
              <a-form-item label="起止时间">
                <a-range-picker  v-model="timeRange" @change="handleRangePickerChange"showTime format="YYYY-MM-DD HH:mm:ss" :style="{width: '320px'}" />
              </a-form-item>
              <a-form-item label="任务状态">
                <a-select show-search optionFilterProp="children" v-model="queryParam.stateType" style="width: 130px" @change="handleStateTypeChange">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="SUBMITTED_SUCCESS">提交成功</a-select-option>
                  <a-select-option value="RUNNING_EXEUTION">执行中</a-select-option>
                  <a-select-option value="READY_PAUSE">等待暂停</a-select-option>
                  <a-select-option value="PAUSE">暂停</a-select-option>
                  <a-select-option value="READY_STOP">等待停止</a-select-option>
                  <a-select-option value="STOP">停止</a-select-option>
                  <a-select-option value="FAILURE">失败</a-select-option>
                  <a-select-option value="SUCCESS">成功</a-select-option>
                  <a-select-option value="NEED_FAULT_TOLERANCE">需要容错</a-select-option>
                  <a-select-option value="KILL">运行中</a-select-option>
                  <a-select-option value="WAITTING_THREAD">线程等待</a-select-option>
                  <a-select-option value="WAITTING_DEPEND">依赖等待</a-select-option>
                  <a-select-option value="PARAMS_ERROR">参数错误</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-spin :spinning="tableSpinning">
        <a-table
          :columns="columns"
          :dataSource="taskInstanceList"
          :pagination="pagination"
          :loading="loading"
          size="middle"
          rowKey="name">
          <span slot="name" slot-scope="text, record">
            <a @click="handleInstanceDetail(record)">{{ text }}</a>
          </span>
          <span slot="time" slot-scope="text">
            <template v-if="text === null">
            </template>
            <template v-else>
               {{ text | moment }}
            </template>
          </span>
          <span slot="commandType" slot-scope="text">
            {{ text | process_execute_type}}
          </span>
          <span slot="tabledetail" slot-scope="text">
            <template>
              <a-popover title="" :content="text" v-if="JSON.stringify(text).length > 17">
              {{ text.substr(0, 15) + '...' }}
            </a-popover>
            <span v-else>
              {{ text }}
            </span>
            </template>
          </span>
          <span slot="recovery" slot-scope="text">
            <template> {{ text | yes_or_no }} </template>
          </span>
          <span slot="state" slot-scope="text">
            <template> {{ text | execution_status }} </template>
          </span>
          <span slot="action" slot-scope="text, record">
              <template>
                <span v-show="record.state === 'SUCCESS' || record.state === 'FAILURE'">
                  <a href="javascript:;" @click="handleRerun(record)">重跑</a>
                  <a-divider type="vertical"></a-divider>
                </span>
                <span v-show="record.state === 'RUNNING_EXEUTION'">
                  <a href="javascript:;" @click="handlePause(record)">暂停</a>
                  <a-divider type="vertical"></a-divider>
                </span>
                <span v-show="record.state === 'RUNNING_EXEUTION'">
                  <a href="javascript:;" @click="handleStop(record)">停止</a>
                  <a-divider type="vertical"></a-divider>
                </span>
                <span v-show="record.state === 'SUCCESS' || record.state === 'FAILURE'">
                  <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该模型实列吗？"></confirm>
                </span>
              </template>
            </span>
        </a-table>
      </a-spin>
      <instance-task-detail ref="modal"></instance-task-detail>
    </a-card>
  </div>
</template>

<script>
    import InstanceTaskDetail from './modules/InstanceTaskDetail'
    import Confirm from '@/components/Notification/Confirm'

    import { processInstanceList, executeInstance, deleteInstance } from '@/api/model'
    export default {
        components: {
            InstanceTaskDetail,
            Confirm
        },
        data () {
            return {
                advanced: false,
                loading: false,
                filter: {},
                timeRange: [],
                layerList: [],
                taskInstanceList: [],
                queryParam: {
                    layer: 'all',
                    topic: '全部',
                    pageNo: 1,
                    pageSize: 10,
                    searchVal: '',
                    stateType: '',
                    startDate: '',
                    endDate: ''
                },
                tableSpinning: false,
                columns: [
                    {
                        title: '模型名称',
                        dataIndex: 'processDefinitionName',
                        width: '12%',
                        scopedSlots: { customRender: 'name' }
                    },
                    {
                        title: '运行类型',
                        dataIndex: 'commandType',
                        width: '12%',
                        scopedSlots: { customRender: 'commandType' }
                    },
                    {
                        title: '开始时间',
                        dataIndex: 'startTime',
                        scopedSlots: { customRender: 'time' },
                        width: '11%'
                    },
                    {
                        title: '结束时间',
                        dataIndex: 'endTime',
                        scopedSlots: { customRender: 'time' },
                        width: '11%'
                    },
                    {
                        title: 'host',
                        dataIndex: 'host',
                        width: '8%'
                    },
                    {
                        title: '运行时长(秒)',
                        dataIndex: 'duration',
                        width: '8%'
                    },
                    {
                        title: '运行次数',
                        dataIndex: 'runTimes',
                        width: '5%'
                    },
                    {
                        title: '容错标识',
                        dataIndex: 'recovery',
                        width: '5%',
                        scopedSlots: { customRender: 'recovery' }
                    },
                    {
                        title: '状态',
                        dataIndex: 'state',
                        width: '5%',
                        scopedSlots: { customRender: 'state' }
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        width: '10%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
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
                        this.getData()
                    },
                    onShowSizeChange: (current, size) => {
                        this.$set(this.pagination, 'current', current)
                        this.$set(this.pagination, 'pageSize', size)
                        this.queryParam.pageNo = current
                        this.queryParam.pageSize = size
                        this.getData()
                    }
                }
            }
        },
        methods: {
            resetSearchForm () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.queryParam.searchVal = ''
                this.queryParam.stateType = ''
                this.queryParam.startDate = ''
                this.queryParam.endDate = ''
                this.timeRange = []
                this.advanced = !this.advanced
                this.getData()
            },
            handleInstanceDetail (record) {
                this.$refs.modal.show(record)
            },
            handleSearch () {
                this.queryParam.pageNo = 1
                this.$set(this.pagination, 'current', 1)
                this.getData()
            },
            handleRerun (record) {
                const that = this
                executeInstance({ 'processDefinitionName': record.projectName, 'processInstanceId': record.id, 'executeType': 'REPEAT_RUNNING' }).then(res => {
                    if (res.code === 0) {
                        that.$message.success('重跑成功')
                        setTimeout(that.getData, 2000)
                    } else {
                        that.$message.error(res.msg)
                    }
                })
            },
            handleRangePickerChange (val) {
                if (val.length === 0) {
                    this.queryParam.startDate = null
                    this.queryParam.endDate = null
                } else {
                    this.queryParam.startDate = val[0].format('YYYY-MM-DD HH:mm:ss')
                    this.queryParam.endDate = val[1].format('YYYY-MM-DD HH:mm:ss')
                }
                this.getData()
            },
            handleStateTypeChange (val) {
                this.queryParam.stateType = val
                this.getData()
            },
            handleDelete (record) {
                const that = this
                deleteInstance({ 'processInstanceId': record.id, 'processDefinitionName': record.projectName }).then(res => {
                    if (res.code === 0) {
                        that.$message.success('删除成功')
                        that.getData()
                    } else {
                        that.$message.error(res.msg)
                    }
                })
            },
            handlePause (record) {
                const that = this
                const params = {
                    'processDefinitionName': record.projectName,
                    'processInstanceId': record.id,
                    'executeType': 'PAUSE'
                }
                executeInstance(params).then(res => {
                    if (res.code === 0) {
                        setTimeout(that.getData, 2000)
                    } else {
                        that.$message.error(res.msg)
                    }
                })
            },
            handleStop (record) {
                const that = this
                const params = {
                    'processDefinitionName': record.projectName,
                    'processInstanceId': record.id,
                    'executeType': 'STOP'
                }
                executeInstance(params).then(res => {
                    if (res.code === 0) {
                        setTimeout(that.getData, 2000)
                    } else {
                        that.$message.error(res.msg)
                    }
                })
            },
            toggleAdvanced () {
                this.advanced = !this.advanced
            },
            getData () {
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                processInstanceList(this.queryParam).then(res => {
                    if (res.code === 0) {
                        this.pagination.total = res.data.total
                        this.taskInstanceList = res.data.totalList
                    } else {
                        console.log('获取元信息失败')
                    }
                })
            }
        },
        created () {
            this.getData()
        },
        computed: {
            title () {
                return this.$route.meta.title
            }
        }
    }
</script>

<style lang="less" scoped>
  .title {
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 16px;
  }
  canvas {
    outline: none;
  }
  .ant-form label {
    font-size: 12px !important;
  }
</style>
