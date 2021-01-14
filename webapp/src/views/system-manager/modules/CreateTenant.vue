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
        <a-form-item  label="租户名称">
          <a-input v-decorator="['tenantName', {rules: [{required: true, message: '请输入租户名'}]}]" />
          <a-input v-decorator="['id']" v-show="false"/>
        </a-form-item>
        <a-form-item label="租户编码">
          <a-input v-decorator="['tenantCode', {rules: [{required: true, message: '请输入租户编码'}]}]" />
        </a-form-item>
        <a-form-item label="队列">
          <a-select show-search optionFilterProp="children" v-decorator="['queueId', {rules: [{required: true, message: '请选择队列'}]}]">
            <a-select-option v-for="item in queueList" :key="item.id" :value="item.id"> {{ item.queueName }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea rows='4' v-decorator="['desc', {rules: [{ required: false }]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { tenantCreate, tenantUpdate, queueList } from '@/api/system'

// 表单字段
const fields = ['id', 'tenantName', 'tenantCode', 'queueId', 'desc']

export default {
  data () {
    return {
        visible: false,
        loading: false,
        title: '',
        queueList: [],
        form: this.$form.createForm(this)
    }
  },
  methods: {
      show () {
          this.form.resetFields()
          this.title = '新建租户'
          this.visible = true
          this.form.resetFields()
      },
      edit (item) {
          this.title = '编辑租户'
          this.visible = true
          const that = this
          that.$nextTick(() => {
              that.form.setFieldsValue(item)
          })
      },
      handleOk () {
          this.loading = true
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  if (values.id > 0) {
                      tenantUpdate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success('更新租户成功')
                              this.visible = false
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      tenantCreate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success('添加租户成功')
                              this.visible = false
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  }
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
      queueList().then(res => {
        this.queueList = res.data
    })
  }
}
</script>
