package net.cpollet.es;

import net.cpollet.es.data.GsonSerializer;
import net.cpollet.es.stores.DefaultAsyncEventStore;
import net.cpollet.es.stores.DefaultListenableEventStore;
import net.cpollet.es.stores.MySqlEventStore;
import net.cpollet.es.database.DefaultConnectionFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.awaitility.Awaitility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws EventNotStoredException, IOException, InterruptedException, ExecutionException, TimeoutException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

        AsyncEventStore store = new DefaultAsyncEventStore(
                new DefaultListenableEventStore(
                        new MySqlEventStore(
                                new DefaultConnectionFactory(dataSource()),
                                new GsonSerializer()
                        ),
                        (Listener) event -> {
                            System.out.println("[listener] " + event);
                        }
                ),
                executor
        );

        for (int i = 0; i < 100; i++) {
            store.storeAsync("aggregateId-" + i, "payload")
                    .thenAccept(r -> System.out.println("[then    ] " + r));
        }

        CompletableFuture<Event> future = store.storeAsync("aggregateId-0", "payload");

        System.out.println("[waiting ] For aggregateId-0 to finish");
        Awaitility.await().until(future::isDone);

        System.out.println("[future  ] " + future.get());

        System.out.println("[waiting ] All executors to be done");
        Awaitility.await().until(() -> executor.getActiveCount() == 0);

        executor.shutdown();
    }

    private static DataSource dataSource() throws IOException {
        Properties properties = new Properties();
        InputStream in = Main.class.getClassLoader().getResourceAsStream("datasource.properties");
        properties.load(in);
        in.close();

        return new DataSource(
                DataSourceFactory.parsePoolProperties(properties)
        );
    }
}
