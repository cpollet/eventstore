package net.cpollet.es.stores;

import com.google.common.truth.Truth;
import net.cpollet.es.data.GsonSerializer;
import net.cpollet.es.data.Serializer;
import net.cpollet.es.database.ConnectionFactory;
import net.cpollet.es.database.DefaultConnectionFactory;
import net.cpollet.es.utils.ClassUtils;
import net.cpollet.es.Event;
import net.cpollet.es.EventNotStoredException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class TestMySqlEventStore {
    private MySqlEventStore eventStore;

    private Serializer serializer;

    @Before
    public void setUp() throws IOException {
        serializer = new GsonSerializer();
        eventStore = new MySqlEventStore(connectionPool(), serializer);
    }

    private static ConnectionFactory connectionPool() throws IOException {
        Properties properties = new Properties();
        InputStream in = TestMySqlEventStore.class.getClassLoader().getResourceAsStream("datasource.properties");
        properties.load(in);
        in.close();

        return new DefaultConnectionFactory(
                new DataSource(
                        DataSourceFactory.parsePoolProperties(properties)
                )
        );
    }

    @Test
    public void store_returnEventInstance() throws EventNotStoredException {
        Map<String, String> meta = new HashMap<>();
        meta.put("key", "value");

        String aggregateId = "aggregateId";

        Event event = eventStore.store(aggregateId, new Payload("a", "b"), meta);

        Truth.assertThat(event.getId()).isNotEmpty();
        Truth.assertThat(event.getAggregateId()).isEqualTo(aggregateId);
        Truth.assertThat(event.getType()).isEqualTo(ClassUtils.getCanonicalBinaryName(Payload.class));
        Truth.assertThat(event.getPayload()).isEqualTo(new Payload("a", "b"));
        Truth.assertThat(event.getMetadata()).containsExactly("key", "value");
        Truth.assertThat(event.getVersion()).isNotNull();
        Truth.assertThat(event.getTimestamp()).isNotNull();
    }

    public static class Payload {
        String a;
        String b;

        Payload(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Payload payload = (Payload) o;

            return (a != null ? a.equals(payload.a) : payload.a == null) && (b != null ? b.equals(payload.b) : payload.b == null);
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }
    }
}
