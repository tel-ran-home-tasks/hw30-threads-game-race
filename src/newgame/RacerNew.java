package newgame;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import game.ThreadsRaceAppl;

public class RacerNew implements Runnable {
    public static final CopyOnWriteArrayList<RaceResult> RESULTS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger ORDER = new AtomicInteger(0);

    private final int id;
    private final int distance;
    private final AtomicBoolean winnerDeclared;
    private final AtomicInteger winnerId;
    private final CountDownLatch done;

    public RacerNew(int id, int distance,AtomicBoolean winnerDeclared,AtomicInteger winnerId,CountDownLatch done) {
        this.id = id;
        this.distance = distance;
        this.winnerDeclared = winnerDeclared;
        this.winnerId = winnerId;
        this.done = done;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            for (int step = 1; step <= distance; step++) {
                if (winnerDeclared.get()) break;
                System.out.print(id);
                int sleep = ThreadLocalRandom.current()
                        .nextInt(ThreadsRaceAppl.MIN_SLEEP_MS,
                                 ThreadsRaceAppl.MAX_SLEEP_MS + 1);
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (!winnerDeclared.get() && winnerDeclared.compareAndSet(false, true)) {
                winnerId.set(id);
            }
        } finally {
            long end = System.nanoTime();
            long timeMs = (end - start) / 1_000_000;
            int order = ORDER.incrementAndGet();
            RESULTS.add(new RaceResult(id, timeMs, order));
            done.countDown();
        }
    }

    public record RaceResult(int threadId, long timeMs, int order) {}
}
