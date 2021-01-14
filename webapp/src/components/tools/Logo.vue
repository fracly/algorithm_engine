<template>
  <div class="logo">
    <router-link :to="{name:'Index'}">
      <LogoSvg alt="logo" />
      <h1 v-if="showTitle" style="margin-left: 26px;">{{ title }}</h1>
    </router-link>
  </div>
</template>

<script>
import LogoSvg from '@/assets/logo.svg?inline'
import { conf } from '@/api/system'

export default {
  name: 'Logo',
  components: {
    LogoSvg
  },
  props: {
    title: {
      type: String,
      default: '交管云脑-AI引',
      required: false
    },
    showTitle: {
      type: Boolean,
      default: true,
      required: false
    }
  },
   created () {
    conf({ key: 'SYS_TITLE' }).then(res => {
      this.title = res.data[0].KEY_VALUE.slice(0, 8)
      }
    )
  },
  methods: {
  }
}
</script>
