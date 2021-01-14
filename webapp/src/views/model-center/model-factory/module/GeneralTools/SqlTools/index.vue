<template>
  <div>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
    <a-form-item label="数据源:">
      <a-select placeholder="请选择数据源" v-model="params.type" @change="reloadDatabaseData">
        <a-select-option v-for="item in dataSourceData" :key="item.id" :value="item.code">
          {{ item.code }}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="选择库:">
      <a-select placeholder="请选择库" v-model="params.datasource" :defaultValue="params.datasource">
        <a-select-option v-for="item in databaseData" :key="item.id">
          {{ item.name }}
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="查询类型">
          <a-select show-search placeholder="请查询类型" v-model="params.sqlType" style="width: 100px">
            <a-select-option key="0" value="0">查询</a-select-option>
            <a-select-option key="1" value="1">非查询</a-select-option>
          </a-select>
          <a-checkbox-group  v-model="params.showType" v-show="params.sqlType==0" style="margin-left: 10px">
            <a-checkbox value="TABLE">表格</a-checkbox>
            <a-checkbox value="ATTACHMENT">附件</a-checkbox>
          </a-checkbox-group>
    </a-form-item>

    <a-form-item>
      <div slot="label">
        <a-row>
          <a-col :span="4">
            sql语句
          </a-col>
          <a-col :span="20">
            <div  @click="() => {this.showCodeEditor=true}"><a-icon type="edit"/><span>编辑</span></div>
          </a-col>
        </a-row>
      </div>


      <div class="from-mirror" style="max-height: 200px;min-height: 180px">
        <a-input
          type="hidden"
          v-model="params.sql"
        > </a-input>
        <a-textarea
          id="code-sql"
          name="code-sql"
          style="opacity: 0;"/>
      </div>
    </a-form-item>

    <!--<a-form-item label="前置sql:">-->
      <!--<a-input v-model="params.preStatements" type="textarea" />-->
    <!--</a-form-item>-->


    <a-form-item label="前置sql:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div >
        <a-icon type="plus-circle" style="color:#429FE1" @click="preStatements"/><span v-if="params.preStatements.length!=0">已添加{{params.preStatements.length}}条前置sql</span>
      </div>
      <a-modal v-if="preStatementsStatus" :visible="preStatementsStatus" destroyOnClose title="前置sql" @cancel="preStatements" width="650px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="savePreStatements">
            保存
          </a-button>
          <a-button key="back" @click="preStatements">
            返回
          </a-button>
        </template>
        <pre-statements :preStatements="params.preStatements"  ref="preStatements"></pre-statements>
      </a-modal>
    </a-form-item>



    <a-form-item label="后置sql:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div >
        <a-icon type="plus-circle" style="color:#429FE1" @click="postStatements"/><span v-if="params.postStatements.length!=0">已添加{{params.postStatements.length}}条后置sql</span>
      </div>
      <a-modal v-if="postStatementsStatus" :visible="postStatementsStatus" destroyOnClose title="后置sql" @cancel="postStatements" width="650px">
        <template slot="footer">

          <a-button key="submit" type="primary" @click="savePostStatements">
            保存
          </a-button>
          <a-button key="back" @click="postStatements">
            返回
          </a-button>
        </template>
        <post-statements :postStatements="params.postStatements"  ref="postStatements"></post-statements>
      </a-modal>
    </a-form-item>

    <a-form-item label="自定义参数:" :label-col="labelCol" :wrapper-col="wrapperCol">
      <div @click="showCustomParams">
        <a-icon type="plus-circle" style="color:#429FE1"/><span v-if="params.localParams.length!=0">已添加{{params.localParams.length}}条自定义参数</span>
      </div>
      <a-modal v-if="customParamsStaus" :visible="customParamsStaus" destroyOnClose title="自定义参数" @cancel="showCustomParams" width="650px">
        <template slot="footer">
          <a-button key="submit" type="primary" @click="saveCustomParams">
            保存
          </a-button>
          <a-button key="back" @click="showCustomParams">
            返回
          </a-button>
        </template>
        <custom-params :CustomParams="params.localParams" ref="customParams" ></custom-params>
      </a-modal>


    </a-form-item>


    <!--<a-form-item label="后置sql:">-->
      <!--<a-input v-model="params.preStatements" type="textarea" />-->
    <!--</a-form-item>-->


  </a-form-model>
    <code-editor :code="params.sql" :type="type" v-if="showCodeEditor" @cancel="() => {this.showCodeEditor=false}" @saveCode="saveCode"></code-editor>
  </div>
</template>

