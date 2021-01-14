<template>
  <div>
    <a-form>
      <iframe :src="url" style="min-width:1200px;height: 800px;margin-left: 5%;margin-top: 20px;border: none;"></iframe>
    </a-form>
  </div>
</template>

<script>

  export default {
    name: 'ReportPreview',
    data () {
      return {
        loading: false,
        url: ''
      }
    },
    methods: {
    },
    mounted () {
      this.url = Glod.dsjmh + '/#/reportPreview/' + this.$route.params.code + '?status=1'
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
    font-family: "Helvetica Neue", sans-serif;
    font-weight: 500;
    font-size: 20px;
    line-height: 14px;
  }
</style>
