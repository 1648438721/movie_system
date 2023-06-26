package com.system.common;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;
//所有实体类父类。
@Data
public class BaseEntity {
    //需要属性 数据库id主键 是自动增长的 :
    //主键的生成策略：IdType.AUTO
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer statu;
}