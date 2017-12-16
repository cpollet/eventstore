package net.cpollet.es;

import net.cpollet.es.data.GsonSerializer;
import net.cpollet.es.database.MySqlEventStore;
import net.cpollet.es.database.TestMySqlEventStore;
import net.cpollet.es.database.TomcatConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws EventNotStoredException, IOException, InterruptedException, ExecutionException, TimeoutException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

        AsyncEventStore store = new DefaultAsyncEventStore(
                new DefaultListenableEventStore(
                        new MySqlEventStore(
                                new TomcatConnectionPool(dataSource()),
                                new GsonSerializer()
                        ),
                        (Listener) event -> {
                            System.out.println("listener: " + event);
                            executor.shutdown();
                        }
                ),
                executor
        );

        for (int i = 0; i < 100; i++) {
            store.store("aggregateId-" + i, "payload");
        }

        Future<StorageResult> future = store.store("aggregateId-0", "payload");

        System.out.println("future: " + future.get(2L, TimeUnit.SECONDS).getEvent());
    }

    private static DataSource dataSource() throws IOException {
        Properties properties = new Properties();
        InputStream in = TestMySqlEventStore.class.getClassLoader().getResourceAsStream("datasource.properties");
        properties.load(in);
        in.close();

        return new DataSource(
                DataSourceFactory.parsePoolProperties(properties)
        );
    }
}