<script>
  import preStatements from "@/views/model-center/model-factory/module/GeneralTools/SqlTools/preStatements"
  import postStatements from "@/views/model-center/model-factory/module/GeneralTools/SqlTools/postStatements"
  import {getDataSouceData} from "@/utils/global";
  import {getDataBaseData,getTableData,getColumData,getUdfs} from '@/api/process'
  import codemirror from '@/utils/codemirror'
  import customParams from "@/views/model-center/model-factory/module/GeneralTools/SqlTools/customParams"
  import {clone} from "@/utils/global";
  import CodeEditor from "@/components/CodeEditor"
  let editor

  export default {
    data() {
      return {
        type:"sql",
        dataSourceData:getDataSouceData(),
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        showCodeEditor:false,
        databaseData:[],
        tableData:[],
        params:{
          type: "",
          datasource: null,
          table: "",
          udfs: "",
          sql: "",
          sqlType: "1" ,
          title: "",
          receivers: "",
          receiversCc: "",
          columnList:[],
          showType: "TABLE",
          localParams: [],
          connParams: "",
          preStatements: [],
          postStatements: [],
          value: "",
          value2: ""
        },
        preStatementsStatus:false,
        postStatementsStatus:false,
        initState:false,
        udfsList:[],
        customParamsStaus: false
      };
    },
    components:{
      preStatements,postStatements, customParams,CodeEditor
    },
    props:{
      tasks: {
        type: Object,
        default: () => {
          return {
            id:"default",
            label:""
          }
        }
      }
    },
    created() {
      if(JSON.stringify(this.tasks)!='{}'){
        this.params = this.tasks
        getDataBaseData({ 'type': this.params.type }).then(res => {
          this.databaseData=res.data;
        })
        getTableData({ 'datasourceId': this.params.datasource,"type":this.params.type }).then(res => {
          this.tableData=res.data;
        })
      }
      // this.$watch('params.type', this.watchType)
      this.$watch('params.datasource', this.watchDataSource)
      this.$watch('params.table', this.watchTable)
    },
    mounted(){
      this.handlerEditor()
    },
    methods: {
      saveCode(code){
        this.params.sql=code;
        this.showCodeEditor=false;
        editor.setValue(code)
      },
      saveCustomParams(){
        let localParams = this.$refs.customParams.LocalParams;
        for(var i=0;i<localParams.length;i++){
          if((localParams[i].prop=="" ||localParams[i].prop==null) && this.customParamsStaus ){
            this.$message.error("props参数不能为空");
            return
          }
        }
        this.params.localParams = clone(localParams);
        this.customParamsStaus=!this.customParamsStaus
      },
      showCustomParams(){
        this.customParamsStaus=!this.customParamsStaus
      },
      /*保存前置sql*/
      savePreStatements(){
        let preStatements = this.$refs.preStatements.LocalParams;
        for(var i=0;i< preStatements.length;i++){
          if((preStatements[i]=="" || preStatements[i]==null) && this.preStatementsStatus ){
            this.$message.error("前置参数不能为空");
            return
          }
        }
        this.params.preStatements = clone(preStatements);
        this.preStatementsStatus=!this.preStatementsStatus
      },
      /*前置sql弹窗*/
      preStatements(){
        this.preStatementsStatus=!this.preStatementsStatus
      },
      /*保存后置sql*/
      savePostStatements(){
        let postStatements = this.$refs.postStatements.LocalParams;
        for(var i=0;i< postStatements.length;i++){
          if((postStatements[i]=="" || postStatements[i]==null) && this.postStatementsStatus ){
            this.$message.error("后置参数不能为空");
            return
          }
        }
        this.params.postStatements = clone(postStatements);
        this.postStatementsStatus=!this.postStatementsStatus
      },

      /*后置sql弹窗*/
      postStatements(){
        this.postStatementsStatus=!this.postStatementsStatus
      },
      /*重新加载库*/
      reloadDatabaseData(){
        this.params.name="读取"+this.params.type
        getDataBaseData({ 'type': this.params.type }).then(res => {
          this.$set(this, 'databaseData', res.data);
          this.$set(this, 'tableData', []);
          this.$set(this.params, 'datasource',"");
          this.$set(this.params, 'table',"");
        })
        this.getUdfs()
      },
      /*重新加载表*/
      reloadTableData(){
        getTableData({ 'datasourceId': this.params.datasource,"type":this.params.type }).then(res => {
          this.$set(this, 'tableData', res.data);
          this.$set(this.params, 'table',"");
        })
      },
      /*重新加载字段信息*/
      reloadTableCloumData(){
        getColumData({ 'datasourceId': this.params.datasource,"type":this.params.type,'table':this.params.table }).then(res => {
          this.$set(this.params, "columnList", res.data);
        })
      },
      /*表查询*/
      filterOption(input, option) {
        return (
          option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
        );
      },
      /*获取udfs*/
      getUdfs(){
        const self=this
        getUdfs({ "type":this.params.type}).then(res => {
          self.udfsList=res.data
        })
      },
      // /*数据源监听*/
      // watchType(){
      //   if(this.params.type!="" && this.params.type!=undefined && this.params.type!=null){
      //     this.reloadDatabaseData()
      //     this.getUdfs()
      //
      //   }
      // },
      /*库监听*/
      watchDataSource(){
        if(this.params.datasource!="" && this.params.datasource!=undefined && this.params.datasource!=null){
          this.reloadTableData()
        }
      },
      /*表监听*/
      watchTable(){
        if(this.params.table!="" && this.params.table!=undefined && this.params.table!=null){
          this.reloadTableCloumData()
        }
      },
      /**
       * 处理代码高亮显示
       */
      handlerEditor () {
        let _that =this
        // editor
        editor = codemirror('code-sql', {
          mode: 'sql',
          readOnly: false
        })
        this.keypress = () => {
          if (!editor.getOption('readOnly')) {
            editor.showHint({
              completeSingle: false
            })
          }
        }
        // Monitor keyboard
        editor.on('keypress', this.keypress)
        editor.on("change",function(){
          _that.params.sql = editor.getValue()
        });
        editor.setValue(this.params.sql)
        return editor
      }
    },
    destroyed () {
      /**
       * 销毁编辑器实例
       */
      if (editor) {
        editor.toTextArea() // Uninstall
        editor.off(('.code-sql'), 'keypress', this.keypress)
      }
    },
  };


  function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
  }
</script>
<style >
  .ant-form-item {
    margin-bottom: 4px!important;
  }

  /deep/.ant-modal-body {
    padding: 5px!important;
  }
</style>

