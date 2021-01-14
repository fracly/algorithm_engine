<template>
  <div>
    <a-input-search v-model="searchKey" placeholder="输入关键字搜索列" />
    <a-divider />
    <a-row :gutter="[16,16]" >
      <a-col :span="12">
        <a-table :columns="columns" rowKey="name" :data-source="pageList" :pagination="false" size="small" :row-selection="rowSelection" bordered :scroll="{y:400}"></a-table>
      </a-col>
      <a-col :span="12">
        <a-table :columns="columnsTemp" :data-source="FiledListTemp" :pagination="false" size="small"  bordered :scroll="{y:400}">
          <a slot="action" slot-scope="text,record" @click="deleteItem(record)" style="text-align: center">
            <a-icon type="delete" style="color:#FF498C;font-size: larger" theme="outlined"/>
          </a>
        </a-table>
      </a-col>
    </a-row>
  </div>
</template>

<script>
  import {uniqueForKey,uniqueForItem,getSubtractForKey,getSubtractForItem,clone} from "@/utils/global";

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
    },
  ];

  export default {
    name: 'SideMenu',
    components: {},
    data() {
      return {
        indeterminate:false,
        checkAll:false,
        searchKey:"",
        columns,
        columnsTemp,
        FiledListTemp:[],
        pageList:[],
        chooseFileds:[],
      };
    },
    computed: {
      rowSelection() {
        let _that=this
         var item = {
            selectedRowKeys: _that.chooseFileds,
            onChange: (selectedRowKeys, selectedRows) => {
              _that.changeRow(selectedRowKeys,selectedRows)
            },
            getCheckboxProps: record => ({
              props: {
                Checked: _that.chooseFileds.includes(record.name)
              }
            }),
          };
        return item
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
      }
    },
    watch:{
      'searchKey': {
        handler(newVal, oldVal) {
          if(newVal==""){
            this.pageList=this.FiledList
          }else{
            this.pageList =  this.FiledList.filter(function(pageList) {
              return Object.keys(pageList).some(function(key) {
                return String(pageList[key]).toLowerCase().indexOf(newVal.trim()) > -1
              })
            })
          }
        }
      }
    },
    created() {
      this.initPage()
    },
    methods: {
      initPage(){
        this.pageList=clone(this.FiledList)
        this.chooseFileds=clone(this.chooseField)
        for(var i=0;i<this.chooseFileds.length;i++){
          this.getItem(this.chooseFileds[i]);
        }
        this.setKey();
      },
      getItem(name){
        for(var i=0;i<this.pageList.length;i++){
          if(name==this.pageList[i].name){
            this.FiledListTemp.push(this.pageList[i])
          }
        }
      },
      setKey(){
        for(var i=0;i<this.pageList.length;i++){
          this.pageList[i].key= this.pageList[i].name
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