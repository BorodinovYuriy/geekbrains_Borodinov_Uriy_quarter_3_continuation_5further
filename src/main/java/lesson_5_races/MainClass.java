package lesson_5_races;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MainClass {
    private static final int CARS_COUNT = 4;

    //защелка (дошли-пошли (countDown()-->await()))
    static CountDownLatch startLine_CDL = new CountDownLatch(CARS_COUNT);

    //цикл-барьер (await()-продолжили-await())
    static CyclicBarrier roadStage_CB = new CyclicBarrier(CARS_COUNT);

    //семафор (ограничение прохода по "туннелю") (true - гарантия наличия очереди при споре)
    static Semaphore tunnel_S = new Semaphore(CARS_COUNT/2,true);
    static CountDownLatch finishLine_CD = new CountDownLatch(CARS_COUNT);

    public static void main(String[] args) {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];

        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }
        //"собираем" автомобили//!
        while (startLine_CDL.getCount() > 0)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        while (finishLine_CD.getCount() > 0) //"финишируем"
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }
}