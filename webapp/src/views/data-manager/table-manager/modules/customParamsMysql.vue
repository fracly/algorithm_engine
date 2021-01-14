<template>
  <div style="text-align: right;height: 350px;overflow-y:auto">
<!--    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>-->
    <a-divider/>
    <div v-for="item in localParams" :key="item.id" style="text-align: left;padding-bottom: 10px;margin-left: 10px;" >
      <a-input style="width: 170px" v-model="item.name"   placeholder="字段名" disabled="true"/>
      &nbsp;&nbsp;
      <a-select style="width: 170px" v-model="item.type" placeholder="请选择字段类型" >
        <a-select-option v-for="a in fillStyleList"  :value="a.value" :key="a.name" > {{a.value}}</a-select-option>
      </a-select>  &nbsp;&nbsp;
        <a-input style="width: 170px" v-model="item.comment"   placeholder="字段描述" disabled="true"/>
<!--      <a-icon type="delete" style="font-size: large;color: #FF498C;text-align: center" @click="deteleItem(item)"/>-->
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
          {name: "整形", value: "int"},
          {name: "长整型", value: "long"},
          {name: "单精度浮点型", value: "float"},
          {name: "双精度浮点型", value: "double"},
          {name: "字符串", value: "varchar"},
            {name: "长文本", value: "text"},
          {name: "日期型", value: "date"},
          {name: "时间戳", value: "timestamp"},
          {name: "布尔", value: "bool"}
        ],
      };
    },
    props:{
      CustomParams: {
        type: Array,
        default: () => {
        }
      },
    },
    methods: {
      // addParm(){
      //   var item={
      //     id:uuid(),
      //     column:undefined,
      //     coefficient:undefined,
      //     fixedValue:null
      //   }
      //   this.localParams.push(item)
      // },
      // deteleItem(node){
      //   const index1 = this.localParams.findIndex(item => item.id === node.id);
      //   this.localParams.splice(index1, 1);
      // }
    },
    /*初始化参数*/
    created() {
              this.localParams = clone(this.CustomParams)
        debugger
        this.localParams.forEach((item,index)=>{
            if (item.type == 'string'){
                item.type='varchar'
            }else if (item.type == 'bingint'){
                item.type='long'
            }
        })
        debugger
    },
  };

  // function uuid() {
  //   var s = [];
  //   var hexDigits = "0123456789abcdef";
  //   for (var i = 0; i < 36; i++) {
  //     s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
  //   }
  //   s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
  //   s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
  //   s[8] = s[13] = s[18] = s[23] = "-";
  //
  //   var uuid = s.join("");
  //   return uuid;
  // }
</script>
<style scoped>
  /deep/.ant-divider-horizontal {
    margin: 5px 0;
  }
</style>

