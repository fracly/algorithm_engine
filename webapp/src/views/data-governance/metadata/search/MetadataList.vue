<template>
  <div>
    <a-card>
      <div style="margin-bottom: 10px;">
        <a-form layout="inline" >
          <a-row>
            <a-col>
              <div class="operate" style="margin-bottom: 20px">
                <a-input style="width: 30%" placeholder="请输入中文名查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
                <a-button type="primary" style="width: 100px;margin-left:20px;" icon="search" @click="handleSearch">查询</a-button>
                <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
                <a-button v-show="userInfo.administrator" style="margin-left:20px;" type="danger" icon="delete" @click="handleCleanAndInit">清档初始化</a-button>
                <a-button type="primary" style="width: 100px;float:right" icon="download" @click="handleDownload">导出</a-button>
              </div>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <a-spin :spinning="tableSpinning">
        <a-table
          :columns="columns"
          :dataSource="metadataList"
          :pagination="pagination"
          :loading="loading"
          size="middle"
          @change="handleTableChange"
          rowKey="name">
          <span slot="time" slot-scope="text">
            {{ text | moment }}
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
          <span slot="action" slot-scope="text, record">
              <template>
                <a href="javascript:;" @click="handleMetadataDetail(record)">详情</a>
              </template>
            </span>
        </a-table>
      </a-spin>
      <metadata-detail ref="modal"></metadata-detail>
      <clean-and-init ref="cleanModal"></clean-and-init>
    </a-card>
  </div>
</template>

<script>
    import MetadataDetail from './modules/MetadataDetail'
    import CleanAndInit from './modules/CleanAndInit'
    import { metadataList, metadataDownload } from '@/api/metadata'

    import { mapGetters } from 'vuex'

    export default {
        components: {
            MetadataDetail,
            CleanAndInit
        },
        data () {
            return {
                loading: false,
                filter: {},
                layerList: [],
                metadataList: [],
                queryParam: {
                    layer: 'all',
                    topic: '全部',
                    pageNo: 1,
                    pageSize: 10,
                    searchVal: ''
                },
                current: 1,
                tableSpinning: false,
                columns: [
                    {
                        title: '中文名',
                        dataIndex: 'tabledescribe',
                        width: '15%'
                    },
                    {
                        title: '表名称',
                        dataIndex: 'tablename',
                        width: '15%'
                    },
                    {
                        title: '表描述',
                        dataIndex: 'tabledetail',
                        scopedSlots: { customRender: 'tabledetail' },
                        width: '15%'
                    },
                    {
                        title: '数据层级',
                        dataIndex: 'cnlayer',
                        width: '6%',
                        filters: [
                            { 'text': '明细层', 'value': 'dwd' },
                            { 'text': '汇总层', 'value': 'dws' },
                            { 'text': '应用层', 'value': 'dwa' }
                        ]
                    },
                    {
                        title: '业务主题',
                        dataIndex: 'topic',
                        width: '15%',
                        align: 'center'
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'createTime',
                        scopedSlots: { customRender: 'time' },
                        width: '13%'
                    },
                    {
                        title: '创建者',
                        dataIndex: 'createUser',
                        width: '12%'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        width: '12%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                pagination: {
                    total: 0,
                    pageSize: 10,
                    current: this.current,
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
                this.getData()
            },
            handleSearch () {
                this.queryParam.pageNo = 1
                this.$set(this.pagination, 'current', 1)
                this.getData()
            },
            handleCleanAndInit () {
                this.$refs.cleanModal.show()
            },
            handleMetadataDetail (item) {
                this.$refs.modal.show(item)
            },
            handleDownload () {
                const params = {}
                if (this.queryParam.topic === '全部') {
                    params.topic = 'all'
                } else {
                    params.topic = this.queryParam.topic
                }
                params.layer = this.queryParam.layer
                params.searchVal = this.queryParam.searchVal
                metadataDownload(params)
            },
            handleTableChange (pagination, filters, sorter) {
                this.$set(this.pagination, 'pageSize', pagination.pageSize)
                const params = {}
                this.filters = filters
                params.searchVal = this.queryParam.searchVal
                params.pageNo = pagination.current
                params.pageSize = pagination.pageSize
                if (filters.cnlayer !== undefined && filters.cnlayer.length > 0) {
                    params.layer = filters.cnlayer.join(',')
                    params.topic = 'all'
                } else {
                    params.layer = 'all'
                    params.topic = 'all'
                }
                metadataList(params).then(res => {
                    if (res.code === 0) {
                        this.pagination.total = res.dataMap.total
                        this.metadataList = res.data
                    } else {
                        console.log('获取元信息失败')
                    }
                })
            },
            getData () {
                const params = {}
                if (this.filters !== undefined && this.filters.cnlayer !== undefined && this.filters.cnlayer.length > 0) {
                    params.layer = this.filters.cnlayer[0]
                    params.topic = 'all'
                } else {
                    params.layer = 'all'
                    params.topic = 'all'
                }
                params.pageNo = this.queryParam.pageNo
                params.pageSize = this.queryParam.pageSize
                params.searchVal = this.queryParam.searchVal.trim()
                metadataList(params).then(res => {
                    if (res.code === 0) {
                        this.pagination.total = res.dataMap.total
                        this.metadataList = res.data
                    } else {
                        console.log('获取元信息失败')
                    }
                })
            }
        },
        filters: {
        },
        mounted () {
            this.getData()
        },
        created () {
        },
        computed: {
            ...mapGetters(['userInfo']),
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
