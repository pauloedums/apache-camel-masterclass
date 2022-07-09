package com.decodedbytes.processor;

import com.decodedbytes.beans.NameAddress;
import com.decodedbytes.beans.OutboundNameAddress;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundMessageProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NameAddress nameAddress = exchange.getIn().getBody(NameAddress.class);
        exchange.getIn().setBody(new OutboundNameAddress(nameAddress.getName(), returnOutboundAddresss(nameAddress)));
        exchange.getIn().setHeader("consumedId", nameAddress.getId());
    }

    private String returnOutboundAddresss(NameAddress nameAddress){
        StringBuilder concatenatedAddress = new StringBuilder(200);
        concatenatedAddress.append(nameAddress.getHouseNumber());
        concatenatedAddress.append(" " + nameAddress.getCity() + ",");
        concatenatedAddress.append(" " + nameAddress.getProvince());
        concatenatedAddress.append(" " + nameAddress.getPostalCode());
        return concatenatedAddress.toString();
    }
}
