package cn.flowboot.system.mapper;

import cn.flowboot.system.domain.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @Entity cn.flowboot.system.domain.entity.SysRole
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    Set<String> queryRoleKeysByUserId(Long userId);

    Set<Long> queryRoleIdsByUserId(Long userId);

    Set<SysRole> queryRolesByUserId(Long userId);

    Set<SysRole> queryRoleAll();
}




