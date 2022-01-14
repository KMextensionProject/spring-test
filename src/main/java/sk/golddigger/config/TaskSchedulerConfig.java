package sk.golddigger.config;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * This configuration class enables task scheduling managed by custom
 * {@link ScheduledExecutorService} with pool size defined by the
 * {@code THREAD_POOL_SIZE} property.
 * 
 * @author mkrajcovic
 */
@Configuration
@EnableScheduling
public class TaskSchedulerConfig implements SchedulingConfigurer {

	private static final Logger logger = Logger.getLogger(TaskSchedulerConfig.class);

	private static final int THREAD_POOL_SIZE = 1;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// java.util.concurrent.ScheduledExecutorService gets wrapped with TaskScheduler
		taskRegistrar.setScheduler(taskExecutor());
		logger.info(resolveMessage("schedulerInit", THREAD_POOL_SIZE));
	}

	@Bean
	public ScheduledExecutorService taskExecutor() {
		return Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
	}
}
