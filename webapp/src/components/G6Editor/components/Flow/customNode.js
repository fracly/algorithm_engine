import G6 from "@antv/g6/build/g6";
import {uniqueId} from '@/components/G6Editor/utils'
import Shape from '@antv/g/src/shapes'

const Util = G6.Util;
import okSvg from "@/assets/icons/ok.svg";
import loadingSvg from "@/assets/icons/loading.gif";
import errorSvg from "@/assets/icons/error.svg";
import stopSvg from "@/assets/icons/stop.svg";
import pauseSvg from "@/assets/icons/pause.svg";
import readySvg from "@/assets/icons/ready.svg";
import banSve from "@/assets/icons/ban.svg";

const customNode = {
  init() {
    G6.registerNode("customNode", {
      draw(cfg, group) {
        let size = cfg.size;
        if (!size) {
          size = [170, 34]
        }
        // 此处必须是NUMBER 不然bbox不正常
        const width = parseInt(size[0]);
        const height = parseInt(size[1]);
        const color = cfg.color;
        // 此处必须有偏移 不然drag-node错位
        const offsetX = -width / 2
        const offsetY = -height / 2
        const mainId = 'rect' + uniqueId()
        const shape = group.addShape("rect", {
          attrs: {
            id: mainId,
            x: offsetX,
            y: offsetY,
            width: width,
            height: height,
            stroke: "#ced4d9",
            fill: '#fff',//此处必须有fill 不然不能触发事件
            radius: 4
          }
        });
        group.addShape("rect", {
          attrs: {
            x: offsetX,
            y: offsetY,
            width: 4,
            height: height,
            fill: color,
            parent: mainId,
            radius: [4, 0, 0, 4]
          }
        });
        group.addShape("text", {
          attrs: {
            id: 'label' + uniqueId(),
            fontFamily: 'iconfont', // 对应css里面的font-family: "iconfont";
            textAlign: 'center',
            textBaseline: 'middle',
            text: getIcon(cfg.iconPath),
            x: offsetX + 16,
            y: offsetY + height / 2,
            fontSize:16,
            parent: mainId,
            fill: cfg.color
          }
        });
        if (cfg.label) {
          const label = group.addShape("text", {
            attrs: {
              id: 'label' + uniqueId(),
              x: offsetX + width / 2,
              y: offsetY + height / 2,
              textAlign: "center",
              textBaseline: "middle",
              text: cfg.label,
              parent: mainId,
              fill: "#565758"
            }
          });
        }
        if (cfg.inPoints) {
          for (let i = 0; i < cfg.inPoints.length; i++) {
            let x,
              y = 0;
            //0为顶 1为底
            if (cfg.inPoints[i][0] === 0) {
              y = 0;
            } else {
              y = height;
            }
            x = width * cfg.inPoints[i][1];
            const id = 'inPoint' + uniqueId()
            group.addShape("circle", {
              attrs: {
                id: 'circle' + uniqueId(),
                parent: id,
                x: x + offsetX,
                y: y + offsetY,
                r: 10,
                isInPointOut: true,
                fill: "#1890ff",
                opacity: 0
              }
            });
            group.addShape("circle", {
              attrs: {
                id: id,
                x: x + offsetX,
                y: y + offsetY,
                r: 4,
                isInPoint: true,
                fill: "#fff",
                stroke: "#1890ff",
                opacity: 0
              }
            });
          }
        }
        if (cfg.outPoints) {
          for (let i = 0; i < cfg.outPoints.length; i++) {
            let x,
              y = 0;
            //0为顶 1为底
            if (cfg.outPoints[i][0] === 0) {
              y = 0;
            } else {
              y = height;
            }
            x = width * cfg.outPoints[i][1];
            const id = 'circle' + uniqueId()
            group.addShape("circle", {
              attrs: {
                id: 'circle' + uniqueId(),
                parent: id,
                x: x + offsetX,
                y: y + offsetY,
                r: 10,
                isOutPointOut: true,
                fill: "#1890ff",
                opacity: 0//默認0 需要時改成0.3
              }
            });
            group.addShape("circle", {
              attrs: {
                id: id,
                x: x + offsetX,
                y: y + offsetY,
                r: 4,
                isOutPoint: true,
                fill: "#fff",
                stroke: "#1890ff",
                opacity: 0
              }
            });
          }
        }

        const img = new Image();
        switch(cfg.stateImage) {
          case "okSvg":
            img.src = okSvg;
            break;
          case "errorSvg":
            img.src = errorSvg;
            break;
          case "stopSvg":
            img.src = stopSvg;
            break;
          case "pauseSvg":
            img.src = pauseSvg;
            break;
          case "readySvg":
            img.src = readySvg;
            break;
          case "loadingSvg":
            img.src = loadingSvg;
            break;
          default:
            break;
        }
        img.onload = ()=>{
          group.addShape("image", {
            attrs: {
              x: offsetX + width - 32,
              y: offsetY + 8,
              width: 16,
              height: 16,
              parent: mainId,
              img: img
            }
          });
        }
        //任务节点是否被禁用
        if(cfg.banState){
          group.addShape("image", {
            attrs: {
              x: offsetX + width-100,
              y: offsetY + 8,
              width: 16,
              height: 16,
              parent: mainId,
              img: banSve
            }
          });
        }
        //group.sort()
        // 添加文本、更多图形
        return shape;
      },
      //设置状态
      setState(name, value, item) {
        const group = item.getContainer();
        const shape = group.get("children")[0]; // 顺序根据 draw 时确定
        const children = group.findAll(g => {
          return g._attrs.parent === shape._attrs.id
        });
        const circles = group.findAll(circle => {
          return circle._attrs.isInPoint || circle._attrs.isOutPoint;
        });
        const selectStyles = () => {
          shape.attr("fill", "#f3f9ff");
          shape.attr("stroke", "#6ab7ff");
          shape.attr("cursor", "move");
          children.forEach(child => {
            child.attr("cursor", "move");
          });
          circles.forEach(circle => {
            circle.attr('opacity', 1)
          })
        };
        const unSelectStyles = () => {
          shape.attr("fill", "#fff");
          shape.attr("stroke", "#ced4d9");
          circles.forEach(circle => {
            circle.attr('opacity', 0)
          })
        };
        switch (name) {
          case "selected":
            //只有选择了才会使用选中样式
            //case "hover":
            if (value) {
              selectStyles()
            } else {
              unSelectStyles()
            }
            break;
        }
      },
      afterDraw(cfg, group) {
        if (cfg.state=="running") {
          let size = cfg.size;
          if (!size) {
            size = [170, 34]
          }
          // 此处必须是NUMBER 不然bbox不正常
          const width = parseInt(size[0]);
          const height = parseInt(size[1]);
          // 此处必须有偏移 不然drag-node错位
          const offsetX = -width / 2
          const offsetY = -height / 2
          // 添加图片 shape
          const imgs = new Image();
          imgs.src = loadingSvg;
          imgs.onload = ()=>{
            const image = group.addShape("image", {
              attrs: {
                x: offsetX + width - 32,
                y: offsetY + 8,
                width: 16,
                height: 16,
                img: imgs
              },
              name: 'image-shape',
            });
            // 该图片 shape 的动画
            image.animate({
              // 动画重复
              repeat: true,
              // 每一帧的操作，入参 ratio：这一帧的比例值（Number）。返回值：这一帧需要变化的参数集（Object）。
              onFrame(ratio) {
                // 旋转通过矩阵来实现
                // 当前矩阵
                const matrix = Util.mat3.create();
                // 目标矩阵
                const toMatrix = Util.transform(matrix, [['t',-61,1],
                  ['r', ratio * Math.PI * 2],['t',offsetX + width - 24,offsetY + 16],
                ]) ;
                // 返回这一帧需要的参数集，本例中只有目标矩阵
                return {
                  matrix: toMatrix
                };
              }
            }, 2000, 'easeSinIn');
          }
        }
        },
      })
    }
}

import fonts from '@/assets/iconfont/iconfont.json';

const icons = fonts.glyphs.map(icon => {
  let unicode_decimal = String.fromCodePoint(icon.unicode_decimal)
  return {
    name: icon.name,
    font_class:icon.font_class,
    unicode: unicode_decimal, // `\\u${icon.unicode}`,
  };
});
const getIcon =(type) => {
  const matchIcon = icons.find(icon => {
    return "icon-"+icon.font_class === type;
  }) || { unicode: '', name: 'default' };
  return matchIcon.unicode;
};

export default customNode
