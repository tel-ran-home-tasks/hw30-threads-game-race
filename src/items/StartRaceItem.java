package items;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import game.Racer;
import telran.view.InputOutput;
import telran.view.Item;

public class StartRaceItem implements Item {
    private final InputOutput io;

    public StartRaceItem(InputOutput io) {
        this.io = io;
    }

    @Override
    public String displayedName() {
        return "Start race";
    }

    @Override
    public void perform() {
        Integer nThreads = io.inputInteger("Enter number of threads (2..100) ", 2, 100);
        if (nThreads == null) return;
        Integer distance = io.inputInteger("Enter distance (>=1) ", 1, Integer.MAX_VALUE);
        if (distance == null) return;

        io.output(String.format("Race starts: threads=%d, distance=%d", nThreads, distance));

        AtomicBoolean winnerDeclared = new AtomicBoolean(false);
        AtomicInteger winnerId = new AtomicInteger(-1);
        CountDownLatch done = new CountDownLatch(nThreads);

        for (int i = 1; i <= nThreads; i++) {
            new Thread(new Racer(i, distance, winnerDeclared, winnerId, done), "Racer-" + i).start();
        }

        try {
            done.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int w = winnerId.get();
        io.output("\n" + (w > 0
                ? ("Congratulations to thread #" + w + " (winner)")
                : "No winner (race was interrupted)."));
    }
}
