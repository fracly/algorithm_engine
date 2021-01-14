<template>
  <a-modal
    :title="title"
    class="data-modal"
    :width="800"
    :visible="visible"
    :maskClosable="false"
    :closable="false"
    @ok="onOk"
    @cancel="close">
    <div class="data-source-form">
      <a-spin :spinning="spinning">
        <a-form :form="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
          <a-form-item>
            <a-input type="hidden" v-decorator="['id']"/>
          </a-form-item>
          <a-form-item label="数据源类型" :required="true">
            <!--            <a-select v-decorator="['type', { rules: [{ required: true, message: '请选择数据源类型！' }] },]" placeholder="请选择数据源类型">-->
            <!--              <a-select-option value="MYSQL">MYSQL</a-select-option>-->
            <!--              <a-select-option value="HIVE">HIVE/IMPALA</a-select-option>-->
            <!--              <a-select-option value="ORACLE">ORACLE</a-select-option>-->
            <!--              <a-select-option value="SQLSERVER">SQLSERVER</a-select-option>-->
            <!--              <a-select-option value="KAFKA">KAFKA</a-select-option>-->
            <!--              <a-select-option value="ELASTICSEARCH">ELASTICSEARCH</a-select-option>-->
            <!--            </a-select>-->
            <a-radio-group v-decorator="['type', { rules: [{required: true, message: '请选择数据源类型'}] }] " v-model="type">
              <a-radio value="MYSQL">MYSQL</a-radio>
              <a-radio value="HIVE">HIVE/IMPALA</a-radio>
              <a-radio value="KAFKA">KAFKA</a-radio>
              <a-radio value="ELASTICSEARCH">ELASTICSEARCH</a-radio>
              <a-radio value="KYLIN">KYLIN</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item label="数据源名称" :required="true">
            <a-input v-decorator="['name', {rules:[{required: true, message: '请输入数据源名称'}]}]"/>
          </a-form-item>
          <a-row>
            <a-col :span="10" style="margin-left: 3%">
              <a-form-item label="数据源IP" :required="true" :label-col="{ span: 10 }" :wrapper-col="{ span: 14 }">
                <a-input v-decorator="['hostname', {rules:[{required: true, message: '请输入IP'}]}]"/>
              </a-form-item>
            </a-col>
            <a-col :span="10" style="margin-left: 3%">
              <a-form-item label="端口" :required="true">
                <a-input v-decorator="['port', {rules:[{required: true, message: '请输入端口号'}]}]"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row >
            <a-col :span="10" style="margin-left: 3%">
              <a-form-item
                label="用户名"
                :label-col="{ span: 10 }"
                :wrapper-col="{ span: 14 }"
                :required="type == 'MYSQL'   || type == 'KYLIN' ">
                <a-input v-decorator="['user', {rules:[{required: type == 'MYSQL' || type == 'KYLIN', message: '请输入用户名'}]}]"/>
              </a-form-item>
            </a-col>
            <a-col :span="10" style="margin-left: 3%">
              <a-form-item label="密码" :required="type == 'MYSQL'  || type == 'KYLIN'">
                <a-input v-decorator="['password', {rules:[{required: type == 'MYSQL' || type == 'KYLIN' , message: '请输入密码'}]}]"/>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="数据库名" :required="type == 'MYSQL' || type == 'HIVE' || type == 'KAFKA'" v-show="type == 'MYSQL' || type == 'HIVE' || type == 'KAFKA'">
            <a-input v-decorator="['database', {rules:[{required: type == 'MYSQL' || type == 'HIVE' || type == 'KAFKA', message: '请输入库名/目录名'}]}]"/>
          </a-form-item>
          <a-form-item label="数据源描述">
            <a-textarea v-decorator="['note', {rules:[{required: false, message: '请输入数据源名称'}]}]"/>
          </a-form-item>
          <a-form-item label="jdbc连接参数" v-show="type == 'MYSQL' || type == 'HIVE'">
            <a-textarea
              style="width: 100%"
              v-decorator="['other', {rules:[{required: false, message: '请输入库名/目录名'}]}]"
              :placeholder="_rtOtherPlaceholder()"/>
          </a-form-item>
        </a-form>
      </a-spin>
    </div>
    <template slot="footer">
      <a-button @click="close">
        取消
      </a-button>
      <a-button
        style="background: #00e064;border-color: #00e064;"
        type="primary"
        @click="testConnection">连接测试
      </a-button>
      <a-button type="primary" @click="onOk">
        保存
      </a-button>
    </template>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import { testAiDataSource, updateAiDataSource, createAiDataSource, getUpdateById } from '../../../../api/datasource'
  import ARadioGroup from 'ant-design-vue/es/radio/Group'

  export default {
    name: 'DatasourceEdit',
    components: { ARadioGroup },
    props: {
      record: Object
    },
    data () {
      return {
        /**
         * 初始化一个form表单
         */
        visible: false,
        form: this.$form.createForm(this),
        spinning: false,
        type: '',
        dataSourceTypes: [],
        title: ''
      }
    },
    mounted () {
      console.log('this.', this.record)
      if (this.record) {
        var a = this.record.connectionParams
        var b = JSON.parse(a)
        this.type = this.record.type
        this.form.setFieldsValue(pick(this.record, ['id']))
        this.form.setFieldsValue(pick(this.record, ['name']))
        this.form.setFieldsValue(pick(this.record, ['note']))
        this.form.setFieldsValue(pick(this.record, ['type']))
        this.form.setFieldsValue(pick(b, ['port']))
        this.form.setFieldsValue(pick(b, ['hostname']))
        this.form.setFieldsValue(pick(b, ['user']))
        this.form.setFieldsValue(pick(b, ['password']))
        this.form.setFieldsValue(pick(b, ['database']))
        this.form.setFieldsValue(pick(b, ['other']))
      }
    },
    methods: {
      edit (record) {
        this.form.resetFields()
        this.visible = true
        const a = record.connectionParams
        const b = JSON.parse(a)
        this.type = record.type
        this.title = '编辑数据源'
        var  other=""
        getUpdateById({"id":record.id})
          .then((res) => {
            if (res.code === 0) {
            let  otherObject =res.data.other
              other= JSON.stringify(otherObject)
              // alert(other)
              if ("{}" != other){
                this.form.setFieldsValue({'other':other})
              }
            } else {
              this.$message.error('获取信息失败')
            }
          })
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(record, ['id']))
          this.form.setFieldsValue(pick(record, ['name']))
          this.form.setFieldsValue(pick(record, ['note']))
          this.form.setFieldsValue(pick(record, ['type']))
          this.form.setFieldsValue(pick(b, ['port']))
          this.form.setFieldsValue(pick(b, ['hostname']))
          this.form.setFieldsValue(pick(b, ['user']))
          this.form.setFieldsValue(pick(b, ['password']))
          this.form.setFieldsValue(pick(b, ['database']))
          // this.form.setFieldsValue({'other':other})
        })
      },
      add () {
        this.form.resetFields()
        this.visible = true
        this.title = '新增数据源'
      },
      close () {
        // this.$emit('ok')
        this.visible = false
      },
      onOk (e) {
        const that = this
        return new Promise(resolve => {
          // const { form: { validateFields } } = this
          this.form.validateFields((errors, values) => {
            if (!errors) {
              this.spinning = true
              const dataSourceParams = this._rtParam(values)
              if (dataSourceParams.id !== '') {
                updateAiDataSource(dataSourceParams)
                  .then((res) => {
                    this.spinning = false
                    if (res.code === 0) {
                      this.$message.success('保存成功')
                      that.close()
                    } else {
                      this.$message.error('保存失败')
                    }
                    that.$emit('refresh')
                    resolve(true)
                  }).catch(err => {
                  this.$message.error('保存失败')
                  console.log('fail', err)
                }).finally(() => {
                  // do nothing
                })
              } else {
                createAiDataSource(dataSourceParams)
                  .then((res) => {
                    this.spinning = false
                    if (res.code === 0) {
                      this.$message.success('创建成功')
                      that.close()
                      that.$emit('refresh')
                    } else {
                      debugger
                      this.$message.error(res.msg)
                    }
                    resolve(true)
                  }).catch(err => {
                    debugger
                  this.spinning = false
                  this.$message.error("请求错误")
                  console.log(err)
                }).finally(() => {
                  // do nothing
                })
              }
            } else {
              this.$message.error("填写项不正确")
            }
            // that.$emit('refresh')
            // that.close()
          })
          // that.$emit('refresh')
          // that.close()
        })
      },
      onCancel () {
        return new Promise(resolve => {
          resolve(true)
        })
      },
      _rtParam (values) {
        return {
          id: values.id == null ? '' : values.id,
          type: this.type,
          name: values.name,
          note: values.note == null ? '' : values.note,
          host: values.hostname,
          port: values.port,
          database: values.database,
          userName: values.user == null? '' : values.user,
          password: values.password == null? '' : values.password,
          other: values.other == null ? '' : values.other
        }
      },
      /**
       * 连接测试
       * */
      testConnection () {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.spinning = true
            const abc = this._rtParam(values)
            // debugger
            testAiDataSource(abc)
              .then((res) => {
                setTimeout(() => {
                  this.spinning = false
                  if (res.code === 0) {
                    this.$message.success('测试成功')
                  } else {
                    this.$message.error(res.msg)
                  }
                }, 800)
              }).catch(err => {
              this.$message.error('请求错误')
              console.log(err.error, err.message)
              this.spinning = false
            })
          } else {
            console.log('信息填写不正确')
            this.$message.error('信息填写不正确')
          }
        })
      },
      _rtOtherPlaceholder () {
        return `请输入格式为 {"key1":"value1","key2":"value2"...} 的参数`
      }
    },
    created () {
      // this.getDtasourceType()
    }
  }
</script>
<style>
  .data-source-form .ant-form-item {
    margin-bottom: 0px;
  }
</style>
