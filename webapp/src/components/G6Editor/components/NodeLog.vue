<template>
  <a-modal
    :visible="visible"
           @cancel="handleCancelLog" :width="width" cancelText="关闭" :closable="closable" :centered="centered">
    <template slot="title">
      <a-row>
        <a-col :span="12" style="font-size: 16px">
          节点日志
        </a-col>
        <a-col :span="12" style="text-align: right;">
         <!-- <a-tooltip>
            <template slot="title">
            下载日志
            </template>
            <a-icon type="download"  style="color: #4AABE5"/>
          </a-tooltip>-->

          <a-tooltip style="margin-left: 15px" v-show="!spinning">
            <template slot="title">
              刷新日志
            </template>
            <a-icon type="redo" style="color: #4AABE5;" @click="loadLog"/>
          </a-tooltip>
          <a-tooltip style="margin-left: 15px"  v-show="spinning">
            <template slot="title">
              请求中
            </template>
            <a-icon type="loading" style="color: #4AABE5;"/>
          </a-tooltip>
          <a-tooltip style="margin-left: 15px;margin-right: 10px" v-show="!FullScreen">
            <template slot="title">
              进入全屏
            </template>
            <a-icon @click="fullScreen" type="fullscreen" style="color: #4AABE5;"/>
          </a-tooltip>
          <a-tooltip style="margin-left: 15px;margin-right: 10px" v-show="FullScreen">
            <template slot="title">
              取消全屏
            </template>
            <a-icon @click="fullScreen" type="fullscreen-exit" style="color: #4AABE5;"/>
          </a-tooltip>

        </a-col>
      </a-row>
    </template>

    <template slot="footer">
      <a-button key="back" @click="handleCancelLog">
        关闭
      </a-button>
    </template>
    <a-spin tip="日志请求中" :spinning="spinning">
      <div  :style="{height: height}" style=";overflow-y: auto; background-color: #002A35;font-size: 14px;">
        <p style="color:#9CABAF;padding: 5px" class="text-wrapper">
          {{log}}
        </p>
      </div>
    </a-spin>

  </a-modal>


</template>

<script>
    import {logdetail} from '@/api/process'
    export default {
        data() {
            return {
                centered:true,
                spinning:false,
                closable:false,
                title: ['<div><img className={styles.picture} src={zy}></img>警告信息</div>'],
                node: {},
                visible:true,
                log:"",
                width:'700px',
                height:'400px',
                FullScreen:false,
                labelCol: { span: 9 },
                wrapperCol: { span: 15 },
            };
        },
        props:{
          taskInstId:{
            type: Number,
            default: () => 0
          },
        },
        created() {
          if(this.taskInstId!=0){
           this.loadLog()
          }
        },
        methods: {
          loadLog(){
            this.spinning=true;
            let _that=this
            logdetail({"taskInstId":_that.taskInstId,"skipLineNum":0,"limit":10000}).then(res => {
              this.spinning=false;
              this.log=res.data
            })
          },
          fullScreen(){
            this.FullScreen =!this.FullScreen
            if(this.FullScreen){
               this.width = (document.body.clientWidth-80) +"px"    // 屏幕宽
               this.height = (document.body.clientHeight-150) +"px"    // 屏幕高\
            }else{
              this.width="700px"    // 屏幕宽
              this.height = "400px"
            }
          },
          handleCancelLog(){
            this.$emit("handleCancelLog")
          }
        }
    };
</script>

<style scoped>
  .text-wrapper {
    white-space: pre-wrap;
  }

    /deep/.ant-modal-body{
        padding: 0px!important;
    }
    /deep/.ant-modal-header {
      padding: 16px 16px;
    }

    /deep/.anticon svg {
      font-size: 16px!important;
    }

    svg{
      font-size: 16px;
    }
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
