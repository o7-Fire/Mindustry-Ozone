import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxyWar {

    public static void main(String[] args) throws Throwable {
        InetSocketAddress s = new InetSocketAddress("127.0.0.1", 8080);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, s);
    }
}
