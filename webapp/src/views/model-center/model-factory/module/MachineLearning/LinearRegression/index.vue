<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="特征列:">
      <span v-if="params.columnList.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnList.length!=0" style="width: 100%;" @click="showChooseFiled">已选择<span class="filedChoose">{{params.characterColumns.length}}</span>个字段</a-button>
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
        <field-selection v-if="FiledChoose" :chooseField="params.characterColumns" :FiledList="params.columnList" ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>
    <a-form-item label="标签列:">
       <a-select  show-search optionFilterProp="children" v-model="params.labelColumn">
        <a-select-option v-for=" d in params.columnList" :key="d.name" :value="d.name">{{ d.comment == ''||d.comment==undefined?d.name:d.comment }} </a-select-option>
       </a-select>
    </a-form-item>

    <a-form-item  label="是否保存模型:">
      <a-switch    default-checked v-model="params.isSaveModel"/>
    </a-form-item>

    <a-form-item  label="模型名称:" v-if="params.isSaveModel">
      <a-input   v-model="params.modelName" @change="initPath()"> </a-input>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import FieldSelection from "@/components/FieldSelection"
  import TagSelectOption from '../../../../../../components/TagSelect/TagSelectOption'

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          columnList: [],
          datasource: 16,
          table: "",
          showType: "TABLE",
          characterColumns: [],
          symbol: "",
          symbolStr: "",
          newTable: "",
          dataStorage: getDataStorage(),
          labelColumn: "",
          isSaveModel: false,
          modelName: "",
          modelPath: "",
          modelType:"LINEAR_REGRESSION"
        },
        customParamsStaus: false,
        initState: false,

      };
    },
    components: { TagSelectOption, FieldSelection},
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
        debugger
        //#####################通用写法###################
        //上次本组件保存的参数
        if(JSON.stringify(this.tasks)!='{}'){
          this.params = this.tasks
        }else{
          //如果上次没有保存过，则生成新table名字 生成一次即可 后续不需要再次生成
          this.params.newTable=this.$parent.getLocalNodeTableName()
          this.params.modelName = this.$parent.getLocalNodeTableName()
        }
        //////////////******************************//自己需要清理的数据
        //判断本次上一个节点的table和上次本组件保存的上个节点的table是否一样，不一样的话，子组件的元素要清空
        let preNodeTableName = this.$parent.getPreNodeTableName()
        if(preNodeTableName != this.params.table){
          // this.params.relatedColumns = []

        }
        //#####################通用写法###################
        //获取上一节点的datasource
        this.params.dataSource = this.$parent.getPreNodeDataSourceId()
        //保存本次的上一节点的表名称
        this.params.table = this.$parent.getPreNodeTableName()
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
      //点击显示模型名称
      initPath(){
           this.params.modelPath = "/escheduler/model/" + this.params.modelName
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