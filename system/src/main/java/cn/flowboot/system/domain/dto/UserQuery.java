package cn.flowboot.system.domain.dto;

import cn.flowboot.common.croe.domain.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2021/09/26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserQuery extends BaseQuery {
    private String keyword;
}
