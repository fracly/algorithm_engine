<template>
  <div class="test-connect-model">
    <a-row :gutter="24">
      <a-col :span="12">
        <a-card title="请求参数">
          <div class="content-left">
            <a-row>
              <a-col :md="8" :sm="8" style="margin-left: 5%">
                <h2 style="font-size: 16px">
                  <a-icon class="newTitle" type="line" :rotate="90" :style="{ fontSize: '16px', color: '#08c' }"/>系统参数</h2>
              </a-col>
            </a-row>
            <div class="inputs">
              <div class="input-param">
                <span>用户秘钥skey:</span>
                <a-input placeholder="系统分配" disabled/>
              </div>
              <div class="input-param">
                <span>用户Secret:</span>
                <a-input placeholder="系统分配" disabled/>
              </div>
              <div class="input-param">
                <span>服务令牌token:</span>
                <a-input placeholder="系统分配" disabled/>
              </div>
              <div class="input-param">
                <span>
                  时间戳timestamp:
                </span>
                <a-input placeholder="系统分配" disabled/>
              </div>

            </div>
            <a-row>
              <a-col :md="8" :sm="8" style="margin-left: 5%">
                <h2 style="font-size: 16px">
                  <a-icon class="newTitle" type="line" :rotate="90" :style="{ fontSize: '16px', color: '#08c' }"/>业务参数</h2>
              </a-col>
            </a-row>
            <a-table
              ref="table"
              size="default"
              :columns="columns"
              rowKey="param_variable"
              :dataSource="paramList"
              :pagination="false"
              :alert="{ show: true, clear: true }"
              :scroll="{y:'250px'}"
              style="border: 1px solid #E0E0E0; margin-left: 80px;margin-top: 20px"
            >
              <span slot="action" slot-scope="text, record">
                <template>
                  <a-input v-model="record.param_exam" :disabled="record.is_required==='2'"></a-input>
                </template>
              </span>

            </a-table>
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="返回结果">
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
    <div class="bottom-p">
      <a-button @click="_close">返回</a-button>
      <a-button
        style="margin-left: 8px;background: #00e064;border-color: #00e064;"
        type="primary"
        @click="_testConnect(true)">连接测试
      </a-button>
      <a-button style="margin-left: 8px" type="primary" @click="_onLine" v-if="info.status === '0'">上线</a-button>
      <a-button style="margin-left: 8px" type="primary" @click="_downLine" v-if="info.status === '1'">下线</a-button>

    </div>
  </div>

