package soontest.testcase;

public class Test {

	public String test1 (String str){
		if(str==null){
			return null;
		}
		System.out.println("Test.String()  str="+str);
		System.out.println("Test.String()  str="+str.toString());
		return str;
	}
	public String test2 (String str1,String str2){
		 
		if(str1==null && str2 == null){
			return null;
		}
		System.out.println("Test.String()  str="+str1.toString());
		System.out.println("Test.String()  str="+str2.toString());
		return null;
	}
}
