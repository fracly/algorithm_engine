<template>
  <a-form layout="inline">
    <a-row :gutter="16" >
      <a-col >
        <a-card title="" :bordered="false" style="margin-bottom: -30px">
          <a-col :span="1" style="margin-top: -8px;" >
            <span class="icon iconfont" style="font-size: 50px;">&#xe67d;</span>
          </a-col>
          <a-col :span="20" style="padding-left: 18px"><h1 style="font-size: 18px;">{{ service.name }}</h1>
            <p>{{ url }}</p></a-col>

        </a-card>
      </a-col>
    </a-row>
    <a-row >
      <a-col :span="8">
        <a-card title="" :bordered="false">
          <a-collapse :activeKey="1" class="collapseStyle" style="border-radius: 0px">
            <a-collapse-panel key="1" header="API基本信息">
              <a-row>
                <a-col :sm="6" :offset="2">服务ID：</a-col>
                <a-col :sm="10" >{{ service.id }}</a-col>
              </a-row>
              <a-row>
                <a-col :span="6" :offset="2">服务名称：</a-col>
                <a-col :span="10">{{ service.name }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">服务编码：</a-col>
                <a-col :sm="10" >{{ service.code }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">服务描述：</a-col>
                <a-col :sm="10" >{{ service.desc }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">服务类型：</a-col>
                <a-col :sm="10" >{{ service.type }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">开发模式：</a-col>
                <a-col :sm="10" >{{ service.mode== '0'?'单表模式':"SQL模式" }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">创建人：</a-col>
                <a-col :sm="10" >{{ service.createUser }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">创建时间：</a-col>
                <a-col :sm="10" >{{ service.createTime| moment }}</a-col>
              </a-row>
            </a-collapse-panel>
          </a-collapse>
          <a-collapse :activeKey="1" style="margin-top: 20px;border-radius: 0px" class="collapseStyle">
            <a-collapse-panel key="1" header="接口信息">
              <a-row>
                <a-col :span="6" :offset="2">调用地址：</a-col>
                <a-col :span="12">{{ url }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">请求方式：</a-col>
                <a-col :sm="10" >POST</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">返回类型：</a-col>
                <a-col :sm="10" >JSON</a-col>
              </a-row>
            </a-collapse-panel>
          </a-collapse>
          <a-collapse :activeKey="1" style="margin-top: 20px;border-radius: 0px" class="collapseStyle">
            <a-collapse-panel key="1" header="数据源信息">
              <a-row>
                <a-col :sm="6" :offset="2">类型：</a-col>
                <a-col :sm="10" >{{ service.datasourceType }}</a-col>
              </a-row>
              <a-row>
                <a-col :sm="6" :offset="2">数据源：</a-col>
                <a-col :sm="10" >{{ service.datasourceId }}</a-col>
              </a-row>
              <a-row v-show="service.mode==='0'">
                <a-col :sm="6" :offset="2">表名：</a-col>
                <a-col :sm="10" >{{ service.table }}</a-col>
              </a-row>
              <a-row v-show="service.mode==='1'">
                <a-col :sm="6" :offset="2">SQL语句：</a-col>
                <a-col :sm="10" >
                  <a-popover
                    title="SQL详细"
                    trigger="click"
                    placement="bottom"
                    :arrowPointAtCenter="true"
                    @visibleChange="(visible)=>showSql(visible)">
                    <template slot="content">
                      <div class="from-mirror">
                        <a-textarea
                          id="report-sql"
                          name="code-sql"
                          style="opacity: 0;"/>
                      </div>
                    </template>
                    <a-button type="primary" ghost size="small" >
                      查看内容
                    </a-button>
                  </a-popover>
                </a-col>
              </a-row>
            </a-collapse-panel>
          </a-collapse>

        </a-card>
      </a-col>
      <a-col :span="15" style="margin-left: -20px">
        <a-card title="" :bordered="false">
          <a-collapse :activeKey="1" class="collapseStyle" style="border-radius: 0px">
            <a-collapse-panel key="1" header="请求参数">
              <a-row>
                <a-col :sm="4" ></a-col>
                <a-col :sm="24">
                  <a-table
                    ref="table"
                    size="middle"
                    :columns="columns"
                    rowKey="param_variable"
                    :dataSource="paramList"
                    :pagination="false"
                    :bordered="true"
                    class="tableStyle"

                  >
                    <span slot="action" slot-scope="text">
                      {{ text | isFilter }}
                    </span>
                  </a-table>
                </a-col>
              </a-row>
            </a-collapse-panel>
          </a-collapse>
          <a-collapse :activeKey="1" style="margin-top: 20px;border-radius: 0px" class="collapseStyle">
            <a-collapse-panel key="1" header="返回参数">
              <a-row>
                <a-col :sm="4" ></a-col>
                <a-col :sm="24">
                  <a-table
                    ref="table"
                    size="middle"
                    :columns="columns2"
                    rowKey="param_name"
                    :dataSource="paramList2"
                    :pagination="false"
                    :bordered="true"
                    class="tableStyle"
                  >
                  </a-table>
                </a-col>
              </a-row>
            </a-collapse-panel>
          </a-collapse>
          <a-collapse :activeKey="1" style="margin-top: 20px;border-radius: 0px" class="collapseStyle">
            <a-collapse-panel key="1" header="参数报文样例" >
              <h1>入参报文样例</h1>
              <a-row>
                <a-col :sm="4" ></a-col>
                <a-col :sm="24">
                  <div class="from-mirror">
                    <a-textarea
                      id="in-sql"
                      name="code-sql"
                      style="opacity: 0;"/>
                  </div>
                </a-col>
              </a-row>
<!--              <h1 style="color: red;margin-top: 5px">入参分页注释：</h1>-->
              <span >注：返回结果超过500条，必须传分页参数(<b style="color: red">pageSize,pageNum</b>），否则默认只返回15条数据；</span>
              <h1 style="margin-top: 5px">出参报文样例</h1>
              <a-row>
                <a-col :sm="4" ></a-col>
                <a-col :sm="24">
                  <div class="from-mirror">
                    <a-textarea
                      id="out-sql"
                      name="code-sql"
                      style="opacity: 0;"/>
                  </div>
                </a-col>
              </a-row>
              <span >注：(<b style="color: red">pageSize,pageNum,totalPage,totalCount</b>）只有分页才会返回字段</span>
            </a-collapse-panel>
          </a-collapse>

        </a-card>
      </a-col>
    </a-row>
    <a-row :gutter="16" >
      <a-col :span="8" push="8">
        <a-button @click="toOrderList" style="margin-bottom: 20px">返回</a-button>
      </a-col>
    </a-row>

  </a-form>
</template>

<script>
  import { Result } from '@/components'
  import { getServerInfo, getParam, editServiceState, getTypeList } from '@/api/service'
  import { getDataSourceList } from '@/api/datasource'
  import _ from 'lodash'
  import codemirror from '@/utils/codemirror'

  let editor
  let editor2

  export default {
    name: 'ShowView',
    data () {
      return {
        loading: false,
        groupName: '',
        roleName: '',
        url: '',
        title: '服务信息',
        service: {},
        visible: false,
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 10 }, sm: { span: 10 } },
        paramList: [],
        dataSourceList: [],
        // 表头
        columns: [
          {
            title: '参数名',
            dataIndex: 'param_variable'
          },
          {
            title: '字段类型',
            dataIndex: 'param_type'
          },
          {
            title: '必填/常量',
            dataIndex: 'is_required',
            scopedSlots: { customRender: 'action' }
          },
          {
            title: '字段描述',
            dataIndex: 'param_des'
          },
          {
            title: '值/示例',
            dataIndex: 'param_exam'
          }
        ],
        paramList2: [],
        // 表头
        columns2: [
          {
            title: '参数名',
            dataIndex: 'param_name'
          },
          {
            title: '字段类型',
            dataIndex: 'param_type'
          },
          {
            title: '字段描述',
            dataIndex: 'param_des'
          }
        ],
        inParam: '',
        outParam: '',
        serviceTypeList: []
      }
    },
    components: { Result },
    methods: {
      toOrderList () {
        this.$router.push('/resource/service')
      },
      testConnect () {
        this.$router.push('/resource/service/verify/' + this.$route.params.id)
      },
      // 获取参数列表
      getParams () {
        getParam({ id: this.$route.params.id, type: '0' }).then(res => {
          this.paramList = res.data
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 获取
      getParams2 () {
        getParam({ id: this.$route.params.id, type: '1' }).then(res => {
          this.paramList2 = res.data
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      getInfo () {
        const self = this
        getServerInfo({ id: this.$route.params.id }).then(res => {
          self.service = res.data
          const inP = JSON.parse(res.data.inParam ? res.data.inParam : '{}')
          if (JSON.stringify(inP) === '{}') {

          } else {
            inP.reqData.pageNum = 1
            inP.reqData.pageSize = 15
          }
          const outP = JSON.parse(res.data.outParam ? res.data.outParam : '{}')
          const dataList = {}
          if (JSON.stringify(inP) === '{}') {
          } else {
            dataList.list = outP.data
            dataList.totalPage = ''
            dataList.totalCount = ''
            dataList.pageNum = ''
            dataList.pageSize = ''
            outP.data = dataList
          }
          self.url = Glod.token + 'ms-consumer/encapsula/v1/service/' + self.service.code + '?token='
          self.inParam = JSON.stringify(JSON.parse(JSON.stringify(inP)), null, 4)
          self.outParam = JSON.stringify(JSON.parse(JSON.stringify(outP)), null, 4)
          self.getDataSourceListByType(self.service.datasourceType)
          getTypeList({ code: '01' }).then(res => {
            for (let i = 0; i < res.data.length; i++) {
             if (res.data[i].code === self.service.type) {
               self.service.type = res.data[i].name
             }
            }
          })

          self.$nextTick(() => {
            setTimeout(() => {
              this.handlerEditor().refresh()
              this.handlerEditor2().refresh()
            }, 100)
          })
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      getDataSourceListByType (value) {
        const self = this
        getDataSourceList({ type: value }).then(res => {
          self.dataSourceList = res.data
          self.service.datasourceId = _.find(self.dataSourceList, { 'id': self.service.datasourceId }).name
        })
      },
      /**
       * 处理代码高亮显示
       */
      handlerEditor () {
        // editor
        editor = codemirror('out-sql', {
          mode: 'python',
          readOnly: true
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
        editor.setValue(this.outParam)
        return editor
      },
      handlerEditor2 () {
        // editor
        editor2 = codemirror('in-sql', {
          mode: 'python',
          readOnly: true
        })
        this.keypress = () => {
          if (!editor2.getOption('readOnly')) {
            editor2.showHint({
              completeSingle: false
            })
          }
        }
        // Monitor keyboard
        editor2.on('keypress', this.keypress)
        editor2.setValue(this.inParam)
        return editor2
      },
      handlerEditor3 () {
        // editor
        editor = codemirror('report-sql', {
          mode: 'sql',
          readOnly: true
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
        editor.setValue(this.service.sql)
        return editor
      },
      // 显示sql
      showSql (v) {
        this.visible = v
        this.$nextTick(() => {
          setTimeout(() => {
            if (v) {
              setTimeout(() => {
                this.handlerEditor3().refresh()
              }, 100)
            } else {
              editor.toTextArea() // Uninstall
              editor.off(('.code-sql'), 'keypress', this.keypress)
            }
          }, 16)
        })
      },
      /***
       *下线
       */
      downLine () {
        this.upProcessState({
          id: this.$route.params.id,
          status: 0
        })
      },
      upProcessState (o) {
        editServiceState(o).then(res => {
          if (o.status === 1) {
            this.$message.success('上线成功')
          } else if (o.status === 0) {
            this.$message.success('下线成功')
          }
          this._getInfo()
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      }
    },
    created () {
      this.getParams()
      this.getParams2()
      this.getInfo()
    },
    filters: {
      isFilter (i) {
        if (i === '1') {
          return '是'
        } else if (i === '0') {
          return '否'
        } else {
          return '常量'
        }
      }
    },
    mounted () {
    }

  }
</script>
<style lang="less">
  .information {
    line-height: 22px;
    margin: 20px 200px 0 0;

    .title {
      font-size: 24px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
      line-height: 32px;
      margin-bottom: 16px;
      text-align: center;

      svg {
        font-size: 24px
      }
    }

    .extra {
      background: #fff;
      .ant-row {
        padding-bottom: 10px;
      }

      .ant-col {
        font-size: 16px;
        line-height: 32px;
      }
    }
  }

  .from-mirror {
    /*width: 500px;*/
    max-height: 200px;
    .CodeMirror {
      border: 1px solid #ddd !important;
      border-radius: 3px;
      max-height: 200px;
    }
  }
  .tableStyle {
    /deep/ .ant-table-thead > tr > th, .ant-table-tbody > tr > td {
      padding: 10px !important;
    }
  }
  .collapseStyle{
    /deep/ .ant-collapse-header{
      padding: 5px!important;
      padding-left: 40px!important;
    }
   /deep/ .ant-collapse-item:last-child > .ant-collapse-content {
      border-radius: 0
   }
  }

</style>
