package hello;

public class FireGreeting implements Runnable {

    private GreetingController listener;

    public FireGreeting(GreetingController listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                // listener.fireGreeting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}