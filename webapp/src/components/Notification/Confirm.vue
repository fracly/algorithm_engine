<template>
  <div style="display: inline">
    <a href="javascript:;" :disabled="disabled" @click="handleClick" v-show="isShow">删除</a>
    <a-modal
      :title="title"
      :visible="visible"
      @ok="handleOk"
      @cancel="handleCancel"
      :closable="false">
      <div class="confirmModal"><a-icon type="exclamation-circle"/><span>{{ content }}</span></div>
    </a-modal>
  </div>

</template>

<script>
    export default {
        name: 'Confirm',
        props: {
            title: {
                type: String,
                default: '确认删除'
            },
            content: {
                type: String,
                required: true
            },
            disabled: {
                type: Boolean,
                default: false
            },
          isShow: {
            type: Boolean,
            default: true
          }
        },
        components: {
        },
        data () {
            return {
                visible: false
            }
        },
        methods: {
            handleOk () {
                this.$emit('confirm')
                this.visible = false
            },
            handleCancel: function () {
                this.visible = false
            },
            handleClick: function () {
                this.visible = true
            }
        }
    }
</script>

<style>
  .confirmModal svg {
    font-size: 20px;
    margin-right:15px;
    color: 	#B80000;
  }
  .confirmModal span {
    font-size: 14px;
  }
</style>
