<template>
  <div class="dep-list-model">
    <a-form-item
      label=""
      :labelCol="{ lg: { span: 5 }, sm: { span: 5 } }"
      :wrapperCol="{ lg: { span: 24 }, sm: { span: 24 } }"
      v-for="(el,i) in dependItemList"
      :key="i"
      class="stepFormText"
    >
      <a-select
        style="width: 15%;margin-left: 10px"
        placeholder="请选择列"
        v-model="el.name"
        show-search
        optionFilterProp="children"
      >
        <a-select-option
          v-for="g in definitionList"
          :key="g.name"
          :value="g.name">{{ g.name }}
        </a-select-option>
      </a-select>
      <a-select
        style="width: 14%;margin-left: 5px"
        placeholder="请选择拼接条件"
        v-model="el.conditions"
      >
        <a-select-option
          v-for="g in conditionsList"
          :key="g"
          :value="g">{{ g }}
        </a-select-option>
      </a-select>
      <a-input
        style="width: 18%;margin-left: 5px"
        v-model="el.example"
        placeholder="请输入值/示例值">
      </a-input>
      <a-select
        style="width: 10%;margin-left: 5px"
        placeholder="是否必填或为常量"
        v-model="el.is_required"
      >
        <a-select-option
          v-for="g in requiredList"
          :key="g.name"
          :value="g.value">{{ g.name }}
        </a-select-option>
      </a-select>
      <a-input
        style="width: 15%;margin-left: 5px"
        v-model="el.alias"
        placeholder="请输入别名">
      </a-input>
      <a-input
        style="width: 15%;margin-left: 5px"
        v-model="el.desc"
        placeholder="请输入字段描述">
      </a-input>
      <span class="operation">
        <a class="delete" @click="_remove(i)">
          <a-icon type="delete" :style="{ fontSize: '16px', color: '#ff0000' }"></a-icon>
        </a>
        <a class="add" @click="_add()" v-if="i === (dependItemList.length - 1)">
          <a-icon type="plus-square" :style="{ fontSize: '16px', color: '#08c' }"></a-icon>
        </a>
      </span>
    </a-form-item>
  </div>
</template>
<script>
  import _ from 'lodash'
  import $ from 'jquery'
  export default {
    name: 'DepList',
    data () {
      return {
        list: [],
        definitionList: [],
        conditionsList: ['=', '>', '<', '!=', 'like', 'leftLike', 'rightLike', 'in', '>=', '<='],
        isInstance: false,
        itemIndex: null,
        requiredList: [{ name: '必填', value: '1' }, { name: '非必填', value: '0' }, { name: '常量', value: '2' }]
      }
    },
    props: {
      dependItemList: Array,
      index: Number,
      columnList: Array
    },
    model: {
      prop: 'dependItemList',
      event: 'dependItemListEvent'
    },
    methods: {
      /**
       * add task
       */
      _add () {
        // dependItemList index
        const val = this.definitionList.length > 0 ? this.definitionList[0].value : undefined
        // add task list
        this.$emit('dependItemListEvent', _.concat(this.dependItemList, this._rtNewParams(val)))
        // remove tooltip
        this._removeTip()
      },
      /**
       * remove task
       */
      _remove (i) {
        this.dependItemList.splice(i, 1)
        this._removeTip()
        if (!this.dependItemList.length) {
          this.$emit('on-delete-all', {
            index: this.index
          })
        }
      },
      _rtNewParams (value) {
        return {
          name: value,
          conditions: undefined,
          example: '',
          alias: '',
          is_required: undefined,
          desc: ''
        }
      },
      /**
       * remove tip
       */
      _removeTip () {
        $('body').find('.tooltip.fade.top.in').remove()
      }
    },
    watch: {
      columnList (val) {
        this.definitionList = val
      }
    },
    beforeCreate () {
    },
    created () {
      this.definitionList = this.columnList
      if (!this.dependItemList.length) {
        const value = this.definitionList[0].name
        this.$emit('dependItemListEvent', _.concat(this.dependItemList, this._rtNewParams(value)))
      } else {
      }
    },
    mounted () {
    },
    components: {}
  }
</script>

<style lang="less" scoped>
  .dep-list-model {
    position: relative;
    min-height: 32px;
    .operation {
      padding-left: 5px;
      a {
        i {
          font-size: 18px;
          vertical-align: middle;
        }
      }
    }
    .delete {
      color: #ff0000;
    }
    .add {
      color: #0097e0;
      margin-left: 10px;
    }
  }
</style>
