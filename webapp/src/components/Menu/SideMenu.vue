<template>
  <a-layout-sider
    :class="['sider', isDesktop() ? null : 'shadow', theme, fixSiderbar ? 'ant-fixed-sidemenu' : null ]"
    width="210px"
    :collapsible="collapsible"
    v-model="collapsed"
    :trigger="null">
    <logo />
    <s-menu
      :collapsed="collapsed"
      :menu="menus"
      :theme="theme"
      :mode="mode"
      @select="onSelect"
      style="padding: 16px 0px;"></s-menu>
    <a-layout-footer
      style="text-align: center;background:none;color: rgb(24, 144, 255);font-size: 12px;
        position: absolute;
        bottom: 20px; "
      :style="{left: collapsed?'0px':'20px'}">
      {{bottom}}
    </a-layout-footer>
  </a-layout-sider>

</template>

<script>
import Logo from '@/components/tools/Logo'
import SMenu from './index'
import { mixin, mixinDevice } from '@/utils/mixin'
import { conf } from '@/api/system'

export default {
  name: 'SideMenu',
  components: { Logo, SMenu },
  mixins: [mixin, mixinDevice],
  data () {
    return {
      bottom: ''
    }
  },
  props: {
    mode: {
      type: String,
      required: false,
      default: 'inline'
    },
    theme: {
      type: String,
      required: false,
      default: 'dark'
    },
    collapsible: {
      type: Boolean,
      required: false,
      default: false
    },
    collapsed: {
      type: Boolean,
      required: false,
      default: false
    },
    menus: {
      type: Array,
      required: true
    }
  },
  methods: {
    onSelect (obj) {
      this.$emit('menuSelect', obj)
    }
  },
  mounted () {
    conf({ key: 'SYS_BOTTOM' }).then(res => {
        this.bottom = res.data[0].KEY_VALUE
      }
    )
  }
}
</script>
