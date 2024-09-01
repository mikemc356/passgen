package com.systemwerx.PassGen;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

public class SxCmdLineTest {

    SxPassGenCmd sxPassGenCmd = new SxPassGenCmd();

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCmdLine() throws Exception {
        System.setProperty("sxlicense", "");
        String parms[] = {"/createapplication", "/application:TSOS0W1","/keystore:keystore","/keystpwd:pwd",
        "/type:ptkt", "/userid:joe", "/sesskey:E001193519561977", "/offset:0"};
        assertEquals(0, sxPassGenCmd.processCommandLine(parms));

        String[] arr1 ="/GENERATEPTKT /application:TSOS0W1 /OFFSET:0 /USERID:MIKEM /SESSKEY:E001193519561977".split(" ");
        assertEquals(sxPassGenCmd.processCommandLine(arr1),0);
        assertEquals(8, systemOutRule.getLog().trim().length());
        systemOutRule.clearLog();

        String[] arr2 ="/GENERATEPTKT /application:TSOS0W1 /KEYSTORE:keystore /keystpwd:pwd".split(" ");
        assertEquals(sxPassGenCmd.processCommandLine(arr2),0);
        assertEquals(8, systemOutRule.getLog().trim().length());
        systemOutRule.clearLog();

        String[] arr3 ="/GENERATEPTKT /application:TSOS0WX /OFFSET:0 /USERID:MIKEM /SESSKEY:E00119351956197".split(" ");
        assertEquals(16,sxPassGenCmd.processCommandLine(arr3));
        systemOutRule.clearLog();

        String[] arr4 = "/GENERATESKEY /application:TSOS0W1 /SEED:1111 /PASSWORD:mike /SEQUENCE:21".split(" ");
        assertEquals(sxPassGenCmd.processCommandLine(arr4),0);
        assertEquals(6,systemOutRule.getLog().trim().split(" ").length);
        systemOutRule.clearLog();

    }

    @AfterClass
    public static void cleanup() throws Exception {
    }

}