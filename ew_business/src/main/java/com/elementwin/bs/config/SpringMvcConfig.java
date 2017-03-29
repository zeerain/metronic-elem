package com.elementwin.bs.config;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.security.SecurityInterceptor;

/***
 * Spring配置文件
 * @author Mazc@dibo.ltd
 */
@Configuration
@EnableWebMvc
@EnableAsync
@EnableTransactionManagement(proxyTargetClass=true)
@ComponentScan(basePackages = "com.elementwin.bs")
@MapperScan(basePackages = "com.elementwin.bs.service.mapper")
public class SpringMvcConfig extends WebMvcConfigurerAdapter implements AsyncConfigurer{

	private static Logger logger = Logger.getLogger(SpringMvcConfig.class);
	
	private final Long maxUploadSize = 10485760L;
	
	@Bean(name = "dataSource")
    public DataSource dataSource() {
    	// 使用DBCP2的DataSource
		BasicDataSource dataSource = new BasicDataSource();
    	dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    	dataSource.setUrl(MetadataCache.getSystemProperty("datasource.url"));
    	dataSource.setUsername(MetadataCache.getSystemProperty("datasource.username"));
    	dataSource.setPassword(MetadataCache.getSystemProperty("datasource.password"));
    	dataSource.setInitialSize(10);
    	dataSource.setMaxIdle(20);
    	dataSource.setMinIdle(5);
    	dataSource.setMaxTotal(100);
    	dataSource.setMaxWaitMillis(30000);
    	dataSource.setRemoveAbandonedOnMaintenance(true);
    	//dataSource.setRemoveAbandonedTimeout(180);
    	//dataSource.setValidationQuery("SELECT 1");
    	//dataSource.setTestWhileIdle(true);
    	//dataSource.setTimeBetweenEvictionRunsMillis(300000);
    	//dataSource.setNumTestsPerEvictionRun(30);
    	//dataSource.setMinEvictableIdleTimeMillis(3600000);
    	
    	return dataSource;
    }

	@Bean
    public PlatformTransactionManager txManager() {
		PlatformTransactionManager txManager = new DataSourceTransactionManager(dataSource());
		return txManager;
    }
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	SecurityInterceptor appInterceptor = new SecurityInterceptor();
    	registry.addInterceptor(appInterceptor);
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception{
    	SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
    	sqlSessionFactory.setDataSource(dataSource());
    	// 设置mybatis-config的路径
    	sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
    	// 获取当前package并切换到model的路径
    	sqlSessionFactory.setTypeAliasesPackage("com.elementwin.ms.model");
    	
    	return sqlSessionFactory.getObject();
    }
    
    @Bean(name = "freeMarkerConfigurer")
    public FreeMarkerConfigurer freeMarkerConfigurer(){
    	FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
    	freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/views/");
    	
    	Properties settings = new Properties();
    	settings.setProperty("locale", "zh_CN");
    	settings.setProperty("default_encoding", "UTF-8");
    	settings.setProperty("url_escaping_charset", "UTF-8");
    	settings.setProperty("number_format", "#");
    	settings.setProperty("date_format", "yyyy/MM/dd");
    	settings.setProperty("time_format", "HH:mm");
    	settings.setProperty("datetime_format", "yyyy/MM/dd HH:mm");
    	
    	if(logger.isDebugEnabled()){
    		settings.setProperty("template_update_delay", "0");
    	}
    	else{
    		settings.setProperty("template_update_delay", "36000000");
    		settings.setProperty("template_exception_handler", "ignore");
    		logger.info("Freemarker Template 缓存生效.");
    	}
    	
    	freeMarkerConfigurer.setFreemarkerSettings(settings);
    	
    	return freeMarkerConfigurer;
    }
    
    @Bean(name = "viewResolver")
	public FreeMarkerViewResolver viewResolver() {
    	FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
    	
    	viewResolver.setCache(true);
    	viewResolver.setPrefix("");
    	viewResolver.setSuffix(".htm");
    	viewResolver.setContentType("text/html;charset=UTF-8");
    	// get session 
    	viewResolver.setAllowSessionOverride(true);
    	viewResolver.setExposeRequestAttributes(true);
    	viewResolver.setExposeSessionAttributes(true);
    	viewResolver.setExposeSpringMacroHelpers(true);
    	
    	viewResolver.setRequestContextAttribute("ctx");
    	
		return viewResolver;
	}

    @Override
    public Validator getValidator(){
    	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    	validator.setProviderClass(org.hibernate.validator.HibernateValidator.class);

    	return validator;
    }
 
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/res/**").addResourceLocations("/res/");
    	registry.addResourceHandler("/img/**").addResourceLocations("/img/");
    }
    
    /**
     * 配置Json与Java对象的转换器
     */
    @Override  
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {  
    	converters.add(new MappingJackson2HttpMessageConverter());  
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }
    
    @Bean  
    public MultipartResolver multipartResolver(){  
        CommonsMultipartResolver bean = new CommonsMultipartResolver();  
        bean.setDefaultEncoding("UTF-8");  
        bean.setMaxUploadSize(maxUploadSize);  
        return bean;
    }
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(3);
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}
}