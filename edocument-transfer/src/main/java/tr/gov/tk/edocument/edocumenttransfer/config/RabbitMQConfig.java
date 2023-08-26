package tr.gov.gib.evdbelge.evdbelgeaktarma.config;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts}")
    private int maxAttempts;
    @Value("${spring.rabbitmq.listener.simple.retry.max-interval}")
    private int maxInterval;
    @Value("${spring.rabbitmq.listener.simple.retry.initial-interval}")
    private int initialInterval;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setChannelTransacted(true);
        //rabbitTemplate.setReplyErrorHandler(new BrokerExceptionHandler());
        return rabbitTemplate;
    }

    @Bean
    public ObservableRejectAndDontRequeueRecoverer observableRecoverer() {
        return new ObservableRejectAndDontRequeueRecoverer();
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .backOffOptions(initialInterval, 1.0, maxInterval)
                .maxAttempts(maxAttempts)
                .recoverer(observableRecoverer())
                .build();
    }


    @Bean
    public SimpleRabbitListenerContainerFactory retryContainerFactory(ConnectionFactory connectionFactory, RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());

        Advice[] adviceChain = { retryInterceptor };
        factory.setAdviceChain(adviceChain);

        return factory;
    }
}
