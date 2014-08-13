package com.orctom.jenkins.plugin.globalpostscript;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hao on 8/13/2014.
 */
public class URL {

    private String protocol;
    private String host;
    private String uri;
    private Map<String, String> parameters = new LinkedHashMap<String, String>();

    public static final Pattern PATTERN = Pattern.compile("^(https?://)?((?:[\\w-]+\\.)+[\\w-]*(?::\\d+)?)(/[\\w\\/]*)*(?:\\?(.*))?(?:#([-a-z\\d_]+))?");

    public URL(String url) {
        Matcher matcher = PATTERN.matcher(url);
        String queryString = null;
        while (matcher.find()) {
            this.protocol = matcher.group(1);
            this.host = matcher.group(2);
            this.uri = matcher.group(3);
            queryString = matcher.group(4);
        }
        if (StringUtils.isNotEmpty(queryString)) {
            String[] params = StringUtils.split(queryString, '&');
            for (String param : params) {
                String[] entry = StringUtils.split(param, '=');
                parameters.put(entry[0], entry[1]);
            }
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParams() {
        return this.parameters;
    }

    public String removeParam(String param) {
        return this.parameters.remove(param);
    }

    public String getParamValue(String param) {
        return this.parameters.get(param);
    }

    public void updateParamValue(String param, String value) {
        this.parameters.put(param, value);
    }

    public void appendToParamValue(String param, String append) {
        String oldValue = getParamValue(param);
        String newValue = null == oldValue ? append : oldValue + append;
        updateParamValue(param, newValue);
    }

    public void prependToParamValue(String param, String prepend) {
        String oldValue = getParamValue(param);
        String newValue = null == oldValue ? prepend : prepend + oldValue;
        updateParamValue(param, newValue);
    }

    public String getQueryString() {
        StringBuilder queryString = new StringBuilder(100);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            queryString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        queryString.deleteCharAt(queryString.length() - 1);
        return queryString.toString();
    }

    public String getURL() {
        return protocol + host + uri + "?" + getQueryString();
    }
}
