package tr.gov.gib.evdbelge.evdbelgepdfolusturma.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfiguration {
	private static final Duration TIMEOUT = Duration.ofSeconds(1);
	@Value("${redis.host}")
	private String redisHostName;

	@Value("${redis.password}")
	private String password;

	@Value("${redis.port}")
	private int port;

	@Value("${server.servlet.context-path}")
	private String clientName;

	@Bean("lettuce")
	@Primary
	public LettuceConnectionFactory lettuceConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostName,
				port);
		redisStandaloneConfiguration.setPassword(password);
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().clientName(clientName).commandTimeout(TIMEOUT).build();
		LettuceConnectionFactory lcf = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
		lcf.afterPropertiesSet();
		return lcf;
	}
	@Bean("lettuceReadOnly")
	public LettuceConnectionFactory lettuceConnectionFactory2() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostName,
				port);
		redisStandaloneConfiguration.setPassword(password);
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder().clientName(clientName + "READONLY").commandTimeout(TIMEOUT).build();
		LettuceConnectionFactory lcf = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
		lcf.afterPropertiesSet();
		return lcf;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, String> redisTemplate(@Qualifier("lettuce") RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setHashValueSerializer(new StringRedisSerializer());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setEnableTransactionSupport(true);
		template.afterPropertiesSet();
		return template;
	}

	@Bean(name = "readOnlyRedisTemplate")
	public RedisTemplate<String, String> readOnlyRedisTemplate(@Qualifier("lettuceReadOnly") RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setHashValueSerializer(new StringRedisSerializer());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.afterPropertiesSet();
		return template;
	}
}
