import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class BankaSimulasyonFX extends Application {

    // Arka Plan Sistemleri
    private OncelikliKuyruk bankaSira;
    private Gise[] giseler;
    private IstatistikYonetici istatistik;
    private Random rand;
    private int dakika = 0;
    private int musteriSayaci = 1;
    private Timeline timeline;
    private final int SIMULASYON_SURESI = 60;
    private boolean simulasayonBasladi = false;

    // Simülasyon hızı tekrar başlatmada korunabilsin diye
    private double simulasayonHizi = 1.0;

    // Arayüz Bileşenleri
    private HBox kuyrukListesiPanel;
    private VBox[] giseKutulari;
    private Label[] giseDurumEtiketleri;
    private Label[] giseMusteriEtiketleri;
    private TextArea logEkrani;
    private Label zamanEtiketi;
    private Button baslatButonu;

    @Override
    public void start(Stage primaryStage) {
        initBackendSystems();

        // ANA EKRAN DÜZENİ (Modern Arka Plan)
        BorderPane anaRoot = new BorderPane();
        anaRoot.setStyle("-fx-background-color: #F0F4F8;"); // Yumuşak mavi-gri arka plan

        // --- ÜST KISIM: Başlık ve Gişeler ---
        VBox ustPanel = new VBox(20);
        ustPanel.setPadding(new Insets(25, 25, 10, 25));
        ustPanel.setAlignment(Pos.CENTER);

        // KURUMSAL BAŞLIK
        Label baslikLabel = new Label("BANKA SIRA YÖNETİM SİSTEMİ");
        baslikLabel.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1A365D; -fx-letter-spacing: 2px;");

        HBox giseHBox = new HBox(30);
        giseHBox.setAlignment(Pos.CENTER);
        giseKutulari = new VBox[3];
        giseDurumEtiketleri = new Label[3];
        giseMusteriEtiketleri = new Label[3];

        for (int i = 0; i < 3; i++) {
            giseKutulari[i] = new VBox(10);
            giseKutulari[i].setAlignment(Pos.CENTER);
            giseKutulari[i].setPrefSize(220, 140);

            // GİŞELER İÇİN DAHA PROFESYONEL VE SADE TASARIM
            giseKutulari[i].setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");

            Label title = new Label("GİŞE " + (i + 1));
            title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            title.setTextFill(Color.web("#4A5568"));

            giseDurumEtiketleri[i] = new Label("MÜSAİT");
            giseDurumEtiketleri[i].setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            giseDurumEtiketleri[i].setTextFill(Color.web("#38A169")); // Canlı Yeşil

            giseMusteriEtiketleri[i] = new Label("Bekleniyor...");
            giseMusteriEtiketleri[i].setFont(Font.font("Segoe UI", 14));
            giseMusteriEtiketleri[i].setTextFill(Color.web("#A0AEC0"));

            giseKutulari[i].getChildren().addAll(title, giseDurumEtiketleri[i], giseMusteriEtiketleri[i]);
            giseHBox.getChildren().add(giseKutulari[i]);
        }
        ustPanel.getChildren().addAll(baslikLabel, giseHBox);
        anaRoot.setTop(ustPanel);

        // --- ORTA KISIM: Kuyruk Ekranı ---
        VBox merkezPanel = new VBox(15);
        merkezPanel.setPadding(new Insets(20, 25, 20, 25));

        Label kuyrukTitle = new Label("Bekleyenler Kuyruğu (Öncelikli)");
        kuyrukTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        kuyrukTitle.setTextFill(Color.web("#2D3748"));

        ScrollPane kuyrukScroll = new ScrollPane();
        kuyrukListesiPanel = new HBox(12);
        kuyrukListesiPanel.setAlignment(Pos.CENTER_LEFT);
        kuyrukListesiPanel.setPadding(new Insets(10));
        kuyrukListesiPanel.setStyle("-fx-background-color: white;");

        kuyrukScroll.setContent(kuyrukListesiPanel);
        kuyrukScroll.setFitToHeight(true);
        kuyrukScroll.setFitToWidth(false);
        kuyrukScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        kuyrukScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        kuyrukScroll.setPrefHeight(120);
        // ScrollPane için modern çerçeve
        kuyrukScroll.setStyle("-fx-background-color: transparent; -fx-background: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        merkezPanel.getChildren().addAll(kuyrukTitle, kuyrukScroll);
        anaRoot.setCenter(merkezPanel);

        // --- ALT KISIM: Loglar ve Buton ---
        HBox altPanel = new HBox(30);
        altPanel.setAlignment(Pos.CENTER);
        altPanel.setPadding(new Insets(10, 25, 25, 25));

        VBox logVBox = new VBox(5);
        Label logTitle = new Label("Sistem Logları");
        logTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        logEkrani = new TextArea();
        logEkrani.setEditable(false);
        logEkrani.setPrefHeight(150);
        logEkrani.setPrefWidth(550);
        // Hacker/Terminal tarzı şık log ekranı
        logEkrani.setStyle("-fx-control-inner-background: #1A202C; -fx-text-fill: #48BB78; -fx-font-family: 'Consolas'; -fx-font-size: 13px; -fx-background-radius: 8;");
        logVBox.getChildren().addAll(logTitle, logEkrani);

        VBox kontrolVBox = new VBox(15);
        kontrolVBox.setAlignment(Pos.CENTER);
        kontrolVBox.setPrefWidth(220);

        zamanEtiketi = new Label("Dakika: 0");
        zamanEtiketi.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        zamanEtiketi.setTextFill(Color.web("#2B6CB0"));

        baslatButonu = new Button("Simülasyonu Başlat");
        baslatButonu.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        baslatButonu.setPrefWidth(200);
        baslatButonu.setPrefHeight(40);
        // Modern gradient buton
        baslatButonu.setStyle("-fx-background-color: linear-gradient(to right, #3182CE, #2B6CB0); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        baslatButonu.setOnAction(e -> handleStartPause());

        Button musteriEkleButonu = new Button("Yeni Müşteri Ekle");
        musteriEkleButonu.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        musteriEkleButonu.setPrefWidth(200);
        musteriEkleButonu.setPrefHeight(35);
        musteriEkleButonu.setStyle("-fx-background-color: linear-gradient(to right, #48BB78, #38A169); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        musteriEkleButonu.setOnAction(e -> openAddCustomerDialog());

        Label hizBaslik = new Label("Simülasyon Hızı: 1.0x");
        hizBaslik.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        hizBaslik.setTextFill(Color.web("#4A5568"));

        Slider hizKaydirici = new Slider(0.2, 3.0, 1.0);
        hizKaydirici.setPrefWidth(180);
        hizKaydirici.setShowTickMarks(true);
        hizKaydirici.setShowTickLabels(true);
        hizKaydirici.setMajorTickUnit(1.0);
        hizKaydirici.setBlockIncrement(0.1);
        hizKaydirici.valueProperty().addListener((obs, oldVal, newVal) -> {
            double rate = newVal.doubleValue();
            simulasayonHizi = rate; // Hız değerini tekrar başlatmada korumak için
            if (timeline != null) {
                timeline.setRate(rate);
            }
            hizBaslik.setText(String.format("Simülasyon Hızı: %.1fx", rate));
        });

        kontrolVBox.getChildren().addAll(zamanEtiketi, baslatButonu, musteriEkleButonu, hizBaslik, hizKaydirici);

        altPanel.getChildren().addAll(logVBox, kontrolVBox);
        anaRoot.setBottom(altPanel);

        Scene scene = new Scene(anaRoot, 950, 750);
        primaryStage.setTitle("Grup 10 - Banka Kuyruk Yönetimi");
        primaryStage.setScene(scene);
        primaryStage.show();

        // İlk Timeline nesnesi oluşturuluyor
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> handleTimeStep()));
        timeline.setCycleCount(SIMULASYON_SURESI);
    }

    private void initBackendSystems() {
        bankaSira = new OncelikliKuyruk();
        istatistik = new IstatistikYonetici();
        rand = new Random();
        giseler = new Gise[3];
        for (int i = 0; i < 3; i++) {
            giseler[i] = new Gise(i + 1);
        }
    }

    private void startSimulation() {
        // Tekrar başlatmada eski Timeline kalmasın diye durduruyoruz
        if (timeline != null) {
            timeline.stop();
        }

        // Her yeni simülasyonda Timeline yeniden oluşturuluyor
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> handleTimeStep()));
        timeline.setCycleCount(SIMULASYON_SURESI);
        timeline.setRate(simulasayonHizi);

        initBackendSystems();
        dakika = 0;
        musteriSayaci = 1;
        simulasayonBasladi = true;
        logEkrani.setText("");
        kuyrukListesiPanel.getChildren().clear();
        baslatButonu.setText("Duraklat");
        baslatButonu.setStyle("-fx-background-color: linear-gradient(to right, #E53E3E, #C53030); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        logEkle("Simülasyon başlatılıyor...");

        for(int i=0; i<3; i++) {
            giseDurumEtiketleri[i].setText("MÜSAİT");
            giseDurumEtiketleri[i].setTextFill(Color.web("#38A169"));
            giseMusteriEtiketleri[i].setText("Bekleniyor...");
            giseKutulari[i].setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        }
        timeline.play();
        kuyrukArayuzunuCiz();
    }

    private void handleTimeStep() {
        dakika++;
        zamanEtiketi.setText("Dakika: " + dakika);
        logEkle("[Dakika: " + dakika + "]");

        if (rand.nextInt(100) < 40) {
            int ihtimal = rand.nextInt(100);
            int oncelik = 4;
            if (ihtimal < 15) oncelik = 1;
            else if (ihtimal < 25) oncelik = 2;
            else if (ihtimal < 40) oncelik = 3;

            int islemSuresi = rand.nextInt(8) + 3;
            Musteri yeni = new Musteri(musteriSayaci++, oncelik, islemSuresi, dakika);
            bankaSira.kuyrugaEkle(yeni);
            logEkle(">> İçeri girdi: M" + yeni.id + " (" + yeni.getOncelikTipi() + ")");
        }

        for (int i = 0; i < 3; i++) {
            if (giseler[i].musaitMi && !bankaSira.bosMu()) {
                Musteri siradaki = bankaSira.kuyruktanCagir();
                giseler[i].musteriAl(siradaki);
                istatistik.musteriKaydet(siradaki, dakika);

                final int idx = i;
                final Musteri m = siradaki;
                giseDurumEtiketleri[idx].setText("İŞLEMDE");
                giseDurumEtiketleri[idx].setTextFill(Color.web("#E53E3E")); // Kırmızı
                giseMusteriEtiketleri[idx].setText("Müşteri: M" + m.id + " (" + m.getOncelikTipi() + ")\nKalan: " + giseler[idx].kalanIslemSuresi + " dk");

                // Gişe çalışırken hafif kırmızımsı bir gölge ve stil
                giseKutulari[idx].setStyle("-fx-background-color: #FFF5F5; -fx-border-color: #FEB2B2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(229,62,62,0.2), 15, 0, 0, 5);");
            }
        }

        for (int i = 0; i < 3; i++) {
            giseler[i].zamanIlerlet();

            // Gişede müşteri varsa kalan süreyi güvenli şekilde güncelle
            if (!giseler[i].musaitMi && giseler[i].suankiMusteri != null) {
                giseMusteriEtiketleri[i].setText("Müşteri: M" + giseler[i].suankiMusteri.id + " (" + giseler[i].suankiMusteri.getOncelikTipi() + ")\nKalan: " + giseler[i].kalanIslemSuresi + " dk");
            } else {
                giseDurumEtiketleri[i].setText("MÜSAİT");
                giseDurumEtiketleri[i].setTextFill(Color.web("#38A169"));
                giseMusteriEtiketleri[i].setText("Bekleniyor...");
                giseKutulari[i].setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
            }
        }

        kuyrukArayuzunuCiz();

        if (dakika == SIMULASYON_SURESI) {
            simulasayonBasladi = false;

            // Simülasyon bittiğinde kuyrukta kalan müşteri sayısı rapora ekleniyor
            int kalanMusteri = bankaSira.kalanMusteriSayisi();

            logEkle("--- Simülasyon Bitti! Rapor oluşturuluyor ---");

            // Rapor artık sadece konsola değil, GUI içindeki log ekranına da yazılıyor
            logEkle(istatistik.raporMetniOlustur(kalanMusteri));

            baslatButonu.setText("Tekrar Başlat");
            baslatButonu.setStyle("-fx-background-color: linear-gradient(to right, #3182CE, #2B6CB0); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        }
    }

    private void kuyrukArayuzunuCiz() {
        kuyrukListesiPanel.getChildren().clear();
        Musteri current = bankaSira.getHead();

        // Kuyruk boşsa ekranda boş görünmesin diye bilgi mesajı gösteriliyor
        if (current == null) {
            Label bosLabel = new Label("Kuyrukta bekleyen müşteri yok.");
            bosLabel.setFont(Font.font("Segoe UI", 14));
            bosLabel.setTextFill(Color.GRAY);
            kuyrukListesiPanel.getChildren().add(bosLabel);
            return;
        }

        boolean isFirst = true;

        while (current != null) {
            if (!isFirst) {
                Label okLabel = new Label("←");
                okLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
                okLabel.setTextFill(Color.web("#718096"));
                okLabel.setAlignment(Pos.CENTER);
                kuyrukListesiPanel.getChildren().add(okLabel);
            }
            isFirst = false;

            HBox musteriKutusu = new HBox(8);
            musteriKutusu.setAlignment(Pos.CENTER);
            musteriKutusu.setPadding(new Insets(8, 12, 8, 12));
            musteriKutusu.setPrefHeight(60);

            String style = "";
            switch (current.oncelikSeviyesi) {
                case 1: style = "-fx-background-color: linear-gradient(to right, #F6E05E, #D69E2E); -fx-text-fill: white;"; break; // Öncelikli (Altın)
                case 2: style = "-fx-background-color: linear-gradient(to right, #63B3ED, #3182CE); -fx-text-fill: white;"; break; // Engelli (Mavi)
                case 3: style = "-fx-background-color: linear-gradient(to right, #68D391, #38A169); -fx-text-fill: white;"; break; // Yaşlı (Yeşil)
                case 4: style = "-fx-background-color: #EDF2F7; -fx-text-fill: #2D3748;"; break; // Standart (Gri)
            }

            musteriKutusu.setStyle(style + " -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

            Label idLabel = new Label("M" + current.id);
            idLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            if(current.oncelikSeviyesi != 4) idLabel.setTextFill(Color.WHITE);

            Label typeLabel = new Label(current.getOncelikTipi());
            typeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
            if(current.oncelikSeviyesi != 4) typeLabel.setTextFill(Color.WHITE);

            Label sureLabel = new Label("(" + current.islemSuresi + " dk)");
            sureLabel.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 11));
            if(current.oncelikSeviyesi != 4) sureLabel.setTextFill(Color.WHITE);
            else sureLabel.setTextFill(Color.web("#718096"));

            musteriKutusu.getChildren().addAll(idLabel, typeLabel, sureLabel);
            kuyrukListesiPanel.getChildren().add(musteriKutusu);

            current = current.next;
        }
    }

    private void handleStartPause() {
        if (timeline.getStatus() == Animation.Status.STOPPED || dakika == SIMULASYON_SURESI) {
            startSimulation();
        } else if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            baslatButonu.setText("Devam Et");
            baslatButonu.setStyle("-fx-background-color: linear-gradient(to right, #ED8936, #DD6B20); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            logEkle("Simülasyon duraklatıldı.");
        } else {
            timeline.play();
            baslatButonu.setText("Duraklat");
            baslatButonu.setStyle("-fx-background-color: linear-gradient(to right, #E53E3E, #C53030); -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
            logEkle("Simülasyon devam ediyor...");
        }
    }

    private void openAddCustomerDialog() {
        if (!simulasayonBasladi) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen önce simülasyonu başlatın!");
            alert.showAndWait();
            return;
        }

        Dialog<Musteri> dialog = new Dialog<>();
        dialog.setTitle("Yeni Müşteri Ekle");
        dialog.setHeaderText("Müşterinin özelliklerini belirleyin:");

        ButtonType ekleButtonType = new ButtonType("Kuyruğa Ekle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ekleButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        ComboBox<String> oncelikCombo = new ComboBox<>();
        oncelikCombo.getItems().addAll("Öncelikli (VIP)", "Engelli", "Yaşlı", "Standart");
        oncelikCombo.setValue("Standart");

        Spinner<Integer> sureSpinner = new Spinner<>(1, 20, 5);

        grid.add(new Label("Öncelik Tipi:"), 0, 0);
        grid.add(oncelikCombo, 1, 0);
        grid.add(new Label("İşlem Süresi (Dk):"), 0, 1);
        grid.add(sureSpinner, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ekleButtonType) {
                int oncelik = 4;
                switch (oncelikCombo.getValue()) {
                    case "Öncelikli (VIP)": oncelik = 1; break;
                    case "Engelli": oncelik = 2; break;
                    case "Yaşlı": oncelik = 3; break;
                    case "Standart": oncelik = 4; break;
                }
                int islemSuresi = sureSpinner.getValue();
                return new Musteri(musteriSayaci++, oncelik, islemSuresi, dakika);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(yeniMusteri -> {
            bankaSira.kuyrugaEkle(yeniMusteri);
            logEkle(">> [MANUEL EKLEME] İçeri girdi: M" + yeniMusteri.id + " (" + yeniMusteri.getOncelikTipi() + ", İşlem Süresi: " + yeniMusteri.islemSuresi + " dk)");
            kuyrukArayuzunuCiz();
        });
    }

    private void logEkle(String mesaj) {
        logEkrani.appendText(mesaj + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}