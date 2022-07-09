package com.decodedbytes.beans;

import org.apache.camel.Exchange;

public class InboundRestProcessingBean {

    public void validate(Exchange exchange){
        NameAddress nameAddress = exchange.getIn().getBody(NameAddress.class);
        exchange.getIn().setHeader("userCity", nameAddress.getCity());
    }

}
