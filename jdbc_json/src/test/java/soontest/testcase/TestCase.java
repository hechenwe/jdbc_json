package soontest.testcase;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;


/**
 * 常规测试用例测试
 */
public class TestCase {
	private static Logger logger = Logger.getLogger(TestCase.class);

	public static void test(Class<?> clas) {
		try {

			Object testObject = clas.newInstance();
			Method[] methods = clas.getDeclaredMethods();
			for (Method m : methods) {
				String methodName = m.getName();
				Class<?>[] paras = m.getParameterTypes();
				Object[] args = new Object[paras.length];
				String pa = "";
				for (int i = 0; i < args.length; i++) {
					Object obj = paras[i].newInstance();
					if (obj instanceof String) {
						obj = null;
					}
					if (i == 0) {
						if (obj != null && obj.equals("")) {
							pa = pa + "\"\"";
						} else if (obj == null) {
							pa = pa + "null";
						}
					} else {
						if (obj != null && obj.equals("")) {
							pa = pa + "," + "\"\"";
						} else if (obj == null) {
							pa = pa + "," + "null";
						}

					}
					args[i] = obj;
				}
				logger.info("【SoonTest】" + clas.getSimpleName() + "." + methodName + "(" + pa + ")");
				m.invoke(testObject, args);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		test(Test.class);
	}

}
