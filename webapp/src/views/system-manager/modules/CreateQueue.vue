<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="loading"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form :form="form">
        <a-form-item  label="名称">
          <a-input v-decorator="['queueName', {rules: [{required: true, message: '请输入队列名称'}]}]" />
          <a-input v-decorator="['id']" v-show="false"/>
        </a-form-item>
        <a-form-item label="队列值">
          <a-input v-decorator="['queue', {rules: [{required: true, message: '请输入队列值'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { queueCreate, queueUpdate, verifyQueue } from '@/api/system'

// 表单字段
const fields = ['id', 'queueName', 'queue']

export default {
  data () {
    return {
        visible: false,
        loading: false,
        title: '',
        form: this.$form.createForm(this)
    }
  },
  methods: {
      show () {
          this.title = '新增队列'
          this.visible = true
          this.form.resetFields()
      },
      edit (item) {
          this.title = '编辑队列'
          this.visible = true
          const that = this
          that.$nextTick(() => {
              that.form.setFieldsValue(item)
          })
      },
      handleOk () {
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  this.loading = true
                  if (values.id > 0) {
                      queueUpdate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success('更新队列成功')
                              this.visible = false
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      verifyQueue(values).then(res => {
                          if (res.code !== 0) {
                              this.$message.error(res.msg)
                              this.loading = false
                          } else {
                              queueCreate(values).then(res => {
                                  if (res.code === 0) {
                                      this.$message.success('添加队列成功')
                                      this.visible = false
                                  } else {
                                      this.$message.error(res.msg)
                                  }
                                  this.$emit('refresh')
                              })
                          }
                      })
                  }
                  this.loading = false
              } else {
                  this.loading = false
              }
          })
      },
      handleCancel () {
          this.visible = false
      }
  },
  created () {
    // 防止表单未注册
    fields.forEach(v => this.form.getFieldDecorator(v))
  }
}
</script>
