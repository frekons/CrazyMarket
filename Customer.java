import java.util.Random;

public class Customer {

    static int _nextCustomerId = 0;

    public int customerId;
    // datafield tiplerini degistirebilirsiniz
    long arrivalTime; // musteri gelis zamani- milisaniye biciminde

    // bekleme zamanini hesaplamada kullanabilirsiniz
    long removalTime; // milisaniye biciminde

    // işleminin kaç saniye süreceği
    long processTime;

    public boolean isStartedProcess = false;

    public Customer(long currentTime) {
        this.customerId = _nextCustomerId++;

        this.arrivalTime = currentTime;

        this.processTime = _random.nextInt(2001) + 1000;

        this.removalTime = -1;
    }
   
    /**
     * Müşterinin işlemini başlatır
     */
    public void startProggress(long currentTime)
    {
        this.processTime += currentTime; // böylelikle işlem bitmiş mi diye kontrol edeceğim

        isStartedProcess = true;
    }

    /**
     * Müşterinin gördüğü işlemin bittiği zamanı kaydeder
     */
    public void stopProggress(long currentTime)
    {
        this.removalTime = currentTime;
    }

    /**
     * Müşterinin şu anda işlem görüp-görmediğini söyler
     * @return
     */
    public boolean isInProggress(long currentTime)
    {
        return currentTime < this.processTime;
    }

    /**
     * saniye cinsinden müşterinin ne kadar süredir beklediğini söyler
     * @return
     */
    public float waitingTime(long currentTime)
    {
        return ((this.removalTime != -1 ? this.removalTime : currentTime) - this.arrivalTime) / 1000.0f;
    }

    private static Random _random = new Random();
}