package cn.flowboot.system.domain.entity;

import cn.flowboot.common.croe.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 角色信息表
 * @TableName sys_role
 */
@TableName(value ="sys_role")
@Data
public class SysRole extends BaseEntity implements Serializable {
    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限字符串
     */
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（1正常 0停用）
     */
    private Boolean status;

    /**
     * 删除标志（1代表存在 0代表删除）
     */
    private Boolean delFlag;

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
    private static final long serialVersionUID = 1L;
}
