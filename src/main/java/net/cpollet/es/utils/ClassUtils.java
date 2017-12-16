package net.cpollet.es.utils;

public class ClassUtils {
    public static String getCanonicalBinaryName(Class clazz) {
        if (!clazz.isMemberClass()) {
            return clazz.getCanonicalName();
        }

        return clazz.getEnclosingClass().getCanonicalName() + "$" + clazz.getSimpleName();
    }
}
