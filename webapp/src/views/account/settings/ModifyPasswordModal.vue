<template>
  <a-modal
    title="修改密码"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    @ok="() => { $emit('ok') }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-form :form="form" v-bind="formLayout">
        <a-form-item label="密码">
          <a-input type="password" v-decorator="['password1', {rules: [{required: true, message: '请输入密码'}]}]" />
        </a-form-item>
        <a-form-item label="确认密码">
          <a-input type="password" v-decorator="['password2', {rules: [{required: true, message: '请确认密码'}]}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
export default {
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    }
    // user: {
    //   type: Object,
    //   default: () => null
    // }
  },
  data () {
    this.formLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 }
      }
    }
    return {
      form: this.$form.createForm(this)
    }
  },
  created () {
  },
  watch: {
    visible (val) {
      this.form.resetFields()
    }
  }
}
</script>
