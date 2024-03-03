package de.mnet.common.tools;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.testng.Assert;

public class ExceptionToolsTest {

    @Test
    public void testGetMessageList() throws Exception {
        final String s1 = "Ich bin Exception 1";
        final String s2 = "Und ich bin Exception 2";
        final RuntimeException r1 = new RuntimeException(s1);
        final RuntimeException r2 = new RuntimeException(s2, r1);

        Assert.assertEquals(ExceptionTools.getMessageList(r2), ImmutableList.of(s2, s1));
    }
}
