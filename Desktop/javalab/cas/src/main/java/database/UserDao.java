package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import domains.User;



//Data Access Object
//CRUD，Create Read Update Delete
public class UserDao {
	private static final String driverClass="com.mysql.cj.jdbc.Driver";
	private static final String jdbcURL="jdbc:mysql://localhost:3306/usermanager?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useSSL=false";
	private static final String user="root";
	private static final String pwd="root";
	//获取数据库连接
	public static Connection getConnection() throws Exception
	{
		Class.forName(driverClass);
		Connection conn=DriverManager.getConnection(jdbcURL, user, pwd);
		return conn;
	}
	//以User对象添加
	public static Boolean add(final User user)throws Exception
	{
		Connection conn=getConnection();
		conn.setAutoCommit(false);//开启了事务操作
		try{
			PreparedStatement ps=conn.prepareStatement("insert into user(id,pwd,name,email,age) values(?,?,?,?,?)");
			ps.setString(1, user.getId());
			ps.setString(2, user.getPwd());
			ps.setString(3, user.getName());
			ps.setString(4, user.getEmail());
			ps.setInt(5, user.getAge());
			ps.execute();
			conn.commit();
			return true;
		}catch(Exception e)
		{
			
			conn.rollback();
			throw e;
		}finally
		{
			conn.close();
		}
	}
	//添加
	public static void addUser(String id,String pwd,String name,String email,int age)throws Exception
	{
		User user=new User();
		user.setAge(age);
		user.setEmail(email);
		user.setId(id);
		user.setName(name);
		user.setPwd(pwd);
		Connection conn=getConnection();
		conn.setAutoCommit(false);//开启了事务操作
		try{
			PreparedStatement ps=conn.prepareStatement("insert into user(id,pwd,name,email,age) values(?,?,?,?,?)");
			ps.setString(1, user.getId());
			ps.setString(2, user.getPwd());
			ps.setString(3, user.getName());
			ps.setString(4, user.getEmail());
			ps.setInt(5, user.getAge());
			ps.execute();
			conn.commit();
		}catch(Exception e)
		{
			
			conn.rollback();
			throw e;
		}finally
		{
			conn.close();
		}		
	}
	
	//获取
	public static User get(final String id)throws Exception
	{
		Connection conn=getConnection();
		try{
			PreparedStatement ps=conn.prepareStatement("select * from user where id=?");
			ps.setString(1,id);
			ps.execute();
			ResultSet rs=ps.getResultSet();
			User user=null;
			if(rs.next())
			{
				user=new User();
				user.setId(rs.getString("id"));
				user.setPwd(rs.getString("pwd"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setAge(rs.getInt("age"));
			}
			return user;
		}catch(Exception e)
		{
			throw e;
		}
		finally
		{
			conn.close();
		}
	}
	
	//获取全部
	public static List<User> getAll()throws Exception
	{
		List<User> users=new ArrayList<User>();
		Connection conn=getConnection();
		try{
			PreparedStatement ps=conn.prepareStatement("select * from user");
			ps.execute();
			ResultSet rs=ps.getResultSet();
			while(rs.next())
			{
				User user=new User();
				user.setId(rs.getString("id"));
				user.setPwd(rs.getString("pwd"));
				user.setName(rs.getString("name"));
				user.setEmail(rs.getString("email"));
				user.setAge(rs.getInt("age"));
				users.add(user);
			}
			return users;
		}catch(Exception e)
		{
			throw e;
		}
		finally
		{
			conn.close();
		}
	}
	//修改
	public static Boolean update(final User user,final String oldId)throws Exception
	{
		Connection conn=getConnection();
		conn.setAutoCommit(false);
		try{
			PreparedStatement ps=conn.prepareStatement("update user set id=?,pwd=?,name=?,email=?,age=? where id=?");
			ps.setString(1, user.getId());
			ps.setString(2, user.getPwd());
			ps.setString(3, user.getName());
			ps.setString(4, user.getEmail());
			ps.setInt(5, user.getAge());
			ps.setString(6, oldId);
			ps.execute();
			conn.commit();
			return true;
		}catch(Exception e)
		{
			conn.rollback();
			throw e;
		}finally
		{
			conn.close();
		}
	}
	//删除
	public static Boolean delete(final String id)throws Exception
	{
		Connection conn=getConnection();
		conn.setAutoCommit(false);
		try{
			PreparedStatement ps=conn.prepareStatement("delete from user where id=?");
			ps.setString(1,id);
			ps.execute();
			conn.commit();
			return true;
		}catch(Exception e)
		{
			conn.rollback();
			throw e;
		}finally
		{
			conn.close();
		}
	}
}
