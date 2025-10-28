package items;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import newgame.RacerNew;
import telran.view.InputOutput;
import telran.view.Item;

public class StartRaceItemNew implements Item {
    private final InputOutput io;

    public StartRaceItemNew(InputOutput io) {
        this.io = io;
    }

    @Override
    public String displayedName() {
        return "Start race";
    }

    @Override
    public void perform() {
        Integer nThreads = io.inputInteger("Enter number of threads (2..100)", 2, 100);
        if (nThreads == null) return;
        Integer distance = io.inputInteger("Enter distance (>=1)", 1, Integer.MAX_VALUE);
        if (distance == null) return;

        io.output(String.format("Race starts: threads=%d, distance=%d", nThreads, distance));

        AtomicBoolean winnerDeclared = new AtomicBoolean(false);
        AtomicInteger winnerId = new AtomicInteger(-1);
        CountDownLatch done = new CountDownLatch(nThreads);

        RacerNew.RESULTS.clear();

        for (int i = 1; i <= nThreads; i++) {
            new Thread(new RacerNew(i, distance, winnerDeclared, winnerId, done), "Racer-" + i).start();
        }

        try {
            done.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int w = winnerId.get();
        io.output("\n \n" + (w > 0
                ? ("Congratulations to thread #" + w + " (winner)!")
                : "No winner (race was interrupted)."));

        var sorted = RacerNew.RESULTS.stream()
                .sorted(Comparator
                        .comparingLong(RacerNew.RaceResult::timeMs)
                        .thenComparingInt(RacerNew.RaceResult::order))
                .toList();

        io.output("\nPlace | Thread | Time(ms)");
        io.output("------------------------------");

        int place = 1;
        for (var r : sorted) {
            io.output(String.format("  %2d   |   %3d   |   %5d", place++, r.threadId(), r.timeMs()));
            System.out.println("");
        }
    }
}