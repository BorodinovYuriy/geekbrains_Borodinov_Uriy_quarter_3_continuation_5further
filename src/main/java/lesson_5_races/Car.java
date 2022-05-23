package lesson_5_races;
public class Car implements Runnable {
    private static int winnerCount = 1;
    static String winner;
    private static volatile int startCount = 1;
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            MainClass.startLine_CDL.countDown();
            MainClass.startLine_CDL.await();
            startCount--;
            if(startCount == 0) System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        winnerCount--;
        if (winnerCount==0) winner = (this.name+" WIN");
        MainClass.finishLine_CDL.countDown();

    }
}