<template>
  <a-card class="card" title="" :bordered="false">
    <div class="dependence-model">
      <a-form >
        <a-form-item
          label=""
          :labelCol="{ lg: { span: 0 }, sm: { span: 0 } }"
          :wrapperCol="{ lg: { span: 24 }, sm: { span: 24 } }"
        >
          <div class="dep-opt">
            <a class="add" @click="_addDep()">
              <a-icon type="plus-square" :style="{ fontSize: '16px', color: '#08c' }"></a-icon>
            </a>
          </div>
          <div class="dep-box">
            <span
              class="dep-relation"
              @click="_setGlobalRelation()"
              v-if="dependTaskList.length"
            ><b>{{ relation === 'and' ? '且' : '或' }}</b></span>
            <div class="dep-list" v-for="(el,$index) in dependTaskList" :key="$index">
              <span
                class="dep-line-pie"
                @click="__setRelation($index)"
                :style="{ display : el.dependItemList.length<=1 ? 'none' : 'block' }"
              ><b>{{ dependTaskList[$index].relation === 'and' ? '且' : '或' }}</b></span>
<!--              <a class="dep-delete" @click="_deleteDep($index)">-->
<!--                <a-icon type="delete" :style="{ fontSize: '16px', color: '#ff0000' }"></a-icon>-->
<!--              </a>-->
              <mDependItemList
                v-model="el.dependItemList"
                :dependTaskList="dependTaskList"
                :columnList="columnList"
                @on-delete-all="_onDeleteAll"
                :index="$index"
              ></mDependItemList>
            </div>
          </div>
        </a-form-item>
      </a-form>
    </div>

  </a-card>
</template>
<script>
  import _ from 'lodash'
  import mDependItemList from './dependItemList'

  export default {
    name: 'Dependence',
    data () {
      return {
        relation: 'and',
        dependTaskList: [],
        isLoading: false
      }
    },
    props: {
      columnList: Array,
      backfillItem: Object
    },
    methods: {
      _addDep () {
        if (!this.isLoading) {
          this.isLoading = true
          this.dependTaskList.push({
            dependItemList: [],
            relation: 'and'
          })
          console.log(this.dependTaskList)
        }
      },
      _deleteDep (i) {
        this.dependTaskList.splice(i.index, 1)
        // $('body').find('.tooltip.fade.top.in').remove()
      },
      _onDeleteAll (i) {
        this._deleteDep(i)
      },
      _setGlobalRelation () {
        this.relation = (this.relation === 'and' ? 'or' : 'and')
      },

      __setRelation (i) {
        this.dependTaskList[i].relation =
          this.dependTaskList[i].relation === 'and' ? 'or' : 'and'
      },
      _verification () {
        this.$emit('on-dependent', {
          relation: this.relation,
          dependTaskList: _.map(this.dependTaskList, v => {
            return {
              relation: v.relation,
              dependItemList: _.map(v.dependItemList, v1 =>
                _.omit(v1, ['depTasksList', 'state', 'dateValueList'])
              )
            }
          })
        })
        return true
      }
    },
    watch: {
      dependTaskList () {
        setTimeout(() => {
          this.isLoading = false
        }, 600)
      },
      columnList (val) {
        this.columnList = val
      },
      backfillItem (val) {
        debugger
        this.dependTaskList = val.dependTaskList
        this.relation = val.relation
      }
    },
    beforeCreate () {
    },
    created () {

    },
    mounted () {
      // debugger
      // this.dependTaskList = this.backfillItem.dependTaskList
      // this.relation = this.backfillItem.relation
    },
    destroyed () {
    },
    computed: {},
    components: { mDependItemList }
  }
</script>

<style lang="less" scoped>
  .dependence-model {
    margin-top: -10px;
    .dep-opt {
      /*margin-bottom: 10px;*/
      /*padding-top: 14px;*/
      /*line-height: 24px;*/
      .add-dep {
        color: #0097e0;
        margin-right: 10px;
        i {
          font-size: 18px;
          vertical-align: middle;
        }
      }
    }
    .text-box {
      width: 112px;
      float: left;
      text-align: right;
      margin-right: 8px;
      margin-top: 16px;
    }
    .dep-list {
      margin-bottom: 16px;
      position: relative;
      border-left: 1px solid #eee;
      padding-left: 16px;
      margin-left: -16px;
      transition: all 0.2s ease-out;
      padding-bottom: 20px;
      &:hover {
        border-left: 1px solid #0097e0;
        transition: all 0.2s ease-out;
        .dep-line-pie {
          transition: all 0.2s ease-out;
          border: 1px solid #0097e0;
          background: #0097e0;
          color: #fff;
        }
      }
      .dep-line-pie {
        transition: all 0.2s ease-out;
        position: absolute;
        width: 20px;
        height: 20px;
        border: 1px solid #e2e2e2;
        text-align: center;
        top: 50%;
        margin-top: -20px;
        z-index: 1;
        left: -10px;
        border-radius: 10px;
        background: #fff;
        font-size: 12px;
        cursor: pointer;
        line-height: 1.5;
        &::selection {
          background: transparent;
        }
        &::-moz-selection {
          background: transparent;
        }
      }
      .dep-delete {
        position: absolute;
        bottom: -6px;
        left: 14px;
        font-size: 18px;
        color: #ff0000;
        cursor: pointer;
      }
    }
    .dep-box {
      border-left: 4px solid #eee;
      margin-left: -46px;
      padding-left: 42px;
      position: relative;
      .dep-relation {
        position: absolute;
        width: 20px;
        height: 20px;
        border: 1px solid #e2e2e2;
        text-align: center;
        top: 50%;
        margin-top: -10px;
        z-index: 1;
        left: -12px;
        border-radius: 10px;
        background: #fff;
        font-size: 12px;
        cursor: pointer;
        line-height: 1.5;
        &::selection {
          background: transparent;
        }
        &::-moz-selection {
          background: transparent;
        }
        &::-webkit-selection {
          background: transparent;
        }
      }
    }
  }
</style>
