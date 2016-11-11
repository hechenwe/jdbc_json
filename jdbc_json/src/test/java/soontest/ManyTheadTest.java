package soontest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 多线程测试
 * 
 * @author pc
 *
 */
public class ManyTheadTest implements SoonTest  {

	/**
	 * 线程数量
	 */
	private int threadNum;
	
	/**执行数量*/
	private int executeNum;
	 
    /**
     * 创建多线程测试实例
     * @param threadNum 线程数量
     */
	public ManyTheadTest(int threadNum ,int executeNum) {
		this.threadNum = threadNum;
		this.executeNum = executeNum;
		
	}

	 
	
	/**
	 * 多线程运行任务
	 * 
	 * @return 多线程获得的结果
	 */
	public List<Object> testMethod(Class<?> testClass, String methodName, Object[] agrs) {
		ExecutorService pool = Executors.newFixedThreadPool(this.threadNum);// 创建一个线程池
	 
		CallabbleMethod method = new CallabbleMethod(testClass, methodName, agrs);
		List<Object> list = new LinkedList<>();
		for (int i = 0; i < this.executeNum; i++) {
			try {
				Future<Object> future = pool.submit(method);// 执行任务并获取Future对象
				Object obj = future.get();
				if(obj!=null){
					//--------------释放资源---------------
					if(obj instanceof Connection){
						((Connection) obj).close();
					}
					//----------------------------
					list.add( obj );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		pool.shutdown();// 关闭线程池
		return list;
	}

	 

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ManyTheadTest mtt = new ManyTheadTest(1,3);
		SoonTest st = (SoonTest) OpenInterfaceTest.newInstance(mtt);
		List< Object > list = st.testMethod( Comput.class,"add",new Integer[]{1243,24343});
		for ( Object  obj : list) {
			System.out.println("结果:" + obj);
		}
	}

}
