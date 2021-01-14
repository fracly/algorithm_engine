<template>
  <div>
    <a-form>
      <iframe :src="url" style="min-width:1200px;height: 800px;margin-left: 5%;border: 1px solid"></iframe>
      <a-form-item :wrapperCol="{span: 8, offset: 10}">
        <a-button  @click="prevStep">上一步</a-button>
        <a-button style="margin-left: 8px" :loading="loading" type="primary" @click="nextStep">发布</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
    import { editStatusReport } from '@/api/report'

    export default {
  name: 'Step3',
  data () {
    return {
      loading: false,
      url: ''
    }
  },
    props: [
        'reportInfo'
    ],
  methods: {
      nextStep () {
        this.loading = true
        this.$emit('nextStep')
      },
      prevStep () {
          editStatusReport({ 'id': this.reportInfo.id, status: 1 }).then(res => {
              if (res.code === 0) {
                  this.$emit('prevStep')
              } else {
                  this.$message.error(res.msg)
              }
          })
      }
  },
    mounted () {
        this.url = Glod.dsjmh + '/#/reportPreview/' + this.reportInfo.code + '?status=1'
    }
}
</script>
<style lang="less" scoped>
  .information {
    line-height: 22px;

    .ant-row:not(:last-child) {
      margin-bottom: 24px;
    }
  }
  .money {
    font-family: "Helvetica Neue",sans-serif;
    font-weight: 500;
    font-size: 20px;
    line-height: 14px;
  }
</style>
