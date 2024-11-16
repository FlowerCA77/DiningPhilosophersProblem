/**
 * <h1>The Dining Problem Philosophers</h1>
 * <h3>by <i>Dijkstra</i></h3>
 * <p>There are <code>NUM_PHILOSOPHERS</code> <i>(default=5)</i> philosophers sitting around a round table, and each philosopher has two behaviors:</p>
 * <ol>
 * <li><b>Thinking: </b>when a philosopher is thinking, he does not need to share resources with other philosophers.</li>
 * <li><b>Eating: </b>when a philosopher eats, he needs to use cutlery, which is shared at the table.</li>
 * </ol>
 * <p>There are <code>NUM_PHILOSOPHERS</code> chopsticks on the table, and each philosopher needs a pair of chopsticks to eat. Each philosopher is on a separate side of the table, with one chopstick in front of him and one chopstick shared with the philosophers to his left and right. Thus, philosophers must have both chopsticks when eating.</p>
 * <p>This program is designed to implement a simulator for this scenario.</p>
 * @author Flower CA77
 */
public class DiningPhilosophers {

    private static final int NUM_PHILOSOPHERS = 5;

    private static final Object[] chopsticks = new Object[NUM_PHILOSOPHERS];

    private static final boolean[] isWaiting = new boolean[NUM_PHILOSOPHERS];

    /**
     * <h2>Famine Probe Thread <code>FamineProbe</code></h2>
     * <p>This thread detects if there is a famine every 2 seconds, when all the philosophers are holding only 1 chopstick, and since there are no more chopsticks on the table the philosophers will starve to death.</p>
     * <p>In fact, this constitutes a deadlock in the circular dependency model.</p>
     * */
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
                    if (!isWaiting[i]) {
                        isAllWaiting = false;
                        break;
                    }
                }
                if (isAllWaiting) {
                    System.out.println("Oh no! It looks like everyone's going to starve to death...");
                    System.exit(-1);
                }
            }
        }
    }

    /**
     * <h2>Philosopher Thread Class <code>Philosopher</code></h2>
     * <p>This thread class constitutes a simulator for the behavioral patterns of philosophers. Each philosopher can only think or eat when he or she comes to the table; philosophers do not eat consecutively. Thinking is implemented as the private method <code>think()</code> and eating is implemented as the private method <code>eat()</code>.</p>
     * */
    static class Philosopher extends Thread {
        private final int id;

        /**
         * <h2>constructor <code>Philosopher(int id)</code></h2>
         * <p>(Generate a philosopher) and bring him to the table.</p>
         * @param id Philosopher's ID
         * */
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

        /**
         * <h2>Philosophers will think.</h2>
         * <p>The philosopher will think, and in this model its thinking time obeys a uniform distribution between (0,20) seconds.</p>
         * */
        private void think() {
            System.out.println("Philosopher " + id + " is thinking ...");
            try {
                Thread.sleep((long) (Math.random() * 20000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * <h2>Philosophers will dine.</h2>
         * <p>The philosopher will be dining, and he will first try to get the chopstick on the left hand side and then try to get the chopstick on the right hand side. The philosopher will eat only if he acquires both two chopsticks at the same time, and the time taken to eat obeys a uniform distribution of (0,5) seconds.</p>
         * <p>If the philosopher is unable to obtain chopsticks before he is ready to eat, then he will wait, at which point he will not put his or her chopsticks down. In our simulation, we assume that if all the philosophers are waiting to get chopsticks, then necessarily they will wait forever (because there are no chopsticks left on the table), at which point we assume that they will all starve to death, i.e., there will be a famine.</p>
         * <p>Let's try to describe the philosopher holding chopsticks in Java with a mutual exclusion lock <code>synchronized (chopstick) { ... }</code> automatically performs the following:
         * <ol>
         * <li>first obtains a mutually exclusive lock for <code>chopstick</code> and holds it immediately</li>
         * <li>while holding the lock, execute the statements in <code>{ ... }</code> while the lock is held.</li>
         * <li>automatically release the lock when it's done.</li>
         * </ol>
         * As you can see, acquiring and holding a mutually exclusive lock on <code>chopstick</code> means that the philosopher has chopstick <code>chopstick</code> (and won't give it to anyone else until he puts it down), and releasing the lock on <code>chopstick</code> means that the philosopher puts it down.</p>
         * */
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

    /**
     * <h2><code>main()</code> method</h2>
     * <p>which is the entry point of this program.</p>
     * */
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
