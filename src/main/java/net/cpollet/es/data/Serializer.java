package net.cpollet.es.data;

public interface Serializer {
    String serialize(Object object);

    <T> T deserialize(String string, String className) throws ClassNotFoundException;
}
