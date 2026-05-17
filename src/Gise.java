public class Gise {
    int giseNo;                 // Gişenin numarası (1, 2, 3 vb.)
    boolean musaitMi;           // Gişe boş mu?
    Musteri suankiMusteri;      // O an gişede işlem gören müşteri
    int kalanIslemSuresi;       // Müşterinin işinin bitmesine kalan dakika

    // Yapıcı Metot
    public Gise(int giseNo) {
        this.giseNo = giseNo;
        this.musaitMi = true;      // Başlangıçta gişe boştur
        this.suankiMusteri = null;
        this.kalanIslemSuresi = 0;
    }

    // Gişeye yeni müşteri alma metodu
    public void musteriAl(Musteri m) {
        this.suankiMusteri = m;
        this.kalanIslemSuresi = m.islemSuresi; // Müşterinin işi kaç dakikaysa sayacı ona kur
        this.musaitMi = false;                 // Gişe artık meşgul
        System.out.println("Gişe " + giseNo + ", Müşteri " + m.id + " (" + m.getOncelikTipi() + ") ile işleme başladı. (İşlem süresi: " + m.islemSuresi + " dk)");
    }

    // Simülasyonda her 1 dakika geçtiğinde bu metot çağrılacak
    public void zamanIlerlet() {
        if (!musaitMi) { // Eğer gişe doluysa
            kalanIslemSuresi--; // Kalan süreyi 1 dakika azalt

            if (kalanIslemSuresi <= 0) { // Süre bittiyse müşteri gider
                System.out.println("Gişe " + giseNo + " işlemeyi bitirdi. Müşteri " + suankiMusteri.id + " ayrıldı.");
                this.musaitMi = true; // Gişe tekrar boşa çıkar
                this.suankiMusteri = null;
            }
        }
    }
}