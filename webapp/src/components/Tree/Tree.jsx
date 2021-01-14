import { Menu, Icon } from 'ant-design-vue'

const { Item, ItemGroup, SubMenu } = Menu

export default {
  name: 'Tree',
  props: {
    dataSource: {
      type: Array,
      required: true
    },
    openKeys: {
      type: Array,
      default: () => []
    },
    editable: {
      type: Boolean,
      default: false
    },
    defaultSelectedKeys: {
      type: Array,
      default: () => []
    }
  },
  created () {
    this.localOpenKeys = this.openKeys.slice(0)
  },
  data () {
    return {
      localOpenKeys: []
    }
  },
  methods: {
    handlePlus (item) {
      this.$emit('add', item)
    },
    handleEdit (item) {
      this.$emit('edit', item)
    },
    handleDelete (item) {
      this.$emit('delete', item)
    },
    handleTitleClick (...args) {
      this.$emit('titleClick', { args })
    },
    handleSearch () {
      this.$emit('handleSearch')
    },
    renderSearch () {
      return null
      // return (
      //   <Search
      //     placeholder="请输入关键字"
      //     style="width: 100%; margin-bottom: 1rem"
      //     id="tableGlobalSearchId"
      //     {...{ on: { search: () => this.handleSearch() } }}
      //   />
      // )
    },
    renderIcon (icon) {
      return icon && (<Icon type={icon} />) || null
    },
    renderMenuItem (item) {
      if (this.editable) {
        return (
          <Item key={item.key}>
            { this.renderIcon(item.icon) }
            { item.title2 }
            <a class="btn">
              <a-icon class="btn" style="width:20px;z-index:1301;right:45px;" type="plus" {...{ on: { click: () => this.handlePlus(item) } }}/>
              <a-icon class="btn" style="width:20px;z-index:1301;right:25px;" type="delete" {...{ on: { click: () => this.handleDelete(item) } }}/>
              <a-icon class="btn" style="width:20px;z-index:1301;right:5px;" type="edit" {...{ on: { click: () => this.handleEdit(item) } }}/>
            </a>
          </Item>
        )
      } else {
        return (
          <Item key={item.key}>
            { this.renderIcon(item.icon) }
            { item.title2 }
          </Item>
        )
      }
    },
    renderItem (item) {
      return item.children ? this.renderSubItem(item, item.key) : this.renderMenuItem(item, item.key)
    },
    renderItemGroup (item) {
      const childrenItems = item.children.map(o => {
        return this.renderItem(o, o.key)
      })

      return (
        <ItemGroup key={item.key}>
          <template slot="title">
            <span>{ item.title }</span>
            <a-dropdown>
              <a class="btn"><a-icon type="ellipsis" /></a>
              <a-menu slot="overlay">
                <a-menu-item key="1">新增</a-menu-item>
                <a-menu-item key="2">合并</a-menu-item>
                <a-menu-item key="3">移除</a-menu-item>
              </a-menu>
            </a-dropdown>
          </template>
          { childrenItems }
        </ItemGroup>
      )
    },
    renderSubItem (item, key) {
      const childrenItems = item.children && item.children.map(o => {
        return this.renderItem(o, o.key)
      })

      const title = (
        <span slot="title">
          { this.renderIcon(item.icon) }
          <span>{ item.title }</span>
        </span>
      )

      if (item.group) {
        return this.renderItemGroup(item)
      }
      // titleClick={this.handleTitleClick(item)}
      return (
        <SubMenu key={key}>
          { title }
          { childrenItems }
        </SubMenu>
      )
    }
  },
  render () {
    const { dataSource, search } = this.$props

    // this.localOpenKeys = openKeys.slice(0)
    const list = dataSource.map(item => {
      return this.renderItem(item)
    })

    return (
      <div class="tree-wrapper">
        { search ? this.renderSearch() : null }
        <Menu mode="inline" class="custom-tree" {...{ on: { click: item => this.$emit('click', item),
            'update:openKeys': val => { this.localOpenKeys = val } } }} openKeys={this.localOpenKeys}
              defaultSelectedKeys={this.defaultSelectedKeys}>
          { list }
        </Menu>
      </div>
    )
  }
}
