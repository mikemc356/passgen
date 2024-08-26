package com.systemwerx.PassGen;

import static org.junit.Assert.assertEquals;
import java.io.File;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class KeystoreTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testKeystoreNew() throws Exception {
        KeyStoreBean kb = new KeyStoreBean();
        kb.create(kb.Key_Supplied, "keystoreTest", "pw");
        kb.open(kb.Key_Supplied, "keystoreTest", "pw");
        KeyStoreEntry ke = new KeyStoreEntry();
        ke.setApplicationName("?????????");
        ke.setUserID("a");
        ke.setSessionKey("pw");
        ke.setGmtOffset(0);
        ke.setPasswordType(1);
        kb.addKey(ke);
        ke = kb.getKey("?????????");
        assertEquals(ke.getSessionKey(), "pw");
        assertEquals(ke.getUserID(), "a");
        kb.close();

        KeyStoreBean kb1 = new KeyStoreBean();
        KeyStoreEntry ke1 = new KeyStoreEntry();
        kb1.open(kb1.Key_Supplied, "keystoreTest", "pw");
        ke = kb1.getKey("?????????");
        assertEquals(ke.getSessionKey(), "pw");
        assertEquals(ke.getUserID(), "a");
        kb.close();
    }

    @AfterClass
    public static void cleanup() throws Exception {
        File keystore = new File("keystoreTest");
        keystore.delete();
    }

}