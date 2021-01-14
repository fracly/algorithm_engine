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
        <a-form-item  label="组名">
          <a-input v-decorator="['name', {rules: [{required: true, message: '请输入组名'}]}]" />
          <a-input v-decorator="['id']" v-show="false"/>
        </a-form-item>
        <a-form-item label="IP列表">
          <a-input v-decorator="['ipList', {rules: [{required: true, message: '请输入IP列表'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { workerGroupCreate } from '@/api/system'

// 表单字段
const fields = ['name', 'ipList']

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
          this.title = '新建worker分组'
          this.visible = true
          this.form.resetFields()
      },
      edit (item) {
          this.title = '编辑worker分组'
          this.visible = true
          const that = this
          that.$nextTick(() => {
              that.form.setFieldsValue(item)
          })
      },
      handleOk () {
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  debugger
                  this.loading = true
                  if (values.id > 0) {
                      workerGroupCreate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success('更新worker分组成功')
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      workerGroupCreate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success('添加worker分组成功')
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  }
                  this.visible = false
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
