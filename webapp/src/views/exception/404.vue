<template>
  <a-result status="404" title="404" sub-title="Sorry, the page you visited does not exist.">
    <template #extra>
      <a-button type="primary" >
        <a href="javascript:;" @click="backLogin">返回登录页</a>
      </a-button>
    </template>
  </a-result>
</template>

<script>
import { ExceptionPage } from '@/components'

export default {
  components: {
    ExceptionPage
  },
  methods: {
    backLogin () {
        this.$router.push('/user/login')
    }
  }
}
</script>

<style scoped>

</style>
