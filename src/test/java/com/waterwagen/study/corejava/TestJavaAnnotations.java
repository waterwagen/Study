package com.waterwagen.study.corejava;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.Test;

public class TestJavaAnnotations
{
	@InheritedAnnotation
	private static class Parent {}
	private static class Child extends Parent {}

	@SuppressWarnings("unused")
	@FieldAndMethodOnlyAnnotation
	private String mFieldAndMethodOnlyAnnoTarget = "";

	@SuppressWarnings("unused")
	@JavadocdAnnotation
	private String mJavadocdAnnoTarget = "";

	@SuppressWarnings("unused")
	@SingleMemberAnno("this is a value")
	private String mSingleMemberAnnoTarget = "";

	@SuppressWarnings("unused")
	@MarkerAnno
	private String mMarkerAnnoTarget = "";

	@SuppressWarnings("unused")
	@MyAnnoSource(name="Source Anno")
	private String mSourceAnnoTarget = "";
	
	@SuppressWarnings("unused")
	@MyAnnoRuntime(name="Runtime Anno")
	private String mRuntimeAnnoTarget = "";

	@Test
	public void testInheritedAnnotation() throws NoSuchFieldException, SecurityException
	{
		Class<Child> child_class = Child.class;
		InheritedAnnotation anno = child_class.getAnnotation(InheritedAnnotation.class);
		assertThat(anno, is(notNullValue()));
		assertThat(anno.annotationType().isAnnotationPresent(Inherited.class), is(equalTo(true)));
	}

	@Test
	public void testFieldAndMethodOnlyAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field field = this.getClass().getDeclaredField("mFieldAndMethodOnlyAnnoTarget");
		FieldAndMethodOnlyAnnotation anno = field.getAnnotation(FieldAndMethodOnlyAnnotation.class);
		assertThat(anno, is(notNullValue()));
		assertThat(anno.annotationType().isAnnotationPresent(Target.class), is(equalTo(true)));
		Target target_anno = anno.annotationType().getAnnotation(Target.class);
		assertThat(Arrays.asList(target_anno.value()), hasSize(2));
		assertThat(target_anno.value(),  allOf(hasItemInArray(ElementType.METHOD), hasItemInArray(ElementType.FIELD)));
	}

	@Test
	public void testJavadocdAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field field = this.getClass().getDeclaredField("mJavadocdAnnoTarget");
		JavadocdAnnotation anno = field.getAnnotation(JavadocdAnnotation.class);
		assertThat(anno, is(notNullValue()));
		assertThat(anno.annotationType().isAnnotationPresent(Documented.class), is(equalTo(true)));
	}

	@Test
	public void testSingleMemberAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field field = this.getClass().getDeclaredField("mSingleMemberAnnoTarget");
		SingleMemberAnno anno = field.getAnnotation(SingleMemberAnno.class);
		assertThat(anno, is(notNullValue()));
		assertThat(anno.value(), is(equalTo("this is a value")));
	}

	@Test
	public void testMarkerAnnotation() throws NoSuchFieldException, SecurityException
	{
		Field field = this.getClass().getDeclaredField("mMarkerAnnoTarget");
		assertThat(field.isAnnotationPresent(MarkerAnno.class), is(equalTo(true)));
	}

	@Test
	public void testRetentionPolicies() throws NoSuchFieldException, SecurityException
	{
		Field field = this.getClass().getDeclaredField("mRuntimeAnnoTarget");
		MyAnnoRuntime anno = field.getAnnotation(MyAnnoRuntime.class);
		assertThat(anno, is(notNullValue()));
		assertThat(anno.name(), is(equalTo("Runtime Anno")));
		
		Field field2 = this.getClass().getDeclaredField("mSourceAnnoTarget");
		MyAnnoSource anno2 = field2.getAnnotation(MyAnnoSource.class);
		assertThat("Expected to not be able to find the source retained anno.", anno2, is(nullValue()));
	}

	/////////////////////
	/// Utility Types ///
	/////////////////////
	
	@Target({ElementType.FIELD,ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	private @interface FieldAndMethodOnlyAnnotation {}
	
	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	private @interface InheritedAnnotation {}
	
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	private @interface JavadocdAnnotation {}
	
	@Retention(RetentionPolicy.RUNTIME)
	private @interface SingleMemberAnno 
	{
		public String value();
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MarkerAnno {}
	
	@Retention(RetentionPolicy.SOURCE)
	private @interface MyAnnoSource 
	{
		public String name();
	}

	@Retention(RetentionPolicy.RUNTIME)
	private @interface MyAnnoRuntime 
	{
		public String name();
	}
}
