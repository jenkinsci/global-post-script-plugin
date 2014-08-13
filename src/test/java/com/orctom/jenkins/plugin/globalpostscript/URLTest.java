package com.orctom.jenkins.plugin.globalpostscript;

import junit.framework.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hao on 8/13/2014.
 */
public class URLTest {

    @Test
    public void testPattern() {
        Assert.assertTrue(URL.PATTERN.matcher("http://www.google.com").matches());
        Assert.assertTrue(URL.PATTERN.matcher("http://www.google.com:100/abc?hello").matches());
        Assert.assertTrue(URL.PATTERN.matcher("https://google.com/a?a=b&b=a+b+c").matches());
        Assert.assertTrue(URL.PATTERN.matcher("http://10.20.13.15/hello/abc?a=b&b=a+b+c").matches());
        Assert.assertTrue(URL.PATTERN.matcher("https://10.20.13.15/hello/abc?a=b&b=a+b+c").matches());

        Assert.assertFalse(URL.PATTERN.matcher("http:/www.google.com").matches());
        Assert.assertFalse(URL.PATTERN.matcher("//www.google.com:80/h").matches());
        Assert.assertFalse(URL.PATTERN.matcher("http:/www.google.com").matches());
        Assert.assertTrue(URL.PATTERN.matcher("http://10.20.13/").matches());
        Assert.assertTrue(URL.PATTERN.matcher("http://ecopsci.uschecomrnd.net/buildByToken/build?job=sync_ag2content_to_dev&token=8a36b668396e7aed7b4576f90bbbdc37").matches());
    }

    @Test
    public void testAppendToParamValue() {
        String jobUrl = "http://ecopsci.uschecomrnd.net/buildByToken/build?job=sync_ag2content_to_dev&token=8a36b668396e7aed7b4576f90bbbdc37";
        URL url = new URL(jobUrl);
        Assert.assertEquals(jobUrl, url.getURL());
        Assert.assertEquals("ecopsci.uschecomrnd.net", url.getHost());
        Assert.assertEquals("http://", url.getProtocol());
        Assert.assertEquals("/buildByToken/build", url.getUri());
        Assert.assertEquals("job=sync_ag2content_to_dev&token=8a36b668396e7aed7b4576f90bbbdc37", url.getQueryString());
        url.appendToParamValue("job", "_appendix");
        Assert.assertEquals("job=sync_ag2content_to_dev_appendix&token=8a36b668396e7aed7b4576f90bbbdc37", url.getQueryString());
    }
}
