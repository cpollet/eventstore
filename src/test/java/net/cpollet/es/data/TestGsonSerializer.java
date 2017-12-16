package net.cpollet.es.data;

import com.google.common.truth.Truth;
import net.cpollet.es.utils.ClassUtils;
import org.junit.Before;
import org.junit.Test;

public class TestGsonSerializer {
    private GsonSerializer serializer;

    @Before
    public void setUp() {
        serializer = new GsonSerializer();
    }

    @Test
    public void serialize_returnsJson() {
        ObjectToSerialize objectToSerialize = new ObjectToSerialize(1L, "someString");

        Object serialized = serializer.serialize(objectToSerialize);

        Truth.assertThat(serialized).isEqualTo("{\"longValue\":1,\"stringValue\":\"someString\"}");
    }

    @Test
    public void deserialize_returnsObject() throws ClassNotFoundException {
        ObjectToSerialize object = serializer.deserialize(
                "{\"longValue\":1,\"stringValue\":\"someString\"}",
                ClassUtils.getCanonicalBinaryName(ObjectToSerialize.class));

        Truth.assertThat(object.longValue).isEqualTo(1L);
        Truth.assertThat(object.stringValue).isEqualTo("someString");
    }

    @Test
    public void serialize_returnsNull_whenObjectIsNull() {
        Truth.assertThat(serializer.serialize(null)).isNull();
    }

    @Test
    public void deserialize_returnsNull_whenStringIsNull() throws ClassNotFoundException {
        Truth.assertThat(serializer.<String>deserialize((String) null, "java.lang.String")).isNull();
    }

    class ObjectToSerialize {
        long longValue;
        String stringValue;

        ObjectToSerialize(long longValue, String stringValue) {
            this.longValue = longValue;
            this.stringValue = stringValue;
        }
    }
}
