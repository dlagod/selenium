package es.ofca.test.psel.common.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

	enum PostActionType {
	    SAVE, FOCUS;
	}
	
	enum AccessType {
	    ALL, ORD_FREE, DISABILITY_FREE, ORD_INTERNAL, DISABILITY_INTERNAL;
	}
	
	int order();
	PostActionType postAction() default PostActionType.SAVE;
	AccessType access() default AccessType.ALL;
}
