<template>
  <div>
    <a-form :form="form" style="max-width: 1000px; margin: 40px auto 0;">
      <a-form-item
        label="报表名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          placeholder="长度为6-16个字符，例如：查询车辆违法统计"
          v-decorator="['name', {rules: [{required: true, message: '报表名称不能为空，且长度为6-16个字符',min:6,max:16,whitespace:true}] }]"/>
      </a-form-item>
      <a-form-item
        label="报表编码"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-input
          placeholder="由字母、或数字、或下划线组成，长度不超过30个字符"
          v-decorator="['code', {rules: [{required: true,
                                          message: '报表编码由字母、或数字、或下划线组成，长度不超过30个字符',max:30,whitespace:true},{validator: checkCode}] }]"/>
      </a-form-item>
      <a-form-item
        label="报表类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-radio-group v-decorator="['type', {initialValue: '1', rules: [{required: true, message: '报表类型必须选择'}] }]">
          <a-radio value="1">excel-统计</a-radio>
          <a-radio value="2">excel-清单</a-radio>
          <a-radio value="3">word</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item
        label="报表表头"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-upload
          name="file"
          :customRequest="upload"
          accept=".xls"
          :file-list="fileList"
          @preview="downloadFile"
          :remove="handleRemove"
          @change="handleChange"
          v-decorator="['file', {rules: [{required: true, message: '报表表头必须上传文件'}] }]"
        >
          <a-button> <a-icon type="upload" /> 选择文件 </a-button>
        </a-upload>
      </a-form-item>

      <a-form-item
        label="报表权限"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-checkbox-group :options="rolesList" v-decorator="['roleId', {initialValue:[roleInit], rules: [{required: true, message: '报表类型必须选择'}] }]">
        </a-checkbox-group>
      </a-form-item>
      <a-form-item
        label="报表分组"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
      >
        <a-select
          placeholder="请选择报表分组"
          v-decorator="['groupId', { rules: [{required: true, message: '报表分组必须选择'}] }]"
        >
          <a-select-option
            v-for="(item) in groupsList"
            :key="item.id"
            :value="item.id">{{ item.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item :wrapperCol="{span: 8, offset: 10}">
        <a-button  type="prevStep" @click="back">返回</a-button>
        <a-button style="margin-left: 8px" type="primary" @click="nextStep">下一步</a-button>
      </a-form-item>
    </a-form>
    <a-divider/>
  </div>
</template>

<script>
    import { getGroupsList, getRolesList, uploadFile, createReport } from '@/api/report'
    // import pick from 'lodash.pick'
    import { downloadFile } from '@/utils/download'

    export default {
        name: 'Step1',
        data () {
            return {
                labelCol: { lg: { span: 5 }, sm: { span: 5 } },
                wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
                form: this.$form.createForm(this),
                groupsList: [],
                rolesList: [],
                fileList: [],
                info: {
                    path: '',
                    id: '',
                    status: 0,
                    origin_file_name: ''
                },
                roleInit: ''
            }
        },
        props: [
            'reportInfo'
        ],
        methods: {
            // 下一步
            nextStep () {
                const { form: { validateFields } } = this
                const self = this
                // 先校验，通过表单校验后，才进入下一步
                validateFields((err, values) => {
                    if (!err) {
                        for (const key in values) {
                            self.info[key] = values[key]
                        }
                        self.info.roleId = self.info.roleId.toString()
                      // 新建或者修改报表
                        createReport(self.info).then(res => {
                        if (res.code === 0) {
                          self.info.id = res.data
                          self.$emit('nextStep')
                        } else {
                          this.$message.error(res.msg)
                        }
                      }).catch(e => {
                        this.$message.error(e.msg)
                      })
                    }
                })
            },
            // 返回
            back () {
                this.$router.push({ name: 'ReportList' })
            },
            groupList () {
                getGroupsList().then(res => {
                    this.groupsList = res.data
                })
            },
            roleList () {
                const self = this
                getRolesList().then(res => {
                    const result = res.data
                    this.rolesList = result.map(option => {
                        if (option.name === '超级管理员') {
                            // self.form.setFieldsValue(pick(option.id, ['roleId']))
                            self.roleInit = option.id
                            return {
                                label: option.name,
                                value: option.id,
                                disabled: true
                            }
                        } else {
                            return {
                                label: option.name,
                                value: option.id
                            }
                        }
                    })
                })
            },
            handleChange (info) {
                this.fileList = []
                this.fileList.push(info.file)
            },
            handleRemove (file) {
                // const index = this.fileList.indexOf(file)
                // const newFileList = this.fileList.slice()
                // newFileList.splice(index, 1)
                // this.fileList = newFileList
                // this.fileList = []
                return false
            },
            downloadFile () {
              downloadFile('/escheduler/reportFrom/download', {
                name: this.reportInfo.path.split('/')[this.reportInfo.path.split('/').length - 1]
              })
            },
            upload (data) {
                const formData = new FormData()
                const self = this
                this.info.origin_file_name = data.file.name
                formData.append('file', data.file)
                uploadFile(formData).then(res => {
                  if (res.code === 0) {
                    self.info.path = res.data
                    data.file.status = 'done'
                    self.$message.success(`${data.file.name} 文件上传成功`)
                    self.handleChange(data)
                  } else {
                    self.info.path = ''
                    self.$message.error(res.msg)
                  }
                }).catch(function (e) {
                    self.$message.error(`${data.file.name} 文件上传失败.`)
                })
            },
            resetFormData () {
                this.form.resetFields()
                this.fileList = []
            },
            getReportData () {
               return this.info
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
            }
            },
        created () {
            this.groupList()
            this.roleList()
        },
        mounted () {
          if (this.reportInfo) {
            this.fileList = [{
              uid: '-1',
              status: 'done',
              name: this.reportInfo.origin_file_name,
              url: this.reportInfo.path
            }]
            this.info = {
              name: this.reportInfo.name,
              code: this.reportInfo.code,
              file: this.fileList[0],
              type: this.reportInfo.type,
              roleId: this.reportInfo.roleId.split(','),
              groupId: this.reportInfo.groupId
            }
            this.form.setFieldsValue({ ...this.info })
            this.info = this.reportInfo
            if (this.reportInfo.param.length > 0 && typeof (this.reportInfo.param) !== 'string') {
              this.info.param = JSON.stringify(this.reportInfo.param)
            }
          }
        },
        watch: {
          reportInfo (val) {
            this.reportInfo = val
            if (this.reportInfo) {
              this.fileList = [{
                uid: '-1',
                status: 'done',
                name: this.reportInfo.origin_file_name ? this.reportInfo.origin_file_name : this.reportInfo.path.split('/')[this.reportInfo.path.split('/').length - 1],
                url: this.reportInfo.path
              }]
              this.reportInfo.origin_file_name = this.reportInfo.origin_file_name ? this.reportInfo.origin_file_name : this.reportInfo.path.split('/')[this.reportInfo.path.split('/').length - 1]
              this.info = {
                name: this.reportInfo.name,
                code: this.reportInfo.code,
                file: this.fileList[0],
                type: this.reportInfo.type,
                roleId: this.reportInfo.roleId.split(','),
                groupId: this.reportInfo.groupId
              }
              this.form.setFieldsValue({ ...this.info })
              this.info = this.reportInfo
              if (this.reportInfo.param.length > 0 && typeof (this.reportInfo.param) !== 'string') {
                this.info.param = JSON.stringify(this.reportInfo.param)
              }
            }
          }
        }
    }
</script>

<style lang="less" scoped>

</style>
