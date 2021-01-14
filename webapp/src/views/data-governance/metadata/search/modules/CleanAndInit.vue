<template>
  <a-modal
    title="请确认操作"
    :width="400"
    :visible="visible"
    :confirmLoading="spinning"
    :closable="false"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <h2>确定清空版本信息并进行初始化？ <br />此操作可能会消耗几分钟左右的时间！</h2>
  </a-modal>
</template>

<script>
import { cleanAndInit } from '@/api/metadata'
    export default {
        name: 'TableDetail',
        data () {
            return {
                visible: false,
                spinning: false
            }
        },
        created () {
        },
        methods: {
            show () {
                this.visible = true
            },
            handleOk () {
                this.spinning = true
                const _this = this
                cleanAndInit().then(res => {
                    if (res.code === 0) {
                        _this.$message.success('清档成功')
                    } else {
                        _this.$message.error('清档失败')
                    }
                    _this.spinning = false
                    _this.visible = false
                })
            },
            handleCancel () {
                this.visible = false
            }
        }
    }
</script>
