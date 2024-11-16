public class DiningPhilosophers {

    private static final int NUM_PHILOSOPHERS = 5;

    private static final Object[] chopsticks = new Object[NUM_PHILOSOPHERS];

    private static final boolean[] isWaiting = new boolean[NUM_PHILOSOPHERS];

    static class FamineProbe extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                boolean isAllWaiting = true;
                for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
                    if(!isWaiting[i]) {
                        isAllWaiting = false;
                        break;
                    }
                }
                if(isAllWaiting) {
                    System.out.println("Oh no! It looks like everyone's going to starve to death...");
                    System.exit(-1);
                }
            }
        }
    }

    static class Philosopher extends Thread {
        private final int id;
        public Philosopher(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }

        private void think() {
            System.out.println("Philosopher " + id + " is thinking ...");
            try {
                Thread.sleep((long) (Math.random() * 20000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void eat() {
            int left = id % NUM_PHILOSOPHERS;
            int right = (id + 1) % NUM_PHILOSOPHERS;
            isWaiting[id] = true;

            synchronized (chopsticks[left]) {
                System.out.println("Philosopher " + id + " is hungry and picked up the chopsticks on his left hand.");
                synchronized (chopsticks[right]) {
                    isWaiting[id] = false;
                    System.out.println("Philosopher " + id + " picked up the chopsticks on his right hand.");
                    System.out.println("Philosopher " + id + " is eating.");
                    try {
                        Thread.sleep((long) (Math.random() * 5000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("Philosopher " + id + " is full now.");
                }
                System.out.println("Philosopher " + id + " put down right chopstick.");
            }
            System.out.println("Philosopher " + id + " put down left chopstick.");
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            chopsticks[i] = new Object();
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            new Philosopher(i).start();
        }

        new FamineProbe().start();
    }
}
