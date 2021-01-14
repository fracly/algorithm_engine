<template>
  <a-modal
    title="权限分配"
    :width="640"
    :visible="visible"
    :confirmLoading="loading"
    :closable="false"
    @ok="() => { $emit('ok', this.selectedRowKeys) }"
    @cancel="() => { $emit('cancel') }"
  >
    <a-spin :spinning="loading">
      <a-table :columns="columns" :data-source="list" :pagination="false" :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, rowKey: 'id' }" />
    </a-spin>
  </a-modal>
</template>

<script>

const columns = [
    {
        title: '#',
        dataIndex: 'id',
        key: 'id'
    },
    {
        title: '菜单名称',
        dataIndex: 'name',
        key: 'name'
    },
    {
        title: '菜单代码',
        dataIndex: 'code',
        key: 'code'
    }]

export default {
  props: {
    visible: {
      type: Boolean,
      required: true
    },
    loading: {
      type: Boolean,
      default: () => false
    },
    list: {
      type: Array,
      default: () => []
    },
    selectedRowKeys: {
        type: Array,
        default: () => []
    }
  },
  data () {
    return {
        form: this.$form.createForm(this),
        columns
    }
  },
    created () {
    },
    computed: {
    },
    methods: {
        onSelectChange (selectedRowKeys) {
            console.log('selectedRowKeys changed: ', selectedRowKeys)
            this.selectedRowKeys = selectedRowKeys
        }
    }
}
</script>
