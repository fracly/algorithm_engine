<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :closable="false"
    :confirmLoading="loading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-spin :spinning="loading">
      <a-form :form="form">
        <a-form-item label="模型组">
          <a-select show-search optionFilterProp="children" :disabled="!isCreate" v-decorator="['projectName', { rules: [{ required: true, message: '请选择模型组'}]}]" @change="handleGroupChange($event)">
            <a-select-option v-for="item in groupList" :key="item.id" :name="item.name" :value="item.name">
              {{ item.name }}
            </a-select-option>
          </a-select>
          <a-input type="hidden" v-decorator="['id', {rules: [{ required: false }]}]" hidden></a-input>
        </a-form-item>
        <a-form-item label="模型">
          <a-select show-search optionFilterProp="children" :disabled="!isCreate" v-decorator="['model', { rules: [{ required: true, message: '请选择模型'}]}]">
            <a-select-option v-for="item in modelList" :key="item.id" :name="item.name" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="起止时间">
          <a-range-picker v-decorator="['timeRange', {rules: [{required: true, message: '请选择起止时间'}]}]"  format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item>
        <a-form-item label="cron表达式">
          <j-cron ref="innerVueCron" v-decorator="['crontab', {rules: [{ required: true, message: '请输入cron表达式!' }]}]" @change="setCorn"></j-cron>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { projectList, processDefinitionList, createSchedule, updateSchedule } from '@/api/model'
  import JCron from './JCron'

// 表单字段
const fields = ['id', 'projectName', 'model', 'startTime', 'endTime', 'crontab']

export default {
    components: {
        JCron
    },
  data () {
    return {
        visible: false,
        loading: false,
        isCreate: true,
        crontab: '',
        groupList: [],
        modelList: [],
        form: this.$form.createForm(this),
        input: '',
        expression: '',
        showCron: false,
        title: ''
    }
  },
  methods: {
      show () {
          this.isCreate = true
          this.title = '新建定时任务'
          this.visible = true
          const that = this
          this.form.resetFields()
          projectList().then(res => {
              that.groupList = res.data
          })
      },
      edit (item) {
          this.title = '编辑定时任务'
          this.isCreate = false
          this.visible = true
          const that = this
          this.form.resetFields()
          projectList().then(res => {
              that.groupList = res.data
              processDefinitionList({ 'projectName': item.projectName }).then(res => {
                  that.modelList = res.data
                  that.form.setFieldsValue(item)
              })
          })
      },
      setCorn (data) {
          this.$nextTick(() => {
              this.form.setFieldsValue({ 'crontab': data })
          })
      },
      handleGroupChange (projectName) {
          const that = this
          const params = { 'projectName': projectName }
          processDefinitionList(params).then(res => {
              that.modelList = res.data
          })
      },
      handleOk () {
          this.form.validateFields((errors, values) => {
              if (!errors) {
                  this.loading = true
                  const schedule = {}
                  schedule.crontab = values.crontab
                  schedule.startTime = values.timeRange[0].format('YYYY-MM-DD HH:mm:ss')
                  schedule.endTime = values.timeRange[1].format('YYYY-MM-DD HH:mm:ss')
                  const formData = new FormData()
                  formData.append('schedule', JSON.stringify(schedule))
                  formData.projectName = values.projectName
                  if (values.id > 0) {
                      formData.append('id', values.id)
                      updateSchedule(formData).then(res => {
                          if (res.code === 0) {
                              this.$message.success('更新调度成功')
                          } else {
                              this.$message.error('更新调度失败')
                          }
                          this.$emit('refresh')
                      })
                  } else {
                      formData.append('processDefinitionId', values.model)
                      createSchedule(formData).then(res => {
                          if (res.code === 0) {
                              this.$message.success('创建调度成功')
                          } else {
                              this.$message.error('创建调度失败')
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
