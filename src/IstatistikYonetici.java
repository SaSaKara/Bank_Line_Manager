public class IstatistikYonetici {
    // Genel Toplamlar
    private int toplamMusteri = 0;
    private int toplamBeklemeSuresi = 0;

    // Müşteri Tiplerine Göre Sayılar ve Bekleme Süreleri
    private int vipSayisi = 0, vipBekleme = 0;
    private int engelliSayisi = 0, engelliBekleme = 0;
    private int yasliSayisi = 0, yasliBekleme = 0;
    private int standartSayisi = 0, standartBekleme = 0;

    // Gişeye müşteri alındığında bu metot çağrılacak ve veriler kaydedilecek
    public void musteriKaydet(Musteri m, int islemeAlinmaZamani) {
        // Bekleme süresi = Gişeye çağrıldığı an - Bankaya girdiği an
        int beklemeSuresi = islemeAlinmaZamani - m.gelisZamani;

        toplamMusteri++;
        toplamBeklemeSuresi += beklemeSuresi;

        // Hangi müşteri tipiyse onun istatistiğine ekle
        switch (m.oncelikSeviyesi) {
            case 1:
                vipSayisi++;
                vipBekleme += beklemeSuresi;
                break;
            case 2:
                engelliSayisi++;
                engelliBekleme += beklemeSuresi;
                break;
            case 3:
                yasliSayisi++;
                yasliBekleme += beklemeSuresi;
                break;
            case 4:
                standartSayisi++;
                standartBekleme += beklemeSuresi;
                break;
        }
    }

    // Simülasyon bittiğinde ekrana şık bir rapor yazdırır
    public void raporuYazdir() {
        System.out.println("\n==================================================");
        System.out.println("            GÜN SONU İSTATİSTİK RAPORU            ");
        System.out.println("==================================================");
        System.out.println("Toplam Hizmet Verilen Müşteri: " + toplamMusteri);

        if (toplamMusteri > 0) {
            System.out.println("Genel Ortalama Bekleme Süresi: " + (toplamBeklemeSuresi / toplamMusteri) + " dk");
        }

        System.out.println("\n--- Detaylı Analiz ---");

        if (vipSayisi > 0) System.out.println("VIP Müşteri: " + vipSayisi + " kişi | Ort. Bekleme: " + (vipBekleme / vipSayisi) + " dk");
        if (engelliSayisi > 0) System.out.println("Engelli Müşteri: " + engelliSayisi + " kişi | Ort. Bekleme: " + (engelliBekleme / engelliSayisi) + " dk");
        if (yasliSayisi > 0) System.out.println("Yaşlı Müşteri: " + yasliSayisi + " kişi | Ort. Bekleme: " + (yasliBekleme / yasliSayisi) + " dk");
        if (standartSayisi > 0) System.out.println("Standart Müşteri: " + standartSayisi + " kişi | Ort. Bekleme: " + (standartBekleme / standartSayisi) + " dk");

        System.out.println("==================================================\n");
    }

    //uygulama içi rapor gösterme
    public String raporMetniOlustur(int kuyruktaKalanMusteri) {
        StringBuilder rapor = new StringBuilder();

        rapor.append("\n===== GÜN SONU İSTATİSTİK RAPORU =====\n");
        rapor.append("Toplam Hizmet Verilen Müşteri: ").append(toplamMusteri).append("\n");
        rapor.append("Kuyrukta Kalan Müşteri: ").append(kuyruktaKalanMusteri).append("\n");

        if (toplamMusteri > 0) {
            double genelOrtalama = (double) toplamBeklemeSuresi / toplamMusteri;
            rapor.append("Genel Ortalama Bekleme Süresi: ")
                    .append(String.format("%.2f", genelOrtalama))
                    .append(" dk\n");
        }

        rapor.append("\n--- Detaylı Analiz ---\n");

        if (vipSayisi > 0) {
            rapor.append("Öncelikli Müşteri: ").append(vipSayisi)
                    .append(" kişi | Ort. Bekleme: ")
                    .append(String.format("%.2f", (double) vipBekleme / vipSayisi))
                    .append(" dk\n");
        }

        if (engelliSayisi > 0) {
            rapor.append("Engelli Müşteri: ").append(engelliSayisi)
                    .append(" kişi | Ort. Bekleme: ")
                    .append(String.format("%.2f", (double) engelliBekleme / engelliSayisi))
                    .append(" dk\n");
        }

        if (yasliSayisi > 0) {
            rapor.append("Yaşlı Müşteri: ").append(yasliSayisi)
                    .append(" kişi | Ort. Bekleme: ")
                    .append(String.format("%.2f", (double) yasliBekleme / yasliSayisi))
                    .append(" dk\n");
        }

        if (standartSayisi > 0) {
            rapor.append("Standart Müşteri: ").append(standartSayisi)
                    .append(" kişi | Ort. Bekleme: ")
                    .append(String.format("%.2f", (double) standartBekleme / standartSayisi))
                    .append(" dk\n");
        }


        return rapor.toString();
    }
}