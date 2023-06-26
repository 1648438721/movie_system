package com.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2023-05-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_film_evaluate")
public class FilmEvaluate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("fid")
    private Long fid;

    @TableField("uid")
    private Long uid;

    @TableField("star")
    private Integer star;

    @TableField("comment")
    private String comment;

    @TableField(exist = false)
    private String Fname;
}
