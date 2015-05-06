package org.encryptor.simple;

/**
 * @author Ilya Tkachuk
 */
public class Starter {
    public static void main(String[] args) {
        Integer port = args.length > 0 ? Integer.valueOf(args[0]) : null;
        String encKey = args.length > 1 ? args[1] : null;

        Config config = new Config();
        config.setPort(port);
        config.setEncKey(encKey);

        new Dispatcher(config).start();
    }
}
