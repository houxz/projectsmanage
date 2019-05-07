package com.emg.poiwebeditor.cache;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RedisCacheEvict {
	String layer();
	String key();
}
