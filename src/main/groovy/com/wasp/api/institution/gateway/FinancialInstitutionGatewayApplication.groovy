package com.wasp.api.institution.gateway

import com.google.common.base.Predicate
import com.paymentcomponents.common.filters.JATFilter
import com.wasp.api.institution.gateway.interceptor.CustomHandlerInterceptor
import com.wasp.api.institution.gateway.kafka.interfaces.LogsChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import javax.servlet.Filter

import static com.google.common.base.Predicates.or
import static springfox.documentation.builders.PathSelectors.regex

@EnableZuulProxy
@EnableDiscoveryClient
@EnableCircuitBreaker
@Configuration
@EnableBinding(LogsChannel.class)
@IntegrationComponentScan
@EnableSwagger2
@SpringBootApplication
class FinancialInstitutionGatewayApplication extends WebMvcConfigurerAdapter {

	@Autowired
	Environment env

	@Bean
	public CustomHandlerInterceptor customHandlerInterceptor() {
		return new CustomHandlerInterceptor()
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory())
	}

	@Bean
	public Filter jatFilter() {
		return new JATFilter(restTemplate(), env)
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(paths())
				.build()
				.ignoredParameterTypes(MetaClass.class)
	}
	@Bean
	ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("WASP Switch")
				.description('''APIs Gateway''')
				.termsOfServiceUrl("")
				.license("")
				.licenseUrl("")
				.version("1.0")
				.build()
	}
	//Here is an example where we select any api that matches one of these paths
	private static Predicate<String> paths() {
		return or(
				regex("/v1.*"),
		);
	}

	@Override
	public void addInterceptors(InterceptorRegistry interceptorRegistry) {
		interceptorRegistry.addInterceptor(customHandlerInterceptor()).excludePathPatterns(
				"/v2/api-docs", "/swagger-resources/**", ".*.css", ".*.ico", "/webjars/**"
		)
	}

	static void main(String[] args) {
		SpringApplication.run FinancialInstitutionGatewayApplication, args
	}
}
