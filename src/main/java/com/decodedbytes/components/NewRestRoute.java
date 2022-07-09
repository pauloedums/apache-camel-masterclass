package com.decodedbytes.components;

import com.decodedbytes.beans.InboundRestProcessingBean;
import com.decodedbytes.beans.NameAddress;
import com.decodedbytes.processor.InboundMessageProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.net.ConnectException;

@Component
public class NewRestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        Predicate isCityAjax = header("userCity").isEqualTo("Ajax");

        onException(JMSException.class, ConnectException.class)
                .routeId("jmsExceptionRouteId")
                .handled(true)
                        .log(LoggingLevel.INFO, "JMS Exception has ocurred; handling graceful");

        restConfiguration()
                .component("jetty")
                .host("0.0.0.0")
                .port(8080)
                .bindingMode(RestBindingMode.json)
                .enableCORS(true);

        rest("masterclass")
                .produces("application/json")
                .post("nameAddress").type(NameAddress.class)
                .route()
                .routeId("newRestRouteId")
                .log(LoggingLevel.INFO, "${body}")

                .bean(new InboundRestProcessingBean())

                    // Setup Rule
                    // If City = Ajax -> send to MQ
                    // Else Send to both DB and MQ

                .choice()
                .when(isCityAjax)
                    .to("direct:toActiveMQ")
                .otherwise()
                    .to("direct:toDB")
                    .to("direct:toActiveMQ")
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform().simple("Message Processed and Result Generated with Body: ${body}")
                .endRest();
//                .process(new InboundMessageProcessor())
//                .log(LoggingLevel.INFO, "Transformed body: " + body())
//                .convertBodyTo(String.class)
//                .to("file:src/data/output?fileName=outputFile.csv&fileExist=append&appendChars=\\n")
//                .end();
        from("direct:toDB")
                .routeId("toDBId")
                .log(LoggingLevel.INFO, ">>> In DB EP")
                .to("jpa:"+NameAddress.class.getName());
        from("direct:toActiveMQ")
                .routeId("toActiveMQId")
                .log(LoggingLevel.INFO, ">>> In AMQ EP")
                .to("activemq:queue:nameaddressqueue?exchangePattern=InOnly");

    }
}
