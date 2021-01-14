<template>
  <div>
    <a-form :form="form" class="step-dataS">
      <a-form-item
        label="数据源类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-select
          placeholder="请选择数据源类型"
          v-decorator="['dataSourceType', { rules: [{required: true, message: '数据源类型必须选择'}] }]"
          @change="handleDataSourceTypeChanged"
        >
          <a-select-option
            v-for="(item) in dataSourceTypeList"
            :key="item.code"
            :value="item.code">{{ item.code }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="数据源"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-select
          placeholder="请选择数据源"
          v-decorator="['dataSourceId', { rules: [{required: true, message: '数据源必须选择'}] }]"
        >
          <a-select-option
            v-for="(item) in dataSourceList"
            :key="item.id"
            :value="item.id">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="SQL"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <div class="from-mirror">
          <a-input
            type="hidden"
            v-decorator="['sql', {rules: [{required: true, message: 'SQL语句不能为空'}] }]"
          ></a-input>
          <a-textarea
            id="code-sql"
            name="code-sql"
            style="opacity: 0;"/>
          <a-button type="primary" @click="_parse" style="position: absolute;bottom: 10px;right: -15%;">解析SQL</a-button>
        </div>
      </a-form-item>
      <a-form-item label="相同值合并列" :labelCol="labelCol" :wrapperCol="wrapperCol">
        <div>
          <a-button type="primary" style="width: 80px;" @click="showChooseFiled">
            选择
          </a-button>
          <span v-show="mergeColList.length>0" style="margin-left: 20px">
              <a-button style="margin-left: 5px" v-for="(item) in mergeColList" :key="item">
                {{item}}
              </a-button>
             </span>
        </div>
        <a-input
          v-show="false"
          v-decorator="['margeCol']"/>
        <!--字段选择模态框-->
        <a-modal :visible="mergeCol" title="自定义列" @cancel="showChooseFiled" width="850px">
          <template slot="footer">
            <a-button key="submit" type="primary" @click="saveChooseFiled">
              确定
            </a-button>
            <a-button key="back" @click="showChooseFiled">
              返回
            </a-button>
          </template>
          <a-col>
            <a-table
              :columns="columnsTemp"
              :data-source="outputList"
              rowKey="name"
              :pagination="false"
              size="small"
              bordered
              :scroll="{y:400}">
              <span slot="merge" slot-scope="text,record">
                <template>
                  <a-checkbox :defaultChecked="record.is_merge" @change="onChange($event,record)"></a-checkbox>
                </template>
              </span>
            </a-table>
          </a-col>

        </a-modal>
      </a-form-item>

      <div class="step-form-style-desc">
        <h4>说明</h4>
        <p>1、建议宏变量名=“p_"+字段名；<br>2、时间段区间查询，where条件里，直接写“=”，在查询条件里配置查询粒度，选择按某区间查询；</p>
      </div>
      <a-divider orientation="left">查询条件配置</a-divider>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-for="(p,i) in params"
        :key="i"
        class="stepFormText"
        style="margin-left: 20%"
      >
        <a-input
          style="width: 20%"
          v-model="p.name"
          disabled>
        </a-input>
        <a-input
          style="width: 30%;margin-left: 10px"
          v-model="p.desc"
          placeholder="请输入中文描述">
        </a-input>
        <a-select
          style="width: 40%;margin-left: 10px"
          placeholder="请选择参数类型"
          @change="((val)=>{_changeType(val, i)})"
          v-model="p.type"
        >
          <a-select-option
            v-for="g in typeList"
            :key="g.code"
            :value="g.code">{{ g.name }}
          </a-select-option>
        </a-select>
        <a style="color: #2d8cf0;cursor: pointer;padding-left: 5px" @click="_showParams(i)">
          <a-icon type="down" v-show="p.isShow"/>
          <a-icon type="right" v-show="!p.isShow"/>
        </a>
        <div
          class="step-form-dict-model"
          v-if="p.type==='select'||p.type==='s-select'
            ||p.type==='tree'||p.type==='department'"
          v-show="p.isShow">
          <a-radio-group
            v-model="p.value"
            style="width: 100%"
          >
            <a-table
              ref="table"
              size="default"
              :columns="columns"
              rowKey="dictName"
              :dataSource="dictList"
              :pagination="false"
              style="border: 1px solid #E0E0E0;"
            >
              <span slot="serial" slot-scope="text, record">
                <a-radio :value="record.dictName"></a-radio>
              </span>
            </a-table>
          </a-radio-group>
        </div>
        <div
          class="step-form-time-model"
          v-if="p.type=='time'"
          v-show="p.isShow">
          <a-form-item
            label="时间格式"
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
          >
            <a-select
              placeholder="请选择时间格式"
              v-model="p.value.dataFormat"
              @change="((val)=>{_handleTimeChange(val, p)})"
            >
              <a-select-option
                v-for="(t) in p.timeList"
                :key="t"
                :value="t">{{ t }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            class="stepFormText"
            label="查询粒度">
            <a-checkbox-group :options="p.grainList" v-model="p.value.queryGrain">
            </a-checkbox-group>
          </a-form-item>
        </div>
      </a-form-item>
      <a-form-item :wrapperCol="{span: 8, offset: 10}">
        <a-button @click="prevStep">上一步</a-button>
        <a-button style="margin-left: 8px" :loading="loading" type="primary" @click="nextStep">下一步</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  import { getDataSourceList } from '@/api/datasource'
  import { getDictList, createReport, reportExcel } from '@/api/report'
  import codemirror from '@/utils/codemirror'
  import _ from 'lodash'
  import { getAnalysisSql } from '@/api/service'

  let editor
  export default {
    name: 'Step2',
    data () {
      return {
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        form: this.$form.createForm(this),
        loading: false,
        dataSourceList: [],
        isParse: false,
        params: [],
        params2: [],
        typeList: [{ name: '文本', code: 'input' }, { name: '下拉框', code: 'select' }, { name: '时间', code: 'time' }, {
          name: '树',
          code: 'tree'
        }, { name: '二级联动', code: 's-select' }, { name: '车牌号', code: 'carplate' }, { name: '机构类型', code: 'department' }],
        dictList: [],
        timeList: ['YYYY-MM-DD HH:mm:ss', 'YYYY-MM-DD HH:mm', 'YYYY-MM-DD HH', 'YYYY-MM-DD', 'YYYY-MM', 'YYYY', 'YYYYMMDDHHmmss', 'YYYYMMDDHHmm', 'YYYYMMDDHH', 'YYYYMMDD', 'YYYYMM'],
        grainList: [
          { label: '按年查询', value: '11', type: 'Y', checked: false }, {
            label: '按月查询',
            value: '12',
            type: 'M',
            checked: false
          },
          { label: '按日查询', value: '13', type: 'D', checked: false }, {
            label: '按时查询',
            value: '14',
            type: 'H',
            checked: false
          },
          { label: '按分查询', value: '15', type: 'm', checked: false }, {
            label: '按秒查询',
            value: '16',
            type: 's',
            checked: false
          },
          { label: '按年区间查询', value: '21', type: 'Y', checked: false }, {
            label: '按月区间查询',
            value: '22',
            type: 'M',
            checked: false
          },
          { label: '按日区间查询', value: '23', type: 'D', checked: false }, {
            label: '按时区间查询',
            value: '24',
            type: 'H',
            checked: false
          },
          { label: '按分区间查询', value: '25', type: 'm', checked: false }, {
            label: '按秒区间查询',
            value: '26',
            type: 's',
            checked: false
          }
        ],
        // 表头
        columns: [
          {
            title: '#',
            scopedSlots: { customRender: 'serial' }
          },
          {
            title: '字典表名',
            dataIndex: 'dictName'
          },
          {
            title: '中文名',
            dataIndex: 'dictDesc'
          }
        ],
        dataSourceTypeList: [
          {
            id: 0,
            code: 'MYSQL',
            disabled: false
          }
          // {
          //   id: 7,
          //   code: 'KAFKA',
          //   disabled: false
          // },
          // {
          //   id: 7,
          //   code: 'ELASTICSEARCH',
          //   disabled: false
          // },
          // {
          //   id: 8,
          //   code: 'KYLIN',
          //   disabled: false
          // }
        ],
        mergeCol: false,
        mergeColList: [],
        columnsTemp: [
          {
            title: '列名',
            dataIndex: 'name'
          },
          {
            title: '是否合并',
            align: 'center',
            dataIndex: 'is_merge',
            scopedSlots: { customRender: 'merge' }
          }
        ],
        outputList: []
      }
    },
    props: [
      'reportInfo'
    ],
    methods: {
      nextStep: function () {
        const that = this
        const { form: { validateFields } } = this
        this.form.setFieldsValue({ sql: editor.getValue() })
        validateFields((err, values) => {
          if (!err) {
            if (this.params.length > 0) {
              for (let i = 0; i < this.params.length; i++) {
                var re = /^[u4E00-u9FA5]+$/
                // 校验是不是中文
                if (!this.params[i].desc || re.test(this.params[i].desc)) {
                  this.$message.warning(`请输入中文描述`)
                  return false
                }
                if (!this.params[i].type) {
                  this.$message.warning(`请输入参数类型`)
                  return false
                }
                if (this.params[i].type === 'time') {
                  if (!this.params[i].value.dataFormat) {
                    this.$message.warning(`请输入时间格式`)
                    return false
                  }
                  if (!this.params[i].value.queryGrain || this.params[i].value.queryGrain.length < 1) {
                    this.$message.warning(`请选择查询粒度`)
                    return false
                  }
                }
              }
            }
            for (const key in values) {
              that.reportInfo[key] = values[key]
            }
            const params = this.params.concat(this.params2)
            debugger
            const data = _.cloneDeep(params)
            if (data.length > 0) {
              for (let i = 0; i < data.length; i++) {
                if (data[i].type === 'time') {
                  data[i].value.queryGrain = data[i].value.queryGrain.join('|')
                }
              }
            }
            that.reportInfo.param = JSON.stringify(data)
            // 创建/修改报表
            createReport(that.reportInfo).then(res => {
              if (res.code === 0) {
                reportExcel({ id: that.reportInfo.id }).then(res => {
                  if (res.code === 0) {
                    that.$emit('nextStep')
                  } else {
                    that.$message.error(res.msg || '')
                  }
                }).catch(e => {
                  that.$message.error(e.msg || '')
                })
              } else {
                that.$message.error(res.msg)
              }
            }).catch(e => {
              that.$message.error(e.msg)
            })
          } else {
            that.loading = false
          }
        })
      },
      prevStep () {
        this.$emit('prevStep')
      },
      handleDataSourceTypeChanged (value) {
        this.form.setFieldsValue({ 'dataSourceType': value, 'dataSourceId': undefined })
        getDataSourceList({ type: value }).then(res => {
          this.dataSourceList = res.data
        })
      },
      /**
       * 处理代码高亮显示
       */
      handlerEditor () {
        // editor
        editor = codemirror('code-sql', {
          mode: 'sql',
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

        editor.setValue(this.reportInfo.sql)

        return editor
      },
      /**
       * 解析参数 要去重
       */
      _parse () {
        if (!editor.getValue()) {
          this.$message.warning(`请输入sql语句`)
          return false
        }
        const sql = editor.getValue()
        let index = sql.indexOf('{') // 字符{出现的位置
        let index2 = sql.indexOf('}') // 字符}出现的位置

        const nameList = []
        const names = []
        for (let i = 0; i < this.params.length; i++) {
          names.push(this.params[i].name)
        }
        while (index !== -1 && index2 !== -1) {
          const name = sql.substring(index + 1, index2)
          nameList.push(name)
          if (this.params.length > 0) {
            if (names.indexOf(name) === -1) {
              this.params.push({
                name: name,
                desc: '',
                type: undefined,
                value: '' || {},
                isShow: true,
                timeList: _.cloneDeep(this.timeList),
                grainList: _.cloneDeep(this.grainList),
                is_merge: false
              })
            }
          } else {
            this.params.push({
              name: name,
              desc: '',
              type: undefined,
              value: '' || {},
              isShow: true,
              timeList: _.cloneDeep(this.timeList),
              grainList: _.cloneDeep(this.grainList),
              is_merge: false
            })
          }

          index = sql.indexOf('{', index + 1) // 从字符串出现的位置的下一位置开始继续查找
          index2 = sql.indexOf('}', index2 + 1) // 从字符串出现的位置的下一位置开始继续查找
        }
        // this.params和nameList的长度要一致 且name数据相同
        for (let i = 0; i < this.params.length; i++) {
          if (nameList.indexOf(this.params[i].name) === -1) {
            this.params.splice(i, i + 1)
          }
        }
        // 去重
        this.params = this._deWeight(this.params)
        console.log(this.params)
      },
      // 去重
      _deWeight (arr) {
        const obj = {}
        arr = arr.reduce(function (a, b) {
          // eslint-disable-next-line no-unused-expressions
          obj[b.name] ? '' : obj[b.name] = true && a.push(b)
          return a
        }, [])
        return arr
      },
      // 参数显示
      _showParams (i) {
        this.params[i].isShow = !this.params[i].isShow
      },
      // 参数列表
      dictLists () {
        getDictList().then(res => {
          this.dictList = res.data
        })
      },
      // 时间动态变化
      _handleTimeChange (val, i) {
        i.value.queryGrain = []
        for (let a = 0; a < i.grainList.length; a++) {
          if (val.indexOf(i.grainList[a].type) < 0) {
            i.grainList[a].disabled = true
          } else {
            i.grainList[a].disabled = false
          }
        }
        console.log(this.params)
      },
      // 选择参数类型
      _changeType (val, i) {
        if (val === 'time') {
          this.params[i].value = {
            queryGrain: [],
            dataFormat: ''
          }
          this.params[i].timeList = _.cloneDeep(this.timeList)
          this.params[i].grainList = _.cloneDeep(this.grainList)
        }
      },
      showChooseFiled () {
        const params = {
          sql: editor.getValue(),
          mode: 1,
          datasourceId: this.form.getFieldValue('dataSourceId')
        }
        getAnalysisSql(params).then(res => {
          if (res.code === 0) {
            this.outputList = res.data
            for (let i = 0; i < this.outputList.length; i++) {
              for (let j = 0; j < this.params2.length; j++) {
                if (this.outputList[i].name === this.params2[j].name) {
                  this.outputList[i].is_merge = true
                }
              }
            }
          } else {
            this.$message.error(res.msg)
          }
        }).catch(e => {
          this.$message.error(e.msg)
        })
        this.mergeCol = !this.mergeCol
      },
      onChange (e, r) {
        r.is_merge = e.target.checked
      },
      saveChooseFiled () {
        this.params2 = []
        this.mergeColList = []
        for (let i = 0; i < this.outputList.length; i++) {
          const p = this.outputList[i]
          if (p.is_merge) {
            this.mergeColList.push(p.name)
            this.params2.push({
              name: p.name,
              type: 'merge'
            })
          }
        }
        this.params2 = this._deWeight(this.params2)
        this.mergeCol = !this.mergeCol
      }
    },
    mounted () {
      if (this.reportInfo) {
        if (this.reportInfo.dataSourceType) {
          this.handleDataSourceTypeChanged(this.reportInfo.dataSourceType)
        }
        const info = {
          dataSourceType: this.reportInfo.dataSourceType ? this.reportInfo.dataSourceType : '',
          dataSourceId: this.reportInfo.dataSourceId ? this.reportInfo.dataSourceId : '',
          sql: this.reportInfo.sql ? this.reportInfo.sql : ''
        }
        this.form.setFieldsValue({ ...info })
        this.params = this.reportInfo.param && this.reportInfo.param.length > 0 ? JSON.parse(this.reportInfo.param) : []
        const params = {
          sql: this.reportInfo.sql,
          mode: 1,
          datasourceId: this.reportInfo.dataSourceId
        }
        for (let i = 0; i < this.params.length; i++) {
          if (this.params[i].type === 'time') {
            const p = this.params[i]
            p.value = typeof (p.value) === 'string' ? JSON.parse(p.value) : p.value
            p.value.queryGrain = p.value.queryGrain.split('|')
            p.timeList = _.cloneDeep(this.timeList)
            p.grainList = _.cloneDeep(this.grainList)
            for (let a = 0; a < p.grainList.length; a++) {
              if (p.value.dataFormat.indexOf(p.grainList[a].type) < 0) {
                p.grainList[a].disabled = true
              } else {
                p.grainList[a].disabled = false
              }
            }
          } else if (this.params[i].type === 'merge') {
            const p = this.params[i]
            this.mergeColList.push(p.name)
            this.params2.push(p)
            this.params.splice(i, 1)
            i = i - 1 // 解决方案
          }
        }
      }
      this.reportInfo.sql = this.reportInfo.sql ? this.reportInfo.sql : ''
      this.handlerEditor()
    },
    destroyed () {
      /**
       * 销毁编辑器实例
       */
      if (editor) {
        editor.toTextArea() // Uninstall
        editor.off(('.code-sql'), 'keypress', this.keypress)
      }
    },
    watch: {},
    created () {
      this.dictLists()
      // this.dataSourceTypeList = dataSourceTypeList()
    }
  }
</script>

<style lang="less">
  .step-dataS {
    max-width: 1000px;
    margin: 40px auto 0;

    .stepFormText {
      margin-bottom: 24px;

      .ant-form-item-label,
      .ant-form-item-control {
        line-height: 22px;
      }
    }

    .from-mirror {
      width: 100%;
      position: relative;
      z-index: 0;
      display: flex;

      .CodeMirror {
        height: auto;
        min-height: 72px;
        width: 110%;
        border: 1px solid #ddd !important;
        border-radius: 3px;
      }

      .CodeMirror-scroll {
        height: auto;
        min-height: 72px;
        width: 100%;
      }

      .CodeMirror-gutters {
        border-left: none;
      }
    }

    .step-form-style-desc {
      padding-left: 166px;
      color: rgba(0, 0, 0, .45);

      h3 {
        margin: 0 0 12px;
        color: rgba(0, 0, 0, .45);
        font-size: 16px;
        line-height: 32px;
      }

      h4 {
        margin: 0 0 4px;
        color: rgba(0, 0, 0, .45);
        font-size: 14px;
        line-height: 22px;
      }

      p {
        margin-top: 0;
        margin-bottom: 12px;
        line-height: 22px;
      }
    }

    .step-form-dict-model {
      margin-top: 25px;
      width: 100%;
    }

    .step-form-time-model {
      margin-top: 25px;
      margin-bottom: 25px;
      border: 1px solid #E0E0E0;
      padding: 15px;
    }

  }
</style>
