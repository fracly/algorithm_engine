<template>
  <a-card :bordered="false">
    <a-steps class="steps" :current="currentTab">
      <a-step title="基本信息" >
        <a-icon slot="icon" type="file-text" style="font-size: 16px"/>
      </a-step>
      <a-step title="数据源" >
        <a-icon slot="icon" type="database" />
      </a-step>
      <a-step title="预览" >
        <a-icon slot="icon" type="eye" /></a-step>
      <a-step title="发布" >
        <a-icon slot="icon" type="check" />
      </a-step>

    </a-steps>
    <div class="content">
      <step1 v-if="currentTab === 0" :reportInfo="reportInfo" @nextStep="nextStep"  @prevStep="prevStep" ref="step1"/>
      <step2 v-if="currentTab === 1" :reportInfo="reportInfo" @nextStep="nextStep" @prevStep="prevStep" ref="step2"/>
      <step3 v-if="currentTab === 2" :reportInfo="reportInfo" @nextStep="nextStep" @prevStep="prevStep"/>
      <step4 v-if="currentTab === 3" :reportInfo="reportInfo" @prevStep="prevStep" @finish="finish"/>
    </div>
  </a-card>
</template>

<script>
import Step1 from './ReportBasic'
import Step2 from './Datasource'
import Step3 from './ReportView'
import Step4 from './ReportRelease'
import { getReportInfo } from '@/api/report'

export default {
  name: 'ReportMain',
  components: {
    Step1,
    Step2,
    Step3,
    Step4
  },
  data () {
    return {
      currentTab: 0,
      // form
      reportInfo: null
    }
  },
  methods: {

    // handler
    nextStep () {
        if (this.currentTab === 0) {
            this.reportInfo = this.$refs.step1.getReportData()
        }
      if (this.currentTab < 4) {
        this.currentTab += 1
      }
    },
    prevStep () {
      if (this.currentTab > 0) {
        this.currentTab -= 1
      }
    },
    finish () {
      this.currentTab = 0
        this.reportInfo = null
    }
  },
    created () {
        if (this.$route.params.id) {
          getReportInfo({ id: this.$route.params.id }).then(res => {
            this.reportInfo = res.data
          }).catch(e => {
            this.$message.error(e.msg || '')
          })
        }
    }
}
</script>

<style lang="less" >
  .steps {
    max-width: 950px;
    margin: 16px auto;
    .ant-steps-item-icon svg {
      font-size: 24px;
    }
  }
</style>
