package com.github.firmwehr.fiascii;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The plural of <a href="https://www.merriam-webster.com/dictionary/fiasco">fiasco</a>, probably.
 * Don't ask me, I am a programmer not linguist.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface FiAscii {

	/**
	 * @return the ascii art transformation
	 */
	String value();
}
