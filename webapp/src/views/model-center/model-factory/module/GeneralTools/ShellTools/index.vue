<template>
  <div>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params" >
    <a-form-item>
      <div slot="label">
        <a-row>
          <a-col :span="4">
            脚本:
          </a-col>
          <a-col :span="20">
            <div  @click="() => {this.showCodeEditor=true}"><a-icon type="edit"/><span>编辑</span></div>
          </a-col>
        </a-row>
       </div>
      <a-textarea
        id="code-shell-mirror"
        name="code-shell-mirror"
        style="opacity: 0;"/>
     <!-- <a-input v-model="params.shell" type="textarea"/>-->
    </a-form-item>
    <a-form-item label="资源:">
      <a-select
        show-search
        mode="multiple"
        placeholder="请选择资源" v-model="resourceList">
        <a-select-option v-for="(item,index) in resources" :key="item.alias">
          {{ item.alias}}
        </a-select-option>
      </a-select>
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
  </a-form-model>
  <code-editor :code="params.rawScript" :type="type" v-if="showCodeEditor" @cancel="() => {this.showCodeEditor=false}" @saveCode="saveCode"></code-editor>
  </div>
</template>

<script>
  import customParams from "@/views/model-center/model-factory/module/GeneralTools/ShellTools/customParams"
  import CodeEditor from "@/components/CodeEditor"

  import codemirror from '@/utils/codemirror'
  import { getResources } from '@/api/process'
  import {clone} from "@/utils/global";

  let editor
  export default {
    data() {
      return {
        labelCol: { span: 9 },
        wrapperCol: { span: 15 },
        type:"shell",
        showCodeEditor:false,
        resources:[],
        params:{
          cloumList:[],
          rawScript:"",
          resourceList:[],
          localParams:[]
        },
        resourceList:[],
        customParamsStaus:false
      };
    },
    components:{
      customParams,CodeEditor
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
    /*初始化参数*/
    created() {
      if(JSON.stringify(this.tasks)!='{}'){
        this.params = this.tasks
      }
      let _that=this
      getResources({ "type":"FILE"}).then(res => {
        _that.resources = res.data
      })
    },
    mounted(){
      this.handlerEditor();
      this.resourceList = _.map(_.cloneDeep(this.params.resourceList), v => v.res)
    },
    watch: {
      // Listening data source
      resourceList(a) {
        this.params.resourceList = _.map(a, v => {
          return {
            res: v
          }
        })
      }
    },
    methods: {
      saveCode(code){
        this.params.rawScript=code;
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
      handlerEditor () {
        debugger
        let _that =this
        // editor
        editor = codemirror('code-shell-mirror', {
          mode: 'shell',
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
          _that.params.rawScript = editor.getValue()
        });
        editor.setValue(this.params.rawScript)
        return editor
      },
    }
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
<style scoped>
  .ant-form-item {
     margin-bottom: 4px!important;
  }

   /deep/.ant-modal-body {
     padding: 5px!important;
   }

</style>

