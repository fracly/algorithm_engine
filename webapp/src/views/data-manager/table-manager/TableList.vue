<template>
  <a-card :bordered="false" class="data">
    <a-row :gutter="8" :style="{ height: minHeight }">
      <a-col :span="4">
        <s-tree
          :dataSource="dataTree"
          :openKeys.sync="openKeys"
          :editable="userInfo.administrator"
          :defaultSelectedKeys="defaultSelectedKeys"
          @click="handleClick"
          @add="addLayer"
          @edit="editLayer"
          @delete="deleteLayer"
         ></s-tree>
      </a-col>
      <a-col :span="20">
        <a-spin :spinning="tableSpinning">
          <div class="operate" style="margin-bottom: 20px">
            <a-input style="width: 30%" placeholder="请输入表中文名查询" v-model="queryParam.searchVal" @keyup.enter="handleSearch"></a-input>
            <a-button type="primary" style="width: 120px;margin-left: 20px;" icon="search" @click="handleSearch">查询</a-button>
            <a-button style="margin-left: 8px" @click="resetSearchForm()">重置</a-button>
            <a-button v-show="userInfo.administrator" type="primary" style="width: 160px;float: right" icon="plus" @click="handleAdd()">新建表</a-button>
          </div>
          <a-table
            :columns="columns"
            :dataSource="afterDataList"
            :pagination="pagination"
            size="middle"
            rowKey="tblDescribe">
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
                <a @click="handleEdit(record)">编辑</a>
                <a-divider type="vertical"/>
                <a @click="handlePreview(record)">预览</a>
                <a-divider type="vertical"/>
                <a-dropdown>
                  <a class="ant-dropdown-link" @click="e => e.preventDefault()">
                    更多<a-icon type="down" />
                  </a>
                  <a-menu slot="overlay">
                    <a-menu-item>
                      <a @click="handleDetail(record)">表详情</a>
                    </a-menu-item>
                    <a-menu-item>
                      <confirm title="确认删除" @confirm="handleDelete(record)" content="确认删除该表吗？"></confirm>
                    </a-menu-item>
                  </a-menu>
                </a-dropdown>
              </template>
              <template v-else>
                <a @click="handlePreview(record)">预览</a>
                <a-divider type="vertical"/>
                <a @click="handleDetail(record)">表详情</a>
              </template>
            </span>
          </a-table>
        </a-spin>
      </a-col>
    </a-row>
    <table-edit ref="modal1" @ok="handleSaveOk"></table-edit>
    <table-view ref="modal2"></table-view>
    <table-detail ref="modal3"></table-detail>
    <layer-edit ref="modal4" @refresh="handleRefresh"></layer-edit>
  </a-card>
</template>

