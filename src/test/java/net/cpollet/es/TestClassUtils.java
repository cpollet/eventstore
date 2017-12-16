package net.cpollet.es;

import com.google.common.truth.Truth;
import net.cpollet.es.utils.ClassUtils;
import org.junit.Test;

public class TestClassUtils {
    @Test
    public void getCanonicalBinaryName_returnsCanonicalName_forNonInternalClasses() {
        String className = ClassUtils.getCanonicalBinaryName(this.getClass());

        Truth.assertThat(className).isEqualTo("net.cpollet.es.TestClassUtils");
    }

    @Test
    public void getCanonicalBinaryName_returnsCanonicalBinaryName_forInternalClasses() {
        String className = ClassUtils.getCanonicalBinaryName(Internal.class);

        Truth.assertThat(className).isEqualTo("net.cpollet.es.TestClassUtils$Internal");
    }

    private class Internal {
    }
}
