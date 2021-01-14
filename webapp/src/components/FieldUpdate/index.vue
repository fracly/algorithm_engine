<template>
  <div>
    <a-input-search v-model="searchKey" placeholder="输入关键字搜索列" />
    <a-divider />
    <a-row :gutter="[16,16]" >
      <a-col :span="11">
        <a-table :columns="columns" :data-source="pageList" :pagination="false" size="small" :row-selection="rowSelection" bordered :scroll="{y:500}">
        </a-table>
      </a-col>
      <a-col :span="13">
        <a-table :columns="columnsTemp" :data-source="FiledListTemp" :pagination="false" size="small"  bordered :scroll="{y:500}">
            <a slot="action" slot-scope="text,record" @click="deleteItem(record)" style="text-align: center">
            <a-icon type="delete" style="color:#FF498C;font-size: larger" theme="outlined"/>
          </a>
           <a-span slot="updateComment" slot-scope="text,record" ><a-input :value="text" @change="e => changeComment(e.target.value, record.key, 'comment')"/> </a-span>
          <a-span slot="updateFiled" slot-scope="text,record" ><a-input :value="text" @change="e => changeFiled(e.target.value, record.key, 'updateName')"/> </a-span>
        </a-table>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import {uniqueForKey,uniqueForItem,getSubtractForKey,getSubtractForItem} from "@/utils/global";
  import cloneDeep from 'lodash.clonedeep'

  const columns = [
    {
      title:"字段名称",
      dataIndex: 'name',
      key: 'name',
      scopedSlots: { customRender: 'name' },
      sorter: (a, b) => a.name.length - b.name.length,
    },
    {
      title: '字段类型',
      dataIndex: 'type',
      key: 'type',
      sorter: (a, b) => a.type.length - b.type.length,
    },
    {
      title: '字段注释',
      dataIndex: 'comment',
      key: 'comment',
    },
  ];

  const columnsTemp = [
    {
      title:"",
      dataIndex: 'name',
      key: 'operation',
      width:50,
      scopedSlots: { customRender: 'action' },
    },
    {
      title:"字段名称",
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.length - b.name.length,
    },
    {
      title: '字段类型',
      dataIndex: 'type',
      key: 'type',
      sorter: (a, b) => a.type.length - b.type.length,
    },
    {
      title: '字段注释',
      dataIndex: 'comment',
      key: 'comment',
      scopedSlots: { customRender: 'updateComment' },
    },
    {
      title: '修改后字段名称',
      dataIndex: 'updateName',
      key: 'xhzd',
      scopedSlots: { customRender: 'updateFiled' },
    },
  ];

  export default {
    name: 'UpdateColumn',
    components: {},
    data() {
      return {
        searchKey:"",
        columns,
        columnsTemp,
        FiledListTemp:[],
        pageList:[],
        chooseFileds:[],
        // changeFileds:[]
      };
    },
    computed: {
      rowSelection() {
        debugger
        let _that=this
        return {
          selectedRowKeys: _that.chooseFileds,
          onChange: (selectedRowKeys, selectedRows) => {
            // debugger
            _that.changeRow(selectedRowKeys,selectedRows)
          },
          getCheckboxProps: record => ({
            props: {
              Checked: _that.savechooseFileds.includes(record.name)
            }
          }),
        };
      },
    },
    props: {
      /*已选择的字段*/
      chooseField:{
        type: Array,
        required: true
      },
      /*选择字段集合*/
      FiledList: {
        type: Array,
        required: true
      },
      /*选择字段集合*/
      beforeColumnList: {
        type: Array,
        required: true
      },
      /*勾选的保存集合*/
      savechooseFileds: {
        type: Array,
        required: true
      }
    },
    watch:{
      'searchKey': {
        handler(newVal, oldVal) {
          if(newVal==""){
            this.pageList=this.FiledList
          }else{
            this.pageList =  this.pageList.filter(function(pageList) {
              return Object.keys(pageList).some(function(key) {
                return String(pageList[key]).toLowerCase().indexOf(newVal) > -1
              })
            })
          }
        }
      }
    },
    created () {
      this.initPage()
    },
    methods: {
      changeFiled (value, key, column){
        debugger
        const FiledListTemp = [...this.FiledListTemp];
        const target = FiledListTemp.filter(item => key === item.key)[0];
        if (target) {
          target[column] = value;
          this.FiledListTemp = FiledListTemp ;
        }
      },
      changeComment (value, key, column){
        debugger
        const FiledListTemp = [...this.FiledListTemp];
        const target = FiledListTemp.filter(item => key === item.key)[0];
        if (target) {
          target[column] = value;
          this.FiledListTemp = FiledListTemp ;
        }
      },
      initPage (){
        debugger
        // this.chooseFields=this.chooseField
        this.pageList=this.FiledList
        // this.chooseFields=this.chooseField
        // for(var i=0;i<this.chooseFields.length;i++){
        //   this.getItem(this.chooseFields[i]);
        // }
        // 使用深复制
        this.FiledListTemp= cloneDeep(this.chooseField)
        this.setKey();
        this.chooseFileds = cloneDeep(this.savechooseFileds)
        debugger
      },
      // getItem(name){
      //   for(var i=0;i<this.pageList.length;i++){
      //     if(name==this.pageList[i].name){
      //       this.FiledListTemp.push(this.FiledList[i])
      //     }
      //   }
      // },
      setKey(){
        for(var i=0;i<this.pageList.length;i++){
          this.pageList[i].key= this.pageList[i].name
          debugger
          // this.pageList[i].updateName= this.pageList[i].name
        }
      },
      deleteItem(items){
        debugger
        const index = this.chooseFileds.findIndex(item => item === items.name);
        this.chooseFileds.splice(index, 1);
        const index1 = this.FiledListTemp.findIndex(item => item.name === items.name);
        this.FiledListTemp.splice(index1, 1);
      },
      changeRow(selectedRowKeys,selectedRows){
        //获取当前展示table 未选择的在右边临时table中去掉
        // selectedRows.updateName = selectedRows['name']
       // var selectedRows1 = selectedRows.filter(x => !this.chooseFileds.includes(x["name"]))
       //  selectedRows = selectedRows1
        for (var i = 0; i <selectedRows.length ; i++) {
         let b= selectedRows[i]["name"]
           if(!this.chooseFileds.includes(b)){
             selectedRows[i].updateName = b
           }
          debugger
        }
        debugger
        // selectedRows["updateName"]=selectedRows.name
        let subtractForKey = getSubtractForKey(this.pageList,selectedRows,"name");
        let filedTemp = this.FiledListTemp.concat(selectedRows)
        this.FiledListTemp = uniqueForKey(filedTemp,"name");
        this.FiledListTemp = getSubtractForKey(this.FiledListTemp,subtractForKey,"name");
        let chooseFiledsTemp=[]
        for(var i = 0;i<this.FiledListTemp.length;i++){
          chooseFiledsTemp.push(this.FiledListTemp[i].name)
        }
        this.chooseFileds = chooseFiledsTemp
      },
    },
  }
</script>
<style>
  /deep/.ant-modal-body {
    padding: 5px!important;
  }
</style>