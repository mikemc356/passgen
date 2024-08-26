package com.systemwerx.PassGen;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class PassTicketTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testPasstickerGen() throws Exception {
        String TransTab =  "00010203372D2E2F1605250B0C0D0E0F101112133C3D322618193F271C1D1E1F405A7F7B4A6C507D4D5D5C4E6B604B61F0F1F2F3F4F5F6F7F8F97A5E4C7E6E6F7CC1C2C3C4C5C6C7C8C9D1D2D3D4D5D6D7D8D9E2E3E4E5E6E7E8E9B1E0BBBA6D79818283848586878889919293949596979899A2A3A4A5A6A7A8A9C04FD0BC0768DC5142434447485253545756586367719C9ECBCCCDDBDDDFECFC705B80BFFF4555CEDE49699A9BABAF5FB8B7AA8A8B2B2C092128656264B438313433B0B22422172906202A46661A35083936303A9F8CAC7273740A757677231514046A783BEE59EBEDCFEFA08EAEFEFBFD8DADA1BECA8F1BB9B6B5E19D90BDB3DAFAEA3E41";
        PassTicketPureBean passticket = new PassTicketPureBean();
        passticket.setApplication("TSOSYS1");
        passticket.setUserID("MIKEM");
        passticket.setSessionKey("E001193519561977");
        passticket.setGmtOffset(0);
        passticket.setTranslateArray(TransTab);
        passticket.setLicense("815C000182CB6D7B1D982137891E2F1FACE02BE967F1CC50C");
        passticket.setDebugTime(1111111);
        String ticket = passticket.getPassTicket();
        assertEquals(ticket, "AEPRRZ3S");
    }
}