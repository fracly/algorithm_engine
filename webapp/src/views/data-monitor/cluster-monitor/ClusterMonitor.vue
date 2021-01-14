<template>
  <a-card>
    <a-row>
      <div class="chart-title">
        <span>图表</span>
        <div style="float: right">
          <ul class="nav-pills">
            <li
              v-for="(item) in timeList"
              :key="item.value"
              @click="changeTime(item.value)"
              :class="time==item.value?'active':''">
              <a>{{ item.name }}</a></li>
          </ul>
        </div>
      </div>
    </a-row>
    <a-row>
      <div class="row">
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="diskIo-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="netIo-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="hdfsIo-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
      </div>
    </a-row>
    <a-row>
      <div class="row">
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="cpu-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="memory-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
        <a-col :xl="8" :lg="8" :md="8" :sm="24" :xs="24">
          <div>
            <div id="vc-line" style="height:260px;margin-top: 20px;">
            </div>
          </div>
        </a-col>
      </div>
    </a-row>
    <a-divider/>
    <a-row>
      <div class="chart-title">
        <span>所有主机</span>
      </div>
      <div class="row">
        <a-table
          ref="table"
          size="middle"
          :columns="columns"
          :dataSource="hostList"
          rowKey="hostname"
          :pagination="false"
          :alert="{ show: true, clear: true }"
        >
          <span slot="serial" slot-scope="text, record, index">
            {{ index + 1 }}
          </span>
          <span slot="status" slot-scope="text">
            <a-icon type="check-circle" theme="twoTone" two-tone-color="#52c41a" v-if="text==='GOOD'"/>
            <a-icon type="exclamation-circle" theme="twoTone" two-tone-color="#FF8300" v-if="text!=='GOOD'"/>

          </span>
          <span slot="memory" slot-scope="text,record">
            {{ record.usedPhysMemGB+'GB' }}/{{ record.totalPhysMem+'GB' }}
            <a-progress
              :percent="(record.usedPhysMemGB/record.totalPhysMem)*100"
              :show-info="false"
              :status="(record.usedPhysMemGB/record.totalPhysMem)*100>50?'exception':'active'"/>
          </span>
        </a-table>
      </div>
    </a-row>
  </a-card>

