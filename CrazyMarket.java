import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class CrazyMarket implements MyQueue<Customer> {

    final String SESLI_HARFLER = "aeıioöuü";

    /**
     * default tekerleme
     */
    String tekerleme = "O piti piti karamela sepeti " + "\nTerazi lastik jimnastik "
            + "\nBiz size geldik bitlendik Hamama gittik temizlendik.";

    private class Node {
        Node next;
        Customer data;

        public Node(Customer data) {
            this.data = data;

            this.next = null;
        }
    }

    protected Node _first = null, _last = null;

    protected int _size = 0;

    protected int _totalCustomersGotProcessed = 0, _maxCustomerCount = 0;

    protected long _totalTimePassed = 0; // milisaniye cinsinden

    /**
     * numberOfCustumer ile verilen sayida musteri hizmet gorene kadar calismaya
     * devam eder
     */
    public CrazyMarket(int numberOfCustomer) {

        this(numberOfCustomer, ""); // constructor call işlemi ilk statement olması gerekiyor, bundan dolayı döngü
                                    // 2. constructorumuzda bulunuyor

    }

    /**
     * numberOfCustumer ile verilen sayida musteri hizmet gorene kadar calismaya
     * devam eder, calisirken verilen tekerlemeyi kullanir
     */
    public CrazyMarket(int numberOfCustomer, String tekerleme) {

        _maxCustomerCount = numberOfCustomer;

        if (tekerleme != "")
            this.tekerleme = tekerleme;

        while (this.shouldRun()) {

            this.mainLoop();
        }

        System.out.println("Toplam işlem gören müşteri sayısı: " + this._totalCustomersGotProcessed);

        for (var customer : this) {
            System.out.println("Customer Id: " + customer.customerId + ", Bekleme Süresi: "
                    + customer.waitingTime(_totalTimePassed) + " saniye");
        }
    }

    private Customer _currentCustomer = null;

    private long lastCustomerTime = 0;

    void mainLoop() {

        if (_totalTimePassed > lastCustomerTime/* && _totalCustomersAddedToList < _maxCustomerCount */) { // eğer [0, 2] saniye geçtiyse

            var newCustomer = new Customer(_totalTimePassed);

            System.out.println("Sıraya yeni müşteri geldi, id: " + newCustomer.customerId);

            enqueue(newCustomer);

            lastCustomerTime = _totalTimePassed + _random.nextInt(2001); // her [0, 2] saniye aralığında yeni bir müşteri
        }

        if (_currentCustomer == null) { // 1-3 saniye arası yeni müşteri almamak için
            _currentCustomer = chooseCustomer(); // bu fonksiyon, müşteriyi seçip listeden siliyor

            if (_currentCustomer != null) {
                System.out.println("İşleme yeni müşteri seçildi, id: " + _currentCustomer.customerId);

                _currentCustomer.startProggress(_totalTimePassed); // bu müşterinin işlemini başlat
            }

        }

        if (_currentCustomer != null && _currentCustomer.isStartedProcess && !_currentCustomer.isInProggress(_totalTimePassed)) // müşterinin işlemi tamamlanmış, zamanı kaydedelim ve totalde işlenen müşteri sayısını arttıralım ve yeni müşteri için yer açalım
        {
            System.out.println("Sıradaki müşterinin işlemi tamamlandı, id: " + _currentCustomer.customerId);

            _currentCustomer.stopProggress(_totalTimePassed); // aslında listeden çıkaracağımız için bunun önemi yok ama fazladan bilgi

            _currentCustomer = null;

            _totalCustomersGotProcessed++;

            // System.out.println("Size: " + _size + ", first: " + _first + ", last: " +
            // _last + (_first != null ? ", first->next: " + _first.next : "")); //
            // debugging için
        }

        this.timePassed(1); // her bir loop call = 1ms
    }

    void timePassed(long howMany) {
        _totalTimePassed += howMany;
    }

    public boolean shouldRun() { // CrazyMarket günlük müşteri limitini doldurdu mu?
        return _totalCustomersGotProcessed < _maxCustomerCount;
    }

    /**
     * kuyrugun basindaki musteriyi yada tekerleme ile buldugu musteriyi return eder
     */
    public Customer chooseCustomer() {

        if (isEmpty())
            return null;

        var customer = _first.data; // eğer aşağıdaki if'e girmezse direkt olarak ilk elemanı return edecek

        if (_totalTimePassed <= customer.arrivalTime + 10 * 1000) { // eğer bekleme süresi 10 saniyeden
                                                                    // küçükse,
                                                                    // customeri tekerleme ile seç

            customer = dequeuWithCounting(tekerleme);
        } else {
            customer = dequeuNext();
        }

        return customer;

    }

    @Override
    public int size() {
        return _size;
    }

    @Override
    public boolean isEmpty() {

        return _size <= 0 || _first == null || _last == null;
    }

    /**
     * listeye yeni eleman ekler
     */
    @Override
    public boolean enqueue(Customer item) {

        Node newLast = new Node(item);

        if (_last == null) { // ilk eklenen üye
            _first = newLast;

            _last = _first;

            _first.next = null;
        } else {
            this._last.next = newLast;

            this._last = newLast;
        }

        _size++;

        return true;
    }

    /**
     * ilk elemanı listeden çıkartır
     */
    @Override
    public Customer dequeuNext() {

        if (isEmpty()) {
            return null;
        }

        var oldFirst = _first;

        _first = oldFirst.next;

        --_size;

        return oldFirst.data;
    }

    /**
     * tekerleme ile eleman seçer ve çıkartır
     */
    @Override
    public Customer dequeuWithCounting(String tekerleme) {

        if (isEmpty())
            return null;

        var length = tekerleme.length();

        if (length <= 0)
            return null;

        StringBuilder sb = new StringBuilder(tekerleme);

        for (int i = sb.length() - 1; i >= 0 && sb.length() > 0; --i) // tekerlemede bulunan sessiz harfleri siliyorum,
                                                                      // böylelikle sb.length() = sesli harf sayısı
            if (SESLI_HARFLER.indexOf(Character.toLowerCase(sb.charAt(i))) == -1)
                sb.deleteCharAt(i);

        int index = sb.length();

        Node before = null;

        var element = _first;

        for (int i = 1; i < index; ++i) { // 1 den başlıyoruz çünkü elementi _first'ten başlattık

            if (element.next == null) { // eğer en son elemandaysak başa dön

                element = _first;

                before = null;
            } else {
                before = element;

                element = element.next;
            }
        }

        if (element == null)
            return null;

        // seçtiğimiz müşteriyi listeden siliyoruz bu bölümde

        if (before != null) { // eğer tekerleme ile seçtiğimiz müşteri, ilk müşteri değilse

            before.next = element.next;

        } else // ilk müşteriyse
        {
            _first = element.next;

            if (_last == element) // ilk eleman ve son eleman aynı elemansa
                _last = element.next;
        }

        return element.data;
    }

    @Override
    public Iterator<Customer> iterator() {
        return new CrazyMarketIterator();
    }

    private static Random _random = new Random();

    private class CrazyMarketIterator implements Iterator<Customer> {
        private Node itr = _first;

        @Override
        public boolean hasNext() {
            return itr != null;
        }

        @Override
        public Customer next() {
            Customer data = itr.data;

            itr = itr.next;

            return data;
        }
    }

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.print("Sıranın günlük müşteri limitini giriniz: ");

        int num = scan.nextInt();

        scan.close();

        var crazyMarket = new CrazyMarket(num);

    }
}