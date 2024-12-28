import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class SpringAsyncDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAsyncDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner runApplication() {
        return args -> {
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

            executorService.schedule(() -> {
                try {
                    executeTaskWithRetries();
                } catch (Exception e) {
                    System.err.println("Task failed after 3 attempts: " + e.getMessage());
                }
            }, 0, TimeUnit.SECONDS);

            executorService.scheduleAtFixedRate(() ->
                            System.out.println("15 секунд від запуску"),
                    15, 15, TimeUnit.SECONDS);
        };
    }

    private void executeTaskWithRetries() throws Exception {
        int maxAttempts = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxAttempts && !success) {
            attempt++;
            try {
                System.out.println("Attempting task execution, attempt: " + attempt);
                performCriticalTask();
                success = true;
                System.out.println("Task executed successfully.");
            } catch (Exception e) {
                System.err.println("Task failed on attempt " + attempt + ". Retrying in 5 seconds...");
                if (attempt == maxAttempts) {
                    throw new Exception("Task failed after maximum attempts", e);
                }
                Thread.sleep(5000);
            }
        }
    }

    private void performCriticalTask() throws Exception {
        // Simulate task logic, throwing an exception randomly for demo purposes.
        if (Math.random() > 0.7) {
            throw new Exception("Simulated task failure");
        }
    }
}
