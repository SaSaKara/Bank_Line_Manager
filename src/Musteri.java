public class Musteri {
    // 1. Müşterinin Özellikleri (Veriler)
    int id;                 // Müşteri takip numarası (Örn: 1, 2, 3...)
    int oncelikSeviyesi;    // 1: VIP, 2: Engelli, 3: Yaşlı, 4: Standart
    int islemSuresi;        // Gişede işinin kaç dakika süreceği
    int gelisZamani;        // Bankaya adım attığı simülasyon dakikası

    // 2. Veri Yapısı Bağlantısı (Linked List Pointer'ı)
    Musteri next;           // Kuyrukta kendisinden sonra gelen kişiyi gösterecek

    // 3. Yapıcı Metot (Constructor) - Yeni müşteri oluştururken kullanılacak
    public Musteri(int id, int oncelikSeviyesi, int islemSuresi, int gelisZamani) {
        this.id = id;
        this.oncelikSeviyesi = oncelikSeviyesi;
        this.islemSuresi = islemSuresi;
        this.gelisZamani = gelisZamani;
        this.next = null;   // İlk oluşturulduğunda arkasında kimse yoktur
    }

    // Ekrana yazdırırken sayı yerine isim yazsın diye küçük bir yardımcı metot
    public String getOncelikTipi() {
        switch (this.oncelikSeviyesi) {
            case 1: return "VIP";
            case 2: return "Engelli";
            case 3: return "Yaşlı";
            case 4: return "Standart";
            default: return "Bilinmeyen";
        }
    }
}