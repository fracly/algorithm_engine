<template>
  <a-card :bordered="false" class="label">
    <a-row :gutter="8" :style="{ height: minHeight }">
      <a-col :span="4">
        <s-tree
          :dataSource="dataTree"
          :openKeys.sync="openKeys"
          :search="true"
          :defaultSelectedKeys="defaultSelectedKeys"
          @click="handleClick"
        > </s-tree>
      </a-col>
      <a-col :span="20">
        <a-spin :spinning="tableSpinning">
          <div class="operate" style="margin-bottom: 20px">
<!--            <a-input style="width: 30%" placeholder="请输入关键字查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>-->
         <!--   <a-button type="primary" style="width: 120px;margin-left: 80%"  @click="">新建标签</a-button>
            <a-button type="primary" style="width: 120px;margin-left: 10px"  @click="">导出</a-button>-->
          </div>
          <a-table
            ref="table"
            :columns="columns"
            :dataSource="dataList"
            :pagination="pagination"
            size="middle"
            rowKey="label_id">
             <span slot="serial" slot-scope="text, record, index">
                 {{ index + 1 }}
             </span>
              <span slot="time" slot-scope="text">
                      {{ text | moment }}
              </span>
            <span slot="change_type" slot-scope="text">
                 <span v-if="text == '01' ">业务属性</span>
                 <span  v-else-if="text == '02'">时空特征</span>
                 <span  v-else-if="text == '03'">违法情况</span>
                 <span  v-else-if="text == '04'">事故情况</span>
                 <span  v-else-if="text == '05'">综合评估</span>
                 <span  v-else-if="text == '06'">cop15-红码</span>
                 <span  v-else-if="text == '07'">cop15-黄码</span>
            </span>
          </a-table>
        </a-spin>
      </a-col>
    </a-row>
    <table-edit ref="modal1" @ok="handleSaveOk"></table-edit>
    <table-view ref="modal2"></table-view>
    <table-detail ref="modal3"></table-detail>
    <layer-edit ref="modal4" @refresh="handleRefresh"></layer-edit>
    <quality-rule ref="modal5" @refresh="handleSearch"></quality-rule>
  </a-card>
</template>

<script>
  import STree from '@/components/Tree/XTree'
  import { STable } from '@/components'
  import { getLeabalList } from '@/api/hybqlabel'

  const openKeys = ['dwd']
  const defaultSelectedKeys = [ 'clbq' ]

  export default {
    name: 'DataList',
    components: {
      STable,
      STree
    },
    data () {
      return {
        // tree 相关变量
        openKeys,
        defaultSelectedKeys,
        dataTree: [],

        // table 相关变量
        tableSpinning: false,
        columns: [
          {
            title: '编号',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '标签代码',
            dataIndex: 'label_code',
          },
          {
            title: '标签名称',
            dataIndex: 'label_name',
          },
          {
            title: '标签类型',
            dataIndex: 'label_type',
            scopedSlots: { customRender: 'change_type' }
          },
          {
            title: '标签描述',
            dataIndex: 'desc',
          },
          {
            title: '更新频率',
            dataIndex: 'updata_freg'
          },
          {
            title: '创建时间',
            dataIndex: 'create_time',
            scopedSlots: { customRender: 'time' }
          }
        ],
        dataList: [],
        // 查询参数
        queryParam: {
          layer: '',
          topic: '',
          pageSize: 10,
          pageNo: 1,
          searchVal: '',
          datasourceId: 0
        },
        layerCN: '明细层',
        defaultKey: [],
        selectedRowKeys: [],
        selectedRows: [],
        pagination: {
          total: 20,
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
            this.$set(this.pagination, 'current', current)
            this.$set(this.pagination, 'pageSize', size)
            this.queryParam.pageNo = current
            this.queryParam.pageSize = size
            this.getData()
          }
        },
        minHeight: '0'

      }
    },
    created () {
      this.handleRefresh()
      this.minHeight = window.screen.height * 0.75 + 'px'
    },
    methods: {
      handleRefresh () {
        this.dataTree = []
        this.queryParam.layer = 'clbq'
        this.queryParam.datasourceId = 2
        this.dataTree.push({ 'title': '标签目录', 'key': 'dwd', 'children': [] })

        const _this = this
        this.dataTree.forEach((item, index) => {
          _this.dataTree[index].children.push({ 'title': "车辆标签",  'key': "clbq" })
          _this.dataTree[index].children.push({ 'title': "驾驶人标签",  'key': "jsrbq" })
        })
        _this.getData()
      },
      handleClick ({ item, key }) {
            debugger
            const _this = this
            _this.pagination.pageNo = 1
            _this.queryParam.layer = key
            _this.getData()
      },
      handleAdd () {
        const params = { }
        params.isCreate = true
        params.layer = this.queryParam.layer
        params.topic = this.queryParam.topic
        params.tableCategory = this.layerCN + '-' + this.queryParam.topic
        params.datasourceId = this.queryParam.datasourceId
        this.$refs.modal1.add(params)
      },
      handleEdit (item) {
        const params = { ...item }
        params.datasourceId = this.queryParam.datasourceId
        this.$refs.modal1.edit(params)
      },
      handleSearch () {
        this.getData()
      },
      handleDelete: function (item) {
        this.tableSpinning = true
        const _this = this
        const params = { 'layer': item.layer, 'tablename': item.tblName }
        deleteTable(params).then(res => {
          if (res.code === 0) {
            _this.$message.success('删除表成功')
            _this.getData()
          } else {
            _this.$message.error('删除表失败')
          }
          _this.tableSpinning = false
        }).catch(err => {
          _this.$message.error(err)
          _this.tableSpinning = false
        })
      },
      handleDetail (item) {
        item.base = this.queryParam.layer
        this.$refs.modal3.show(item)
      },
      handlePreview (item) {
        const _this = this
        const params = { 'base': _this.queryParam.layer, 'tblName': item.tblName }
        _this.$refs.modal2.show(params)
      },
      handleSaveOk () {
        this.getData()
      },
      onSelectChange (selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectedRows = selectedRows
      },
      getData () {
        const _this = this
        let params= _this.queryParam
        if (_this.queryParam.layer == 'clbq'){
          getLeabalList(params).then(res => {
            if (res.code === 0) {
              this.dataList = res.data.totalList
              this.pagination.total = res.data.total
            } else {
              _this.$message.error('查询表失败')
            }

          }).catch(err => {
            _this.$message.error(err)

          })
        }else {
          _this.dataList = []
          _this.pagination.total = _this.dataList.length
        }
      }
    }
  }
</script>

<style lang="less">
  .ant-input {
    border-radius: 0px;
  }

  .label .btn{
  visibility: hidden;
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
