<template>
  <div style="text-align: right;height: 350px">
    <a-button type="primary" icon="plus-circle" @click="addParm" >新增</a-button>
    <a-divider/>
    <div v-for="(item,index) in LocalParams"  style="text-align: left;padding-bottom: 5px">
      <a-row>
        <a-col :span="20">
          <a-input v-model="LocalParams[index]"  placeholder="输入后置sql语句"/>
        </a-col>
        <a-col :span="4">
          <a-icon type="delete" style="font-size: xx-large!important;color: #FF498C;text-align: center" @click="deteleItem(index)"/>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script>
  import {clone} from "@/utils/global";

  export default {
    data() {
      return {
        LocalParams:[]
      };
    },
    props:{
     postStatements: {
        type: Array,
        default: () => {
        }
      }
    },
    created() {
      this.initPage()
    },
    methods: {
      initPage(){
        let clone1 = clone(this.postStatements);
        this.LocalParams = clone1
      },
      addParm(){
        var item=""
        this.LocalParams.push(item)
      },
      deteleItem(index){
        this.LocalParams.splice(index,1);
      }
    }
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

