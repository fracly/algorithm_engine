<template>
  <a-card :bordered="false">
    <div class="information">
      <div class="extra">
        <a-row>
          <a-col :span="4" :offset="5">报表名称：</a-col>
          <a-col :span="8">{{ reportInfo.name }}</a-col>
        </a-row>
        <a-row>
          <a-col :sm="4" :offset="5">报表编码：</a-col>
          <a-col :sm="8">{{ reportInfo.code }}</a-col>
        </a-row>
        <a-row>
          <a-col :sm="4" :offset="5">报表分组：</a-col>
          <a-col :sm="8">{{ groupName }}</a-col>
        </a-row>
        <a-row>
          <a-col :sm="4" :offset="5">查询权限：</a-col>
          <a-col :sm="8">{{ roleName }}</a-col>
        </a-row>
        <a-row>
          <a-col :sm="4" :offset="5">SQL：</a-col>
          <a-col :sm="13">
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
              <a-button type="primary" ghost>
                查看内容
              </a-button>
            </a-popover>
          </a-col>
        </a-row>
<!--        <a-row>-->
<!--          <a-col :sm="4" :offset="5">查询参数：</a-col>-->
<!--          <a-col-->
<!--            :sm="8"-->
<!--            :offset="i==0?0:9"-->
<!--            v-for="(p,i) in reportInfo.param"-->
<!--            :key="i">-->
<!--            <a-input-->
<!--              style="width: 20%"-->
<!--              v-model="p.name"-->
<!--              disabled>-->
<!--            </a-input>-->
<!--            <a-input-->
<!--              style="width: 35%;margin-left: 10px"-->
<!--              v-model="p.desc"-->
<!--              disabled>-->
<!--            </a-input>-->
<!--            <a-select-->
<!--              style="width: 35%;margin-left: 10px"-->
<!--              v-model="p.type"-->
<!--              disabled-->
<!--            >-->
<!--              <a-select-option-->
<!--                v-for="g in typeList"-->
<!--                :key="g.code"-->
<!--                :value="g.code">{{ g.name }}-->
<!--              </a-select-option>-->
<!--            </a-select>-->
<!--          </a-col>-->
<!--        </a-row>-->
        <a-row>
          <a-col :sm="4" :offset="5">报表地址：</a-col>
          <a-col :sm="12">{{ url }}</a-col>
        </a-row>
        <a-row>
          <a-col :sm="4" :offset="5">报表表头：</a-col>
          <a-col :sm="8">
            <a-button type="primary" ghost icon="cloud-download" @click="download">
              下载
            </a-button>
            <span style="margin-left: 10px">{{excel}}</span>
          </a-col>
        </a-row>
      </div>
      <div class="ant-result-extra">
        <a-button key="back" @click="toOrderList">返回</a-button>
        <a-button type="primary" @click="showReport">预览</a-button>
      </div>
    </div>
  </a-card>

</template>

<script>
  import { Result } from '@/components'
  import { getGroupsList, getRolesList, getReportInfo } from '@/api/report'
  import codemirror from '@/utils/codemirror'
  import { downloadFile } from '@/utils/download'

  let editor

  export default {
    name: 'ShowView',
    data () {
      return {
        loading: false,
        groupName: '',
        roleName: '',
        url: '',
        title: '报表信息',
        reportInfo: {},
        visible: false,
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 10 }, sm: { span: 10 } },
        excel: '',
        typeList: [{ name: '文本', code: 'input' }, { name: '下拉框', code: 'select' }, { name: '时间', code: 'time' }, {
          name: '树',
          code: 'tree'
        }, { name: '二级下拉框', code: 's-select' }, { name: '车牌号', code: 'carplate' }, { name: '机构类型', code: 'department' }]
      }
    },
    components: { Result },
    methods: {
      toOrderList () {
        this.$router.push('/report/custom')
      },
      showReport () {
        this.$router.push({ path: `/report/custom/preview/` + this.reportInfo.code })
      },
      groupList () {
        const self = this
        getGroupsList().then(res => {
          for (const i in res.data) {
            if (res.data[i].id === self.reportInfo.groupId) {
              self.groupName = res.data[i].name
            }
          }
        })
      },
      roleList () {
        const self = this
        getRolesList().then(res => {
          const role = []
          for (const i in res.data) {
            if (self.reportInfo.roleId.indexOf(res.data[i].id) !== -1) {
              role.push(res.data[i].name)
            }
          }
          self.roleName = role.toString()
        })
      },
      /**
       * 处理代码高亮显示
       */
      handlerEditor () {
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
        editor.setValue(this.reportInfo.sql)
        return editor
      },
      // 显示sql
      showSql (v) {
        this.visible = v
        this.$nextTick(() => {
          setTimeout(() => {
            if (v) {
              setTimeout(() => {
                this.handlerEditor().refresh()
              }, 100)
            } else {
              editor.toTextArea() // Uninstall
              editor.off(('.code-sql'), 'keypress', this.keypress)
            }
          }, 16)
        })
      },
      // 下载
      download () {
        downloadFile('/escheduler/reportFrom/download', {
          name: this.reportInfo.path.split('/')[this.reportInfo.path.split('/').length - 1]
        })
      }
    },
    created () {
      const self = this
      if (this.$route.params.id) {
        getReportInfo({ id: this.$route.params.id }).then(res => {
          self.reportInfo = res.data
          self.groupList()
          self.roleList()
          self.url = Glod.dsjmh + '/#/customReport/' + self.reportInfo.code + '?glbmbh=&class='
          self.excel = self.reportInfo.origin_file_name
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
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
    width: 800px;
    position: relative;

    .CodeMirror {
      min-height: 72px;
      width: 100%;
      border: 1px solid #ddd !important;
      border-radius: 3px;
    }

    .CodeMirror-scroll {
      min-height: 72px;
      width: 100%;
    }

    .CodeMirror-gutters {
      border-left: none;
    }
  }
</style>
