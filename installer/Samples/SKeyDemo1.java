import java.io.Serializable;
import com.systemwerx.utils.*;
import com.systemwerx.PassGen.*;
import java.util.*;

public class SKeyDemo1
{ 
	public static void main(String args[]) 
	{
	  try 
	   {
       SKeyBean s = new SKeyBean();
       s.setSeed("1111");
       s.setPassword("2222");
       s.setHashAlgorithm(s.MD5);
       System.out.println(" MD5 Password is "+s.generatePassword(23));
       s.setHashAlgorithm(s.MD4);
       System.out.println(" MD4 Password is "+s.generatePassword(23));
       }
      catch ( Exception e)
       {
        Class c = e.getClass();
        String s = c.getName();
        System.out.println("Exception >>>"+s+"  "+e.getMessage());
       }       
       return;
    } 
}
