<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="数据资产总数（条）" :total="hiveRecordTotal">
          <a-tooltip title="系统中所有数据资产的总记录数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">Hive表数</span>
              {{ hiveTableTotal }}
            </trend>
          </div>
          <template slot="footer">系统数据资产相关指标统计</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="模型任务总数（个）" :total="processDefinitionCount">
          <a-tooltip title="系统所有用户的模型总数量" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
<!--            <trend flag="up" style="margin-right: 16px;font-size:30px;">-->
<!--              <span slot="term">日均运行</span>-->
<!--              {{ this.processInstance30AvgCount }}-->
<!--            </trend>-->
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">今日成功</span>
              {{ processInstanceTodaySuccessCount }} 个
            </trend>
            <trend flag="up">
              <span slot="term">今日失败</span>
              {{ processInstanceTodayFailedCount }} 个
            </trend>
          </div>
          <template slot="footer">查看失败明细, 请单击 <a @click="redirectToLog">详情</a></template>

        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="报表总数（个）" :total="reportCount">
          <a-tooltip title="总调用为历史以来报表调用的总次数" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">总调用</span>{{ reportInvoke30AvgCount }}
            </trend>
          </div>
          <template slot="footer">报表调用的情况统计</template>
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '24px' }">
        <chart-card :loading="loading" title="接口服务总数（个）" :total="serviceCount">
          <a-tooltip title="日均调用为近30日的调用均值" slot="action">
            <a-icon type="info-circle-o" />
          </a-tooltip>
          <div>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">日均调用</span>
              {{ serviceInvoke30AvgCount}}
            </trend>
            <trend flag="up" style="margin-right: 16px;">
              <span slot="term">总调用</span>
              {{ serviceInvokeCount }}
            </trend>
          </div>
          <template slot="footer">接口服务相关指标的统计</template>
        </chart-card>
      </a-col>
    </a-row>
    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
          <div class="extra-wrapper" slot="tabBarExtraContent">
          </div>
          <a-tab-pane loading="true" tab="日接口服务调用" key="1">
            <a-row>
              <a-col :xl="16" :lg="12" :md="12" :sm="24" :xs="24">
                <bar style="height: 200px" :data="serviceInvokeTimesData" :scale="invokeTimeScale" title="调用数（次）" />
              </a-col>
              <a-col :xl="8" :lg="12" :md="12" :sm="24" :xs="24">
                <rank-list title="数据资产top10" :list="rankList"/>
<!--                <h4 class="title" style="margin-bottom: -10px;">表数据Top10</h4>-->
<!--                <div id="label-chart" style="height:300px;"></div>-->
              </a-col>
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-card>
    <div v-show="Glod.project_name === 'yunnan'"class="antd-pro-pages-dashboard-analysis-twoColLayout" :class="isDesktop() ? 'desktop' : ''">
      <a-row :gutter="24" type="flex" :style="{ marginTop: '24px' }">
        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
          <a-card :loading="loading" :bordered="false" title="大数据应用平台支队排名" :style="{ height: '100%' }">
            <a-table
              row-key="index"
              size="small"
              :columns="searchTableColumns"
              :dataSource="departmentList"
              :pagination="{ pageSize: 10 }"
              :bordered="true"
            >
              <span slot="serial" slot-scope="text, record, index">
                {{index + 1}}
              </span>
            </a-table>
          </a-card>
        </a-col>
        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">
          <a-card :loading="loading" :bordered="false" title="大数据应用平台部门排名" :style="{ height: '100%' }">
            <a-table
              row-key="index"
              size="small"
              :columns="departmentTableColumns"
              :dataSource="detachmentList"
              :pagination="{ pageSize: 10 }"
              :bordered="true"
            >
              <span slot="serial" slot-scope="text, record, index">
                {{index + 1}}
              </span>
            </a-table>
          </a-card>
        </a-col>
<!--        <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">-->
<!--          <a-card class="antd-pro-pages-dashboard-analysis-salesCard" :loading="loading" :bordered="false" title="任务运行情况统计" :style="{ height: '100%' }">-->
<!--            <div slot="extra" style="height: inherit;">-->
<!--              <div class="analysis-salesTypeRadio">-->
<!--                <a-radio-group defaultValue="0" v-model="typePercentageDateType" @change="handleTypePercentageDateTypeChange">-->
<!--                  <a-radio-button value="0">全部</a-radio-button>-->
<!--                  <a-radio-button value="1">今日</a-radio-button>-->
<!--                  <a-radio-button value="7">近一周</a-radio-button>-->
<!--                  <a-radio-button value="30">近一月</a-radio-button>-->
<!--                  <a-radio-button value="180">近半年</a-radio-button>-->
<!--                </a-radio-group>-->
<!--              </div>-->

