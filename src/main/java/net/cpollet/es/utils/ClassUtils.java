package net.cpollet.es.utils;

public class ClassUtils {
    private ClassUtils() {
        // utility class, cannot be instantiated
    }

    public static String getCanonicalBinaryName(Class clazz) {
        if (!clazz.isMemberClass()) {
            return clazz.getCanonicalName();
        }

        return clazz.getEnclosingClass().getCanonicalName() + "$" + clazz.getSimpleName();
    }
}
