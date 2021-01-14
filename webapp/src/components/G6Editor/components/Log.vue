<template>
    <div style="height: 480px;overflow-y: auto">
        <div v-for="item in LogList">
          <span style="display:block; float:left; width:45px;">
            <span v-show="item.type=='start'"  style="color:#54CCEF;width: 50px">[&nbsp;&nbsp;&nbsp;INFO&nbsp;&nbsp;&nbsp;]</span>&nbsp;&nbsp;&nbsp;
            <span v-show="item.type=='end' && item.state!='FAILURE' && item.state!='PARAMS_ERROR'" style="color:#54CCEF;width: 50px">[&nbsp;&nbsp;&nbsp;INFO&nbsp;&nbsp;&nbsp;]</span>
            <span v-show="item.type=='end' && (item.state=='FAILURE' || item.state=='PARAMS_ERROR')" style="color:#DE604B;width: 50px">[ERROR]</span>
          </span>
          <span style="display:block; float:left; width:120px;">{{item.time}}</span>&nbsp;


          <span v-show="item.type=='start'">节点：{{item.nodeName}},开始执行</span>&nbsp;
          <span  v-show="item.type=='end' && item.state!='FAILURE' && item.state!='PARAMS_ERROR' && item.state!='STOP' && item.state!='KILL'" >节点：{{item.nodeName}},执行成功</span>
          <span  v-show="item.type=='end' && (item.state=='STOP' || item.state=='KILL')">节点：{{item.nodeName}},停止执行</span>
          <span  v-show="item.type=='end' && (item.state=='FAILURE' || item.state=='PARAMS_ERROR')">节点：{{item.nodeName}},执行失败<br /></span>
          <span style=""  v-show="item.type=='end' && item.state=='PARAMS_ERROR'">
            <span  style="display:block; float:left; width:45px;color:#DE604B;">[ERROR]</span>
            参数校验失败：{{item.resultTable}}</span>&nbsp;
          </div>
    </div>
</template>

<script>

    export default {
        data() {
            return {
                node: {},

            };
        },
        props:{
          LogList: {
            type: Array,
            default: () => {
            }
          },
        },
        created() {
        },
        methods: {
        }
    };
</script>

<style>
    .context-menu {

        border: 1px solid #e4e7ed;
        border-radius: 4px;
        background-color: #fff;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        box-sizing: border-box;
        margin: 5px 0;
        z-index: 1;
        display: none;
    }

    .context-menu li {
        cursor: pointer;
        font-size: 12px;
        height: 28px;
        line-height: 28px;
    }

    .context-menu li:hover {
        background-color: #f5f7fa;
    }
</style>
