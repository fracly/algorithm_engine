<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="自定义参数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div @click="showCustomParams">
        <a-icon type="plus-circle" style="color:#429FE1"/><span v-if="params.localParams.length!=0">已添加{{params.localParams.length}}条自定义参数</span>
      </div>
      <a-modal v-if="customParamsStaus" :visible="customParamsStaus" destroyOnClose title="自定义参数" @cancel="showCustomParams" width="600px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveCustomParams">
            保存
          </a-button>
          <a-button key="back" @click="showCustomParams">
            返回
          </a-button>
        </template>
        <custom-params :CustomParams="params.localParams"  :pageList="params.columnList" ref="CustomParams"></custom-params>
      </a-modal>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataBaseData, getTableData, getColumData} from '@/api/process'
  import FieldSelection from "@/components/FieldSelection"
  import customParams from "@/views/model-center/model-factory/module/Preprocess/DataFilter/customParams"

  export default {
    data() {
      return {
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          columnList:[],
          datasource: "",
          table: "",
          labelColumn: "",
          condition:"",
          num: "",
          newTable: "",
          dataStorage:16,
          localParams: [],
        },
        customParamsStaus: false,
        initState: false,
      };
    },
    components: {FieldSelection, customParams},
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
    watch: {
      "params.num": function () {
        this.params.num=this.params.num.replace(/ /g, '!!');
        //debugger
      }
    },
    methods: {
      /*页面初始化 按需加载，具体方法查看module.vue*/
      initPage() {
        debugger
        //#####################通用写法###################
        //上次本组件保存的参数
        if(JSON.stringify(this.tasks)!='{}'){
          this.params = this.tasks
        }else{
          //如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable=this.$parent.getLocalNodeTableName()
        }
        //////////////******************************//自己需要清理的数据
        //判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空
        let preNodeTableName = this.$parent.getPreNodeTableName()
        if(preNodeTableName != this.params.table){
          this.params.localParams = []
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
          this.params.columnList = data.point0List;
        }
      },
      /*展开显示字段选择的弹窗*/
      showChooseFiled() {
        this.FiledChoose = !this.FiledChoose
      },
      saveChooseFiled(){
        this.params.characterColumns = this.$refs.filedSelect.chooseFileds;
        this.FiledChoose = !this.FiledChoose
      },
      saveCustomParams(){
        let localParams =this.$refs.CustomParams.localParams
        for(var i=0;i<localParams.length;i++){
          if((localParams[i].column=="" ||localParams[i].column==null) ){
            this.$message.error("列不能为空");
            return
          }else if(localParams[i].coefficient=="" ||localParams[i].coefficient==null){
            this.$message.error("判断条件不能为空");
            return
          }else if(localParams[i].fixedValue=="" || localParams[i].fixedValue==null  ){
            this.$message.error("值不能为空");
            return
          }
        }
        this.params.localParams =localParams
        this.params.labelColumn = ""
        this.params.condition = ""
        this.params.num = ""
        //确认的时候构建传给下个下各组件的参数，也是传给后台执行任务的参数
        for (let i=0;i < this.params.localParams.length;i++){
          let  a = this.params.localParams[i].column
          let  b = this.params.localParams[i].coefficient
          // let  c = this.params.localParams[i].fixedValue.replaceAll(" ","---").replaceAll(":","--")
          //   let  c = this.params.localParams[i].fixedValue.replace(/\ /g,"---").replace(/\:/g,"--")
            let  c = this.params.localParams[i].fixedValue.replace(/ /g,"---").replace(/:/g,"--")
          this.params.labelColumn +=(a)
          this.params.condition +=(b)
          this.params.num +=(c)
          if (i != (this.params.localParams.length-1)){
            this.params.labelColumn +=(":")
            this.params.condition  +=(":")
            this.params.num   +=(":")
          }
        }
        debugger
        this.customParamsStaus=!this.customParamsStaus
      },
      showCustomParams(){
        this.customParamsStaus=!this.customParamsStaus
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