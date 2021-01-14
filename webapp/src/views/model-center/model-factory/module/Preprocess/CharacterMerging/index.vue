<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="列选择:">
      <span v-if="params.columnListAgo.length==0" style="color: red">上游节点未找到字段信息</span>
      <a-button v-if="params.columnListAgo.length!=0" style="width: 100%;" @click="showChooseFiled">已选择<span class="filedChoose">{{params.selectColumnList.length}}</span>个字段</a-button>
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
        <field-selection v-if="FiledChoose" :chooseField="params.selectColumnList" :FiledList="params.columnListAgo" ref="filedSelect"></field-selection>
      </a-modal>
    </a-form-item>
    <!--拼接符号-->
    <a-form-item label="拼接符号:">
     <a-input  v-model="params.symbol"  @change="changeSymbol()"/>
    </a-form-item>
    <a-form-item label="案例:">
      <a-input  v-model="params.symbolStr"  style="color: black" disabled="true"/>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import {getDataSouceData, getDataStorage} from "@/utils/global";
  import FieldSelection from "@/components/FieldSelection"

  export default {
    data() {
      return {
        dataSourceData: getDataSouceData(),
        labelCol: {span: 9},
        wrapperCol: {span: 15},
        FiledChoose:false,
        params: {
          columnListAgo:[],
          columnList:[],
          datasource: 16,
          table: "",
          showType: "TABLE",
          selectColumnList: [],
          symbol: "",
          symbolStr: "",
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
          this.params.selectColumnList = []
          this.params.symbol = ""
          this.params.symbolStr = ""
        }
        //#####################通用写法###################
        //获取上一节点的datasource
        this.params.datasource = this.$parent.getPreNodeDataSourceId()
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
          this.params.columnListAgo = data.point0List;
        }
      },
      /*展开显示字段选择的弹窗*/
      showChooseFiled() {
        this.FiledChoose = !this.FiledChoose
      },
      saveChooseFiled(){
        this.params.selectColumnList = this.$refs.filedSelect.chooseFileds;
        this.FiledChoose = !this.FiledChoose
        this.updatasymbolStr()
      },

      changeSymbol(){
        let _that = this
        //修改案例值
        _that.updatasymbolStr()
        //给column拼接增加的列
        _that.addPinjieLie()
      },

      //修改案例的值
      updatasymbolStr() {
           let _that = this
           let symbolStr=""
          if(_that.params.symbol !== "" ){
            for(let i=0; i < _that.params.selectColumnList.length ; i++){
              if ( i == _that.params.selectColumnList.length -1){
                symbolStr = symbolStr + _that.params.selectColumnList[i]
              }else {
                symbolStr = symbolStr + _that.params.selectColumnList[i] + _that.params.symbol
              }
            }
          }else {
            _that.params.symbolStr = ''
          }
            _that.params.symbolStr = symbolStr

      },

  //给column拼接增加的列的方法
  addPinjieLie() {
        debugger
    this.params.columnList = JSON.parse(JSON.stringify(this.params.columnListAgo));
    let list = [];
    for (let i = 0; i < this.params.columnListAgo.length; i++) {
      list.push(this.params.columnList[i].name)
    }
    if (list.indexOf("merge_col") > 0){
    }else {
      this.params.columnList.push(
        {
          key: "merge_col",
          name: "merge_col",
          type: 'string',
          comment: '拼接列'
        })
    }
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