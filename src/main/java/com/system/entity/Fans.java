package com.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Byterain
 * @since 2023-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_fans")
public class Fans extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("avatar")
    private String avatar;

    @TableField("email")
    private String email;

    @TableField("sex")
    private Integer sex;

    @TableField("info")
    private String info;

    @TableField("delTag")
    private Integer deltag;

    @TableField(exist = false)
    private LocalDateTime date;
    @TableField(exist = false)
    private BigDecimal people;

}
