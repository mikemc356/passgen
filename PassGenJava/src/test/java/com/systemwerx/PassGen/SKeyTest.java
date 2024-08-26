package com.systemwerx.PassGen;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SKeyTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSkeyGen() throws Exception {
        try 
        {
        SKeyBean s = new SKeyBean();
        s.setSeed("1111");
        s.setPassword("2222");
        s.setHashAlgorithm(s.MD5);
        String pwd = s.generatePassword(23);
        assertEquals(pwd,"NINE SILK ARK WEAR KICK COCK");
        System.out.println(" MD5 Password is "+s.generatePassword(23));
        s.setHashAlgorithm(s.MD4);
        pwd = s.generatePassword(23);
        assertEquals(pwd,"LET TOY HUED TILE LARK EMIT");
        }
       catch ( Exception e)
        {
         Class c = e.getClass();
         String s = c.getName();
         System.out.println("Exception >>>"+s+"  "+e.getMessage());
        }       
 
        //assertEquals(ticket, "AEPRRZ3S");
    }
}