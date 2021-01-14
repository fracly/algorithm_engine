<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="失败策略:">
      <a-radio-group v-model="params.failureStrategy">
        <a-radio value="CONTINUE">
          继续
        </a-radio>
        <a-radio value="END">
          结束
        </a-radio>
      </a-radio-group>
    </a-form-item>
    <a-form-item label="流程优先级:">
      <a-select  style="width: 180px" v-model="params.processInstancePriority">
        <a-select-option value="HIGHEST">
          <a-icon type="arrow-up" style="color: #FF0000"/>HIGHEST
        </a-select-option>
        <a-select-option value="HIGH">
          <a-icon type="arrow-up" style="color: #FF0000"/>HIGH
        </a-select-option>
        <a-select-option value="MEDIUM">
          <a-icon type="arrow-up" style="color: #EA9E87"/> MEDIUM
        </a-select-option>
        <a-select-option value="LOW">
          <a-icon type="arrow-down" style="color: #2A8734"/>LOW
        </a-select-option>
        <a-select-option value="LOWEST">
          <a-icon type="arrow-down" style="color: #2A8734"/> LOWEST
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="WORK分组:">
      <a-select  style="width: 180px"  v-model="params.workerGroupId">
        <a-select-option v-for="item in workGroup" :value="item.id">
            {{item.name}}
        </a-select-option>
      </a-select>
    </a-form-item>
  </a-form-model>
</template>

<script>
  import eventBus from "@/components/G6Editor/utils/eventBus";
  import Grid from "@antv/g6/build/grid";
  import {updateWorkInfo,getWorkInfo,getAllGroup} from "@/api/process"

  export default {
    data() {
      return {
        labelCol: { span: 8 },
        wrapperCol: { span: 16 },
        params:{
          scheduleTime:"",
          warningType: "NONE",
          failureStrategy: "CONTINUE",
          workerGroupId: -1,
          warningGroupId: "",
          id: null,
          processInstancePriority: "MEDIUM",
          runMode: "RUN_MODE_SERIAL",
          definitionId: "",
          processDefinitionId:"",
        },
        allowSave:false,
        workGroup:[{id:-1,name:"Default"}]
      };
    },
    props:{
      definitionId: {
        type: Number,
        default: () => {
          return 0
        }
      }
    },
    //当传入的note id放生变化时
    watch:{
      'definitionId': {
        handler(newVal, oldVal) {
          if(newVal==""){
            this.allowSave=false;
          }else{
            this.allowSave=false;
            this.params.definitionId =  newVal
            this.params.processDefinitionId =  newVal
            this.getWorkInfo()
          }
        }
      }
    },
    created() {
      var _that = this
      getAllGroup().then(res => {
        for(var i=0;i<res.data.length;i++){
          this.workGroup.push(res.data[i])
        }
      })
    },
    methods: {
      saveWorkInfo(){
        if(this.definitionId==0){
          console.error("保存work信息错误，流程未保存无法保存任务信息")
          return
        }
        let _that=this
        updateWorkInfo(this.params).then(res => {
          if (res.code != 0) {
            return false;
          } else {
            _that.getWorkInfo()
          }
        })
      },
      getWorkInfo(){
        let _that =this
        getWorkInfo({"definitionId":_that.definitionId}).then(res => {
          if (res.code != 0 || res.data==null) {
            this.params = {
              scheduleTime:"",
              warningType: "NONE",
              failureStrategy: "CONTINUE",
              workerGroupId: -1,
              warningGroupId: "",
              id: null,
              processInstancePriority: "MEDIUM",
              runMode: "RUN_MODE_SERIAL",
              definitionId: _that.definitionId,
              processDefinitionId: _that.definitionId,
            };
          } else {
            this.params=res.data;
            this.params.processDefinitionId=res.data.definitionId;
          }
        })
      },
    }
  };
</script>
<style>
  .ant-form-item {
     margin-bottom: 4px!important;
  }
</style>

