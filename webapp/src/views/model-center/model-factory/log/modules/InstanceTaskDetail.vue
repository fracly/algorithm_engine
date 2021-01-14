<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :closable="false"
  >
    <a-spin :spinning="loading">
      <a-table
        ref="table"
        size="default"
        :columns="columns"
        rowKey="id"
        :dataSource="taskList"
        :pagination="false"
        :scroll="{ y: 330 }"
        style="overflow-x:auto;overflow-y:auto;height:400px;">
        <span slot="time" slot-scope="text">
              <span v-if="text !== null">
                  {{ text | moment }}
              </span>
            </span>
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
        <span slot="taskType" slot-scope="text">
          {{ text | task_type }}
        </span>
        <span slot="state" slot-scope="text">
          {{ text | execution_status }}
        </span>
        <span slot="action" slot-scope="text, record">
          <template>
            <a @click="handleLogDetail(record)">日志</a>
          </template>
        </span>
      </a-table>
    </a-spin>
    <template slot="footer">
      <a-button type="default" @click="handleClose">关闭</a-button>
    </template>
    <log-detail ref="modal"></log-detail>
  </a-modal>
</template>

<script>
    import LogDetail from './LogDetail'
    import { instanceTaskList } from '@/api/model'
    export default {
        name: 'InstanceTaskDetail',
        components: {
            LogDetail
        },
        data () {
            return {
                title: '',
                visible: false,
                loading: false,
                taskList: [],
                columns: [
                    {
                        title: '节点名称',
                        dataIndex: 'name',
                        width: '8%'
                    },
                    {
                        title: '模型实例名称',
                        dataIndex: 'processInstanceName',
                        width: '10%'
                    },
                    {
                        title: '节点类型',
                        dataIndex: 'taskType',
                        scopedSlots: { customRender: 'taskType' },
                        width: '8%'
                    },
                    {
                        title: '状态',
                        dataIndex: 'state',
                        width: '8%',
                        scopedSlots: { customRender: 'state' }
                    },
                    {
                        title: '提交时间',
                        dataIndex: 'submitTime',
                        scopedSlots: { customRender: 'time' },
                        width: '11%'
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
                        width: '10%'
                    },
                    {
                        title: '运行时长(秒)',
                        dataIndex: 'duration',
                        width: '8%'
                    },
                    {
                        title: '重复次数',
                        dataIndex: 'retryTimes',
                        width: '5%'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        width: '7%',
                        scopedSlots: { customRender: 'action' }
                    }
                ]
            }
        },
        created () {
        },
        methods: {
            show (record) {
                this.visible = true
                this.title = '模型实例任务列表'
                const _this = this
                const params = { 'processInstanceId': record.id }
                instanceTaskList(params).then(res => {
                    _this.taskList = res.data
                })
            },
            handleClose () {
                this.visible = false
            },
            handleLogDetail (record) {
                debugger
                this.$refs.modal.show(record.id)
            }
        }
    }
</script>
