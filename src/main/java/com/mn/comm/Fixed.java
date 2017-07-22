package com.mn.comm;

import java.lang.annotation.*;

/**
 * 标识字段是否是可变的
 * Created by mn on 2017/7/22 0022.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Fixed {
}
