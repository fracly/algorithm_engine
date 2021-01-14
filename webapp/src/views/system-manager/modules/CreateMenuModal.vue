<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="loading"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel">
    <a-spin :spinning="loading">
      <a-form :form="form">
        <a-form-item  label="父菜单">
          <a-select show-search optionFilterProp="children" placeholder="请选择父级菜单" v-decorator="['pid', {rules: [{required: true, message: '请选择父级菜单'}]}]">
            <a-select-option
              :key="menu.id"
              v-for="(menu) in pidList"
              :value="menu.id">{{ menu.name }}
            </a-select-option>
          </a-select>
          <a-input v-decorator="['id']" hidden />
        </a-form-item>
        <a-form-item  label="菜单名称">
          <a-input v-decorator="['name', {rules: [{required: true, message: '请输入菜单名称'}]}]" />
        </a-form-item>
        <a-form-item label="菜单代码">
          <a-input v-decorator="['code', {rules: [{required: true, message: '请输入菜单代码'}]}]" />
        </a-form-item>
        <a-form-item label="访问URL">
          <a-input v-decorator="['url', {rules: [{required: true, message: '请输入菜单对应URL'}]}]" />
        </a-form-item>
        <a-form-item label="菜单序号">
          <a-input v-decorator="['sort', {rules: [{required: true, message: '请输入菜单排序'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { systemMenuCreate, systemMenuUpdate, systemFlatMenuList } from '@/api/system'

// 表单字段
const fields = ['name', 'code', 'id', 'url', 'pid', 'sort']

export default {
  data () {
    return {
        title: '',
        visible: false,
        loading: false,
        pidList: [],
        id: 0,
        form: this.$form.createForm(this)
    }
  },
    methods: {
      show () {
          this.title = '新建菜单'
          this.form.resetFields()
          this.visible = true
      },
      edit (item) {
          this.title = '编辑菜单'
          this.visible = true
          const that = this
          that.$nextTick(() => {
              this.form.setFieldsValue(item)
          })
      },
      handleOk () {
          this.loading = true
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  if (values.id > 0) {
                      systemMenuUpdate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success(res.msg)
                          } else {
                              this.$message.error(res.msg)
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      systemMenuCreate(values).then(res => {
                          if (res.code === 0) {
                              this.$message.success(res.msg)
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
      const that = this
      systemFlatMenuList({ 'status': 0, 'name': '' }).then(res => {
          if (res.code === 0) {
              that.pidList.push({ 'name': '顶级菜单', 'id': 0 })
              res.data.forEach((item) => {
                  that.pidList.push({ 'name': item.name, 'id': item.id })
              })
          } else {
              console.error(res.msg)
          }
      })
  }
}
</script>
