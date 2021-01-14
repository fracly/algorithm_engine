<template>
  <div class="card-container" :style="styleObject">
    <a-tabs type="card" :tabBarGutter="0" :activeKey="defaultActiveKey" @change="changeTab">
      <a-tab-pane key="0" tab="模型列表" :forceRender="true">
        <process-tree @resetTopo="resetTopo" @loadTopo="loadTopo" @loadPage="loadPage" ref="itemChild" :height="height" :module="module" class="tree"></process-tree>
      </a-tab-pane>
      <a-tab-pane key="1" tab="模型组件">
        <compenent-tree :height="height" :page="page" :command="command" class="tree"></compenent-tree>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script>
  import Item from "./item";
  /*组件菜单地址*/
  import CompenentTree from "@/views/model-center/model-factory/process/CompenentTree";
  /*任务菜单地址*/
  import ProcessTree from "@/views/model-center/model-factory/process/ProcessTree";
  import eventBus from "@/components/G6Editor/utils/eventBus";

  export default {
    components: {Item, CompenentTree, ProcessTree},
    data() {
      return {
        defaultActiveKey:'0',
        page: null,
        command: null,
        styleObject: {
          height: this.height - 40 + "px"
        }
      };
    },
    created() {
      this.bindEvent();
    },
    props: {
      height: {
        type: Number,
        default: 0
      },
      module: {
        type: Object,
        default: () => {
        }
      },
    },
    methods: {
      resetTopo(processId){
        this.$emit('resetTopo',processId)
      },
      changeTab(key){
        this.defaultActiveKey=key
      },
      changeTree(workType) {
        this.defaultActiveKey='0';
      },
      bindEvent() {
        eventBus.$on("afterAddPage", page => {
          this.page = page;
          this.command = page.command;
          this.loadPage(page)
        });
      },
      //重新加载树
      loadTree() {
        this.$refs.itemChild.loadTree();
      },
      loadTopo(data,readOnly) {
        this.$emit('loadTopo', data,readOnly);
        //this.defaultActiveKey='1'
      },
      /*未知bug 父页面以及部分子组件无法获取eventBus 的数据 修改为父组件传递参数的形式*/
      loadPage(page) {
        this.page = page;
        this.command = page.command;
      }
    }
  };
</script>

<style lang="less" scoped>
  /deep/.ant-tabs-bar{
    margin: 0 0 0 0;
  }

  .tree{
    border-right: 1px solid #e8e8e8;
    border-bottom: 1px solid #e8e8e8;
  }

  .ant-tabs-nav {
    position: relative;
    display: inline-block;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    margin: 0;
    padding-left: 0;
    list-style: none;
    -webkit-transition: -webkit-transform .3s cubic-bezier(.645,.045,.355,1);
    transition: -webkit-transform .3s cubic-bezier(.645,.045,.355,1);
    transition: transform .3s cubic-bezier(.645,.045,.355,1);
    transition: transform .3s cubic-bezier(.645,.045,.355,1),-webkit-transform .3s cubic-bezier(.645,.045,.355,1);
  }

  .card-container {
    /deep/.ant-tabs.ant-tabs-card .ant-tabs-card-bar {
      .ant-tabs-tab{
        border-radius: 0px;
        margin-left: -1px;
      }
      .ant-tabs-tab-active {
        border-bottom:  solid 2px #0070cc;
        border-radius: 0;
        color: #0070cc;
      }
    }
  }
  /*.card-container {
    background-color: #f7f9fb;;

    /deep/ .ant-tabs.ant-tabs-card .ant-tabs-card-bar {
      .ant-tabs-tab {
        background: #f0f0f0;
        border: 1px solid rgb(221, 221, 221);
        border-bottom: 3px solid rgb(221, 221, 221);
      }

      .ant-tabs-tab-active {
        height: 40px;
        color: rgb(73, 155, 219);
        border-bottom: 1px solid rgb(249, 249, 249);
        border-top: 3px solid rgb(0, 112, 205);
        background-color: rgb(249, 249, 249);
        border-radius: unset;
      }
    }
  }*/

  .itempannel {
    height: 100%;
    left: 0px;
    z-index: 2;
    background: #f7f9fb;
    min-height: 100vh;
    /*width: 300px;*/
    /*padding-top: 8px;*/
    border-right: 1px solid #e6e9ed;
  }
</style>
