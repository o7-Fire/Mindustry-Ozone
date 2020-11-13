public class b implements Runnable {

    public static void main(String[] args) throws InterruptedException {
        String c = "\b\b";
        int sleep = 500;
        while (true) {
            Thread.sleep(sleep);
            System.out.print(c);
            System.out.print("||");
            Thread.sleep(sleep);
            System.out.print(c);
            System.out.print("\\\\");
            Thread.sleep(sleep);
            System.out.print(c);
            System.out.print("==");
            Thread.sleep(sleep);
            System.out.print(c);
            System.out.print("//");
        }
    }

    @Override
    public void run() {

    }
}