</template>
<script>
  import echart from 'echarts'
  import { getInfo, getCpu, getMem, getVc, getHostList } from '@/api/cloudera'

  let diskIoChart
  let netIoChart
  let hdfsIoChart
  let cpuChart
  let vcChart
  let memoryChart
  export default {
    name: 'ClusterMonitor',
    data () {
      return {
        diskIOList: [],
        netIOList: [],
        hdfsIOList: [],
        cpuList: [],
        memoryList: [],
        vcList: [],
        hostList: [],
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '状态',
            dataIndex: 'healthSummary',
            scopedSlots: { customRender: 'status' }
          },
          {
            title: '名称',
            dataIndex: 'hostname'
          },
          {
            title: 'IP',
            dataIndex: 'ipAddress'
          },
          {
            title: '内核',
            dataIndex: 'numCores'
          },
          {
            title: '物理内存',
            dataIndex: 'totalPhysMem',
            scopedSlots: { customRender: 'memory' }
          }
        ],
        timeList: [{ name: '30分钟', value: '30m' }, { name: '6小时', value: '6h' }, { name: '1天', value: '1d' },
          { name: '7天', value: '7d' }, { name: '1个月', value: '1mo' }, { name: '3个月', value: '3mo' }],
        time: '30m'
      }
    },
    methods: {
      // 获取Host列表
      getHostsList () {
        getHostList().then(res => {
          this.hostList = res.data
        })
      },
      // 磁盘IO信息
      getDiskIoInfo () {
        const self = this
        getInfo({ type: this.time, param: 'cluster_disk_io' }).then(res => {
          if (res.code === 0) {
            diskIoChart.clear()
            self.diskIOList = res.data
            const date = []
            for (let j = 0; j < self.diskIOList.i.length; j++) {
              date.push(self.diskIOList.i[j].name)
              self.diskIOList.i[j].value2 = self.diskIOList.i[j].value
              self.diskIOList.i[j].value = (self.diskIOList.i[j].value / 1024 / 1024).toFixed(1)
              self.diskIOList.o[j].value2 = self.diskIOList.o[j].value
              self.diskIOList.o[j].value = (self.diskIOList.o[j].value / 1024 / 1024).toFixed(1)
            }
            const time = this.time
            const log = {
              title: {
                text: '群集磁盘 IO'
              },
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              color: ['#AFCB7B', '#9ED3EC'],
              legend: {
                data: ['总磁盘字节数写入', '总磁盘字节数读取'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: '总磁盘字节数写入',
                  type: 'line',
                  areaStyle: {},
                  data: self.diskIOList.i
                },
                {
                  name: '总磁盘字节数读取',
                  type: 'line',
                  areaStyle: {},
                  data: self.diskIOList.o
                }
              ]
            }
            diskIoChart.setOption(log)
          } else {
            diskIoChart.clear()
            const date = []
            const log = {
              title: {
                text: '群集磁盘 IO'
              },
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              color: ['#AFCB7B', '#9ED3EC'],
              legend: {
                data: ['总磁盘字节数写入', '总磁盘字节数读取'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: '总磁盘字节数写入',
                  type: 'line',
                  areaStyle: {},
                  data: []
                },
                {
                  name: '总磁盘字节数读取',
                  type: 'line',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            diskIoChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 网络IO信息
      getNetIoInfo () {
        const self = this
        getInfo({ type: this.time, param: 'cluster_net_io' }).then(res => {
          if (res.code === 0) {
            netIoChart.clear()
            self.netIOList = res.data
            const date = []
            for (let j = 0; j < self.netIOList.i.length; j++) {
              date.push(self.netIOList.i[j].name)
              self.netIOList.i[j].value2 = self.netIOList.i[j].value
              self.netIOList.i[j].value = (self.netIOList.i[j].value / 1024 / 1024).toFixed(1)
              self.netIOList.o[j].value2 = self.netIOList.o[j].value
              self.netIOList.o[j].value = (self.netIOList.o[j].value / 1024 / 1024).toFixed(1)
            }
            const time = this.time
            const log = {
              title: {
                text: '群集网络 IO'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              legend: {
                data: ['网络接口中的总传送的字节数', '网络接口中的总接收的字节数'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    // rotate: 45,
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: '网络接口中的总传送的字节数',
                  type: 'line',
                  areaStyle: {},
                  data: self.netIOList.i
                },
                {
                  name: '网络接口中的总接收的字节数',
                  type: 'line',
                  areaStyle: {},
                  data: self.netIOList.o
                }
              ]
            }
            netIoChart.setOption(log)
          } else {
            netIoChart.clear()
            self.netIOList = []
            const date = []
            const log = {
              title: {
                text: '群集网络 IO'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              legend: {
                data: ['网络接口中的总传送的字节数', '网络接口中的总接收的字节数'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: '网络接口中的总传送的字节数',
                  type: 'line',
                  areaStyle: {},
                  data: []
                },
                {
                  name: '网络接口中的总接收的字节数',
                  type: 'line',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            netIoChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // hdfsIo信息
      getHdfsIoInfo () {
        const self = this
        getInfo({ type: this.time, param: 'hdfs_io' }).then(res => {
          if (res.code === 0) {
            hdfsIoChart.clear()
            self.hdfsIOList = res.data
            const date = []
            const time = this.time
            for (let j = 0; j < self.hdfsIOList.i.length; j++) {
              date.push(self.hdfsIOList.i[j].name)
              self.hdfsIOList.i[j].value2 = self.hdfsIOList.i[j].value
              self.hdfsIOList.i[j].value = (self.hdfsIOList.i[j].value / 1024 / 1024).toFixed(1)
              self.hdfsIOList.o[j].value2 = self.hdfsIOList.o[j].value
              self.hdfsIOList.o[j].value = (self.hdfsIOList.o[j].value / 1024 / 1024).toFixed(1)
            }
            const log = {
              title: {
                text: 'HDFS IO'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              legend: {
                data: ['DataNodes中的总写入的字节', 'DataNodes中的总读取的字节'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: 'DataNodes中的总写入的字节',
                  type: 'line',
                  areaStyle: {},
                  data: self.hdfsIOList.i
                },
                {
                  name: 'DataNodes中的总读取的字节',
                  type: 'line',
                  areaStyle: {},
                  data: self.hdfsIOList.o
                }
              ]
            }
            hdfsIoChart.setOption(log)
          } else {
            hdfsIoChart.clear()
            self.hdfsIOList = res.data
            const date = []
            const log = {
              title: {
                text: 'HDFS IO'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                  let text = '时间：' + params[0].name + '<br />'
                  for (let i = 0; i < params.length; i++) {
                    if ((params[i].data.value) > 1) {
                      text = text + params[i].seriesName + ':' + (params[i].value) + ' MB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    } else {
                      text = text + params[i].seriesName + ':' + (params[i].data.value2 / 1024).toFixed(1) + ' KB/second' + '(' + params[i].data.value2 + ' B/second)' + '<br />'
                    }
                  }
                  return text
                }
              },
              legend: {
                data: ['DataNodes中的总写入的字节', 'DataNodes中的总读取的字节'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} M/s'
                  }
                }
              ],
              series: [
                {
                  name: 'DataNodes中的总写入的字节',
                  type: 'line',
                  areaStyle: {},
                  data: []
                },
                {
                  name: 'DataNodes中的总读取的字节',
                  type: 'line',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            hdfsIoChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // cpu信息
      getCpuInfo () {
        const self = this
        getCpu({ type: this.time }).then(res => {
          if (res.code === 0) {
            cpuChart.clear()
            self.cpuList = res.data
            const date = []
            const time = this.time
            for (let j = 0; j < self.cpuList.max.length; j++) {
              date.push(self.cpuList.max[j].name)
            }
            const log = {
              title: {
                text: 'CPU 使用率'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['主机CPU使用率-最大', '主机CPU使用率-最小'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} %'
                  }
                }
              ],
              series: [
                {
                  name: '主机CPU使用率-最大',
                  type: 'line',
                  areaStyle: {},
                  data: self.cpuList.max
                },
                {
                  name: '主机CPU使用率-最小',
                  type: 'line',
                  areaStyle: {},
                  data: self.cpuList.min
                }
              ]
            }
            cpuChart.setOption(log)
          } else {
            cpuChart.clear()
            self.cpuList = res.data
            const date = []
            const log = {
              title: {
                text: 'CPU 使用率'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['主机CPU使用率-最大', '主机CPU使用率-最小'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} %'
                  }
                }
              ],
              series: [
                {
                  name: '主机CPU使用率-最大',
                  type: 'line',
                  areaStyle: {},
                  data: []
                },
                {
                  name: '主机CPU使用率-最小',
                  type: 'line',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            cpuChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 内存信息
      getMemoryInfo () {
        const self = this
        getMem({ type: this.time }).then(res => {
          if (res.code === 0) {
            memoryChart.clear()
            self.memoryList = res.data
            const date = []
            const time = this.time
            for (let j = 0; j < self.memoryList.KY.length; j++) {
              date.push(self.memoryList.KY[j].name)
            }
            const log = {
              title: {
                text: '内存（已分配且可用）'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['可用内存', '已分配的内存（累计)'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} G'
                  }
                }
              ],
              series: [
                {
                  name: '可用内存',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: self.memoryList.KY
                },
                {
                  name: '已分配的内存（累计)',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: self.memoryList.YY
                }
              ]
            }
            memoryChart.setOption(log)
          } else {
            memoryChart.clear()
            self.memoryList = res.data
            const date = []
            const log = {
              title: {
                text: '内存（已分配且可用）'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['可用内存', '已分配的内存（累计)'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} G'
                  }
                }
              ],
              series: [
                {
                  name: '可用内存',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: []
                },
                {
                  name: '已分配的内存（累计)',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            memoryChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // vc信息
      getVcInfo () {
        const self = this
        getVc({ type: this.time }).then(res => {
          if (res.code === 0) {
            vcChart.clear()
            self.vcList = res.data
            const date = []
            const time = this.time
            for (let j = 0; j < self.vcList.KY.length; j++) {
              date.push(self.vcList.KY[j].name)
            }
            const log = {
              title: {
                text: 'VCores（已分配且可用）'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['可用 VCore', '已分配的 VCore（累计)'],
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
                  data: date,
                  boundaryGap: true,
                  axisLabel: {
                    formatter: function (value, index) {
                      if (time.indexOf('mo') !== -1) {
                        return value.split(' ')[0]
                      } else if (time.indexOf('d') !== -1) {
                        const a = value.split(' ')[0]
                        const b = value.split(' ')[1]
                        return a.split('-')[1] + '-' + a.split('-')[2] + ' ' + b.split(':')[0] + ':' + b.split(':')[1]
                      } else {
                        const a = value.split(' ')[1]
                        return a.split(':')[0] + ':' + a.split(':')[1]
                      }
                    }
                  }
                }
              ],
              yAxis: [
                {
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} vcores'
                  }
                }
              ],
              series: [
                {
                  name: '可用 VCore',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: self.vcList.KY
                },
                {
                  name: '已分配的 VCore（累计)',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: self.vcList.YY
                }
              ]
            }
            vcChart.setOption(log)
          } else {
            vcChart.clear()
            self.vcList = res.data
            const date = []
            const log = {
              title: {
                text: 'VCores（已分配且可用）'
              },
              color: ['#AFCB7B', '#9ED3EC'],
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: ['可用 VCore', '已分配的 VCore（累计)'],
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
                  type: 'value',
                  axisLabel: {
                    formatter: '{value} vcores'
                  }
                }
              ],
              series: [
                {
                  name: '可用 VCore',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: []
                },
                {
                  name: '已分配的 VCore（累计)',
                  type: 'line',
                  stack: '总量',
                  areaStyle: {},
                  data: []
                }
              ]
            }
            vcChart.setOption(log)
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 时间切换
      changeTime (val) {
        this.time = val
        this.getDiskIoInfo()
        this.getNetIoInfo()
        this.getHdfsIoInfo()
        this.getCpuInfo()
        this.getMemoryInfo()
        this.getVcInfo()
        this.getHostsList()
      }
    },
    created () {
      this.getDiskIoInfo()
      this.getNetIoInfo()
      this.getHdfsIoInfo()
      this.getCpuInfo()
      this.getMemoryInfo()
      this.getVcInfo()
      this.getHostsList()
    },
    mounted () {
      diskIoChart = echart.init(document.getElementById('diskIo-line'))
      netIoChart = echart.init(document.getElementById('netIo-line'))
      hdfsIoChart = echart.init(document.getElementById('hdfsIo-line'))
      cpuChart = echart.init(document.getElementById('cpu-line'))
      vcChart = echart.init(document.getElementById('vc-line'))
      memoryChart = echart.init(document.getElementById('memory-line'))
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

  .nav-pills > li {
    float: left;
    list-style: none;
  }

  .nav-pills > li + li {
    margin-left: 8px;
  }

  .nav-pills > .active > a {
    color: red;
  }
</style>
