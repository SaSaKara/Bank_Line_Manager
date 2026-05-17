import java.util.Random;

public class AnaMerkez {
    public static void main(String[] args) {
        System.out.println("--- Banka Simülasyonu Başlıyor ---\n");

        // 1. Gerekli sistemleri başlat
        OncelikliKuyruk bankaSira = new OncelikliKuyruk();
        IstatistikYonetici istatistik = new IstatistikYonetici();
        Random rand = new Random();

        // 3 adet gişe oluşturuyoruz ve bir diziye koyuyoruz
        Gise[] giseler = new Gise[3];
        for (int i = 0; i < 3; i++) {
            giseler[i] = new Gise(i + 1);
        }

        int musteriSayaci = 1;
        int SIMULASYON_SURESI = 60; // Banka 60 dakika (1 saat) boyunca açık kalacak

        // 2. Zaman Döngüsü (Her döngü 1 dakikayı temsil eder)
        for (int dakika = 1; dakika <= SIMULASYON_SURESI; dakika++) {
            System.out.println("\n[Dakika: " + dakika + "]");

            // a) Yeni müşteri gelme simülasyonu (Her dakika %40 ihtimalle bir müşteri gelsin)
            if (rand.nextInt(100) < 40) {
                // Rastgele öncelik belirleme (%60 Standart, %15 VIP, %15 Yaşlı, %10 Engelli)
                int ihtimal = rand.nextInt(100);
                int oncelik = 4; // Varsayılan: Standart

                if (ihtimal < 15) oncelik = 1;           // VIP
                else if (ihtimal < 25) oncelik = 2;      // Engelli
                else if (ihtimal < 40) oncelik = 3;      // Yaşlı

                // İşlem süresini 3 ile 10 dakika arası rastgele belirle
                int islemSuresi = rand.nextInt(8) + 3;

                Musteri yeniMusteri = new Musteri(musteriSayaci++, oncelik, islemSuresi, dakika);
                bankaSira.kuyrugaEkle(yeniMusteri);
                System.out.println(">> Bankadan içeri girdi: M" + yeniMusteri.id + " (" + yeniMusteri.getOncelikTipi() + ")");
            }

            // b) Boş gişelere kuyruktaki müşterileri atama
            for (Gise gise : giseler) {
                if (gise.musaitMi && !bankaSira.bosMu()) {
                    Musteri siradaki = bankaSira.kuyruktanCagir();
                    gise.musteriAl(siradaki);
                    istatistik.musteriKaydet(siradaki, dakika); // Bekleme süresini hesapla ve kaydet
                }
            }

            // c) Gişelerdeki işlemleri ilerlet (Herkesin süresinden 1 dk düşer)
            for (Gise gise : giseler) {
                gise.zamanIlerlet();
            }
        }

        System.out.println("\n--- 60 Dakika Doldu, Banka Kapandı ---");

        // 3. Gün Sonu İstatistiklerini Yazdır
        istatistik.raporuYazdir();
    }
}