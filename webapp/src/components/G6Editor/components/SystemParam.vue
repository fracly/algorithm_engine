<template>
  <a-form-model layout="vertical" class="ant-advanced-search-form" :model="params">
    <a-form-item label="运行标志:">
      <a-radio-group v-model="params.runFlag">
        <a-radio value="NORMAL">
          正常
        </a-radio>
        <a-radio value="FORBIDDEN">
          禁止执行
        </a-radio>
      </a-radio-group>
    </a-form-item>
    <a-form-item label="任务优先级:">
      <a-select  style="width: 180px" v-model="params.taskInstancePriority">
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
      <a-select  style="width: 180px" v-model="params.workerGroupId">
        <a-select-option v-for="item in workGroup" :value="item.id">
          {{item.name}}
        </a-select-option>
      </a-select>
    </a-form-item>
    <a-form-item label="失败重试次数:">
       <a-input-number   v-model="params.maxRetryTimes" :min="0" :max="5"/>(次)
    </a-form-item>
    <a-form-item label="失败重试间隔:">
        <a-input-number  v-model="params.retryInterval" :min="0" :max="120"/>(分)
    </a-form-item>
    <!--<a-form-item label="超时告警:">
      <a-switch default-checked  v-model="params.timeout.enable"/>
    </a-form-item>
    <a-form-item label="超时策略:" v-show="params.timeout.enable">
      <a-checkbox-group v-model="params.timeout.strategy">
          <a-checkbox value="1">
            超时告警
          </a-checkbox>
          <a-checkbox value="2">
            超时失败
          </a-checkbox>
      </a-checkbox-group>
    </a-form-item>
    <a-form-item label="超时时长:" v-show="params.timeout.enable">
      <a-input-number v-model="params.timeout.interval" :min="0" :max="120"/>(分)
    </a-form-item>-->


  </a-form-model>
</template>

<script>
  import {updateWorkInfo,getWorkInfo,getAllGroup} from "@/api/process"
  import eventBus from "@/components/G6Editor/utils/eventBus";

  export default {
    data() {
      return {
        labelCol: { span: 8 },
        wrapperCol: { span: 16 },
        params:{
          runFlag: "NORMAL",
          maxRetryTimes: "0",
          retryInterval: "1",
          taskInstancePriority: "MEDIUM",
          workerGroupId: -1,
          timeout: {
            strategy: "",
            interval: null,
            enable: false
          },
        },
        workGroup:[{id:-1,name:"Default"}]
      };
    },
    props:{
      node: {
        type: Object,
        default: () => {
          return {}
        }
      },
      graph: {
        type: Object,
        default: () => {
          return {}
        }
      }
    },
    //当传入的note id放生变化时
    watch:{
        'params.runFlag':{
          handler(newVal, oldVal) {
              let node = this.getNodes(this.node.tasks.name)
              if(newVal=="FORBIDDEN"){
                const model = {
                  banState:true
                };
                this.graph.update(node, model);
              }else{
                const model = {
                  banState:false
                };
                this.graph.update(node, model);
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
      if(null != this.node &&"tasks" in this.node &&this.node.tasks) {
        this.params = this.node.tasks
      }
    },
    methods: {
      getNodes(key) {
        let nodes = this.graph.getNodes();
        for (var i = 0; i < nodes.length; i++) {
          if (nodes[i]._cfg.model.tasks.name == key) {
            return nodes[i];
          }
        }
        return null
      },
      getChildParams() {
        return this.params;
      },
    }
  };
</script>
<style>
  .ant-form-item {
     margin-bottom: 4px!important;
  }
</style>

