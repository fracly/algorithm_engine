<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="列选择:">
      <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnListAgo.length!=0" style="width: 100%;" @click="showChooseFiled">已选择<span class="filedChoose">{{params.characterColumns.length}}</span>个字段</a-button>
      <!--字段选择模态框-->
      <a-modal  :visible="FiledChoose" title="字段选择" @cancel="showChooseFiled" width="850px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveChooseFiled">
            保存
          </a-button>
          <a-button key="back" @click="showChooseFiled">
            返回
          </a-button>
        </template>
        <field-selection v-if="FiledChoose" :chooseField="params.characterColumns" :FiledList="params.columnListAgo" ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import {getDataBaseData, getTableData, getColumData} from '@/api/process'
  import FieldSelection from "@/components/FieldSelection"

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          columnList:[],
          columnListAgo:[],
          datasource: "",
          table: "",
          showType: "TABLE",
          characterColumns: [],
          newTable: "",
          dataStorage: getDataStorage()
        },
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
        this.params.datasource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
        //#####################通用写法###################

        //####################组件按需加载####################
        /*获取上游节点字段信息*/
        this.getProNodeColumList()
        //####################组件按需加载####################
        debugger
      },

      /*获取上游节点的字段值*/
      getProNodeColumList(){
        /*获取上游节点字段信息数据*/
        let _that = this
        let data = this.$parent.getPreNodeColumList()
        if("point0List" in data){
          this.params.columnListAgo = data.point0List;
        }
      },
      /*展开显示字段选择的弹窗*/
      showChooseFiled() {
        this.FiledChoose = !this.FiledChoose
      },
      saveChooseFiled(){
        this.params.columnList= this.$refs.filedSelect.FiledListTemp
        this.params.characterColumns = this.$refs.filedSelect.chooseFileds;
        this.FiledChoose = !this.FiledChoose
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
  .filedChoose{
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