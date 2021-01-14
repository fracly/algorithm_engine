<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :closable="false"
  >
    <template slot="footer">
      <a-button type="default" @click="handleOk">关闭</a-button>
    </template>
    <a-spin :spinning="loading">
      <a-table
        ref="table"
        :scroll="{ y: 350, x: '100%' }"
        size="default"
        :columns="columns"
        rowKey="name"
        :dataSource="columnList"
        :pagination="false"
        style="overflow-x:auto;">
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
      </a-table>
    </a-spin>
  </a-modal>
</template>

<script>
    import { previewTable } from '@/api/model'
    export default {
        name: 'TableView',
        components: {},
        data () {
            return {
                title: '数据预览',
                visible: false,
                loading: false,
                // 查询条件
                queryParam: {
                },
                // 表头
                columns: [
                    {
                        title: '#',
                        scopedSlots: { customRender: 'serial' }
                    }
                ],
                columnList: [],
                tableName: '',
                column: []
            }
        },
        props: {
        },
        created () {
        },
        methods: {
            show: function (params) {
                const _this = this
                _this.visible = true
                _this.loading = true
                this.columnList = []
                this.columns = []
                previewTable(params).then(res => {
                    if (res.data !== null && res.data.length > 0) {
                        Object.keys(res.data[0]).forEach((item, index) => {
                            _this.columns.push({ 'title': item, 'dataIndex': item, 'key': index, 'width': '120px', 'ellipsis': true })
                        })
                        _this.columnList = res.data
                    }
                    _this.loading = false
                })
            },
            handleOk () {
                this.visible = false
            }
        }
    }
</script>
