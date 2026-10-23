import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Oyunun ekranı, haritası ve kale kontrolleri.
 * BU DOSYAYI DEĞİŞTİRMEYİN. Sizin göreviniz Savasci, hayvan ve Iksir sınıflarındaki boşlukları doldurmak.
 */
public class OyunPaneli extends JPanel {

    // ---------------- SABİTLER ----------------
    static final int GEN = 1000, YUK = 650;
    static final int DUNYA = 3450;
    static final int YOL_UST = 330, YOL_ALT = 545;
    static final int[] KALE_X = {700, 1450, 2200, 2900};
    static final int ORMAN_KAPI = 3180;
    static final int BITIS = 3330;

    static final String[] SINIF = {"Aslan", "Kurt", "Ayi", "Kartal", "Tavsan", "Kedi", "Panda", "Tilki"};
    static final String[] AD = {"Aslan", "Kurt", "Ayı", "Kartal", "Tavşan", "Kedi", "Panda", "Tilki"};
    static final int[] CAN = {120, 100, 160, 90, 80, 95, 150, 95};
    static final int[] GUC = {14, 12, 10, 16, 12, 13, 11, 15};
    static final int[] SALDIRI = {28, 22, 20, 31, 24, 25, 22, 25};
    static final String[] EK = {", ormanın kralı!", ", sürünün lideri!", ", dağların gücü!", ", göklerin hükümdarı!",
            ", ormanın en hızlısı!", ", gecenin avcısı!", ", bambu ustası!", ", ormanın en kurnazı!"};

    // ---------------- DURUM ----------------
    private int ekran = 0;               // 0 seçim, 1 oyun, 2 kazandı, 3 arena
    private int secilen = 0;
    private String isim = "";
    private boolean[] kaleTamam = new boolean[4];
    private String[] kaleNeden = new String[4];
    private int gecilenKale = 0;         // kaç kaleden geçildi
    private Savasci savasci;

    private double px = 120, py = 440;
    private boolean sagaBakiyor = true;
    private boolean sol, sag, yukari, asagi;
    private int kare = 0;
    private int hasarBekleme = 0;
    private int saldiriAnim = 0;

    private String bildirim = "";
    private int bildirimSure = 0;
    private int kapiUyari = -1;          // kapalı kapının önündeyse kale no

    private ArrayList<int[]> dikenler = new ArrayList<int[]>();
    private ArrayList<int[]> iksirler = new ArrayList<int[]>();   // x, y, alındı mı
    private ArrayList<double[]> golgeler = new ArrayList<double[]>(); // x, y, can
    private ArrayList<Object[]> kagitlar = new ArrayList<Object[]>();

    private JTextField isimKutusu = new JTextField();
    private JButton baslaButonu = new JButton("YOLA ÇIK");
    private JButton devamButonu = new JButton("KALDIĞIN YERDEN DEVAM");
    private JButton arenaButonu = new JButton("ARENAYA GİT");
    private JTextField arenaKutusu = new JTextField();
    private JButton katilButonu = new JButton("ARENAYA KATIL");
    private String arenaMesaji = "";
    private String secimMesaji = "";
    private ArenaIstemcisi istemci;
    private long sonHareket = 0, sonSaldiriZamani = 0;
    private int gonderilenX = -1, gonderilenY = -1;
    private boolean gonderilenYon = true;
    private Preferences kayit = Preferences.userRoot().node("hayvan-savascilar-oyunu");
    private Timer zamanlayici;

