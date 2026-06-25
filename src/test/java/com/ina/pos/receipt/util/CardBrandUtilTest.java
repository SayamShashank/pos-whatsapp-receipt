package com.ina.pos.receipt.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardBrandUtilTest {

    @Test
    void testResolveBrand(){
        String maestro = CardBrandUtil.resolveBrand("678729");
        String masterCard = CardBrandUtil.resolveBrand("238729");
        String amex = CardBrandUtil.resolveBrand("348729");
        String discover = CardBrandUtil.resolveBrand("601129");
        String diners = CardBrandUtil.resolveBrand("301129");
        String jcb = CardBrandUtil.resolveBrand("352830");
        String rupay = CardBrandUtil.resolveBrand("818501");
        assertEquals("DINERS",diners);
        assertEquals("MAESTRO",maestro);
        assertEquals("MASTERCARD",masterCard);
        assertEquals("AMEX",amex);
        assertEquals("DISCOVER",discover);
        assertEquals("JCB",jcb);
        assertEquals("RUPAY",rupay);
    }

}
