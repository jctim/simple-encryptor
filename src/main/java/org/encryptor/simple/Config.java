package org.encryptor.simple;

/**
 * @author Ilya Tkachuk
 */
public class Config {
    private Integer port;
    private String encKey;

    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public String getEncKey() {
        return encKey;
    }

    public void setEncKey(final String encKey) {
        this.encKey = encKey;
    }
}
