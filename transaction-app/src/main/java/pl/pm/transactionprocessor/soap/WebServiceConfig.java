package pl.pm.transactionprocessor.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/*
 * Klasa konfigurująca usługę sieciową SOAP do obsługi transakcji.
 */
@Order(1)
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        ServletRegistrationBean<MessageDispatcherServlet> servletRegistrationBean =
                new ServletRegistrationBean<>(servlet, "/ws/*");
        servletRegistrationBean.setLoadOnStartup(1);

        return servletRegistrationBean;
    }

    @Bean(name = "transactions")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema transactionsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("TransactionsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://transactionprocessor.pm.pl/soap");
        wsdl11Definition.setSchema(transactionsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema transactionsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/transactions.xsd"));
    }
}