<!--            </div>-->
<!--            <h4>任务类型分布</h4>-->
<!--            <div>-->
<!--              &lt;!&ndash; style="width: calc(100% - 240px);" &ndash;&gt;-->
<!--              <div>-->
<!--                <v-chart :force-fit="true" :height="405" :data="pieData" :scale="pieScale">-->
<!--                  <v-tooltip :showTitle="false" dataKey="item*percent" />-->
<!--                  <v-axis />-->
<!--                  &lt;!&ndash; position="right" :offsetX="-140" &ndash;&gt;-->
<!--                  <v-legend dataKey="item"/>-->
<!--                  <v-pie position="percent" color="item" :vStyle="pieStyle" />-->
<!--                  <v-coord type="theta" :radius="0.75" :innerRadius="0.6" />-->
<!--                </v-chart>-->
<!--              </div>-->
<!--            </div>-->
<!--          </a-card>-->
<!--        </a-col>-->
      </a-row>
    </div>
  </div>
</template>

<script>
    import moment from 'moment'
    import { ChartCard, MiniArea, MiniBar, MiniProgress, RankList, Bar, Trend, NumberInfo, MiniSmoothArea } from '@/components'
    import { mixinDevice } from '@/utils/mixin'
    import { getProcessDefinitionCount, getProcessInstanceSuccessCount, getProcessInstanceFailedCount,
        getReportCount, getReportInvoke30AvgCount,
        getServiceCount, getServiceInvokeCount, getServiceInvoke30AvgCount,
        getServiceInvokeTimesByDay, getHiveRecordRankTop10, getHiveRecordTotal, getHiveTableTotal, getDetachmentUsage, getDepartmentUsage } from '@/api/analysis'

    const searchUserData = []
    for (let i = 0; i < 7; i++) {
        searchUserData.push({
            x: moment().add(i, 'days').format('YYYY-MM-DD'),
            y: Math.ceil(Math.random() * 10)
        })
    }
    const searchUserScale = [
        {
            dataKey: 'x',
            alias: '时间'
        },
        {
            dataKey: 'y',
            alias: '用户数'
        }]

    const invokeTimeScale = [
        {
            dataKey: 'x',
            alias: '时间'
        },
        {
            dataKey: 'y',
            alias: '调用次数'
        }]

    const searchCountScale = [
        {
            dataKey: 'x',
            alias: '时间'
        },
        {
            dataKey: 'y',
            alias: '搜索次数'
        }
    ]

    const downloadScale = [
        {
            dataKey: 'x',
            alias: '时间'
        },
        {
            dataKey: 'y',
            alias: '下载数'
        }]

    const searchTableColumns = [
        {
            title: '排名',
            scopedSlots: { customRender: 'serial' },
            width: 90
        },
        {
            dataIndex: 'sjmc',
            title: '支队名称'
        },
        {
            dataIndex: 'pv',
            title: '使用次数(本月累计)'
        }
    ]

    const departmentTableColumns = [
        {
            title: '排名',
            scopedSlots: { customRender: 'serial' },
            width: 90
        },
        {
            dataIndex: 'sjmc',
            title: '部门名称'
        },
        {
            dataIndex: 'pv',
            title: '使用次数(本月累计)'
        }
    ]
    export default {
        name: 'Analysis',
        mixins: [mixinDevice],
        components: {
            ChartCard,
            MiniArea,
            MiniBar,
            MiniProgress,
            RankList,
            Bar,
            Trend,
            NumberInfo,
            MiniSmoothArea
        },
        data () {
            return {
                Glod,
                loading: true,
                rankList: [],
                detachmentList: [],
                departmentList: [],

                // 模型任务相关统计
                processDefinitionCount: 0,
                processInstance30AvgCount: 0,
                processInstanceTodayCount: 0,
                processInstanceTodaySuccessCount: 0,
                processInstanceTodayFailedCount: 0,

                // 报表相关统计
                reportCount: 0,
                reportInvoke30AvgCount: 0,

                // 服务相关统计
                serviceCount: 0,
                serviceInvokeCount: 0,
                serviceInvoke30AvgCount: 0,

                // 服务接口调用统计
                serviceInvokeDateType: '60',
                endTime: moment().format('YYYY-MM-DD HH:mm:ss'),
                startTime: moment().subtract(10, 'days').format('YYYY-MM-DD HH:mm:ss'),
                serviceInvokeTimesData: [],

                // 数据资产统计
                hiveRecordTotal: 0,
                hiveTableTotal: 0,

                // 下载量统计
                downloadScale,
                downloadCount: 0,
                avgDownloadCount: 0,
                downloadByDay: [],

                // 数据类型初始值
                typePercentageDateType: '7',
                searchCountTotal: 0,
                searchUserTotal: 0,
                searchUserData: [],
                searchUserScale,
                invokeTimeScale,
                searchCountData: [],
                searchCountScale,
                searchTableColumns,
                departmentTableColumns,
                searchData: [],
                increaseDateRange: [],

                //
                sourceData: [],
                pieStyle: {
                    stroke: '#fff',
                    lineWidth: 1
                },
                url: ''
            }
        },
        methods: {
            redirectToLog () {
                this.$router.push('/model/factory/log')
            },
            handleTypePercentageDateTypeChange () {},
            processStatistic () {
                getProcessDefinitionCount().then(res => {
                    this.processDefinitionCount = res.data
                })
                getProcessInstanceSuccessCount().then(res => {
                    this.processInstanceTodaySuccessCount = res.data
                })
                getProcessInstanceFailedCount().then(res => {
                    this.processInstanceTodayFailedCount = res.data
                })
            },
            reportStatistic () {
                getReportCount().then(res => {
                    this.reportCount = res.data
                })
                getReportInvoke30AvgCount().then(res => {
                    this.reportInvoke30AvgCount = res.data
                })
            },
            serviceStatistic () {
                getServiceCount().then(res => {
                    this.serviceCount = res.data
                })
                getServiceInvokeCount().then(res => {
                    this.serviceInvokeCount = res.data
                })
                getServiceInvoke30AvgCount().then(res => {
                    this.serviceInvoke30AvgCount = res.data
                })
                getHiveRecordTotal().then(res => {
                    this.hiveRecordTotal = res.data
                })
                getHiveTableTotal().then(res => {
                    this.hiveTableTotal = res.data
                })
            },
            getServiceInvokeTimesByDay () {
                getServiceInvokeTimesByDay({ 'startTime': this.startTime, 'endTime': this.endTime }).then(res => {
                    for (let i = 0; i < res.data.length; i++) {
                        this.serviceInvokeTimesData.push({ x: res.data[i].x + ' ', y: res.data[i].y })
                    }
                })
            },
            getHiveRecordRankTop10 () {
                getHiveRecordRankTop10().then(res => {
                    this.rankList = res.data
                })
            },
            getUsageTop () {
                getDetachmentUsage().then(res => {
                    this.detachmentList = res
                })
                getDepartmentUsage().then(res => {
                    this.departmentList = res
                })
            }
        },
        created () {
        },
        mounted () {
            setTimeout(() => {
                this.loading = !this.loading
            }, 1000)
        },
        watch: {
            loading (val) {
                if (!val) {
                    setTimeout(() => {
                        this.processStatistic()
                        this.serviceStatistic()
                        this.reportStatistic()
                        this.getServiceInvokeTimesByDay()
                        this.getHiveRecordRankTop10()
                        if (Glod.project_name === 'yunnan') {
                            this.getUsageTop()
                        }
                    }, 1000)
                }
            }
        }
    }
</script>

<style lang="less" >
  /deep/.ant-layout-content {
    background-color: #f0f2f5 !important;
  }
  .extra-wrapper {
    line-height: 55px;
    padding-right: 24px;

    .extra-item {
      display: inline-block;
      margin-right: 24px;

      a {
        margin-left: 24px;
      }
    }
  }

  .antd-pro-pages-dashboard-analysis-twoColLayout {
    position: relative;
    display: flex;
    display: block;
    flex-flow: row wrap;
  }

  .antd-pro-pages-dashboard-analysis-salesCard {
    height: calc(100% - 24px);
    /deep/ .ant-card-head {
      position: relative;
    }
  }

  .dashboard-analysis-iconGroup {
    i {
      margin-left: 16px;
      color: rgba(0,0,0,.45);
      cursor: pointer;
      transition: color .32s;
      color: black;
    }
  }
  .analysis-salesTypeRadio {
    position: absolute;
    right: 54px;
    bottom: 12px;
  }
</style>