    public OyunPaneli() {
        setPreferredSize(new Dimension(GEN, YUK));
        setLayout(null);
        setFocusable(true);
        kaleleriKontrolEt();
        haritayiKur();

        secilen = kayit.getInt("hayvan", 0);
        if (secilen < 0 || secilen >= SINIF.length) {
            secilen = 0;
        }
        isimKutusu.setText(kayit.get("isim", ""));
        isimKutusu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        isimKutusu.setBounds(350, 478, 300, 38);
        baslaButonu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        baslaButonu.setBounds(350, 526, 300, 42);
        devamButonu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        devamButonu.setBounds(350, 576, 300, 32);
        add(isimKutusu);
        add(baslaButonu);
        add(devamButonu);
        arenaButonu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        arenaButonu.setBounds(350, 614, 300, 30);
        add(arenaButonu);
        arenaKutusu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        arenaKutusu.setBounds(250, 545, 270, 38);
        arenaKutusu.setText(kayit.get("arena_adresi", ""));
        katilButonu.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        katilButonu.setBounds(530, 545, 220, 38);
        add(arenaKutusu);
        add(katilButonu);
        arenaKutusu.setVisible(false);
        katilButonu.setVisible(false);
        arenaButonu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dogrudanArenaya();
            }
        });
        ActionListener katil = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                arenayaKatil();
            }
        };
        katilButonu.addActionListener(katil);
        arenaKutusu.addActionListener(katil);
        devamButonunuAyarla();

        baslaButonu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oyunuBaslat(false);
            }
        });
        devamButonu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oyunuBaslat(true);
            }
        });
        isimKutusu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oyunuBaslat(false);
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (ekran == 0) {
                    for (int i = 0; i < SINIF.length; i++) {
                        int kx = 95 + (i % 4) * 210, ky = 128 + (i / 4) * 168;
                        if (e.getX() >= kx && e.getX() <= kx + 180 && e.getY() >= ky && e.getY() <= ky + 156) {
                            secilen = i;
                            devamButonunuAyarla();
                        }
                    }
                }
                requestFocusInWindow();
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                tusDegisti(e.getKeyCode(), true);
                if (e.getKeyCode() == KeyEvent.VK_SPACE && ekran == 1) {
                    saldirmayaCalis();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && ekran == 3) {
                    arenadaSaldir();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && ekran == 3) {
                    istemci.kapat();
                    kazandiEkraninaDon("Arenadan çıktın.");
                }
            }

            public void keyReleased(KeyEvent e) {
                tusDegisti(e.getKeyCode(), false);
            }
        });

        zamanlayici = new Timer(16, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ekran == 1) {
                    guncelle();
                } else if (ekran == 3) {
                    arenaGuncelle();
                }
                kare++;
                repaint();
            }
        });
        zamanlayici.start();
    }

    private void devamButonunuAyarla() {
        arenaButonu.setVisible(ekran == 0 && kayit.getBoolean("kazandi_" + SINIF[secilen], false));
        boolean kayitVar = kayit.getInt("kale", 0) > 0 && !kayit.get("isim", "").isEmpty();
        devamButonu.setVisible(ekran == 0 && kayitVar && kayit.getInt("hayvan", -1) == secilen);
        if (kayitVar) {
            devamButonu.setText("KALDIĞIN YERDEN DEVAM (Kale " + kayit.getInt("kale", 0) + ")");
        }
    }

    // ---------------- ARENA ----------------

    private void kazandiEkraninaDon(String mesaj) {
        ekran = 2;
        arenaMesaji = mesaj;
        isimKutusu.setVisible(false);
        baslaButonu.setVisible(false);
        devamButonu.setVisible(false);
        arenaButonu.setVisible(false);
        arenaKutusu.setVisible(true);
        katilButonu.setVisible(true);
        katilButonu.setEnabled(true);
        sol = sag = yukari = asagi = false;
    }

    // Oyunu daha önce bitirmiş öğrenci, yolu yeniden yürümeden arenaya gidebilir.
    private void dogrudanArenaya() {
        isim = isimKutusu.getText().trim();
        if (isim.isEmpty()) {
            isim = AD[secilen];
        }
        hayvanKaleleriniKontrolEt();
        for (int k = 0; k < 4; k++) {
            if (!kaleTamam[k]) {
                secimMesaji = "Arenaya girmek için bütün kaleler açık olmalı. Eksik: Kale " + (k + 1) + ".";
                repaint();
                return;
            }
        }
        try {
            savasci = (Savasci) hayvanOlustur(isim);
        } catch (Throwable t) {
            secimMesaji = "Savaşçı oluşturulamadı.";
            return;
        }
        kayit.put("isim", isim);
        kayit.putInt("hayvan", secilen);
        kazandiEkraninaDon("");
    }

    private void arenayaKatil() {
        final String adres = arenaKutusu.getText().trim();
        if (adres.isEmpty()) {
            arenaMesaji = "Öğretmeninin söylediği arena adresini yaz (örnek: 192.168.1.20).";
            repaint();
            return;
        }
        if (savasci == null) {
            arenaMesaji = "Savaşçı bulunamadı.";
            return;
        }
        kayit.put("arena_adresi", adres);
        arenaMesaji = "Bağlanılıyor: " + adres + " ...";
        katilButonu.setEnabled(false);
        repaint();
        final ArenaIstemcisi yeni = new ArenaIstemcisi();
        final int maxCan = savasci.getMaxCan();
        final int saldiri = savasci.saldir();
        new Thread(new Runnable() {
            public void run() {
                final boolean basarili = yeni.baglan(adres, isim, secilen, maxCan, saldiri);
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        katilButonu.setEnabled(true);
                        if (basarili) {
                            istemci = yeni;
                            ekran = 3;
                            px = 500;
                            py = 370;
                            arenaKutusu.setVisible(false);
                            katilButonu.setVisible(false);
                            requestFocusInWindow();
                        } else {
                            arenaMesaji = yeni.getHata() + " Adresi ve aynı ağda olduğunuzu kontrol edin.";
                        }
                        repaint();
                    }
                });
            }
        }).start();
    }

    private void arenaGuncelle() {
        if (!istemci.isBagli()) {
            kazandiEkraninaDon(istemci.getHata());
            return;
        }
        int[] k = istemci.yeniKonumuAl();
        if (k != null) {
            px = k[0];
            py = k[1];
        }
        double hiz = 4;
        if (sol) {
            px -= hiz;
            sagaBakiyor = false;
        }
        if (sag) {
            px += hiz;
            sagaBakiyor = true;
        }
        if (yukari) {
            py -= hiz;
        }
        if (asagi) {
            py += hiz;
        }
        px = Math.max(ArenaSunucusu.SOL, Math.min(ArenaSunucusu.SAG, px));
        py = Math.max(ArenaSunucusu.UST, Math.min(ArenaSunucusu.ALT, py));
        long simdi = System.currentTimeMillis();
        if (simdi - sonHareket >= 100 && ((int) px != gonderilenX || (int) py != gonderilenY || sagaBakiyor != gonderilenYon)) {
            istemci.hareket((int) px, (int) py, sagaBakiyor);
            gonderilenX = (int) px;
            gonderilenY = (int) py;
            gonderilenYon = sagaBakiyor;
            sonHareket = simdi;
        }
        if (saldiriAnim > 0) {
            saldiriAnim--;
        }
    }

    private void arenadaSaldir() {
        long simdi = System.currentTimeMillis();
        if (simdi - sonSaldiriZamani < 500) {
            return;
        }
        sonSaldiriZamani = simdi;
        saldiriAnim = 12;
        istemci.saldir();
    }

    private void arenaCiz(Graphics2D g) {
        ArenaSunucusu.arenaZeminiCiz(g, kare);
        java.util.ArrayList<String[]> liste = istemci.getOyuncular();
        String[] ben = null;
        for (String[] o : liste) {
            if (Integer.parseInt(o[0]) == istemci.getBenimId()) {
                ben = o;
            } else {
                ArenaSunucusu.oyuncuCiz(g, o, false, kare);
            }
        }
        String[] kendim = ben == null
                ? new String[]{"0", "" + secilen, "0", "0", "1", "" + savasci.getMaxCan(), "" + savasci.getMaxCan(), "0", "0", "0", "1", isim}
                : ben.clone();
        kendim[2] = String.valueOf((int) px);
        kendim[3] = String.valueOf((int) py);
        kendim[4] = sagaBakiyor ? "1" : "-1";
        kendim[8] = saldiriAnim > 0 ? "1" : "0";
        ArenaSunucusu.oyuncuCiz(g, kendim, true, kare);

        g.setColor(new Color(20, 30, 40, 210));
        g.fillRoundRect(12, 10, 976, 60, 14, 14);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        g.drawString("ARENA", 26, 38);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.drawString(isim + " (" + AD[secilen] + ")", 120, 34);
        int can = Integer.parseInt(kendim[5]), max = Math.max(1, Integer.parseInt(kendim[6]));
        g.setColor(new Color(80, 30, 30));
        g.fillRoundRect(120, 44, 200, 12, 8, 8);
        g.setColor(new Color(220, 70, 70));
        g.fillRoundRect(120, 44, 200 * Math.max(0, can) / max, 12, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.drawString(can + " / " + max + "    Saldırı " + savasci.saldir() + "    Skor " + kendim[7], 330, 55);
        g.setColor(new Color(200, 210, 220));
        g.drawString("Ok tuşları: yürü    BOŞLUK: saldır    ESC: arenadan çık", 620, 34);

        java.util.ArrayList<String[]> sirali = new java.util.ArrayList<String[]>(liste);
        java.util.Collections.sort(sirali, new java.util.Comparator<String[]>() {
            public int compare(String[] a, String[] b) {
                return Integer.parseInt(b[7]) - Integer.parseInt(a[7]);
            }
        });
        java.util.ArrayList<String[]> satirlar = new java.util.ArrayList<String[]>();
        for (String[] o : sirali) {
            satirlar.add(new String[]{o[11], o[7]});
        }
        ArenaSunucusu.skorKutusuCiz(g, satirlar, liste.size());
        ArenaSunucusu.olaylariCiz(g, istemci.getOlaylar());
    }

    // ---------------- KALE KONTROLLERİ ----------------

    private void kaleleriKontrolEt() {
        // Kale 1: Savasci
        try {
            Savasci s = new Savasci("Deneme", 100, 10);
            if (!"Deneme".equals(s.getIsim()) || s.getCan() != 100 || s.getGuc() != 10) {
                kaleNeden[0] = "Savaşçının alanları boş. Savasci.java: GÖREV 1a (yapıcıda alanlara değer verin).";
            } else if (s.getMaxCan() != 100) {
                kaleNeden[0] = "maxCan değeri verilmemiş. Savasci.java: GÖREV 1a (this.maxCan = can;).";
            } else {
                s.hasarAl(30);
                int birinci = s.getCan();
                s.hasarAl(500);
                if (birinci != 70) {
                    kaleNeden[0] = "Savaşçı hasar almıyor. Savasci.java: GÖREV 1b (can = can - miktar;).";
                } else if (s.getCan() != 0) {
                    kaleNeden[0] = "Can 0'ın altına düşüyor. Savasci.java: GÖREV 1b (if ile 0 kontrolü).";
                } else {
                    kaleTamam[0] = true;
                }
            }
        } catch (Throwable t) {
            kaleNeden[0] = "Savasci sınıfı hata veriyor: " + t.getClass().getSimpleName();
        }

        // Kale 3: Iksir (Kale 2 ve 4 seçilen hayvana bağlı, oyun başlarken kontrol edilir)
        try {
            Object o = new Iksir(40);
            if (!(o instanceof Esya)) {
                kaleNeden[2] = "Iksir henüz bir Esya değil. Iksir.java: GÖREV 3a (extends Esya).";
            } else {
                Esya e = (Esya) o;
                if (!"Can İksiri".equals(e.getAd())) {
                    kaleNeden[2] = "İksirin adı \"Can İksiri\" olmalı. Iksir.java: GÖREV 3b (super(\"Can İksiri\");).";
                } else {
                    Savasci t = new Savasci("Deneme", 100, 10);
                    t.hasarAl(50);
                    e.kullan(t);
                    if (t.getCan() != 90) {
                        kaleNeden[2] = "İksir iyileştirmiyor. Iksir.java: GÖREV 3c (s.iyiles(iyilestirme);).";
                    } else {
                        kaleTamam[2] = true;
                    }
                }
            }
        } catch (Throwable t) {
            kaleNeden[2] = "Iksir sınıfı hata veriyor: " + t.getClass().getSimpleName();
        }
    }

    // Seçilen hayvanın nesnesini oluşturur. Sonuç Object türündedir; çünkü öğrenci "extends Savasci"
    // yazana kadar hayvan sınıfı bir Savasci değildir ve kale kontrolü bunu instanceof ile sınar.
    private Object hayvanOlustur(String ad) {
        switch (secilen) {
            case 0: return new Aslan(ad);
            case 1: return new Kurt(ad);
            case 2: return new Ayi(ad);
            case 3: return new Kartal(ad);
            case 4: return new Tavsan(ad);
            case 5: return new Kedi(ad);
            case 6: return new Panda(ad);
            default: return new Tilki(ad);
        }
    }

    private void hayvanKaleleriniKontrolEt() {
        kaleTamam[1] = false;
        kaleTamam[3] = false;
        String dosya = SINIF[secilen] + ".java";
        // Kale 2: extends Savasci ve super(...)
        try {
            Object o = hayvanOlustur("Deneme");
            if (!(o instanceof Savasci)) {
                kaleNeden[1] = AD[secilen] + " henüz bir Savasci değil. " + dosya + ": GÖREV 2a (extends Savasci).";
            } else {
                Savasci s = (Savasci) o;
                if (s.getCan() != CAN[secilen] || s.getGuc() != GUC[secilen] || !"Deneme".equals(s.getIsim())) {
                    kaleNeden[1] = AD[secilen] + " için can " + CAN[secilen] + ", güç " + GUC[secilen]
                            + " olmalı. " + dosya + ": GÖREV 2b (super(isim, " + CAN[secilen] + ", " + GUC[secilen] + ");).";
                } else {
                    kaleTamam[1] = true;
                }
            }
        } catch (Throwable t) {
            kaleNeden[1] = AD[secilen] + " sınıfı hata veriyor: " + t.getClass().getSimpleName();
        }
        // Kale 4: tanit() ve saldir() geçersiz kılma
        try {
            Object o = hayvanOlustur("Deneme");
            if (!(o instanceof Savasci)) {
                kaleNeden[3] = "Önce Kale 2 açılmalı: " + dosya + " GÖREV 2a ve 2b.";
            } else {
                Savasci s = (Savasci) o;
                if (!("Ben Deneme" + EK[secilen]).equals(s.tanit())) {
                    kaleNeden[3] = "tanit() \"Ben ..." + EK[secilen] + "\" döndürmeli. " + dosya + ": GÖREV 4a (super.tanit()).";
                } else if (s.saldir() != SALDIRI[secilen]) {
                    kaleNeden[3] = "saldir() " + SALDIRI[secilen] + " döndürmeli, şu an " + s.saldir() + ". " + dosya + ": GÖREV 4b (super.saldir()).";
                } else {
                    kaleTamam[3] = true;
                }
            }
        } catch (Throwable t) {
            kaleNeden[3] = AD[secilen] + " sınıfı hata veriyor: " + t.getClass().getSimpleName();
        }
    }

    private String kaleDosyasi(int k) {
        if (k == 0) {
            return "Savasci.java";
        }
        if (k == 2) {
            return "Iksir.java";
        }
        return SINIF[secilen] + ".java";
    }

    // ---------------- HARİTA ----------------

    private void haritayiKur() {
        int[][] d = {{980, 380}, {1180, 500}, {1640, 360}, {1790, 480}, {1960, 400},
                     {2380, 370}, {2520, 500}, {2690, 420}};
        for (int[] a : d) {
            dikenler.add(a);
        }
        int[][] i = {{1700, 515}, {2050, 345}, {2460, 450}, {2780, 360}, {3020, 515}, {3060, 360}};
        for (int[] a : i) {
            iksirler.add(new int[]{a[0], a[1], 0});
        }
        golgeler.add(new double[]{3010, 400, 60});
        golgeler.add(new double[]{3080, 480, 60});
        golgeler.add(new double[]{3130, 360, 60});

        kagit(260, 440, "Hoş geldin savaşçı! Ok tuşlarıyla yürü. Yolun sonunda orman var. "
                + "Her kalenin kapısı bir sınıfın tamamlanmasını bekler. Kâğıtların üzerinden geçerek ipuçlarını oku.");
        kagit(470, 360, "Kale 1, Savasci.java dosyasını bekliyor. GÖREV 1a: Yapıcıda parametreleri alanlara ata: "
                + "this.isim = isim; ... maxCan da can ile aynı değeri almalı.");
        kagit(560, 510, "GÖREV 1b: hasarAl metodu canı miktar kadar azaltmalı. Can 0'ın altına inerse 0 yapılmalı.");
        kagit(860, 470, "Kale 2, seçtiğin hayvanın dosyasını bekliyor. GÖREV 2a: class satırına extends Savasci ekle. "
                + "Böylece hayvanın bir Savasci olur ve Savasci'nın alanlarını, metotlarını kalıtımla alır.");
        kagit(1250, 360, "GÖREV 2b: extends ekleyince yapıcının ilk satırına super(isim, can, güç) yazmalısın. "
                + "Yazmazsan Java, Savasci'da olmayan boş bir yapıcı arar ve program derlenmez!");
        kagit(1560, 450, "Dikkat, dikenler canını azaltır! İksirler canını yeniler ama önce Kale 3'ü açmalısın: Iksir.java.");
        kagit(1880, 360, "GÖREV 3a ve 3b: Iksir sınıfı extends Esya ile Esya'dan kalıtım almalı, "
                + "yapıcıda super(\"Can İksiri\") ile Esya'nın yapıcısını çağırmalı.");
        kagit(2110, 500, "GÖREV 3c: Iksir'deki kullan metodu, Esya'daki kullan metodunu geçersiz kılar (override). "
                + "İçine s.iyiles(iyilestirme); yaz.");
        kagit(2330, 470, "Kale 4 ustalık ister! GÖREV 4a: tanit() metodunda \"\" yerine super.tanit() yaz. "
                + "GÖREV 4b: saldir() metodunda 0 yerine super.saldir() ile hesaplanan saldırıyı yaz.");
        kagit(2600, 360, "Ormanın kapısını üç Gölge bekliyor. Kale 4'ten sonra BOŞLUK tuşuyla saldırabilirsin. "
                + "Saldırı gücün, hayvanının saldir() metodundan gelir.");
    }

    private void kagit(int x, int y, String metin) {
        kagitlar.add(new Object[]{x, y, metin});
    }

    // ---------------- OYUN AKIŞI ----------------

    private void oyunuBaslat(boolean devam) {
        isim = isimKutusu.getText().trim();
        if (isim.isEmpty()) {
            isim = AD[secilen];
        }
        hayvanKaleleriniKontrolEt();
        kayit.putInt("hayvan", secilen);
        kayit.put("isim", isim);

        int baslangicKale = 0;
        if (devam && kayit.getInt("hayvan", -1) == secilen) {
            int kayitli = kayit.getInt("kale", 0);
            for (int k = 0; k < kayitli && k < 4 && kaleTamam[k]; k++) {
                baslangicKale = k + 1;
            }
        }
        gecilenKale = baslangicKale;
        px = baslangicKale == 0 ? 120 : KALE_X[baslangicKale - 1] + 90;
        py = 440;
        savasciyiHazirla();
        if (savasci != null) {
            savasci.iyiles(10000);
        }
        isimKutusu.setVisible(false);
        baslaButonu.setVisible(false);
        devamButonu.setVisible(false);
        arenaButonu.setVisible(false);
        ekran = 1;
        bildir(baslangicKale == 0 ? "Yolculuk başlıyor, " + isim + "!" : "Kale " + baslangicKale + "'den devam ediyorsun.");
        requestFocusInWindow();
    }

    private void savasciyiHazirla() {
        savasci = null;
        try {
            if (gecilenKale >= 2 && kaleTamam[1]) {
                savasci = (Savasci) hayvanOlustur(isim);
            } else if (gecilenKale >= 1 && kaleTamam[0]) {
                savasci = new Savasci(isim, 100, 10);
            }
        } catch (Throwable t) {
            savasci = null;
        }
    }

    private void tusDegisti(int kod, boolean basili) {
        if (kod == KeyEvent.VK_LEFT || kod == KeyEvent.VK_A) {
            sol = basili;
        } else if (kod == KeyEvent.VK_RIGHT || kod == KeyEvent.VK_D) {
            sag = basili;
        } else if (kod == KeyEvent.VK_UP || kod == KeyEvent.VK_W) {
            yukari = basili;
        } else if (kod == KeyEvent.VK_DOWN || kod == KeyEvent.VK_S) {
            asagi = basili;
        }
    }

    private void guncelle() {
        double hiz = 4;
        double yeniX = px, yeniY = py;
        if (sol) {
            yeniX -= hiz;
            sagaBakiyor = false;
        }
        if (sag) {
            yeniX += hiz;
            sagaBakiyor = true;
        }
        if (yukari) {
            yeniY -= hiz;
        }
        if (asagi) {
            yeniY += hiz;
        }
        yeniY = Math.max(YOL_UST + 10, Math.min(YOL_ALT - 10, yeniY));
        yeniX = Math.max(30, Math.min(DUNYA - 30, yeniX));

        // Kale kapıları
        kapiUyari = -1;
        for (int k = 0; k < 4; k++) {
            int kapi = KALE_X[k] - 40;
            if (px <= kapi && yeniX > kapi) {
                if (!kaleTamam[k]) {
                    yeniX = kapi;
                    kapiUyari = k;
                } else if (gecilenKale <= k) {
                    kaleyiGec(k);
                }
            }
        }
        // Orman kapısı: Gölgeler yenilmeden geçilemez
        if (px <= ORMAN_KAPI - 30 && yeniX > ORMAN_KAPI - 30 && canliGolgeSayisi() > 0) {
            yeniX = ORMAN_KAPI - 30;
            bildir("Orman kapısı kapalı! Önce " + canliGolgeSayisi() + " Gölge'yi yenmelisin.");
        }
        px = yeniX;
        py = yeniY;
        for (int k = 0; k < 4; k++) {
            int kapi = KALE_X[k] - 40;
            if (!kaleTamam[k] && px >= kapi - 20 && px <= kapi) {
                kapiUyari = k;
            }
        }

        // Dikenler
        if (hasarBekleme > 0) {
            hasarBekleme--;
        }
        for (int[] d : dikenler) {
            if (mesafe(px, py, d[0], d[1]) < 38 && hasarBekleme == 0) {
                hasarVer(10, "Diken battı! -10 can");
            }
        }

        // İksirler
        for (int[] i : iksirler) {
            if (i[2] == 0 && mesafe(px, py, i[0], i[1]) < 32) {
                if (!kaleTamam[2]) {
                    bildir("Bu iksiri kullanamıyorsun: Iksir.java tamamlanmamış (Kale 3).");
                } else if (savasci != null) {
                    try {
                        Object o = new Iksir(40);
                        ((Esya) o).kullan(savasci);
                        i[2] = 1;
                        bildir("Can İksiri içtin! Canın yenilendi.");
                    } catch (Throwable t) {
                        bildir("İksir hata verdi.");
                    }
                }
            }
        }

        // Gölgeler
        for (double[] g : golgeler) {
            if (g[2] <= 0) {
                continue;
            }
            double m = mesafe(px, py, g[0], g[1]);
            if (m < 260 && m > 30) {
                g[0] += (px - g[0]) / m * 1.3;
                g[1] += (py - g[1]) / m * 1.3;
                g[0] = Math.max(KALE_X[3] + 60, g[0]);
            }
            if (m < 44 && hasarBekleme == 0) {
                hasarVer(8, "Gölge sana dokundu! -8 can");
            }
        }

        if (saldiriAnim > 0) {
            saldiriAnim--;
        }
        if (bildirimSure > 0) {
            bildirimSure--;
        }

        // Bayılma
        if (savasci != null && savasci.getCan() <= 0) {
            savasci.iyiles(10000);
            px = gecilenKale == 0 ? 120 : KALE_X[gecilenKale - 1] + 90;
            py = 440;
            bildir("Bayıldın! Son geçtiğin kaleden devam ediyorsun.");
        }

        // Kazanma
        if (px >= BITIS && canliGolgeSayisi() == 0) {
            kayit.putInt("kale", 0);
            kayit.putBoolean("kazandi_" + SINIF[secilen], true);
            kazandiEkraninaDon("");
        }
    }

    private void kaleyiGec(int k) {
        gecilenKale = k + 1;
        kayit.putInt("kale", gecilenKale);
        savasciyiHazirla();
        if (savasci != null) {
            savasci.iyiles(10000);
        }
        if (k == 0) {
            bildir("Kale 1 geçildi! Savaşçın hazır, can barın açıldı.");
        } else if (k == 1) {
            bildir("Kale 2 geçildi! Artık gerçek bir " + AD[secilen] + " oldun: can " + savasci.getMaxCan()
                    + ", güç " + savasci.getGuc() + ".");
        } else if (k == 2) {
            bildir("Kale 3 geçildi! Can İksirleri artık işe yarıyor.");
        } else {
            bildir(savasci.tanit() + "  BOŞLUK tuşuyla saldırabilirsin! Saldırı: " + savasci.saldir());
        }
    }

    private void saldirmayaCalis() {
        if (!kaleTamam[3] || gecilenKale < 4 || savasci == null) {
            bildir("Henüz saldıramıyorsun: Kale 4'ü (tanit ve saldir) geçmelisin.");
            return;
        }
        saldiriAnim = 12;
        double[] hedef = null;
        double enYakin = 95;
        for (double[] g : golgeler) {
            double m = mesafe(px, py, g[0], g[1]);
            if (g[2] > 0 && m < enYakin) {
                enYakin = m;
                hedef = g;
            }
        }
        if (hedef != null) {
            hedef[2] -= savasci.saldir();
            if (hedef[2] <= 0) {
                bildir("Bir Gölge yok oldu! Kalan: " + canliGolgeSayisi());
            }
        }
    }

    private void hasarVer(int miktar, String mesaj) {
        if (savasci == null) {
            return;
        }
        savasci.hasarAl(miktar);
        hasarBekleme = 45;
        bildir(mesaj);
    }

    private int canliGolgeSayisi() {
        int n = 0;
        for (double[] g : golgeler) {
            if (g[2] > 0) {
                n++;
            }
        }
        return n;
    }

    private void bildir(String m) {
        bildirim = m;
        bildirimSure = 200;
    }

    private static double mesafe(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    // ---------------- ÇİZİM ----------------

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (ekran == 0) {
            secimCiz(g);
        } else if (ekran == 1) {
            oyunCiz(g);
        } else if (ekran == 2) {
            kazandiCiz(g);
        } else {
            arenaCiz(g);
        }
    }

    private void secimCiz(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(40, 70, 50), 0, YUK, new Color(20, 35, 28)));
        g.fillRect(0, 0, GEN, YUK);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
        ortala(g, "HAYVAN SAVAŞÇILAR", 70);
        g.setColor(new Color(210, 225, 210));
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        ortala(g, "Savaşçını seç, ona bir isim ver ve ormana ulaşmak için yola çık.", 110);

        for (int i = 0; i < SINIF.length; i++) {
            int kx = 95 + (i % 4) * 210, ky = 128 + (i / 4) * 168;
            g.setColor(i == secilen ? new Color(70, 110, 80) : new Color(45, 70, 55));
            g.fillRoundRect(kx, ky, 180, 156, 20, 20);
            g.setStroke(new BasicStroke(i == secilen ? 4 : 2));
            g.setColor(i == secilen ? new Color(245, 210, 90) : new Color(90, 120, 95));
            g.drawRoundRect(kx, ky, 180, 156, 20, 20);
            g.setStroke(new BasicStroke(1));
            HayvanCizimi.ciz(g, i, kx + 90, ky + 62, 1.15, true, kare);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 19));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(AD[i], kx + 90 - fm.stringWidth(AD[i]) / 2, ky + 122);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            g.setColor(new Color(210, 225, 210));
            String s1 = "Can " + CAN[i] + "  ·  Güç " + GUC[i];
            fm = g.getFontMetrics();
            g.drawString(s1, kx + 90 - fm.stringWidth(s1) / 2, ky + 143);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g.drawString("Savaşçının adı (" + SINIF[secilen] + ".java):", 350, 470);
        if (!secimMesaji.isEmpty()) {
            g.setColor(new Color(255, 180, 170));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            ortala(g, secimMesaji, 455);
        }
    }

    private void oyunCiz(Graphics2D g) {
        int kamera = (int) Math.max(0, Math.min(DUNYA - GEN, px - 400));

        // Gökyüzü ve tepeler (yavaş kayar)
        g.setPaint(new GradientPaint(0, 0, new Color(120, 185, 240), 0, 300, new Color(200, 230, 250)));
        g.fillRect(0, 0, GEN, 300);
        g.setColor(new Color(120, 170, 120));
        for (int i = -1; i < 8; i++) {
            int hx = i * 260 - (kamera / 4) % 260;
            g.fillOval(hx, 200, 360, 200);
        }
        g.translate(-kamera, 0);

        // Zemin ve yol
        g.setColor(new Color(100, 165, 85));
        g.fillRect(kamera, 290, GEN, YUK - 290);
        g.setColor(new Color(200, 175, 125));
        g.fillRect(kamera, YOL_UST, GEN, YOL_ALT - YOL_UST);
        g.setColor(new Color(185, 160, 110));
        for (int x = (kamera / 80) * 80; x < kamera + GEN; x += 80) {
            g.fillOval(x + 20, 390 + (x / 80 % 3) * 40, 14, 8);
        }

        // Arka plan ağaçları
        for (int x = 100; x < ORMAN_KAPI; x += 170) {
            agacCiz(g, x, 300, 1.0);
            agacCiz(g, x + 85, 615, 1.0);
        }
        // Orman
        for (int x = ORMAN_KAPI + 20; x < DUNYA + 100; x += 55) {
            agacCiz(g, x, 300 + (x % 3) * 8, 1.3);
            agacCiz(g, x + 25, 610, 1.3);
        }
        g.setColor(new Color(60, 120, 60, 120));
        g.fillRect(ORMAN_KAPI, YOL_UST, DUNYA - ORMAN_KAPI, YOL_ALT - YOL_UST);

        for (int[] d : dikenler) {
            dikenCiz(g, d[0], d[1]);
        }
        for (int[] i : iksirler) {
            if (i[2] == 0) {
                iksirCiz(g, i[0], i[1]);
            }
        }
        for (Object[] k : kagitlar) {
            kagitCiz(g, (Integer) k[0], (Integer) k[1]);
        }
        for (int k = 0; k < 4; k++) {
            kaleCiz(g, k);
        }
        ormanKapisiCiz(g);
        for (double[] gl : golgeler) {
            if (gl[2] > 0) {
                golgeCiz(g, (int) gl[0], (int) gl[1], gl[2]);
            }
        }

        // Savaşçı
        int ziplama = (sol || sag || yukari || asagi) ? (int) (Math.abs(Math.sin(kare * 0.3)) * 4) : 0;
        if (hasarBekleme > 30 && kare % 6 < 3) {
            // yanıp sönme
        } else {
            HayvanCizimi.ciz(g, secilen, (int) px, (int) py - 20 - ziplama, 1.0, sagaBakiyor, kare);
        }
        if (saldiriAnim > 0) {
            g.setColor(new Color(255, 240, 150, 200));
            g.setStroke(new BasicStroke(6));
            int yon = sagaBakiyor ? 1 : -1;
            g.drawArc((int) px + yon * 20 - 45, (int) py - 65, 90, 90, sagaBakiyor ? -60 : 120, 120);
            g.setStroke(new BasicStroke(1));
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(isim, (int) px - fm.stringWidth(isim) / 2, (int) py - 84);

        g.translate(kamera, 0);

        hudCiz(g);

        // Yakındaki ipucu kâğıdı
        for (Object[] k : kagitlar) {
            if (mesafe(px, py, (Integer) k[0], (Integer) k[1]) < 50) {
                kutuCiz(g, "İPUCU KÂĞIDI", (String) k[2], new Color(245, 232, 195), new Color(90, 65, 30));
            }
        }
        if (kapiUyari >= 0) {
            kutuCiz(g, "KALE " + (kapiUyari + 1) + " KAPISI KAPALI",
                    "Bu kale " + kaleDosyasi(kapiUyari) + " dosyasının tamamlanmasını bekliyor.  " + kaleNeden[kapiUyari]
                            + "  Dosyayı doldur, kaydet ve oyunu yeniden başlat. \"Kaldığın yerden devam\" ile buraya dönebilirsin.",
                    new Color(250, 215, 210), new Color(130, 30, 25));
        }
        if (bildirimSure > 0) {
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            fm = g.getFontMetrics();
            int w = fm.stringWidth(bildirim) + 30;
            g.setColor(new Color(20, 30, 40, 200));
            g.fillRoundRect(GEN / 2 - w / 2, 90, w, 36, 14, 14);
            g.setColor(Color.WHITE);
            g.drawString(bildirim, GEN / 2 - w / 2 + 15, 114);
        }
    }

    private void hudCiz(Graphics2D g) {
        g.setColor(new Color(20, 30, 40, 190));
        g.fillRoundRect(12, 12, 360, 70, 14, 14);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        if (savasci == null) {
            g.setColor(new Color(255, 190, 180));
            g.drawString("Savaşçı bilgisi yok: Savasci.java tamamlanmamış.", 24, 40);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            g.setColor(new Color(220, 220, 220));
            g.drawString("Kale 1 geçilince can barın görünecek.", 24, 62);
        } else {
            g.setColor(Color.WHITE);
            String tur = kaleTamam[1] && gecilenKale >= 2 ? AD[secilen] : "Acemi savaşçı";
            g.drawString(savasci.getIsim() + "  (" + tur + ")", 24, 36);
            int max = Math.max(1, savasci.getMaxCan());
            int dolu = (int) (220.0 * savasci.getCan() / max);
            g.setColor(new Color(80, 30, 30));
            g.fillRoundRect(24, 46, 220, 14, 8, 8);
            g.setColor(new Color(220, 70, 70));
            g.fillRoundRect(24, 46, Math.max(0, dolu), 14, 8, 8);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            g.drawString(savasci.getCan() + " / " + savasci.getMaxCan(), 252, 58);
            String alt = "Güç " + savasci.getGuc();
            if (kaleTamam[3] && gecilenKale >= 4) {
                alt += "   ·   Saldırı " + savasci.saldir();
            }
            g.drawString(alt, 24, 76);
        }
        // Kale ilerlemesi
        g.setColor(new Color(20, 30, 40, 190));
        g.fillRoundRect(GEN - 262, 12, 250, 40, 14, 14);
        for (int k = 0; k < 4; k++) {
            g.setColor(gecilenKale > k ? new Color(110, 220, 130) : (kaleTamam[k] ? new Color(240, 200, 90) : new Color(200, 80, 70)));
            g.fillRoundRect(GEN - 250 + k * 46, 22, 38, 20, 6, 6);
            g.setColor(new Color(20, 30, 40));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g.drawString("K" + (k + 1), GEN - 241 + k * 46, 37);
        }
        g.setColor(new Color(60, 140, 70));
        g.fillOval(GEN - 66, 20, 24, 24);
    }

    private void kutuCiz(Graphics2D g, String baslik, String metin, Color zemin, Color yazi) {
        int x = 60, w = GEN - 120, y = YUK - 150, h = 135;
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(x + 4, y + 4, w, h, 16, 16);
        g.setColor(zemin);
        g.fillRoundRect(x, y, w, h, 16, 16);
        g.setColor(yazi);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        g.drawString(baslik, x + 20, y + 28);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        satirlaraBolCiz(g, metin, x + 20, y + 54, w - 40, 21);
    }

    private void satirlaraBolCiz(Graphics2D g, String metin, int x, int y, int genislik, int satirAraligi) {
        FontMetrics fm = g.getFontMetrics();
        String[] kelimeler = metin.split(" ");
        StringBuilder satir = new StringBuilder();
        for (String k : kelimeler) {
            String deneme = satir.length() == 0 ? k : satir + " " + k;
            if (fm.stringWidth(deneme) > genislik && satir.length() > 0) {
                g.drawString(satir.toString(), x, y);
                y += satirAraligi;
                satir = new StringBuilder(k);
            } else {
                satir = new StringBuilder(deneme);
            }
        }
        if (satir.length() > 0) {
            g.drawString(satir.toString(), x, y);
        }
    }

    private void kazandiCiz(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(30, 90, 50), 0, YUK, new Color(10, 40, 20)));
        g.fillRect(0, 0, GEN, YUK);
        for (int x = -20; x < GEN + 40; x += 70) {
            agacCiz(g, x, 250 + (x % 3) * 10, 1.5);
            agacCiz(g, x + 35, 660, 1.6);
        }
        for (int i = 0; i < 60; i++) {
            int fx = (i * 137 + kare * (1 + i % 3)) % GEN;
            int fy = (i * 89 + kare * 2) % YUK;
            g.setColor(new Color(255, 240, 120, 160));
            g.fillOval(fx, fy, 4, 4);
        }
        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(150, 150, 700, 350, 30, 30);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
        ortala(g, "KAZANDIN!", 210);
        HayvanCizimi.ciz(g, secilen, GEN / 2, 325, 2.0, true, kare);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        ortala(g, isim + " ormana ulaştı.", 435);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));
        String t = savasci != null ? savasci.tanit() : "";
        ortala(g, "\"" + t + "\"", 468);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.drawString("Arena adresi (öğretmeninin söylediği):", 250, 538);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        ortala(g, arenaMesaji, 610);
    }

    private void ortala(Graphics2D g, String s, int y) {
        g.drawString(s, GEN / 2 - g.getFontMetrics().stringWidth(s) / 2, y);
    }

    // ---------------- NESNE ÇİZİMLERİ ----------------

    private void kaleCiz(Graphics2D g, int k) {
        int x = KALE_X[k];
        Color tas = new Color(150, 150, 160), koyu = new Color(110, 110, 122);
        // Kuleler
        g.setColor(tas);
        g.fillRect(x - 70, 170, 50, 140);
        g.fillRect(x + 20, 170, 50, 140);
        for (int i = 0; i < 3; i++) {
            g.fillRect(x - 70 + i * 18, 156, 12, 16);
            g.fillRect(x + 20 + i * 18, 156, 12, 16);
        }
        // Duvar
        g.fillRect(x - 40, 230, 80, YOL_ALT - 230 + 20);
        g.setColor(koyu);
        for (int yy = 240; yy < YOL_ALT; yy += 22) {
            g.drawLine(x - 40, yy, x + 40, yy);
        }
        // Kapı
        int kx = x - 32, ky = 360, kw = 64, kh = 150;
        if (gecilenKale > k || kaleTamam[k]) {
            g.setColor(new Color(60, 45, 35));
            g.fillRoundRect(kx, ky, kw, kh, 30, 30);
            g.setColor(new Color(200, 175, 125));
            g.fillRect(kx + 6, ky + 30, kw - 12, kh - 30);
        } else {
            g.setColor(new Color(70, 50, 35));
            g.fillRoundRect(kx, ky, kw, kh, 30, 30);
            g.setColor(new Color(40, 40, 45));
            g.setStroke(new BasicStroke(3));
            for (int i = 1; i < 5; i++) {
                g.drawLine(kx + i * 13, ky + 4, kx + i * 13, ky + kh);
            }
            for (int j = 1; j < 7; j++) {
                g.drawLine(kx, ky + j * 21, kx + kw, ky + j * 21);
            }
            g.setStroke(new BasicStroke(1));
        }
        // Bayrak ve tabela
        g.setColor(new Color(90, 70, 50));
        g.drawLine(x - 45, 156, x - 45, 110);
        g.setColor(gecilenKale > k ? new Color(90, 200, 110) : new Color(210, 70, 60));
        int dalga = (int) (Math.sin(kare * 0.1 + k) * 4);
        g.fillPolygon(new int[]{x - 45, x - 10, x - 45}, new int[]{110, 120 + dalga, 132}, 3);
        g.setColor(new Color(240, 225, 190));
        g.fillRoundRect(x - 34, 240, 68, 26, 8, 8);
        g.setColor(new Color(70, 50, 30));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        String t = "KALE " + (k + 1);
        g.drawString(t, x - g.getFontMetrics().stringWidth(t) / 2, 258);
    }

    private void ormanKapisiCiz(Graphics2D g) {
        int x = ORMAN_KAPI;
        g.setColor(new Color(110, 75, 40));
        g.fillRect(x - 10, 300, 18, YOL_ALT - 290);
        g.fillRect(x - 10, 290, 18, 30);
        g.setColor(new Color(150, 105, 60));
        g.fillRoundRect(x - 60, 270, 120, 34, 10, 10);
        g.setColor(new Color(250, 235, 200));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g.drawString("ORMAN", x - g.getFontMetrics().stringWidth("ORMAN") / 2, 294);
        if (canliGolgeSayisi() > 0) {
            g.setColor(new Color(90, 40, 120, 110));
            g.fillRect(x - 6, YOL_UST, 14, YOL_ALT - YOL_UST);
        }
    }

    private void agacCiz(Graphics2D g, int x, int y, double o) {
        g.setColor(new Color(110, 75, 45));
        g.fillRect(x - (int) (6 * o), y - (int) (40 * o), (int) (12 * o), (int) (40 * o));
        g.setColor(new Color(40, 120, 55));
        g.fillOval(x - (int) (32 * o), y - (int) (95 * o), (int) (64 * o), (int) (64 * o));
        g.setColor(new Color(55, 145, 70));
        g.fillOval(x - (int) (20 * o), y - (int) (88 * o), (int) (30 * o), (int) (28 * o));
    }

    private void dikenCiz(Graphics2D g, int x, int y) {
        g.setColor(new Color(60, 110, 50));
        g.fillOval(x - 26, y - 22, 52, 36);
        g.setColor(new Color(50, 40, 30));
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * i / 7;
            int x1 = x + (int) (Math.cos(a) * 22), y1 = y - 4 - (int) (Math.sin(a) * 16);
            int x2 = x + (int) (Math.cos(a) * 34), y2 = y - 4 - (int) (Math.sin(a) * 26);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void iksirCiz(Graphics2D g, int x, int y) {
        int z = (int) (Math.sin(kare * 0.1 + x) * 3);
        g.setColor(new Color(230, 230, 240));
        g.fillRect(x - 4, y - 30 + z, 8, 8);
        g.setColor(new Color(210, 40, 60));
        g.fillOval(x - 12, y - 24 + z, 24, 24);
        g.setColor(new Color(255, 150, 160));
        g.fillOval(x - 6, y - 19 + z, 7, 7);
        g.setColor(new Color(120, 80, 50));
        g.fillRect(x - 5, y - 34 + z, 10, 5);
    }

    private void kagitCiz(Graphics2D g, int x, int y) {
        int z = (int) (Math.sin(kare * 0.08 + x) * 2);
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x - 16, y + 2, 32, 8);
        g.setColor(new Color(245, 230, 190));
        g.fillRect(x - 14, y - 34 + z, 28, 34);
        g.setColor(new Color(200, 175, 120));
        g.fillOval(x - 17, y - 38 + z, 34, 9);
        g.fillOval(x - 17, y - 4 + z, 34, 9);
        g.setColor(new Color(120, 90, 50));
        for (int i = 0; i < 3; i++) {
            g.drawLine(x - 9, y - 26 + i * 8 + z, x + 9, y - 26 + i * 8 + z);
        }
        g.setColor(new Color(200, 40, 40));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.drawString("?", x - 4, y - 42 + z);
    }

    private void golgeCiz(Graphics2D g, int x, int y, double can) {
        int z = (int) (Math.sin(kare * 0.12 + x) * 4);
        g.setColor(new Color(60, 30, 80, 220));
        g.fillOval(x - 26, y - 60 + z, 52, 56);
        int[] xs = {x - 26, x - 16, x - 8, x, x + 8, x + 16, x + 26};
        int[] ys = {y - 30 + z, y + z, y - 10 + z, y + 2 + z, y - 10 + z, y + z, y - 30 + z};
        g.fillPolygon(xs, ys, 7);
        g.setColor(new Color(255, 70, 70));
        g.fillOval(x - 13, y - 44 + z, 9, 9);
        g.fillOval(x + 4, y - 44 + z, 9, 9);
        g.setColor(new Color(40, 20, 50));
        g.fillRect(x - 25, y - 76 + z, 50, 7);
        g.setColor(new Color(190, 90, 230));
        g.fillRect(x - 25, y - 76 + z, (int) (50 * Math.max(0, can) / 60.0), 7);
    }

}
