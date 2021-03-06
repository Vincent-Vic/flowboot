package cn.flowboot.system.domain.entity;

import cn.flowboot.common.croe.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 菜单权限表
 * @TableName sys_menu
 */
@TableName(value ="sys_menu")
@Data
public class SysMenu extends BaseEntity implements Serializable {
    /**
     * 菜单ID
     */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 是否为外链（1是 0否）
     */
    private Boolean isFrame;

    /**
     * 是否缓存（1缓存 0不缓存）
     */
    private Boolean isCache;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    private String menuType;

    /**
     * 菜单状态（1显示 0隐藏）
     */
    private Boolean visible;

    /**
     * 菜单状态（1正常 0停用）
     */
    private Boolean status;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private List<SysMenu> children;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
