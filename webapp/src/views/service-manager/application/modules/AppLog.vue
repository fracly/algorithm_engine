<template>
  <a-card>
    <a-form :form="form" style="max-width: 1000px; margin: 40px auto 0;">
      <a-form-item
        label="应用名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          show-search
          optionFilterProp="children"
          placeholder="请选择应用名称"
          v-model="info.applicationName"
          @change="getServerListByApp"
        >
          <a-select-option
            v-for="(item) in appList"
            :key="item.apikey"
            :value="item.apikey">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="接口选择"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          show-search
          optionFilterProp="children"
          placeholder="请选择接口"
          v-model="info.serviceName"
        >
          <a-select-option
            v-for="(item) in serversList"
            :key="item.service_id"
            :value="item.service_id">{{ item.service_name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="统计项"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-radio-group v-model="info.statistics">
          <a-radio value="QPS" :disabled="true">QPS</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        label="监控项"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-checkbox-group :options="statusList" v-model="status">
        </a-checkbox-group>
      </a-form-item>
      <a-form-item
        label="统计时间"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-range-picker
          style="max-width: 468px; width: 100%;"
          showTime
          format="YYYY-MM-DD HH:mm:ss"
          v-model="time">
        </a-range-picker>
      </a-form-item>
      <a-form-item
        label="统计维度"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-radio-group v-model="info.mode">
          <a-radio value="D" >按日</a-radio>
          <a-radio value="H" >按时</a-radio>
        </a-radio-group>
        <a-button type="primary" @click="search" style="margin-left: 20px">查询</a-button>
      </a-form-item>
      <a-divider/>
      <a-row >
        <div class="row">
          <div class="app">
            <div id="log-line"  v-show="showChart" style="height:260px;margin-top: 20px;">
            </div>
            <a-empty v-show="!showChart"/>
          </div>
        </div>
      </a-row>
      <a-row >
        <div class="chart-title">
          <span>调用失败详情</span>
        </div>
        <div class="row">
          <a-table
            ref="table"
            size="default"
            :columns="columns"
            rowKey="log_id"
            :dataSource="errorLogList"
            :pagination="pagination"
            :alert="{ show: true, clear: true }"
          >
            <span slot="serial" slot-scope="text, record, index">
              {{ index + 1 }}
            </span>
            <span slot="time" slot-scope="text">
              {{ text | moment }}
            </span>
            <span slot="description" slot-scope="text">
              <a-popover title="" :content="text" v-if="text?text.length > 20:text">
                {{ text.substr(0, 20) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>

          </a-table>
        </div>
      </a-row>
      <a-row :gutter="16" >
        <div class="row">
        <a-col  :span="8" push="8">
          <a-button  @click="back" style="margin-top:10px;margin-bottom: 10px">返回</a-button>
        </a-col>
        </div>
      </a-row>

    </a-form>
  </a-card>
</template>

<script>
  import { applicationList, applicationInfo, applicationFailInfo, interList } from '@/api/service'
  import echart from 'echarts'
  import dayjs from 'dayjs'
  import moment from 'moment'
  import 'moment/locale/zh-cn'
  import _ from 'lodash'

  let myChart
  export default {
    name: 'AppLog',
    data () {
      return {
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 10 }, sm: { span: 10 } },
        form: this.$form.createForm(this),
        statusList: [{ label: '调用成功', value: '2' }, { label: '调用失败', value: '1' }],
        serversList: [],
        appList: [],
        errorLogList: [],
        logList: [],
        info: {
          applicationName: '',
          serviceName: '',
          statistics: 'QPS',
          status: '',
          time: null,
          mode: 'D',
          pageNo: 1,
          pageSize: 10,
          total: 0
        },
        status: ['2', '1'],
        time: [],
        // 表头
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '日志ID',
            dataIndex: 'log_id'
          },
          {
            title: '服务名称',
            dataIndex: 'service_name'
          },
          {
            title: '所属应用',
            dataIndex: 'application_name'
          },
          {
            title: '调用时间',
            dataIndex: 'begin_time',
            scopedSlots: { customRender: 'time' }

          },
          {
            title: '日志',
            dataIndex: 'log_msg',
            scopedSlots: { customRender: 'description' }

          }
        ],
        pagination: {
          total: 20,
          pageSize: 10,
          showTotal: (total) => `共${total}条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          onChange: (page, pageSize) => {
            this.info.pageNo = page
            this.info.pageSize = pageSize
            this.getApplicationFailInfo()
          },
          onShowSizeChange: (current, size) => {
            this.info.pageNo = current
            this.info.pageSize = size
            this.getApplicationFailInfo()
          }
        },
        searchParams: {
          pageNo: 1,
          pageSize: 200,
          searchVal: ''
        },
        showChart: true
      }
    },
    props: [
    ],
    methods: {
      getServerListByApp (value) {
        this.info.serviceName = ''
        this.getServersList(value)
      },
      // 获取所有接口列表
      getServersList (id) {
        const self = this
        interList({ key: id }).then(res => {
          self.serversList = res.data
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },

      // 获取所有应用列表
      getAppsList () {
        const self = this
        applicationList(this.searchParams).then(res => {
          self.appList = res.data.lists
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 调用次数
      getApplicationInfo () {
        const self = this
        applicationInfo(this.info).then(res => {
          myChart.clear()
          self.logList = res.data
          const data = []
          const date = []
          if (self.logList.succ) {
            data.push('调用成功')
            for (let i = 0; i < self.logList.succ.length; i++) {
              date.push(self.logList.succ[i].name)
            }
          }
          if (self.logList.fail) {
            data.push('调用失败')
            if (date.length < 1) {
              for (let i = 0; i < self.logList.fail.length; i++) {
                date.push(self.logList.fail[i].name)
              }
            }
          }
          if (!self.logList.succ && !self.logList.fail) {
            self.showChart = false
          } else {
            self.showChart = true
            const log = {
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: data,
                top: 20
              },
              grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
              },
              xAxis: [
                {
                  type: 'category',
                  data: date
                }
              ],
              yAxis: [
                {
                  type: 'value'
                }
              ],
              series: [

                {
                  name: '调用失败',
                  type: 'line',
                  data: this.logList.fail
                },
                {
                  name: '调用成功',
                  type: 'line',
                  data: this.logList.succ
                }
              ]
            }
            myChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 错误日志列表
      getApplicationFailInfo () {
        const self = this
        applicationFailInfo(this.info).then(res => {
          self.errorLogList = res.data.lists
          self.pagination.total = res.data.totalCount
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },

      // 查询
      search () {
        if (!this.info.applicationName && !this.info.serviceName) {
          this.$message.warning('应用和接口必须选择一个查询')
          return false
        }
        if (!this.info.mode) {
          this.$message.warning('必须选择统计纬度')
          return false
        }
        if (this.status.length < 1) {
          this.$message.warning('必须选择监控项')
          return false
        }
        const time = _.cloneDeep(this.time)
        time[0] = time[0].format('YYYY-MM-DD HH:mm:ss')
        time[1] = time[1].format('YYYY-MM-DD HH:mm:ss')
        this.info.time = time.toString()
        this.info.status = this.status.toString()
        this.getApplicationInfo()
        this.getApplicationFailInfo()
      },

      back () {
        this.$router.push({ path: `/resource/application` })
      }
    },
    created () {
      this.getAppsList()
      const beginDate = dayjs().add(-7, 'day').format('YYYY-MM-DD HH:mm:ss')
      const endDate = dayjs().format('YYYY-MM-DD HH:mm:ss')
      this.time[0] = moment(beginDate, 'YYYY-MM-DD HH:mm:ss')
      this.time[1] = moment(endDate, 'YYYY-MM-DD HH:mm:ss')
      // this.time[0] = dayjs().add(-7, 'day').format('YYYY-MM-DD HH:mm:ss')
      // this.time[1] = dayjs().format('YYYY-MM-DD HH:mm:ss')
    },
    mounted () {
      myChart = echart.init(document.getElementById('log-line'))
      if (this.$route.params.serviceName) {
        this.info.serviceName = this.$route.params.serviceName
      }
      if (this.$route.params.applicationName) {
        this.info.applicationName = this.$route.params.applicationName.apikey
        this.getServersList(this.info.applicationName)
      } else {
        this.getServersList('')
      }
      this.search()
    },
    watch: {
    }
  }
</script>
<style lang="less" scoped>
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
</style>
