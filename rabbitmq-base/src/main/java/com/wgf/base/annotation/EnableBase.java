package com.wgf.base.annotation;

import com.wgf.base.config.DatasourceConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({DatasourceConfig.class})
@Documented
public @interface EnableBase {
}
