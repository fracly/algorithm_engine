<template>
  <a-card :bordered="false" class="data">
    <a-row :gutter="8" :style="{ height: minHeight }">
      <a-col>
        <a-spin :spinning="tableSpinning">
          <div class="operate" style="margin-bottom: 20px">
            <a-input style="width: 30%" placeholder="请输入表名称查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
            <a-button type="primary" style="width: 120px;margin-left: 20px;" icon="search" @click="handleSearch">查询</a-button>
            <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
          </div>
          <a-table
            :columns="columns"
            :dataSource="dataList"
            :pagination="pagination"
            size="middle"
            rowKey="key"
            @change="handleTableChange">
            <span slot="time" slot-scope="text">
              <span v-if="text !== null">
                  {{ text | moment }}
              </span>
            </span>
            <span slot="description" slot-scope="text">
              <a-popover title="" :content="text" v-if="text.length > 10">
                {{ text.substr(0, 10) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>
            <span slot="tblName" slot-scope="text">
              <a-popover title="" :content="text" v-if="text.length > 20">
                {{ text.substr(0, 20) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>
            <span slot="warn" slot-scope="text">
              <span v-if="text === null || text.toString().length <= 2">
                无
              </span>
              <a-popover title="" :content="JSON.stringify(text)" v-else-if="JSON.stringify(text).length > 20">
                {{ JSON.stringify(text).substr(0, 20) + '...' }}
              </a-popover>
              <span v-else>
                {{ text }}
              </span>
            </span>
            <span slot="layer" slot-scope="text, record">
              <span>
                {{ record.layerCn + '-' + record.topic }}
              </span>
            </span>
            <span slot="action" slot-scope="text, record">
              <template v-if="userInfo.administrator">
                <a @click="handleEditRule(record)">规则配置</a>
              </template>
              <template v-else>
                <a @click="handleEditRule(record)">查看</a>
              </template>
            </span>
          </a-table>
        </a-spin>
      </a-col>
    </a-row>
    <quality-rule ref="modal5" @refresh="handleSearch"></quality-rule>
  </a-card>
</template>

<script>
    import { STable } from '@/components'

    import QualityRule from './modules/QualityRule'
    import { queryModel } from '@/api/model'
    import { mapGetters } from 'vuex'
    export default {
        name: 'DataList',
        components: {
            STable,
            QualityRule
        },
        data () {
            return {
                // table 相关变量
                tableSpinning: false,
                columns: [
                    {
                        title: '表中文名',
                        dataIndex: 'tblDescribe',
                        scopedSlots: { customRender: 'description' }
                    },
                    {
                        title: '表英文名',
                        dataIndex: 'tblName',
                        scopedSlots: { customRender: 'tblName' }
                    },
                    {
                        title: '表描述',
                        dataIndex: 'area',
                        scopedSlots: { customRender: 'description' }
                    },
                    {
                        title: '数据质量',
                        dataIndex: 'tblQuality'
                    },
                    {
                        title: '表告警',
                        dataIndex: 'tblWarn',
                        scopedSlots: { customRender: 'warn' }
                    },
                    {
                        title: '存储类型',
                        dataIndex: 'type'
                    },
                    {
                        title: '数据层级',
                        dataIndex: 'layerCn',
                        scopedSlots: { customRender: 'layerCn' },
                        width: '7%',
                        filters: [
                            { text: '明细层', value: 'dwd' },
                            { text: '汇总层', value: 'dws' },
                            { text: '应用层', value: 'dwa' }
                        ]
                    },
                    {
                        title: '业务主题',
                        dataIndex: 'topic',
                        scopedSlots: { customRender: 'topic' }
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'create_time',
                        scopedSlots: { customRender: 'time' }
                    },
                    {
                        title: '创建者',
                        dataIndex: 'userName'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        width: '8%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                dataList: [],
                // 查询参数
                queryParam: {
                    base: '',
                    topic: '',
                    pageNo: 1,
                    pageSize: 10,
                    searchVal: '',
                    datasourceId: 0
                },
                layerCN: '明细层',
                pagination: {
                    total: 0,
                    pageSize: 10,
                    showTotal: (total) => `共${total}条`,
                    showSizeChanger: true,
                    pageSizeOptions: ['10', '20', '50', '100'],
                    onChange: (page, pageSize) => {
                        this.queryParam.pageNo = page
                        this.queryParam.pageSize = pageSize
                        this.$set(this.pagination, 'current', page)
                        this.$set(this.pagination, 'pageSize', pageSize)
                        this.getData()
                    },
                    onShowSizeChange: (current, size) => {
                        this.queryParam.pageNo = current
                        this.queryParam.pageSize = size
                        this.$set(this.pagination, 'current', current)
                        this.$set(this.pagination, 'pageSize', size)
                        this.getData()
                    }
                },
                minHeight: '0'

            }
        },
        created () {
            this.getData()
            this.minHeight = window.screen.height * 0.75 + 'px'
        },
        computed: {
            ...mapGetters(['userInfo'])
        },
        methods: {
            handleTableChange (pagination, filters, sorter) {
                this.$set(this.pagination, 'pageSize', pagination.pageSize)
                this.queryParam.pageSize = pagination.pageSize
                this.queryParam.pageNo = pagination.current
                if (filters.layerCn !== undefined && filters.layerCn.length > 0) {
                    this.queryParam.base = filters.layerCn.join(',')
                    this.queryParam.topic = 'all'
                } else {
                    this.queryParam.base = 'all'
                    this.queryParam.topic = 'all'
                }
                this.getData()
            },
            resetSearchForm () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.queryParam.searchVal = ''
                this.getData()
            },
            handleSearch () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.getData()
            },
            handleEditRule (item) {
                const info = {}
                info.base = item.layer
                this.$refs.modal5.show(item, info)
            },
            getData () {
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                queryModel(this.queryParam).then((res) => {
                    if (res.code === 0) {
                        this.dataList = res.data.totalList
                        this.pagination.total = res.data.total
                    } else {
                        this.$message.error('获取模型列表失败')
                    }
                }).catch(err => {
                    this.$message.error('获取数据源的数据失败' + err)
                })
            }
        }
    }
</script>

<style lang="less">
  .ant-input {
    border-radius: 0px;
  }
  .custom-tree {

    /deep/ .ant-menu-item-group-title {
      position: relative;

      &:hover {
        .btn {
          display: block;
        }
      }
    }

    /deep/ .ant-menu-item {
      &:hover {
        .btn {
          display: block;
        }
      }
      font-size: 12px;
    }

    /deep/ .btn {
      display: none;
      position: absolute;
      top: 0;
      right: 10px;
      width: 20px;
      height: 40px;
      line-height: 40px;
      z-index: 1050;

      &:hover {
        transform: scale(1.2);
        transition: 0.5s all;
      }
    }
  }
</style>
