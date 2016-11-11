package soontest;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class CallabbleMethod implements Callable<Object> {
	private Class<?> testClass;

	private String methodName;

	private Object[] agrs;

	public CallabbleMethod(Class<?> testClass, String methodName, Object[] agrs) {
		this.testClass = testClass;
		this.methodName = methodName;
		this.agrs = agrs;
	}

	@Override
	public Object call() throws Exception {

	 
		Object object = this.testClass.newInstance();
		try {
			Class<?>[] classArray = new Class<?>[this.agrs.length];
			for (int i = 0; i < this.agrs.length; i++) {
				classArray[i] = this.agrs[i].getClass();
			}

			Method method = this.testClass.getDeclaredMethod(this.methodName, classArray);
			Object obj = method.invoke(object, this.agrs);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
