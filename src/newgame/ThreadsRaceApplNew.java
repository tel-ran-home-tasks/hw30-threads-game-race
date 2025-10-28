package newgame;

import items.StartRaceItemNew;
import telran.view.ConsoleInputOutput;
import telran.view.ExitItem;
import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;

public class ThreadsRaceApplNew {
	public static final int MIN_SLEEP_MS = 2;
    public static final int MAX_SLEEP_MS = 5;

    public static void main(String[] args) {
        InputOutput io = new ConsoleInputOutput();
        Item[] items = {
            new StartRaceItemNew(io),
            new ExitItem()
        };
        new Menu(items, io).runMenu();
    }
}
