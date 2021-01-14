<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row>
          <a-col>
            <div class="operate" style="margin-bottom: 20px">
              <a-col :md="6" :sm="12">
                <a-form-item label="">
                  <a-select
                    placeholder="请选择任务状态"
                    v-model="queryParam.status"
                  >
                    <a-select-option
                      v-for="(item) in statusList"
                      :key="item.code"
                      :value="item.code">{{ item.name }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <a-button type="primary" style="width: 120px;margin-left:20px;" icon="search" @click="handleSearch">查询
              </a-button>
              <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <a-table
      ref="table"
      size="middle"
      :columns="columns"
      :dataSource="logList"
      rowKey="name"
      :pagination="false">

      <span slot="progress" slot-scope="text,record">
        <a-progress :percent="text" size="small" status="exception" v-if="text!==100&&record.job_status==='ERROR'" />
        <a-progress :percent="text" size="small" v-if="text===100" />
        <a-progress :percent="text"  size="small" v-if="text!=100&record.job_status==='PENDING'" />
       <a-progress :percent="text" strokeColor="navy" size="small" v-if="text!=100&record.job_status==='DISCARDED'" />
      </span>
      <span slot="status" slot-scope="text">
         <a-badge status="processing" :text="text" v-if="text==='PENDING'||text==='PENDING'"/>
         <a-badge status="success" v-if="text==='FINISHED'" :text="text" />
         <a-badge status="error" v-if="text==='ERROR'||text==='DISCARDED'" :text="text" />
      </span>
      <span slot="description" slot-scope="text">
        <a-popover title="" :content="text" v-if="text.length > 40">
          {{ text.substr(0, 40) + '...' }}
        </a-popover>
        <span v-else>
          {{ text }}
        </span>
      </span>

      <span slot="duration" slot-scope="text">
        <span v-if="text>60">{{ (text/60).toFixed(2) }}mins</span>
        <span v-else>
          {{ text }}s
        </span>
      </span>
      <span slot="action" slot-scope="text,record ">
        <template>
          <a @click="handleDetail(record)">详情</a>
        </template>
      </span>
      <span slot="time" slot-scope="text">
        {{ text | moment }}
      </span>
    </a-table>
    <log-detail-modal
      ref="detailModal"
      :visible="visible"
      :loading="loading"
      @refresh="handleSearch"></log-detail-modal>
  </a-card>
</template>

<script>
  import { queryLogList } from '@/api/kylin'
  // import Moment from 'moment'
  import Confirm from '@/components/Notification/Confirm'
  import LogDetailModal from './LogDetailModal'

  export default {
    name: 'LogList',
    components: {
      Confirm,
      LogDetailModal
    },
    data () {
      return {
        // 查询参数
        queryParam: {
          cubeName: '',
          status: undefined
        },
        // 表头
        columns: [
          {
            title: '任务名称',
            dataIndex: 'name',
            scopedSlots: { customRender: 'description' },
            width: '25%'
          },
          {
            title: 'CUBE名称',
            dataIndex: 'display_cube_name'
          },
          {
            title: '进度',
            dataIndex: 'progress',
            scopedSlots: { customRender: 'progress' },
            width: '15%'
          },
          {
            title: '状态',
            dataIndex: 'job_status',
            scopedSlots: { customRender: 'status' },
          },
          {
            title: '上次构建时间',
            dataIndex: 'last_modified',
            scopedSlots: { customRender: 'time' }
          },
          {
            title: '构建时间',
            dataIndex: 'duration',
            scopedSlots: { customRender: 'duration' }
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            width: '10%'
          }
        ],
        logList: [],
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
        },
        statusList: [{ name: 'NEW', code: 0 }, { name: 'PENDING', code: 1 }, { name: 'RUNNING', code: 2 }, { name: 'STOPPED', code: 32 },
          { name: 'FINISHED', code: 4 }, { name: 'ERROR', code: 8 }, { name: 'DISCARDED', code: 16 }]
      }
    },
    created () {
      this.list()
    },
    methods: {
      resetSearchForm () {
        this.queryParam.status = undefined
        this.queryParam.searchVal = ''
        this.list()
      },
      handleSearch () {
        this.list()
      },
      handleOk () {
      },
      handleDetail (record) {
        const editObj = {}
        editObj.name = record.name
        editObj.cubeId = record.uuid
        editObj.steps = record.steps
        editObj.projectName = record.projectName
        editObj.status = record.job_status
        editObj.duration = record.duration
        this.$refs.detailModal.edit(editObj)
      },
      handleLog (record) {
      },
      handleCancel () {
        this.visible = false
      },
      list () {
        this.queryParam.cubeName = this.$route.params.name
        queryLogList(this.queryParam).then(res => {
          this.logList = res.data
        })
      }
    }
  }
</script>
