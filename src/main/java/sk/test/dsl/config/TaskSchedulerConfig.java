package sk.test.dsl.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import sk.test.dsl.store.StoreName;

@Configuration
@EnableScheduling
public class TaskSchedulerConfig implements SchedulingConfigurer {

	private static final Logger LOGGER = Logger.getLogger(TaskSchedulerConfig.class.getName());

	private static final int THREAD_POOL_SIZE = StoreName.values().length - 1;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// java.util.concurrent.ScheduledExecutorService gets wrapped with TaskScheduler
		taskRegistrar.setScheduler(taskExecutor());
		LOGGER.info("Scheduled executor service has been initialized with the pool size of " + THREAD_POOL_SIZE);
	}

	@Bean
	public ScheduledExecutorService taskExecutor() {
		return Executors.newScheduledThreadPool(THREAD_POOL_SIZE, getDaemonThreadFactory());
	}

	@Bean
	public ThreadFactory getDaemonThreadFactory() {
		return task -> {
			Thread thread = new Thread(task);
			thread.setDaemon(true);
			return thread;
		};
	}
}
