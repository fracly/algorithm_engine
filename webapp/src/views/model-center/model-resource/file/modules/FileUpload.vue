<template>
  <a-modal
    centered
    v-model="visible"
    :width="800"
    :maskClosable="false"
    :title="title"
  >
    <template slot="footer">
      <a-button type="default" :disabled="testing" @click="handleCancel">返回</a-button>
      <a-button type="primary" v-show="!testing" @click="handleOk">确定</a-button>
      <a-button v-if="testing"  type="primary" loading>Loading</a-button>
    </template>
    <a-form :form="form">
      <a-form-item
        :required="true"
        label="文件名称"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-input
          placeholder="请输入文件名称"
          v-decorator="['name', {rules: [{required: true, message: '文件名称不能为空'}] }]"/>
      </a-form-item>
      <a-form-item
        label="描述"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
      >
        <a-textarea
          placeholder="请输入描述"
          :auto-size="{ minRows: 2 }"
          v-decorator="['desc',{initialValue:''}]"/>
      </a-form-item>
      <a-form-item
        :required="true"
        label="文件类型"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        class="stepFormText"
        v-if="isAdd"
      >
        <a-select placeholder="请选择文件类型" v-decorator="['type', {rules: [{required: true, message: '文件类型不能为空'}] }]">
          <a-select-option value="FILE">文件</a-select-option>
          <a-select-option value="UDF">函数</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item
        label="上传文件"
        :labelCol="labelCol"
        :wrapperCol="wrapperCol"
        v-if="isAdd"
      >
        <a-upload
          name="file"
          :before-upload="beforeUpload"
          :file-list="fileList"
          :remove="handleRemove"
          @change="handleChange"
          v-decorator="['file', {rules: [{required: true, message: '必须上传文件'}] }]"
        >
          <a-button>
            <a-icon type="upload"/>
            上传
          </a-button><span style="color: #bfbfbf">  文件大小不能超过500MB</span>
          <a-progress :percent="per" v-show="testing"/>
        </a-upload>
      </a-form-item>

    </a-form>

  </a-modal>
</template>

<script>
  import { createFile, resourceRename } from '@/api/resources'
  import TagSelectOption from '../../../../../components/TagSelect/TagSelectOption'
  import { axios } from '@/utils/request'

  export default {
    name: 'FileUpload',
    components: { TagSelectOption },
    data () {
      return {
        stepLoading: false,
        form: this.$form.createForm(this, { name: 'FileUpload' }),
        labelCol: { lg: { span: 5 }, sm: { span: 5 } },
        wrapperCol: { lg: { span: 16 }, sm: { span: 16 } },
        fileList: [],
        visible: false,
        title: '',
        isAdd: false,
        item: {},
        testing: false,
        per: 0
      }
    },
    methods: {
      add () {
        this.$nextTick(() => {
          setTimeout(() => {
            this.form.resetFields()
            this.fileList = []
            this.visible = true
            this.title = '文件上传'
            this.isAdd = true
          }, 1000)
        })
      },
      edit (item) {
        const file = {
         name: item.alias,
         desc: item.desc
        }
        this.$nextTick(() => {
          setTimeout(() => {
            this.form.setFieldsValue({ ...file })
          }, 100)
        })
        this.visible = true
        this.isAdd = false
        this.title = '重命名'
        this.item = item
      },
      handleOk () {
        const self = this
        this.stepLoading = true
        this.form.validateFields((err, values) => {
          if (!err) {
            if (self.isAdd) {
              const formData = new FormData()
              formData.append('name', values.name)
              formData.append('desc', values.desc)
              formData.append('file', self.fileList[0])
              formData.append('type', values.type)
              this.testing = true
              return new Promise((resolve, reject) => {
                axios({
                  url: 'resources/create',
                  method: 'post',
                  data: formData,
                  timeout: 0,
                  emulateJSON: false,
                  onUploadProgress (progressEvent) {
                    const loaded = progressEvent.loaded
                    const total = progressEvent.total
                    self.per = Math.floor(100 * loaded / total)
                  }
                }).then(res => {
                  if (res.code === 0) {
                    this.testing = false
                    this.$message.info('文件上传成功')
                    self.handleCancel()
                    self.$emit('refresh')
                  } else {
                    this.$message.error(res.msg)
                  }
                }).catch(e => {
                  this.testing = false
                  this.$message.error(e.message)
                })
              }).catch(error => {
                this.$message.error(error)
              })
            } else {
              const file = {
                name: values.name,
                desc: values.desc,
                id: self.item.id,
                type: self.item.type
              }
              resourceRename(file).then(res => {
                if (res.code === 0) {
                  this.$message.info('重命名成功')
                  self.handleCancel()
                  self.$emit('refresh')
                } else {
                  this.$message.error(res.msg)
                }
              }).catch(e => {
                this.$message.error(e.msg)
              })
            }
          }
          this.stepLoading = false
          this.$emit('error', { err })
        })
      },
      handleCancel () {
        this.visible = false
        this.$emit('cancel')
      },
      handleRemove (file) {
        const index = this.fileList.indexOf(file)
        const newFileList = this.fileList.slice()
        newFileList.splice(index, 1)
        this.fileList = newFileList
      },
      handleChange (info) {
        this.fileList = this.fileList.slice(-1)
        this.form.setFieldsValue({ name: info.file.name })
      },
      beforeUpload (file) {
        this.fileList = [...this.fileList, file]
        return false
      }
    }
  }
</script>
<style lang="less" scoped>
</style>
