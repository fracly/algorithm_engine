import eventBus from "@/components/G6Editor/utils/eventBus";
export default {
    getDefaultCfg() {
        return {
            backKeyCode: 8,
            deleteKeyCode: 46
        };
    },
    getEvents() {
        return {
            keyup: 'onKeyUp',
            keydown: 'onKeyDown'
        };
    },

    onKeyDown(e) {
        const code = e.keyCode || e.which;
        switch (code) {
            case this.deleteKeyCode:
                eventBus.$emit('deleteItem')
                break
            //修复回车时候 删除节点的问题 保留删除键可以删除节点
            case this.backKeyCode:
                break
        }
    },
    onKeyUp() {
        this.keydown = false;
    }
};
