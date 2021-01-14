<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="计算方法:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-select  v-model="params.method" :defaultValue="params.method" style="width: 100px">
        <a-select-option key="最大值" value="max">最大值</a-select-option>
        <a-select-option key="最小值" value="min">最小值</a-select-option>
        <a-select-option key="平均值" value="avg">平均值</a-select-option>
        <a-select-option key="总和" value="sum">总和</a-select-option>
        <a-select-option key="计数" value="count">计数</a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="计算列选择:">
      <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnListAgo.length!=0" style="width: 100%;" @click="showChooseCountFiled">已选择<span class="filedChooseCount">{{params.countColumns.length}}</span>个字段</a-button>
      <!--字段选择模态框-->
      <a-modal  :visible="FiledChooseCount" title="字段选择" @cancel="showChooseCountFiled" width="850px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseCountFiled">
            保存
          </a-button>
          <a-button key="back" @click="showChooseCountFiled">
            返回
          </a-button>
        </template>
        <field-selection v-if="FiledChooseCount" :chooseField="params.countColumns" :FiledList="params.columnListAgo" ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>

    <a-form-item label="分组列选择:">
      <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnListAgo.length!=0" style="width: 100%;" @click="showChooseGroupFiled">已选择<span class="filedChooseGroup">{{params.groupColumns.length}}</span>个字段</a-button>
      <!--字段选择模态框-->
      <a-modal  :visible="FiledChooseGroup" title="字段选择" @cancel="showChooseGroupFiled" width="850px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseGroupFiled">
            保存
          </a-button>
          <a-button key="back" @click="showChooseGroupFiled">
            返回
          </a-button>
        </template>
        <field-selection v-if="FiledChooseGroup" :chooseField="params.groupColumns" :FiledList="params.columnListAgo" ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>

  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import {getDataBaseData, getTableData, getColumData} from '@/api/process'
  import FieldSelection from "@/components/FieldSelection"
  import _ from 'lodash'

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChooseCount:false,
        FiledChooseGroup:false,
        params: {
          columnListAgo: [],
          columnList:[],
          datasource: "",
          table: "",
          showType: "TABLE",
          countColumns: [],
          groupColumns: [],
          method: "",
          newTable: "",
          dataStorage: getDataStorage()
        },
        col1: [],
        col0: [],
        customParamsStaus: false,
        initState: false
      };
    },
    components: {FieldSelection},
    props: {
      tasks: {
        type: Object,
        default: () => {
          return {
            id: "default",
            label: ""
          }
        }
      },
    },
    created() {
      this.initPage();
    },
    methods: {
      /*页面初始化 按需加载，具体方法查看module.vue*/
      initPage() {
        //#####################通用写法###################
        //上次本组件保存的参数
        if(JSON.stringify(this.tasks)!='{}'){
          this.params = this.tasks
        }else{
          //如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable=this.$parent.getLocalNodeTableName()
        }
        //获取上一节点的datasource
        this.params.dataSource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
        //#####################通用写法###################

        //####################组件按需加载####################
        /*获取上游节点字段信息*/
        this.getProNodeColumList()
        //####################组件按需加载####################
      },

      /*获取上游节点的字段值*/
      getProNodeColumList(){
        debugger
        /*获取上游节点字段信息数据*/
        let _that = this
        let data = this.$parent.getPreNodeColumList()
        if("point0List" in data){
          this.params.columnListAgo = data.point0List;
        }
      },
      /*展开显示字段选择的弹窗*/
      showChooseCountFiled() {
        this.FiledChooseCount = !this.FiledChooseCount
      },
      saveChooseCountFiled (){
        this.params.countColumns = this.$refs.filedSelect.chooseFileds;
        const columnList = _.cloneDeep(this.params.columnListAgo)
        this.col0 = []
        for (let a = 0; a < columnList.length; a++) {
          for (let i = 0; i < this.params.countColumns.length; i++) {
            if (columnList[a].name === this.params.countColumns[i]) {
              if (this.params.method !== 'count') {
                this.col0.push({
                  name: this.params.countColumns[i] + '_' + this.params.method,
                  type: 'double',
                  comment: columnList[a].comment
                })
              } else {
                this.col0.push({
                  name: this.params.countColumns[i] + '_' + this.params.method,
                  type: 'int',
                  comment: columnList[a].comment
                })
              }
            }
          }
        }
        this.col1 = []
        for (let a = 0; a < columnList.length; a++) {
          for (let i = 0; i < this.params.groupColumns.length; i++) {
            if (columnList[a].name === this.params.groupColumns[i]) {
              this.col1.push(columnList[a])
            }
          }
        }
        this.params.columnList = _.concat(this.col0, this.col1)
        this.FiledChooseCount = !this.FiledChooseCount
      },
      showChooseGroupFiled () {
        this.FiledChooseGroup = !this.FiledChooseGroup
      },
      saveChooseGroupFiled () {
        this.params.groupColumns = this.$refs.filedSelect.chooseFileds;
        const columnList = _.cloneDeep(this.params.columnListAgo)
        this.col1 = []
        for (let a = 0; a < columnList.length; a++) {
          for (let i = 0; i < this.params.groupColumns.length; i++) {
            if (columnList[a].name === this.params.groupColumns[i]) {
              this.col1.push(columnList[a])
            }
          }
        }
        this.col0 = []
        for (let a = 0; a < columnList.length; a++) {
          for (let i = 0; i < this.params.countColumns.length; i++) {
            if (columnList[a].name === this.params.countColumns[i]) {
              if (this.params.method !== 'count') {
                this.col0.push({
                  name: this.params.countColumns[i] + '_' + this.params.method,
                  type: 'double',
                  comment: columnList[a].comment
                })
              } else {
                this.col0.push({
                  name: this.params.countColumns[i] + '_' + this.params.method,
                  type: 'int',
                  comment: columnList[a].comment
                })
              }
            }
          }
        }
        this.params.columnList = _.concat(this.col1, this.col0)
        this.FiledChooseGroup = !this.FiledChooseGroup
      }
    }
  };


</script>
<style scoped>
  /deep/.ant-modal-body {
    padding: 15px!important;
  }
</style>

<style>
  .filedChooseCount{
    color:#4FAEEC;
    padding-left: 3px;
    padding-right:3px
  }
  .filedChooseGroup{
    color:#4FAEEC;
    padding-left: 3px;
    padding-right:3px
  }
  .el-tooltip__popper[x-placement^=top] .popper__arrow {
    border-top-color: #1ab394;
  }
  .el-tooltip__popper[x-placement^=top] .popper__arrow:after {
    border-top-color: #1ab394;
  }
  .atooltip {
    background: #1ab394 !important;
  }


</style>