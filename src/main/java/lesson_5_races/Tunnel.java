package lesson_5_races;

public class Tunnel extends Stage {
    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }
    @Override
    public void go(Car c) {
        try {
            try {

                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                MainClass.tunnel_S.acquire();//!


                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап(прошел туннель): " + description);
                MainClass.tunnel_S.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}