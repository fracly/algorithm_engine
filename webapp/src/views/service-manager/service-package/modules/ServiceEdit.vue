<template>
  <a-card class="card" title="" :bordered="false">
    <a-form :form="form" class="form">
      <a-row>
        <a-col :md="24" :sm="24" style="margin-left: 10%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="基本信息" >
            </a-tab-pane>
          </a-tabs>
        </a-col>
      </a-row>
      <a-form-item
        label="服务名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          placeholder="请输入服务名称，例如:查询车辆违法信息接口"
          v-decorator="['name', {rules: [{required: true, message: '服务名称不能为空，且长度为6-100个字符',min:6,max:100,whitespace:true}] }]"/>
      </a-form-item>
      <a-form-item
        label="服务编码"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          placeholder="请输入服务编码,例如:queryCar"
          v-decorator="['code', {rules: [{required: true,
                                          message: '报表编码由字母、或数字、或下划线组成，长度不超过30个字符',max:30,whitespace:true},{validator: checkCode}] }]"/>
      </a-form-item>
      <a-form-item
        label="服务类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-select
          show-search
          optionFilterProp="children"
          placeholder="请选择服务类型"
          v-decorator="['type', { rules: [{required: true, message: '服务类型必须选择'}] }]"
        >
          <a-select-option
            v-for="(item) in serviceTypeList"
            :key="item.code"
            :value="item.code">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="服务描述"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          placeholder="请输入服务描述,例如:车辆违法接口"
          v-decorator="['desc', {rules: [{message: '长度不超过128个字符',max:128,whitespace:true}] }]"/>
      </a-form-item>
      <a-row>
        <a-col :md="24" :sm="24" style="margin-left: 10%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="数据源信息">
            </a-tab-pane>
          </a-tabs>
        </a-col>
      </a-row>
      <a-form-item
        label="开发模式"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-radio-group v-decorator="['mode', { rules: [{required: true, message: '报表类型必须选择'}] }]" @change="modeChange">
          <a-radio :value="0">单表模式</a-radio>
          <a-radio :value="1" >SQL模式</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        label="数据源类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-select
          placeholder="请选择数据源类型"
          v-decorator="['datasourceType', { rules: [{required: true, message: '数据源类型必须选择'}] }]"
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
          show-search
          optionFilterProp="children"
          placeholder="请选择数据源"
          v-decorator="['datasourceId', { rules: [{required: true, message: '数据源必须选择'}] }]"
          @change="handleDataSourceChanged"
        >
          <a-select-option
            v-for="(item) in dataSourceList"
            :key="item.id"
            :value="item.id">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="表名"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        v-show="form.getFieldValue('mode')===0"
      >
        <a-select
          placeholder="请选择表名"
          @change="handleTableChanged"
          :showSearch="true"
          v-decorator="['table', { rules: [{required: true, message: '表名必须选择'}] }]"
        >
          <a-select-option
            v-for="(item) in tableList"
            :key="item.name"
            :value="item.name">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-row v-show="form.getFieldValue('mode')===1">
        <a-col :md="24" :sm="24" style="margin-left: 10%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="SQL语句" >
            </a-tab-pane>
          </a-tabs>
        </a-col>

      </a-row>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="{ lg: { span: 18 }, sm: { span: 18 } }"
        class="stepFormText"
        v-show="form.getFieldValue('mode')===1"
        style="margin-left: 10%"
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
        <p>注释:<br/>1、sql语句结尾不能加"；"；<br>2、宏变量请使用${}表示，例如：id=${p_id}；</p>
      </a-form-item>
      <a-row>
        <a-col :md="24" :sm="24" style="margin-left: 10%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="输出参数" >
            </a-tab-pane>
          </a-tabs>
        </a-col>

      </a-row>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="{ lg: { span: 14 }, sm: { span: 14 } }"
        class="stepFormText"
        v-show="form.getFieldValue('mode')===0"
        style="margin-left: 10%"
      >
        <a-table
          ref="table"
          size="default"
          :columns="columns"
          rowKey="name"
          :dataSource="columnList"
          :pagination="false"
          :bordered="true"
          :scroll="{y:'400px'}"
          class="tableStyle"
          :row-selection="rowSelection"
        >
          <template slot="comment" slot-scope="text, record">
            <editable-cell :text="text" v-show="record.isCheck" @change="onCellChange(record.key, 'comment', $event)"/>
            <span v-show="!record.isCheck">{{ text }}</span>
          </template>
        </a-table>
      </a-form-item>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="{ lg: { span: 14 }, sm: { span: 14 } }"
        class="stepFormText"
        style="margin-left: 10%"
        v-show="form.getFieldValue('mode')=== 1"
      >
        <a-table
          ref="table"
          size="default"
          :columns="outColumns"
          rowKey="name"
          :dataSource="outputList1"
          :pagination="false"
          :bordered="true"
          :scroll="{y:'400px'}"
          class="tableStyle"
        >
          <span slot="type" slot-scope="text,record">
            <template>
              <a-select
                placeholder="请选择参数类型"
                v-model="record.type"
              >
                <a-select-option
                  v-for="(item) in typeList"
                  :key="item"
                  :value="item">{{ item }}
                </a-select-option>
              </a-select>
            </template>
          </span>
          <span slot="desc" slot-scope="text, record">
            <template>
              <a-input v-model="record.comment" placeholder="请输入中文描述">
              </a-input>
            </template>
          </span>
        </a-table>
      </a-form-item>
      <a-row>
        <a-col :md="24" :sm="24" style="margin-left: 10%">
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="输入参数" >
            </a-tab-pane>
          </a-tabs>
        </a-col>
      </a-row>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="{ lg: { span: 20 }, sm: { span: 24 } }"
        class="stepFormText"
        v-show="form.getFieldValue('mode')===0"
        style="margin-left: 12%"
      >
        <dependent
          @on-dependent="_onDependent"
          ref="DEPENDENT"
          :column-list="columnList"
          :backfill-item="inputList"></dependent>
      </a-form-item>
      <a-form-item
        label=""
        :labelCol="labelCol"
        :wrapperCol="{ lg: { span: 20 }, sm: { span: 20 } }"
        class="stepFormText"
        style="margin-left: 10%"
        v-show="form.getFieldValue('mode')=== 1"
      >
        <a-table
          ref="table"
          size="default"
          :columns="inColumns"
          rowKey="param_name"
          :dataSource="inputList1"
          :pagination="false"
          :bordered="true"
          :scroll="{y:'400px'}"
          class="tableStyle"
        >
          <span slot="type" slot-scope="text,record">
            <template>
              <a-select
                placeholder="请选择参数类型"
                v-model="record.param_type"
              >
                <a-select-option
                  v-for="(item) in typeList"
                  :key="item"
                  :value="item">{{ item }}
                </a-select-option>
              </a-select>
            </template>
          </span>
          <span slot="desc" slot-scope="text, record">
            <template>
              <a-input v-model="record.param_des" placeholder="请输入中文描述">
              </a-input>
            </template>
          </span>
          <span slot="join" slot-scope="text,record">
            <template>
              <a-select
                placeholder="请选择参数类型"
                v-model="record.param_join"
              >
                <a-select-option
                  v-for="(item) in conditionsList"
                  :key="item"
                  :value="item">{{ item }}
                </a-select-option>
              </a-select>
            </template>
          </span>
          <span slot="exam" slot-scope="text,record">
            <template>
              <a-input v-model="record.param_exam" placeholder="请输入示例值"></a-input>
            </template>
          </span>
          <span slot="required" slot-scope="text,record">
            <template>
              <a-checkbox :defaultChecked="record.is_required" v-model="record.is_required"></a-checkbox>
            </template>
          </span>

        </a-table>
      </a-form-item>
      <a-form-item :wrapperCol="{span: 10, offset: 10}">
        <a-button @click="back">返回</a-button>
        <a-button
          style="margin-left: 8px;background: #00e064;border-color: #00e064;"
          type="primary"
          @click="testConnect(true)"
          v-show="!testing">测试
        </a-button>
        <a-button v-if="testing" style="margin-left: 8px;background: #00e064;border-color: #00e064;" type="primary" loading>
          Loading
        </a-button>
        <a-button style="margin-left: 8px" type="primary" @click="ok" :disabled="isTest">提交</a-button>
      </a-form-item>
    </a-form>
  </a-card>
