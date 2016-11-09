package example.dao;

 
import java.util.List;
import java.util.Map;

 

import com.sooncode.jdbc.Jdbc;
import com.sooncode.jdbc.JdbcFactory;
 
import com.sooncode.jdbc.bean.JsonBeans;
import com.sooncode.jdbc.sql.Parameter;


 

/***
 * 
 * @author pc
 *
 */
public class UserDao {

	private Jdbc jdbc = JdbcFactory.getJdbc();
	
	public JsonBeans getUser4age( int age){
		
		String sql = "select * from user where age <=?";
		Parameter p = new Parameter();
		p.setReadySql(sql);
		p.addParameter(age);
		
		List<Map<String,Object>> list = jdbc.gets(p);
		JsonBeans beans = new JsonBeans(null,list);
		
		return beans;
		
	}
}