</template>
<script>
  // import _ from 'lodash'
  import $ from 'jquery'
  import { editServiceState, getServerInfo, getParam, getToken, testServer } from '@/api/service'

  export default {
    name: 'ServiceVerify',
    data () {
      return {
        // 表头
        columns: [
          {
            title: '参数名',
            dataIndex: 'param_variable'
          },
          // {
          //   title: '字段类型',
          //   dataIndex: 'param_type'
          // },
          // {
          //   title: '字段描述',
          //   dataIndex: 'param_des'
          // },
          {
            title: '连接符号',
            dataIndex: 'param_join'
          },
          {
            title: '值/示例',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' }
          }
        ],
        paramList: [],
        info: {},
        pageSize: '10',
        pageNum: '1'
      }
    },
    methods: {
      _close () {
        if (this.info.status === '0') {
          this.$router.push({ path: `/resource/service/edit/` + this.$route.params.id })
        } else {
          this.$router.push({ path: `/resource/service/view/` + this.$route.params.id })
        }
      },
      // 测试
      _testConnect () {
        $('#textarea-ft').val('正在测试,请稍后.....')
        getToken().then(res => {
          if (res) {
            const token = res.access_token
            const params = {}
            const param = {}
            for (let i = 0; i < this.paramList.length; i++) {
              if (this.paramList[i].is_required !== '2') {
                const a = this.paramList[i].param_variable
                param[a] = this.paramList[i].param_exam
              }
            }
            param.status = 1
            params.code = this.info.code
            params.token = token
            params.reqData = param
            params.seqId = Math.floor(Math.random() * 10000)
            params.timeStamp = new Date().getTime()
            testServer(params).then(resource => {
              debugger
              if (resource.code === 200) {
                let html = '正在测试,请稍后.....\n'
                html = html + '测试成功\n'
                html = html + '===================Request Info===================\n'
                html = html + 'method:POST\n'
                html = html + 'url:' + Glod.token + 'ms-consumer/encapsula/v1/service/' + this.info.code + '?token=' + token + '\n'
                html = html + '===================Request Info===================\n'
                html = html + JSON.stringify(resource.data)
                $('#textarea-ft').val(html)
              } else {
                let html = '正在测试,请稍后.....\n'
                html = html + '测试失败\n'
                html = html + '===================Request Info===================\n'
                html = html + 'method:POST\n'
                html = html + 'url:' + Glod.token + 'ms-consumer/encapsula/v1/service/' + this.info.code + '?token=' + token + '\n'
                html = html + '===================Request Info===================\n'
                html = html + JSON.stringify(resource.message)
                $('#textarea-ft').val(html)
              }
            }).catch(e => {
              if (e.code === 200) {
                let html = '正在测试,请稍后.....\n'
                html = html + '测试成功\n'
                html = html + '===================Request Info===================\n'
                html = html + 'method:POST\n'
                html = html + 'url:' + Glod.token + 'ms-consumer/encapsula/v1/service/' + this.info.code + '?token=' + token + '\n'
                html = html + '===================Respond Info===================\n'
                html = html + JSON.stringify(e)
                $('#textarea-ft').val(html)
              } else {
                let html = '正在测试,请稍后.....\n'
                html = html + '测试失败\n'
                html = html + '===================Request Info===================\n'
                html = html + 'method:POST\n'
                html = html + 'url:' + Glod.token + 'ms-consumer/encapsula/v1/service/' + this.info.code + '?token=' + token + '\n'
                html = html + '===================Request Info===================\n'
                html = html + JSON.stringify(e)
                $('#textarea-ft').val(html)
              }
            })
          } else {
            this.$message.error(res.msg)
          }
        }).catch(e => {
          this.$message.error(e.message)
        })
      },

      _getParams () {
        getParam({ id: this.$route.params.id, type: '0' }).then(res => {
          this.paramList = res.data
        }).catch(e => {
          this.$message.error(e.message)
        })
      },

      //
      _getInfo () {
        getServerInfo({ id: this.$route.params.id }).then(res => {
          this.info = res.data
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      /**
       * 上线
       */
      _onLine () {
        this._upProcessState({
          id: this.$route.params.id,
          status: 1
        })
      },
      /***
       *下线
       */
      _downLine () {
        this._upProcessState({
          id: this.$route.params.id,
          status: 0
        })
      },
      _upProcessState (o) {
        editServiceState(o).then(res => {
          if (o.status === 1) {
            this.$message.success('上线成功')
          } else if (o.status === 0) {
            this.$message.success('下线成功')
          }
          this.$router.push('/resource/service')
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      }
    },
    mounted () {
      this._getParams()
      this._getInfo()
    },

    components: {}
  }
</script>
<style lang="less" scoped>
  .test-connect-model {
    .content-left{
      width: 100%;
      height: 100%;
      min-height: 600px;
      .inputs {
        margin-bottom: 20px;
        .input-param {
          height: 50px;
          line-height: 50px;
          padding-left: 80px;
          span {
            width: 125px;
            display: inline-block;
          }
          input {
            height: 30px;
            width: 350px;
            outline-style: none;
            border: 1px solid #ccc;
            border-radius: 5px;
            padding-left: 9px;
          }
        }
      }
    }
    .content-right {
      width: 100%;
      height: 100%;
      background-color: #002a35;
      .textarea-ft {
        width: 100%;
        height: 600px;
        overflow: auto;
        background-color: #303030;
        border: 0;
        font-family: "Microsoft Yahei,Arial,Hiragino Sans GB,tahoma,SimSun,sans-serif";
        font-weight: 700;
        resize: none;
        line-height: 1.6;
        padding: 6px;
        color: #fff;
        font-size: 16px;
      }
    }

    .bottom-p {
      text-align: center;
      height: 72px;
      line-height: 72px;
      border-radius: 0 0 3px 3px;
      position: absolute;
      left: 56%;
      transform: translateX(-50%);
      bottom: 20px;
      top:810px
    }
  }
</style>
