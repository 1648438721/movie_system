package com.system.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ChartVo {
    private LocalDateTime date;
    private BigDecimal value;
}
