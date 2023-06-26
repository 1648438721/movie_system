package com.system.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Byterain
 * @since 2023-05-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_orders")
public class Orders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;

    /**
     * 拍片的id
     */
    @TableField("aid")
    private Long aid;

    /**
     * 座位号
     */
    @TableField("seats")
    private String seats;

    @TableField("price")
    private BigDecimal price;

    /**
     * 支付时间
     */
    @TableField("pay_at")
    private LocalDateTime payAt;

    @TableField(exist = false)
    private String filmName;

    @TableField(exist = false)
    private String fansName;

    @TableField(exist = false)
    private Integer sumall;

    @TableField(exist = false)
    private LocalDateTime date;
    @TableField(exist = false)
    private BigDecimal money;

}
