public class OncelikliKuyruk {
    private Musteri head; // Kuyruğun en başındaki (gişeye ilk gidecek) müşteri

    // Yapıcı metot: Başlangıçta bankada kimse yok, kuyruk boş.
    public OncelikliKuyruk() {
        this.head = null;
    }

    // 1. KUYRUĞA EKLEME (ENQUEUE) - İşin sırrı bu metotta!
    public void kuyrugaEkle(Musteri yeniMusteri) {
        // Durum 1: Eğer kuyruk tamamen boşsa VEYA
        // Yeni gelen müşterinin önceliği, sıranın en başındakinden BİLE daha yüksekse
        // (Not: Sayı küçüldükçe öncelik artar. Örn: VIP(1) > Yaşlı(3))
        if (head == null || head.oncelikSeviyesi > yeniMusteri.oncelikSeviyesi) {
            yeniMusteri.next = head; // Yeni müşteri, eski başı arkasına alır
            head = yeniMusteri;      // Kuyruğun yeni başı artık bu müşteri olur
            return;
        }

        // Durum 2: Araya veya Sona Ekleme
        Musteri current = head;

        // Kuyrukta ilerle: Bizden DAHA ÖNCELİKLİ veya AYNI ÖNCELİKTE olanları geç.
        // Eşitlik (<=) durumu "İlk Gelen İlk Hizmet Alır" (FIFO) kuralını korur.
        // Yani iki VIP varsa, ilk gelen VIP önde kalır.
        while (current.next != null && current.next.oncelikSeviyesi <= yeniMusteri.oncelikSeviyesi) {
            current = current.next;
        }

        // Doğru yeri bulduk! Araya kaynak yapıyoruz:
        yeniMusteri.next = current.next; // Yeni müşterinin arkasına, current'ın arkasındakini bağla
        current.next = yeniMusteri;      // Current'ın arkasına da yeni müşteriyi bağla
    }

    // 2. KUYRUKTAN ÇAĞIRMA (DEQUEUE) - Sırası geleni gişeye al
    public Musteri kuyruktanCagir() {
        if (head == null) {
            return null; // Sırada kimse yok
        }
        Musteri islemGorecek = head; // En baştakini al
        head = head.next;            // İkinci sıradaki kişiyi yeni "baş" yap
        return islemGorecek;         // Müşteriyi gişeye gönder
    }

    // Kuyruk boş mu diye kontrol et
    public boolean bosMu() {
        return head == null;
    }

    // TEST İÇİN: O an kuyrukta kimlerin olduğunu ekrana yazdırır
    public void kuyruguYazdir() {
        Musteri current = head;
        System.out.print("Mevcut Kuyruk: ");
        if (current == null) {
            System.out.println("Boş");
            return;
        }
        while (current != null) {
            System.out.print("[M" + current.id + " - " + current.getOncelikTipi() + "] -> ");
            current = current.next;
        }
        System.out.println("null");
    }
    public Musteri getHead() {
        return this.head;
    }
}