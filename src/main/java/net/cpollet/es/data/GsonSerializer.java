package net.cpollet.es.data;

import com.google.gson.Gson;

public class GsonSerializer implements Serializer {
    @Override
    public String serialize(Object object) {
        if (object == null) {
            return null;
        }

        return new Gson().toJson(object);
    }

    @Override
    public <T> T deserialize(String string, String className) throws ClassNotFoundException {
        if (string == null) {
            return null;
        }

        return new Gson().<T>fromJson(string, Class.forName(className));
    }
}