<script>
    import STree from '@/components/Tree/Tree'
    import { STable } from '@/components'

    import TableEdit from './modules/TableEdit'
    import TableView from './modules/TableView'
    import TableDetail from './modules/TableDetail'
    import LayerEdit from './modules/LayerEdit'
    import Confirm from '@/components/Notification/Confirm'
    import {clone} from "@/utils/global";

    import { queryTopic, deleteTopic, queryTable, deleteTable } from '@/api/model'
    import { mapGetters } from 'vuex'

    const openKeys = ['dwd']
    const defaultSelectedKeys = [ 'dwd-0' ]
    export default {
        name: 'DataList',
        components: {
            STable,
            STree,
            TableEdit,
            TableDetail,
            TableView,
            LayerEdit,
            Confirm
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
                        title: '表中文名',
                        dataIndex: 'tblDescribe',
                        scopedSlots: { customRender: 'description' },
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };
                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '表英文名',
                        dataIndex: 'tblName',
                        scopedSlots: { customRender: 'tblName' },
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };

                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '表描述',
                        dataIndex: 'area',
                        scopedSlots: { customRender: 'description' },
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };
                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '层级',
                        dataIndex: 'layer',
                        scopedSlots: { customRender: 'layer' },
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };
                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '创建时间',
                        dataIndex: 'create_time',
                        scopedSlots: { customRender: 'time' },
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };
                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '创建者',
                        dataIndex: 'userName',
                        customRender: (text, row, index) => {
                            const tmp = row.typelength
                            const obj = {
                                children: text,
                                attrs: {

                                },
                            };
                            if(tmp>1&&row.isIndex){
                                obj.attrs.rowSpan = tmp;
                            }else if(tmp>1){
                                obj.attrs.rowSpan = 0;
                            }else{

                            }
                            return obj;
                        }
                    },
                    {
                        title: '存储类型',
                        dataIndex: 'type'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        key: 'action',
                        width: '15%',
                        scopedSlots: { customRender: 'action' }
                    }
                ],
                dataList:[],
                afterDataList:[],
                // 查询参数
                queryParam: {
                    layer: '',
                    topic: '',
                    pageNo: 1,
                    pageSize: 10,
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
            this.handleRefresh()
            this.minHeight = window.screen.height * 0.75 + 'px'
        },
        computed: {
            ...mapGetters(['userInfo'])
        },
        methods: {
            handleRefresh () {
                this.dataTree = []
                this.queryParam.layer = 'dwd'
                this.queryParam.datasourceId = 2
                this.dataTree.push({ 'title': '明细层', 'key': 'dwd', 'children': [] })
                this.dataTree.push({ 'title': '汇总层', 'key': 'dws', 'children': [] })
                this.dataTree.push({ 'title': '应用层', 'key': 'dwa', 'children': [] })
                this.dataTree.push({ 'title': '维表层', 'key': 'dim', 'children': [] })

                const _this = this
                this.dataTree.forEach((item, index) => {
                    queryTopic({ 'layer': item.key }).then(res => {
                        _this.dataTree[index].children = []
                        res.data.forEach((item2, index2) => {
                            if (index === 0 && index2 === 0) {
                                _this.queryParam.topic = item2.layer
                                _this.getData()
                            }
                            if (item2.layer.length >= 10) {
                                _this.dataTree[index].children.push({ 'title2': item2.layer.substring(0, 7) + '..', 'title': item2.layer, 'value': item2.id, 'key': item.key + '-' + index2, 'sort': item2.id })
                            } else {
                                _this.dataTree[index].children.push({ 'title2': item2.layer, 'title': item2.layer, 'value': item2.id, 'key': item.key + '-' + index2, 'sort': item2.id })
                            }
                        })
                    })
                })
            },
            handleClick ({ item, key }) {
                this.queryParam.pageNo = 1
                this.$set(this.pagination, 'current', 1)
                const _this = this
                _this.pagination.pageNo = 1
                const two = key.split('-')
                _this.dataTree.forEach((item) => {
                    if (two[0] === item.key) {
                        _this.queryParam.layer = item.key
                        _this.layerCN = item.title
                        item.children.forEach((item2) => {
                            if (key === item2.key) {
                                _this.queryParam.topic = item2.title
                            }
                        })
                    }
                })
                _this.getData()
            },
            handleAdd () {
                const params = { }
                params.isCreate = true
                params.layer = this.queryParam.layer
                params.topic = this.queryParam.topic
                params.tableCategory = this.layerCN
                params.datasourceId = this.queryParam.datasourceId
                this.$refs.modal1.add(params)
            },
            handleEdit (item) {
                const params = { ...item }
                params.datasourceId = this.queryParam.datasourceId
                this.$refs.modal1.edit(params)
            },
            handleSearch () {
                this.queryParam.pageNo = 1
                this.$set(this.pagination, 'current', 1)
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
            addLayer (item) {
                item.isCreate = true
                this.$refs.modal4.add(item)
            },
            editLayer (item) {
                item.isCreate = false
                this.$refs.modal4.edit(item)
            },
            deleteLayer (item) {
                const that = this
                this.$confirm({
                    title: '确认删除该模型目录?',
                    content: '只有在该模型目录下没有任何表的时候才可以被删除！',
                    onOk () {
                        const params = { 'layer': item.key.split('-')[0], 'topic': item.title }
                        deleteTopic(params).then(res => {
                            if (res.code === 0) {
                                that.$message.success('删除模型目录成功')
                                that.handleRefresh()
                            } else {
                                that.$message.error('删除模型目录失败')
                            }
                        })
                    },
                    onCancel () {}
                })
                console.log(item)
            },
            resetSearchForm () {
                this.$set(this.pagination, 'current', 1)
                this.queryParam.pageNo = 1
                this.queryParam.searchVal = ''
                this.getData()
            },
            getData () {
                this.queryParam.searchVal = this.queryParam.searchVal.trim()
                // this.dataList = []
                // this.afterDataList = []
                debugger
                  queryTable(this.queryParam).then((res) => {
                      this.dataList = []
                      this.afterDataList = []
                      if (res.code === 0) {
                          this.dataList = res.data.totalList
                          this.pagination.total = res.data.total
                          let list =this.dataList
                          for(let a=0;a<list.length;a++ ){
                              let types = list[a].type
                              let typeSplit = types.split(",");
                              let data= list[a]
                              for (let b=0;b<typeSplit.length;b++){
                                  let type = typeSplit[b]
                                  let dataClone = clone(data);
                                  if(b != 0) {
                                      dataClone.tblDescribe = dataClone.tblDescribe + '-' + b;
                                  }
                                  if(b==0){
                                      dataClone.isIndex= true
                                  }
                                  dataClone.type=type
                                  dataClone.typelength=typeSplit.length
                                  this.afterDataList.push(dataClone)
                              }
                          }
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
    min-height: 750px!important;

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
