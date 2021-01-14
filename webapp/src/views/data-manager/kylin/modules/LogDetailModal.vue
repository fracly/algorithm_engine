<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :closable="false"
    :confirmLoading="loading"
  >
    <template slot="footer">
      <a-button type="primary" @click="handleOk">确定</a-button>
    </template>
    <div class="log-model">
      <a-row :gutter="24" style="margin-top: 10px">
        <a-col :span="10">
          <a-card title="详细信息">
            <div class="content-left">
              <a-timeline style="margin-top:20px">
                <a-timeline-item
                  v-for="(item,index) in info.steps"
                  :key="item.id"
                  :value="item.id"
                  :color="item.step_status==='FINISHED'?'green':item.step_status==='ERROR'?'red':'grey'">
                  <a-icon
                    slot="dot"
                    type="check-circle"
                    theme="twoTone"
                    two-tone-color="#52c41a"
                    style="font-size: 16px;"
                    v-if="item.step_status==='FINISHED'"/>
                  <a-icon slot="dot" type="warning" style="font-size: 16px;" v-if="item.step_status==='ERROR'"/>
                  <a-icon slot="dot" type="more" :rotate="90" style="font-size: 16px;" v-if="item.step_status==='PENDING'"/>
                  <a-icon slot="dot" type="loading"  style="font-size: 16px;" v-if="item.step_status==='RUNNING'||item.step_status==='WAITING'"/>
                  <a-icon slot="dot" type="stop"  style="font-size: 16px;" v-if="item.step_status==='DISCARDED'"/>
                  {{ item.exec_start_time | moment }}<br>
                  <span>
                    #{{ index+1 }} 步骤名称:{{ item.name }}
                  </span><br>
                  <span>
                    数据大小:{{ item.info.hdfs_bytes_written }}
                  </span><br>
                  <span>
                    耗时:{{ ((item.exec_end_time-item.exec_start_time)/60000).toFixed(2) }}mins
                  </span><br>
                  <span>
                    等待时间:{{ item.exec_wait_time }}
                  </span><br>
                  <span>
                    <a-icon type="file-text" @click="showLog(item.id,index)" v-if="item.step_status!=='PENDING'"/>
                  </span>
                </a-timeline-item>
              </a-timeline>
            </div>
          </a-card>
        </a-col>
        <a-col :span="14">
          <a-card :title="'第'+step+'步日志'">
            <div class="content-right">
              <textarea
                id="textarea-ft"
                class="textarea-ft"
                spellcheck="false"
              ></textarea>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

<script>
  import { queryStepLog } from '@/api/kylin'
  import $ from 'jquery'
  export default {
    name: 'LogDetailModal',
    data () {
      return {
        visible: false,
        loading: false,
        title: '日志',
        info: {
          name: '',
          cubeId: '',
          steps: '',
          projectName: '',
          status: '',
          duration: ''
        },
        step: ''
      }
    },
    methods: {
      _close () {
        this.$router.push({ path: `/manager/kylin/log/` + this.$route.params.name })
      },
      edit (item) {
        debugger
        this.visible = true
        this.info = item
        this.title = item.name + '日志'
      },
      handleOk () {
        this.visible = false
      },
      handleCancel () {
        this.visible = false
      },
      showLog (stepId, index) {
        queryStepLog({ id: this.info.cubeId, stepId: stepId }).then(res => {
          $('#textarea-ft').val(res.data.cmd_output)
          this.step = index + 1
        })
      }
    },
    mounted () {
    }
  }
</script>

<style lang="less" >
  .log-model {
    .content-left{
      width: 100%;
      height: 100%;
      max-height: 500px;
      overflow: auto;
    }
    .content-right {
      width: 100%;
      height: 100%;
      max-height: 500px;
      .textarea-ft {
        width: 100%;
        min-height: 500px;
        overflow: auto;
        border: 0;
        font-family: "Microsoft Yahei,Arial,Hiragino Sans GB,tahoma,SimSun,sans-serif";
        font-weight: 700;
        resize: none;
        line-height: 1.6;
        padding: 6px;
        color: #303030;
        font-size: 12px;
      }
    }

    /deep/ .ant-descriptions-item-label{
      font-size: 8px!important;
    }
    /deep/ .ant-descriptions-item-content{
      font-size: 8px!important;
    }
    /deep/ .ant-badge-status-text{
      font-size: 8px!important;
    }
  }
</style>
