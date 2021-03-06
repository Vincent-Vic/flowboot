package cn.flowboot.system.domain.dto;

import cn.flowboot.common.croe.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 参数配置表
 * @TableName sys_config
 */
@Data
public class ConfigDto extends BaseEntity implements Serializable {
    /**
     * 参数主键
     */
    @TableId(type = IdType.AUTO)
    private Integer configId;

    /**
     * 参数名称
     */
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "配置key不能为空")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    /**
     * 是否系统内置
     */
    private Boolean configType;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
