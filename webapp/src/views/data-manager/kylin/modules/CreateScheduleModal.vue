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
        <a-form-item label="CUBE模型">
          <a-select
            show-search
            optionFilterProp="children"
            :disabled="!isCreate"
            placeholder="请选择CUBE"
            v-decorator="['cubeName', { rules: [{ required: true, message: '请选择CUBE'}]}]">
            <a-select-option v-for="item in groupList" :key="item.name" :name="item.name" :value="item.name">
              {{ item.name }}
            </a-select-option>
          </a-select>
          <a-input type="hidden" v-decorator="['id', {rules: [{ required: false }]}]" hidden></a-input>
        </a-form-item>
        <a-form-item label="CUBE运行周期">
          <a-input v-if="!isSaveFixedTime" :disabled="!isSaveFixedTime" placeholder="请输入运行周期(天)" style="width: 50%;margin-right: 5px" v-decorator="['type', {rules: [{required: true, message: '请输入运行周期(天)'}]}]"/>
          是否固定时间：<a-switch @change="isSave"/>
          <a-radio-group v-if="isSaveFixedTime" style="margin-left: 5px" v-decorator="['type', {initialValue: '1', rules: [{required: true, message: '报表类型必须选择'}] }]">
            <a-radio value="M">本月</a-radio>
            <a-radio value="LM">上月</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="CUBE运行时分秒">
          <a-time-picker :defaultValue="defaultValue" v-decorator="['time', {rules: [{required: true, message: '请选择起止时间'}]}]" format="HH:mm:ss"/>
        </a-form-item>
        <a-form-item label="CRON起止时间">
          <a-range-picker
            style="width: 100%"
            v-decorator="['timeRange', {rules: [{required: true, message: '请选择起止时间'}]}]"
            format="YYYY-MM-DD HH:mm:ss"/>
        </a-form-item>
        <a-form-item label="CRON表达式">
          <j-cron
            ref="innerVueCron"
            v-decorator="['crontab', {rules: [{ required: true, message: '请输入cron表达式!' }]}]"
            @change="setCorn"></j-cron>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { queryCubeList, createSchedule, updateSchedule } from '@/api/kylin'
  import JCron from './JCron'
  import Moment from 'moment'

  // 表单字段
  const fields = ['id', 'cubeName', 'startTime', 'endTime', 'crontab']

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
        title: '',
        defaultValue: '',
        isSaveFixedTime: false

      }
    },
    methods: {
      show () {
        this.isCreate = true
        this.title = '新建定时任务'
        this.visible = true
        const that = this
        this.form.resetFields()
        this.defaultValue = Moment('00:00:00', 'HH:mm:ss')
        queryCubeList().then(res => {
          that.groupList = res.data
          this.form.setFieldsValue({ 'type': '1' })
        })
      },
      edit (item) {
        this.title = '编辑定时任务'
        this.isCreate = false
        this.visible = true
        const that = this
        this.form.resetFields()
        this.defaultValue = Moment('00:00:00', 'HH:mm:ss')
        if (item.type === 'LM' || item.type === 'M') {
         this.isSaveFixedTime = true
        } else {
          this.isSaveFixedTime = false
        }
        queryCubeList().then(res => {
          that.groupList = res.data
          that.form.setFieldsValue(item)
        })
        if (item.crontab) {
          this.daysReverseExp(item.crontab)
        }
      },
      setCorn (data) {
        this.$nextTick(() => {
          this.form.setFieldsValue({ 'crontab': data })
        })
      },
      handleOk () {
        if (!this.form.getFieldValue('time')) {
          this.form.setFieldsValue({ 'time': Moment('00:00:00', 'HH:mm:ss') })
        }
        this.form.validateFields((errors, values) => {
          if (!errors) {
            this.loading = true
            const schedule = {}
            schedule.crontab = values.crontab
            schedule.startTime = values.timeRange[0].format('YYYY-MM-DD HH:mm:ss')
            schedule.endTime = values.timeRange[1].format('YYYY-MM-DD HH:mm:ss')
            const params = {}
            params.schedule = JSON.stringify(schedule)
            params.time = values.time.format('HH:mm:ss')
            if (this.isSaveFixedTime) {
              params.type = values.type
            } else {
              params.type = this.daysReverseExp(values.crontab)
            }
            params.cubeName = values.cubeName
            if (values.id > 0) {
              params.id = values.id
              updateSchedule(params).then(res => {
                if (res.code === 0) {
                  this.$message.success('更新调度成功')
                } else {
                  this.$message.error('更新调度失败')
                }
                this.$emit('refresh')
              })
            } else {
              createSchedule(params).then(res => {
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
      },
      isSave (checked) {
        this.isSaveFixedTime = checked
        if (checked) {
          this.form.setFieldsValue({ 'type': '' })
        } else {
          this.form.setFieldsValue({ 'type': '1' })
        }
      },
      daysReverseExp (cron) {
        const days = cron.split(' ')[3]
        const day = {
          cronEvery: '',
          incrementStart: 1,
          incrementIncrement: 1,
          rangeStart: 1,
          rangeEnd: 1,
          specificSpecific: [],
          cronLastSpecificDomDay: 1,
          cronDaysBeforeEomMinus: 1,
          cronDaysNearestWeekday: 1
        }
        let dayCount = 0
        if (!days.includes('?')) {
          switch (true) {
            case days.includes('*'):
              day.cronEvery = '1'
              dayCount = 1
              break
            case days.includes('?'):
              // 2、4、11
              break
            case days.includes('/'):
              day.cronEvery = '3'
              day.incrementStart = days.split('/')[0]
              day.incrementIncrement = days.split('/')[1]
              dayCount = parseInt(day.incrementIncrement) + 1
              break
            case days.includes(','):
              day.cronEvery = '5'
              day.specificSpecific = days.split(',').map(Number).sort()
          }
        } else {
          switch (true) {
            default:
              day.cronEvery = '4'
              dayCount = 7
          }
        }
        console.log(dayCount)
        return dayCount
      }

    },
    created () {
      // 防止表单未注册
      fields.forEach(v => this.form.getFieldDecorator(v))
    }
  }
</script>
