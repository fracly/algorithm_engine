<template>
  <iframe :style="styleObj" sandbox="allow-scripts allow-same-origin" name="yarnFrame" frameborder="0" src="/cluster?user.name=admin" seamless ></iframe>
</template>
<script>
    export default {
        name: 'MonitorYarn',
        data () {
            return {
                styleObj: {
                    width: '100%',
                    height: '0'
                }
            }
        },
        created () {
            this.styleObj.height = window.screen.height * 0.89 + 'px'
        }
    }
</script>
