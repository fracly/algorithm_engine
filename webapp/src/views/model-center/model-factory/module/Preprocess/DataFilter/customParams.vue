<template>
  <div style="text-align: right;height: 350px;overflow-y:auto">
    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>
    <a-divider/>
    <div v-for="item in localParams" :key="item.id" style="text-align: left;padding-bottom: 10px;margin-left: 10px;" >
      <a-select show-search optionFilterProp="children" style="width: 170px" v-model="item.column" placeholder="请选择列">
            <a-select-option v-for="a in choseList"  :value="a.name"> {{ a.comment == ''||a.comment==undefined?a.name:a.comment }}</a-select-option>
      </a-select>
      &nbsp;&nbsp;
      <a-select style="width: 170px" v-model="item.coefficient" placeholder="判断条件">
        <a-select-option v-for="a in fillStyleList"  :value="a.value"> {{a.name}}</a-select-option>
      </a-select>  &nbsp;&nbsp;
        <a-input style="width: 170px" v-model="item.fixedValue"    placeholder="请输出值"/>
      &nbsp;&nbsp;
      <a-icon type="delete" style="font-size: large;color: #FF498C;text-align: center" @click="deteleItem(item)"/>
    </div>
  </div>
</template>

<script>
  import {clone} from "@/utils/global";

  export default {
    data() {
      return {
        choseList: [],
        localParams: [],
        fillStyleList: [
          {name: "大于", value: "大于"},
          {name: "大于等于", value: "大于等于"},
          {name: "小于", value: "小于"},
          {name: "小于等于", value: "小于等于"},
          {name: "等于", value: "等于"},
          {name: "不等于", value: "不等于"},
          {name: "in", value: "in"},
          {name: "between", value: "between"},

        ],
      };
    },
    props:{
      CustomParams: {
        type: Array,
        default: () => {
        }
      },
      pageList: {
        type: Array,
        required: true
      }
    },
    methods: {
      addParm(){
        var item={
          id:uuid(),
          column:undefined,
          coefficient:undefined,
          fixedValue:null
        }
        this.localParams.push(item)
      },
      deteleItem(node){
        const index1 = this.localParams.findIndex(item => item.id === node.id);
        this.localParams.splice(index1, 1);
      }
    },
    /*初始化参数*/
    created() {
              this.choseList = this.pageList
              this.localParams = clone(this.CustomParams)
    },
  };


  function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
  }
</script>
<style scoped>
  /deep/.ant-divider-horizontal {
    margin: 5px 0;
  }
</style>

