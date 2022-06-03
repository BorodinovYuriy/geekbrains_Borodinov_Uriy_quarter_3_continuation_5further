package lesson_5_races;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import static lesson_5_races.Car.winner;
public class MainClass {
    public static final int CARS_COUNT = 4;
    static CountDownLatch startLine_CDL = new CountDownLatch(CARS_COUNT);
    static CountDownLatch finishLine_CDL = new CountDownLatch(CARS_COUNT);
    static Semaphore tunnel_S = new Semaphore(CARS_COUNT/2,true);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }
        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        finishLine_CDL.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        System.out.println(winner);

    }
}