</template>

<script>
  import { getDataSourceList, getTablesById, getColumnById } from '@/api/datasource'
  import { getAnalysisSql, updateServer, addParam, addServer, getServerInfo, getParam, getTypeList } from '@/api/service'
  import codemirror from '@/utils/codemirror'
  import _ from 'lodash'
  import dependent from './dependent'

  let editor
  const EditableCell = {
    template: `
      <div class="editable-cell" >
        <div v-if="editable" class="editable-cell-input-wrapper">
          <a-input :value="value" @change="handleChange" @pressEnter="check" /><a-icon
            type="check"
            class="editable-cell-icon-check"
            @click="check"
          />
        </div>
        <div v-else class="editable-cell-text-wrapper">
          {{ value || ' ' }}
          <a-icon type="edit" class="editable-cell-icon" @click="edit" />
        </div>
      </div>
    `,
    props: {
      text: String
    },
    data () {
      return {
        value: this.text,
        editable: false
      }
    },
    methods: {
      handleChange (e) {
        const value = e.target.value
        this.value = value
      },
      check () {
        this.editable = false
        this.$emit('change', this.value)
      },
      edit () {
        this.editable = true
      }
    },
    watch: {
      text (val) {
        this.value = val
      }
    }
  }
  export default {
    name: 'ServiceEdit',
    components: {
      dependent,
      EditableCell
    },
    data () {
      return {
        form: this.$form.createForm(this),
        labelCol: { lg: { span: 4 }, sm: { span: 4 } },
        wrapperCol: { lg: { span: 10 }, sm: { span: 10 } },
        dataSourceList: [],
        dataSourceTypeList: [
          {
            id: 0,
            code: 'MYSQL',
            disabled: false
          },
          // {
          //   id: 7,
          //   code: 'KAFKA',
          //   disabled: false
          // },
          {
            id: 7,
            code: 'ELASTICSEARCH',
            disabled: false
          },
          {
            id: 8,
            code: 'KYLIN',
            disabled: false
          }
        ],
        tableList: [],
        columnList: [],
        // 表头
        columns: [
          {
            title: '字段名',
            dataIndex: 'name'
          },
          {
            title: '字段类型',
            dataIndex: 'type'
          },
          {
            title: '字段描述',
            dataIndex: 'comment',
            width: '40%',
            scopedSlots: { customRender: 'comment' }
          }
        ],
        outColumns: [
          {
            title: '字段名',
            dataIndex: 'name'
          },
          {
            title: '字段类型',
            dataIndex: 'type',
            scopedSlots: { customRender: 'type' }
          },
          {
            title: '字段描述',
            dataIndex: 'comment',
            scopedSlots: { customRender: 'desc' }
          }
        ],
        inColumns: [
          {
            title: '字段名',
            dataIndex: 'param_name'
          },
          {
            title: '字段类型',
            dataIndex: 'param_type',
            scopedSlots: { customRender: 'type' }
          },
          {
            title: '字段描述',
            dataIndex: 'param_des',
            scopedSlots: { customRender: 'desc' }
          },
          {
            title: '拼接条件',
            dataIndex: 'param_join',
            scopedSlots: { customRender: 'join' }
          },
          {
            title: '示例值',
            dataIndex: 'param_exam',
            scopedSlots: { customRender: 'exam' }
          },
          {
            title: '是否必填',
            dataIndex: 'is_required',
            align: 'center',
            scopedSlots: { customRender: 'required' }
          }
        ],
        typeList: [],
        typeList1: ['STRING', 'INT', 'LONG', 'FLOAT', 'DOUBLE'], // HIVE字段类型
        typeList2: ['INT', 'FLOAT', 'DOUBLE', 'VARCHAR', 'TEXT'], // mysql字段类型
        // 输出列表-sql模式
        outputList1: [],
        // 输入参数-sql模式
        inputList1: [],
        // 输出参数-单标模式
        outputList: '',
        // 输入条件配置-单标模式
        inputList: {},
        isTest: true,
        conditionsList: ['=', '>', '<', '!=', 'like', 'leftLike', 'rightLike', 'in', '>=', '<='],
        service: {
          status: 0,
          params: '',
          id: '',
          sql: '',
          inParam: '', // 入参报文样例
          outParam: '' // 出参报文样例
        },
        activeKey: '1',
        requiredList: [{ name: '必填', value: '1' }, { name: '非必填', value: '0' }],
        serviceTypeList: [],
        testing: false,
        paramList: []
      }
    },
    methods: {
      onCellChange (key, dataIndex, value) {
        const dataSource = [...this.columnList]
        const target = dataSource.find(item => item.key === key)
        if (target) {
          target[dataIndex] = value
          this.columnList = dataSource
        }
      },
      // 模式切换
      modeChange (e) {
        if (editor) {
          editor.toTextArea() // Uninstall
          editor.off(('.code-sql'), 'keypress', this.keypress)
        }
        this.form.setFieldsValue({ 'datasourceType': undefined, 'datasourceId': undefined, table: undefined })
        const list = [
            {
              id: 0,
              code: 'MYSQL'
            },
            {
              id: 7,
              code: 'ELASTICSEARCH'
            },
            {
              id: 8,
              code: 'KYLIN'
            }
          ]
        if (e.target.value === 1) {
          setTimeout(() => {
            this.handlerEditor().refresh()
          }, 10)
          const list2 = _.cloneDeep(this.dataSourceTypeList)
          list2.forEach((item, k) => {
              if (item.code === 'ELASTICSEARCH') {
                list2.splice(k, 1)
              }
          })
          this.dataSourceTypeList = list2
        } else {
            this.dataSourceTypeList = list
        }
      },
      // 数据源类型接口
      handleDataSourceTypeChanged (value) {
        this.form.setFieldsValue({ 'datasourceType': value, 'datasourceId': undefined, table: undefined })
        getDataSourceList({ type: value }).then(res => {
          this.dataSourceList = res.data
        })
      },
      // 数据源接口
      handleDataSourceChanged: function (value) {
        this.form.setFieldsValue({ 'datasourceId': value, table: undefined })
        if (value === 'MYSQL') {
          this.typeList = this.typeList1
        } else {
          this.typeList = this.typeList2
        }
        getTablesById({ type: this.form.getFieldValue('datasourceType'), datasourceId: value }).then(res => {
          this.tableList = res.data
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 表接口
      handleTableChanged (value) {
        this.form.setFieldsValue({ 'table': value })
        this.outputList = ''
        const self = this
        this.inputList = {
          dependTaskList: [],
          relation: 'and'
        }
        const param = {
          type: this.form.getFieldValue('datasourceType'),
          datasourceId: this.form.getFieldValue('datasourceId'),
          table: value
        }
        getColumnById(param).then(res => {
          self.columnList = res.data
          if (self.form.getFieldValue('datasourceType') === 'KYLIN') {
            for (let i = 0; i < self.columnList.length; i++) {
              self.columnList[i].type = self.columnList[i].type.split(' ')[0]
            }
          }
          for (let i = 0; i < self.columnList.length; i++) {
              self.columnList[i].key = i + 1
              if (self.outputList.indexOf(self.columnList[i].name) !== -1) {
              self.columnList[i].isCheck = true
            }
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },

      // 表接口
      handleTableChanged2 (value) {
        this.form.setFieldsValue({ 'table': value })
        this.outputList = ''
        const self = this
        const param = {
          type: this.form.getFieldValue('datasourceType'),
          datasourceId: this.form.getFieldValue('datasourceId'),
          table: value
        }
        getColumnById(param).then(res => {
          self.columnList = res.data
          if (self.form.getFieldValue('datasourceType') === 'KYLIN') {
            for (let i = 0; i < self.columnList.length; i++) {
              self.columnList[i].type = self.columnList[i].type.split(' ')[0]
            }
          }
          if (self.$route.params.id) {
            getParam({ id: self.$route.params.id, type: '1' }).then(res => {
              for (let i = 0; i < self.columnList.length; i++) {
                for (let j = 0; j < res.data.length; j++) {
                  if (res.data[j].param_name === self.columnList[i].name) {
                    self.columnList[i].comment = res.data[j].param_des ? res.data[j].param_des : self.columnList[i].comment
                  }
                }
              }
            }).catch(e => {
              this.$message.error(e.msg || '')
            })
          }
          for (let i = 0; i < self.columnList.length; i++) {
            self.columnList[i].key = i + 1
            if (self.outputList.indexOf(self.columnList[i].name) !== -1) {
              self.columnList[i].isCheck = true
            }
          }
        }).catch(e => {
          this.$message.error(e.msg || '')
        })
      },
      // 选择输入参数
      arrChange (e) {
        this.outputList = _.join(e, ',')
        for (let i = 0; i < this.columnList.length; i++) {
          if (this.outputList.indexOf(this.columnList[i].name) !== -1) {
            this.columnList[i].isCheck = true
          } else {
            this.columnList[i].isCheck = false
          }
        }
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

        editor.setValue(this.service.sql)
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
        if (!this.form.getFieldValue('datasourceId')) {
          this.$message.warning(`请选择数据源`)
          return false
        }
        const sql = editor.getValue()
        // 正则匹配
        // const word = /{/
        // if (!word.test(sql)) {
        //   this.$message.warning(`sql语句中参数格式要按照宏变量名写法`)
        //   return false
        // }
        const params = {
          sql: sql,
          mode: this.form.getFieldValue('mode'),
          datasourceId: this.form.getFieldValue('datasourceId')
        }
        getAnalysisSql(params).then(res => {
          if (res.code === 0) {
            const data = []
            if (this.outputList1.length > 0) {
              for (let i = 0; i < this.outputList1.length; i++) {
                for (let j = 0; j < res.data.length; j++) {
                  if (this.outputList1[i].name === res.data[j].name) {
                    data.push(this.outputList1[i])
                  }
                }
              }
            }
            this.outputList1 = data.concat(this._different(data, res.data))
          } else {
            this.$message.error(res.msg)
          }
        }).catch(e => {
          this.$message.error(e.msg)
        })
        let index = sql.indexOf('{') // 字符{出现的位置
        let index2 = sql.indexOf('}') // 字符}出现的位置

        const nameList = []
        const names = []
        for (let i = 0; i < this.inputList1.length; i++) {
          names.push(this.inputList1[i].param_name)
        }
        while (index !== -1 && index2 !== -1) {
          const name = sql.substring(index + 1, index2)
          nameList.push(name)
          if (this.inputList1.length > 0) {
            if (names.indexOf(name) === -1) {
              this.inputList1.push({
                param_name: name,
                param_variable: name,
                param_type: '',
                param_des: '',
                param_join: '',
                type: undefined,
                param_exam: '',
                is_required: true
              })
            }
          } else {
            this.inputList1.push({
              param_name: name,
              param_variable: name,
              param_type: '',
              param_des: '',
              param_join: '',
              type: undefined,
              param_exam: '',
              is_required: true

            })
          }

          index = sql.indexOf('{', index + 1) // 从字符串出现的位置的下一位置开始继续查找
          index2 = sql.indexOf('}', index2 + 1) // 从字符串出现的位置的下一位置开始继续查找
        }
        // this.params和nameList的长度要一致 且name数据相同
        for (let a = 0; a < this.inputList1.length; a++) {
          if (nameList.indexOf(this.inputList1[a].param_name) === -1) {
            this.inputList1.splice(a, a + 1)
          }
        }
        // 去重
        this.inputList1 = this._deWeight(this.inputList1)
      },
      // 两个数组中不同的数据
      _different (array1, array2) {
        const result = []
        for (let i = 0; i < array2.length; i++) {
          const obj = array2[i]
          const num = obj.name
          let flag = false
          for (let j = 0; j < array1.length; j++) {
            const aj = array1[j]
            const n = aj.name
            if (n === num) {
              flag = true
              break
            }
          }
          if (!flag) {
            result.push(obj)
          }
        }
        return result
      },
      // 去重
      _deWeight (arr) {
        // const result = {}
        // const finalResult = []
        // for (let i = 0; i < arr.length; i++) {
        //   result[arr[i].param_name] = arr[i]
        // }
        // for (const item in result) {
        //   finalResult.push(result[item])
        // }
        // return finalResult
        const obj = {}
        arr = arr.reduce(function (a, b) {
          // eslint-disable-next-line no-unused-expressions
          obj[b.param_name] ? '' : obj[b.param_name] = true && a.push(b)
          return a
        }, [])
        return arr
      },
      // 返回
      back () {
        this.$router.push('/resource/service')
      },
      // 点击测试触发
      testConnect (flag) {
        // 验证参数
        if (!this.$refs['DEPENDENT']._verification()) {
          return false
        }
        const that = this
        const { form: { validateFields } } = this
        this.form.setFieldsValue({ sql: '1' })
        if (this.form.getFieldValue('mode') === 1) {
          this.form.setFieldsValue({ table: '1' })
        }
        validateFields((err, values) => {
          let params
          if (!err) {
            // 校验参数
            if (values.mode === 0) {
              // 输出，必须有至少一个字段
              if (that.outputList.length === 0) {
                that.$message.warning(`请选择输出参数`)
                return false
              }
              const list = that.inputList.dependTaskList
              for (let i = 0; i < list.length; i++) {
                const list2 = list[i].dependItemList
                for (let j = 0; j < list2.length; j++) {
                  if (!list2[j].name) {
                    that.$message.warning(`请选择输入参数-名称`)
                    return false
                  }
                  if (!list2[j].conditions) {
                    that.$message.warning(`请选择输入参数-连接条件`)
                    return false
                  }
                  if (!list2[j].example) {
                    that.$message.warning(`请选择输入参数-示例`)
                    return false
                  }
                  if (!list2[j].is_required) {
                    that.$message.warning(`请选择输入参数是否必填`)
                    return false
                  }
                }
              }
            } else {
              // 输出，必须有至少一个字段
              if (that.outputList1.length === 0) {
                that.$message.warning(`请选择输出参数`)
                return false
              }
            }
            for (const key in values) {
              that.service[key] = values[key]
            }
            const inParam = {}
            const outParam = {}
            // 如果单标模式 拼接sql 且出现多次 不同宏变量
            if (values.mode === 0) {
              let sql = 'select ' + that.outputList + ' from ' + that.service.table + ' where 1=1 '
              const list = _.cloneDeep(that.inputList.dependTaskList)
              const nameList = []
              for (let i = 0; i < list.length; i++) {
                const list2 = list[i].dependItemList
                const relation = list[i].relation
                let sql1 = '( 1=1 '
                for (let j = 0; j < list2.length; j++) {
                  let a = 1
                  if (list2[j].conditions === 'leftLike' || list2[j].conditions === 'rightLike') {
                    list2[j].conditions = 'like'
                  }
                  if (nameList.indexOf(list2[j].name) !== -1) {
                    a++
                    if (list2[j].alias) {
                    } else {
                      list2[j].alias = list2[j].name + a
                      that.inputList.dependTaskList[i].dependItemList[j].alias = list2[j].name + a
                    }
                  } else {
                    if (list2[j].alias) {
                    } else {
                      list2[j].alias = list2[j].name
                      that.inputList.dependTaskList[i].dependItemList[j].alias = list2[j].name
                    }
                  }
                  sql1 = sql1 + relation + ' ' + list2[j].name + ' ' + list2[j].conditions + ' ${' + list2[j].alias + '}' + ' '
                  nameList.push(list2[j].name)
                }
                sql1 = sql1 + ' )'
                sql = sql + ' ' + that.inputList.relation + ' ' + sql1
              }
              console.log(sql)
              that.service.sql = sql
              const inputNameList = []
              for (let i = 0; i < that.inputList.dependTaskList.length; i++) {
                const list = that.inputList.dependTaskList[i].dependItemList
                for (let j = 0; j < list.length; j++) {
                  inputNameList.push(list[j])
                }
              }
              console.log(inputNameList)

              // 拼接参数
              that.service.params = JSON.stringify({ input: that.inputList, output: that.outputList })
              params = []
              for (let i = 0; i < that.columnList.length; i++) {
                // 输出参数
                if (that.outputList.split(',').indexOf(that.columnList[i].name) !== -1) {
                  const param = {
                    param_name: that.columnList[i].name,
                    param_type: that.columnList[i].type,
                    param_des: that.columnList[i].comment,
                    param_join: '',
                    type: 1,
                    param_exam: ''
                  }
                  params.push(param)
                  outParam[that.columnList[i].name] = ''
                }
                // 输入参数
                for (let j = 0; j < inputNameList.length; j++) {
                  if (inputNameList[j].name === that.columnList[i].name) {
                    const param = {
                      param_name: that.columnList[i].name,
                      param_variable: inputNameList[j].alias,
                      param_type: that.columnList[i].type,
                      param_des: inputNameList[j].desc ? inputNameList[j].desc : that.columnList[i].comment,
                      param_join: inputNameList[j].conditions,
                      type: 0,
                      param_exam: inputNameList[j].example,
                      is_required: inputNameList[j].is_required
                    }
                    params.push(param)
                    if (param.is_required !== '2') {
                      inParam[inputNameList[j].alias] = ''
                    }
                  }
                }
              }
              that.service.inParam = '{"seqId": "","timeStamp":"","reqData":' + JSON.stringify(inParam) + '}'
              that.service.outParam = '{"code": 200,"seqId": "","message": "请求成功","data":[' + JSON.stringify(outParam) + ']}'
              console.log(that.service)
            } else if (values.mode === 1) { // 如果是sql模式
              // 拼接参数
              that.service.params = JSON.stringify({ input: that.inputList1, output: that.outputList1 })
              params = []
              // 输出参数
              for (let i = 0; i < that.outputList1.length; i++) {
                const param = {
                  param_name: that.outputList1[i].name,
                  param_type: that.outputList1[i].type,
                  param_des: that.outputList1[i].comment,
                  param_join: '',
                  type: 1,
                  param_exam: ''
                }
                outParam[that.outputList1[i].name] = ''
                params.push(param)
              }
              const inputList1 = _.cloneDeep(this.inputList1)
              for (let i = 0; i < inputList1.length; i++) {
                inputList1[i].type = 0
                inputList1[i].is_required === true ? inputList1[i].is_required = '1' : inputList1[i].is_required = '0'
              }
              params = params.concat(inputList1)
              for (let i = 0; i < that.inputList1.length; i++) {
                inParam[that.inputList1[i].param_name] = ''
              }
              that.service.inParam = '{"seqId": "","timeStamp":"","reqData":' + JSON.stringify(inParam) + '}'
              that.service.outParam = '{"code": 200,"seqId": "","message": "请求成功","data":[' + JSON.stringify(outParam) + ']}'
              that.service.sql = editor.getValue()
            }
            // 修改数据
            if (that.service.id !== '') {
              this.testing = true
              updateServer(that.service).then(res => {
                if (res.code === 0) {
                  // 修改参数
                  addParam({ id: that.service.id, param: JSON.stringify(params) }).then(res => {
                    that.$message.success('修改参数成功')
                    this.testing = false
                    if (flag) {
                      that.$router.push('/resource/service/verify/' + that.service.id)
                    } else {
                      that.$router.push('/resource/service')
                    }
                  }).catch(e => {
                    that.$message.error('修改参数失败' + e.msg)
                  })
                } else {
                  that.$message.error(res.msg)
                }
              }).catch(e => {
                that.$message.error(e.msg)
              })
            } else {
              this.testing = true
              // 新增数据
              addServer(that.service).then(res => {
                if (res.code === 0) {
                  that.service.id = res.data
                  // 新增参数
                  addParam({ id: that.service.id, param: JSON.stringify(params) }).then(res => {
                    that.$message.success('新增参数成功')
                    this.testing = false
                    if (flag) {
                      that.$router.push('/resource/service/verify/' + that.service.id)
                    } else {
                      that.$router.push('/resource/service')
                    }
                  }).catch(e => {
                    that.$message.error('新增参数失败:' + e.msg)
                  })
                } else {
                  that.$message.error(res.msg)
                }
              }).catch(e => {
                that.$message.error(e.msg)
              })
            }
          } else {
            _.forIn(err, function (value, key) {
              // that.$message.warning(value.errors[0].message)
            })
          }
        })
      },
      // 提交
      ok () {
        this.testConnect(false)
      },
      _onDependent (o) {
        this.inputList = Object.assign(this.inputList, {}, o)
      },
      // 自定义检查参数
      checkCode (rule, value, callback) {
        const pwdRegex = new RegExp('.*[A-Za-z]{1,}.*')
        const regex = new RegExp('^\\w+$')
        if (!regex.test(value)) {
          callback(new Error('报表编码只能有字母、下划线、数字组成'))
        }
        if (!pwdRegex.test(value)) {
          callback(new Error('报表编码必须有字母'))
        }
        callback()
      },
      getType () {
        getTypeList({ code: '01' }).then(res => {
          this.serviceTypeList = res.data
        })
      }

    },
    created () {
      this.getType()
    },
    mounted () {
      const that = this
      // 初始化
      if (this.$route.params.id) {
        getServerInfo({ id: this.$route.params.id }).then(res => {
          that.service = _.cloneDeep(res.data)
          setTimeout(() => {
            that.handlerEditor().refresh()
          }, 10)
          that.isTest = false
          that.service.mode = parseInt(that.service.mode)
          const service = {
            name: that.service.name,
            code: that.service.code,
            desc: that.service.desc,
            mode: that.service.mode,
            sql: that.service.sql,
            type: that.service.type
          }
          that.form.setFieldsValue({ ...service })
          that.handleDataSourceTypeChanged(res.data.datasourceType)
          that.handleDataSourceChanged(res.data.datasourceId)
          if (service.mode === 0) {
            that.handleTableChanged2(res.data.table)
          }
          const params = that.service.params ? JSON.parse(that.service.params) : ''
          if (that.service.mode === 0) {
            that.inputList = params.input || {}
            that.outputList = params.output || ''
          } else {
            that.inputList1 = params.input || []
            that.outputList1 = params.output || []
          }
        }).catch(e => {
          that.$message.error(e.msg || '')
        })
      } else {
        // 初始化默认类型为开发模式
        this.form.setFieldsValue({ 'mode': 0 })
      }
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
    computed: {
      rowSelection () {
        return {
          onChange: this.arrChange,
          selectedRowKeys: this.outputList === '' ? [] : this.outputList.split(',')
        }
      }
    }
  }
</script>

<style lang="less">
  .card {
    margin-bottom: 24px;
  }

  .from-mirror {
    /*width: 100%;*/
    position: relative;
    /*z-index: 0;*/
    display: flex;
  }

  .from-mirror .CodeMirror {
    /*height: auto;*/
    /*min-height: 172px;*/
    width: 100%;
    border: 1px solid #ddd !important;
    border-radius: 3px;
  }

  .CodeMirror .CodeMirror-scroll {
    /*height: auto;*/
    /*min-height: 172px;*/
    /*width: 100%;*/
  }

  .ant-form-item {
    margin-bottom: 5px;
  }

  .newTitle svg {
    font-size: 16px;
  }

  .tableStyle {
    /deep/ .ant-table-thead > tr > th, .ant-table-tbody > tr > td {
      padding: 10px !important;
    }
  }

  .ant-form label {
    font-size: 12px;
  }
  .ant-form-item-children input{
    font-size: 12px;
  }
  .ant-tabs-tab-active {
    font-size: 14px;
  }
  .editable-cell {
    position: relative;
  }

  .editable-cell-input-wrapper,
  .editable-cell-text-wrapper {
    padding-right: 24px;
  }

  .editable-cell-text-wrapper {
    padding: 5px 24px 5px 5px;
  }

  .editable-cell-icon,
  .editable-cell-icon-check {
    position: absolute;
    right: 0;
    width: 20px;
    cursor: pointer;
  }

  .editable-cell-icon {
    line-height: 18px;
    display: none;
  }

  .editable-cell-icon-check {
    line-height: 28px;
  }

  .editable-cell:hover .editable-cell-icon {
    display: inline-block;
  }

  .editable-cell-icon:hover,
  .editable-cell-icon-check:hover {
    color: #108ee9;
  }

  .editable-add-btn {
    margin-bottom: 8px;
  }
</style>
