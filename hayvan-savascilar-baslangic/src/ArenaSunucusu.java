import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ARENA SUNUCUSU — öğretmen bilgisayarında çalıştırılır. (Hazır, değiştirmeyin.)
 *
 * NetBeans: bu dosyaya sağ tıklayın > Run File.   Eclipse: sağ tıklayın > Run As > Java Application.
 * Açılan pencerede bilgisayarın IP adresi yazar. Öğrenciler oyunu bitirince bu adresi yazarak arenaya katılır.
 * Pencereyi projeksiyona yansıtırsanız bütün sınıf arenayı izleyebilir.
 *
 * Ağ yükünü düşük tutmak için:
 *  - isim, hayvan ve en yüksek can yalnızca katılırken bir kez gönderilir (OYUNCU mesajı),
 *  - saniyede 10 kez yalnızca konum, can ve skor gönderilir (DURUM mesajı),
 *  - her öğrencinin kendi gönderme kuyruğu vardır; yavaş bir bağlantı diğerlerini bekletmez.
 */
public class ArenaSunucusu extends JPanel {

    // Arena sınırları (oyun ekranıyla aynı koordinatlar)
    public static final int SOL = 50, SAG = 950, UST = 130, ALT = 610;
    static final int SALDIRI_MENZILI = 90;
    static final long SALDIRI_BEKLEME = 500;
    static final long KORUMA_SURESI = 2000;
    static final int EN_FAZLA_OYUNCU = 80;
    static final int DURUM_ARALIGI_MS = 100;

    static class Oyuncu {
        int id, tur, x, y, yon = 1, can, maxCan, saldiri, skor;
        String isim;
        long sonSaldiri, sonVurulma, korumaBitis;
        final ArrayBlockingQueue<String> kuyruk = new ArrayBlockingQueue<String>(64);

        // Mesajı gönderme kuyruğuna koyar; kuyruk doluysa (yavaş bağlantı) mesaj atlanır.
        void gonder(String mesaj) {
            kuyruk.offer(mesaj);
        }

        String bilgiMesaji() {
            return "OYUNCU|" + id + "|" + tur + "|" + maxCan + "|" + isim;
        }
    }

    private final ArrayList<Oyuncu> oyuncular = new ArrayList<Oyuncu>();
    private final LinkedList<String> olaylar = new LinkedList<String>();
    private final Random rastgele = new Random();
    private int sonrakiId = 1;
    private int kare = 0;
    private String adresler = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ArenaSunucusu sunucu = new ArenaSunucusu();
                JFrame pencere = new JFrame("Hayvan Savaşçılar - Arena Sunucusu");
                pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                pencere.add(sunucu);
                pencere.pack();
                pencere.setLocationRelativeTo(null);
                pencere.setVisible(true);
                sunucu.baslat();
            }
        });
    }

    public ArenaSunucusu() {
        setPreferredSize(new Dimension(1000, 650));
        adresler = adresleriBul();
    }

    private void baslat() {
        final ServerSocket ss;
        try {
            ss = new ServerSocket(ArenaIstemcisi.PORT, 100);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Sunucu başlatılamadı. Port " + ArenaIstemcisi.PORT
                    + " kullanımda olabilir; sunucu zaten açık mı?\n" + e.getMessage());
            return;
        }
        arkaPlanda(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        final Socket s = ss.accept();
                        arkaPlanda(new Runnable() {
                            public void run() {
                                istemciyiDinle(s);
                            }
                        });
                    } catch (Exception e) {
                        // devam
                    }
                }
            }
        });
        // Durum gönderimi ayrı bir iş parçacığında: ekran ya da yavaş bir öğrenci onu bekletmez.
        arkaPlanda(new Runnable() {
            public void run() {
                while (true) {
                    durumGonder();
                    try {
                        Thread.sleep(DURUM_ARALIGI_MS);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        // Projeksiyon ekranı saniyede yaklaşık 15 kez çizilir.
        new Timer(66, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                kare++;
                repaint();
            }
        }).start();
    }

    private static void arkaPlanda(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    private void istemciyiDinle(final Socket s) {
        Oyuncu o = null;
        try {
            s.setTcpNoDelay(true);
            BufferedReader okuyucu = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            final PrintWriter yazici = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
            String satir;
            while ((satir = okuyucu.readLine()) != null) {
                if (satir.length() > 200) {
                    continue;
                }
                String[] p = satir.split("\\|", -1);
                try {
                    if (p[0].equals("KATIL") && o == null && p.length >= 5) {
                        final Oyuncu yeni = new Oyuncu();
                        yeni.isim = ArenaIstemcisi.temizle(p[1]);
                        yeni.tur = sinirla(Integer.parseInt(p[2]), 0, 7);
                        yeni.maxCan = sinirla(Integer.parseInt(p[3]), 1, 300);
                        yeni.saldiri = sinirla(Integer.parseInt(p[4]), 1, 60);
                        yeni.can = yeni.maxCan;
                        ArrayList<Oyuncu> digerleri;
                        synchronized (oyuncular) {
                            if (oyuncular.size() >= EN_FAZLA_OYUNCU) {
                                yazici.println("OLAY|Arena dolu (" + EN_FAZLA_OYUNCU + " savaşçı). Biraz sonra yeniden dene.");
                                break;
                            }
                            yeni.id = sonrakiId++;
                            yerlestir(yeni);
                            digerleri = new ArrayList<Oyuncu>(oyuncular);
                            oyuncular.add(yeni);
                        }
                        o = yeni;
                        // Bu öğrencinin gönderme iş parçacığı: kuyruktaki mesajları sırayla yazar.
                        arkaPlanda(new Runnable() {
                            public void run() {
                                try {
                                    while (!s.isClosed()) {
                                        String m = yeni.kuyruk.poll(1, TimeUnit.SECONDS);
                                        if (m != null) {
                                            yazici.println(m);
                                        }
                                    }
                                } catch (Exception e) {
                                    // bağlantı kapandı
                                }
                            }
                        });
                        yeni.gonder("SEN|" + yeni.id + "|" + yeni.x + "|" + yeni.y);
                        for (Oyuncu d : digerleri) {
                            yeni.gonder(d.bilgiMesaji());
                        }
                        herkese(yeni.bilgiMesaji());
                        olay(yeni.isim + " arenaya katıldı!");
                    } else if (p[0].equals("HAREKET") && o != null && p.length >= 4) {
                        synchronized (oyuncular) {
                            o.x = sinirla(Integer.parseInt(p[1]), SOL, SAG);
                            o.y = sinirla(Integer.parseInt(p[2]), UST, ALT);
                            o.yon = Integer.parseInt(p[3]) < 0 ? -1 : 1;
                        }
                    } else if (p[0].equals("SALDIR") && o != null) {
                        saldiriYap(o);
                    }
                } catch (NumberFormatException e) {
                    // bozuk mesaj, yok say
                }
            }
        } catch (Exception e) {
            // bağlantı koptu
        }
        if (o != null) {
            synchronized (oyuncular) {
                oyuncular.remove(o);
            }
            herkese("CIKTI|" + o.id);
            olay(o.isim + " arenadan ayrıldı.");
        }
        try {
            s.close();
        } catch (Exception e) {
            // önemsiz
        }
    }

    private void saldiriYap(Oyuncu a) {
        long simdi = System.currentTimeMillis();
        Oyuncu yenilen = null;
        synchronized (oyuncular) {
            if (simdi - a.sonSaldiri < SALDIRI_BEKLEME) {
                return;
            }
            a.sonSaldiri = simdi;
            Oyuncu hedef = null;
            double enYakin = SALDIRI_MENZILI;
            for (Oyuncu b : oyuncular) {
                if (b == a || simdi < b.korumaBitis) {
                    continue;
                }
                double m = Math.hypot(a.x - b.x, a.y - b.y);
                if (m < enYakin) {
                    enYakin = m;
                    hedef = b;
                }
            }
            if (hedef == null) {
                return;
            }
            hedef.can -= a.saldiri;
            hedef.sonVurulma = simdi;
            if (hedef.can <= 0) {
                a.skor++;
                hedef.can = hedef.maxCan;
                yerlestir(hedef);
                hedef.gonder("KONUM|" + hedef.x + "|" + hedef.y);
                yenilen = hedef;
            }
        }
        if (yenilen != null) {
            olay(a.isim + ", " + yenilen.isim + " adlı savaşçıyı yendi!");
        }
    }

    private void yerlestir(Oyuncu o) {
        o.x = SOL + 40 + rastgele.nextInt(SAG - SOL - 80);
        o.y = UST + 30 + rastgele.nextInt(ALT - UST - 60);
        o.korumaBitis = System.currentTimeMillis() + KORUMA_SURESI;
    }

    // DURUM|id,x,y,yon,can,skor,bayrak;...   bayrak: 1 saldırıyor, 2 vuruldu, 4 koruma kalkanı
    private void durumGonder() {
        long simdi = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("DURUM|");
        ArrayList<Oyuncu> kopya;
        synchronized (oyuncular) {
            kopya = new ArrayList<Oyuncu>(oyuncular);
            for (int i = 0; i < kopya.size(); i++) {
                Oyuncu o = kopya.get(i);
                int bayrak = (simdi - o.sonSaldiri < 200 ? 1 : 0) | (simdi - o.sonVurulma < 200 ? 2 : 0)
                        | (simdi < o.korumaBitis ? 4 : 0);
                if (i > 0) {
                    sb.append(';');
                }
                sb.append(o.id).append(',').append(o.x).append(',').append(o.y).append(',').append(o.yon)
                        .append(',').append(o.can).append(',').append(o.skor).append(',').append(bayrak);
            }
        }
        String mesaj = sb.toString();
        for (Oyuncu o : kopya) {
            o.gonder(mesaj);
        }
    }

    private void herkese(String mesaj) {
        for (Oyuncu o : kopyaAl()) {
            o.gonder(mesaj);
        }
    }

    // Yeni bir olayı (katılma, yenme) listeye ekler ve herkese iletir.
    private void olay(String metin) {
        synchronized (olaylar) {
            olaylar.addFirst(metin);
            while (olaylar.size() > 6) {
                olaylar.removeLast();
            }
        }
        herkese("OLAY|" + metin);
    }

    private ArrayList<Oyuncu> kopyaAl() {
        synchronized (oyuncular) {
            return new ArrayList<Oyuncu>(oyuncular);
        }
    }

    private static int sinirla(int d, int min, int max) {
        return Math.max(min, Math.min(max, d));
    }

    private static String adresleriBul() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> arayuzler = NetworkInterface.getNetworkInterfaces();
            while (arayuzler.hasMoreElements()) {
                NetworkInterface ni = arayuzler.nextElement();
                if (!ni.isUp() || ni.isLoopback()) {
                    continue;
                }
                Enumeration<InetAddress> ipler = ni.getInetAddresses();
                while (ipler.hasMoreElements()) {
                    InetAddress ip = ipler.nextElement();
                    if (ip instanceof Inet4Address) {
                        if (sb.length() > 0) {
                            sb.append("   ");
                        }
                        sb.append(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            // adres bulunamadı
        }
        return sb.length() == 0 ? "bulunamadı (localhost ile deneyin)" : sb.toString();
    }

    // ---------------- ÇİZİM (projeksiyon görünümü) ----------------

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        arenaZeminiCiz(g, kare);

        g.setColor(new Color(20, 30, 40, 210));
        g.fillRoundRect(12, 10, 976, 60, 14, 14);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        g.drawString("ARENA", 26, 38);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        g.drawString("Adres: " + adresler, 120, 38);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        g.setColor(new Color(200, 210, 220));
        g.drawString("Öğrenciler KAZANDIN ekranında bu adresi yazıp ARENAYA KATIL düğmesine basar.   Port: "
                + ArenaIstemcisi.PORT, 26, 60);

        ArrayList<Oyuncu> kopya = kopyaAl();
        long simdi = System.currentTimeMillis();
        for (Oyuncu o : kopya) {
            String[] alan = {String.valueOf(o.id), String.valueOf(o.tur), String.valueOf(o.x), String.valueOf(o.y),
                String.valueOf(o.yon), String.valueOf(o.can), String.valueOf(o.maxCan), String.valueOf(o.skor),
                simdi - o.sonSaldiri < 200 ? "1" : "0", simdi - o.sonVurulma < 200 ? "1" : "0",
                simdi < o.korumaBitis ? "1" : "0", o.isim};
            oyuncuCiz(g, alan, false, kare);
        }
        skorTablosuCiz(g, kopya);
        ArrayList<String> ol;
        synchronized (olaylar) {
            ol = new ArrayList<String>(olaylar);
        }
        olaylariCiz(g, ol);
        if (kopya.isEmpty()) {
            g.setColor(new Color(255, 255, 255, 220));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            String t = "Arena boş. Oyunu bitiren öğrencileri bekliyor...";
            g.drawString(t, 500 - g.getFontMetrics().stringWidth(t) / 2, 360);
        }
    }

    // Oyun ve sunucu aynı arena çizimini kullanır.
    static void arenaZeminiCiz(Graphics2D g, int kare) {
        g.setColor(new Color(60, 120, 60));
        g.fillRect(0, 0, 1000, 650);
        for (int x = 0; x < 1000; x += 60) {
            g.setColor(new Color(40, 100, 50));
            g.fillOval(x - 10, 70 + (x % 3) * 6, 70, 60);
            g.fillOval(x + 10, 610 + (x % 2) * 8, 70, 60);
        }
        g.setColor(new Color(150, 145, 140));
        g.fillRoundRect(SOL - 40, UST - 40, SAG - SOL + 80, ALT - UST + 80, 60, 60);
        g.setColor(new Color(215, 190, 140));
        g.fillRoundRect(SOL - 20, UST - 20, SAG - SOL + 40, ALT - UST + 40, 50, 50);
        g.setColor(new Color(200, 175, 125));
        for (int i = 0; i < 40; i++) {
            g.fillOval(SOL + (i * 137) % (SAG - SOL), UST + (i * 89) % (ALT - UST), 14, 8);
        }
        g.setColor(new Color(190, 165, 115));
        g.setStroke(new BasicStroke(3));
        g.drawOval(400, 280, 200, 170);
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(130, 125, 120));
        int[][] kuleler = {{SOL - 40, UST - 40}, {SAG + 4, UST - 40}, {SOL - 40, ALT + 4}, {SAG + 4, ALT + 4}};
        for (int[] k : kuleler) {
            g.fillOval(k[0], k[1], 36, 36);
        }
        g.setColor(new Color(210, 60, 60));
        for (int[] k : kuleler) {
            int dalga = (int) (Math.sin(kare * 0.1 + k[0]) * 3);
            g.drawLine(k[0] + 18, k[1] + 18, k[0] + 18, k[1] - 14);
            g.fillPolygon(new int[]{k[0] + 18, k[0] + 38, k[0] + 18}, new int[]{k[1] - 14, k[1] - 8 + dalga, k[1] - 2}, 3);
        }
    }

    // alan: id, tur, x, y, yon, can, maxCan, skor, saldiriyor, vuruldu, korumali, isim
    static void oyuncuCiz(Graphics2D g, String[] alan, boolean ben, int kare) {
        int tur = Integer.parseInt(alan[1]);
        int x = Integer.parseInt(alan[2]);
        int y = Integer.parseInt(alan[3]);
        boolean saga = Integer.parseInt(alan[4]) > 0;
        int can = Integer.parseInt(alan[5]);
        int max = Math.max(1, Integer.parseInt(alan[6]));
        boolean saldiriyor = alan[8].equals("1");
        boolean vuruldu = alan[9].equals("1");
        boolean korumali = alan[10].equals("1");
        if (ben) {
            g.setColor(new Color(255, 230, 90, 120));
            g.fillOval(x - 34, y + 2, 68, 18);
        }
        if (korumali) {
            g.setColor(new Color(150, 200, 255, 90));
            g.fillOval(x - 38, y - 64, 76, 90);
        }
        if (!(vuruldu && kare % 2 == 0)) {
            HayvanCizimi.ciz(g, tur, x, y - 20, 0.9, saga, kare);
        }
        if (saldiriyor) {
            g.setColor(new Color(255, 240, 150, 210));
            g.setStroke(new BasicStroke(6));
            g.drawArc(x + (saga ? 1 : -1) * 18 - 45, y - 62, 90, 90, saga ? -60 : 120, 120);
            g.setStroke(new BasicStroke(1));
        }
        g.setColor(new Color(40, 20, 20, 200));
        g.fillRect(x - 24, y - 78, 48, 6);
        g.setColor(ben ? new Color(110, 220, 120) : new Color(230, 70, 70));
        g.fillRect(x - 24, y - 78, 48 * Math.max(0, can) / max, 6);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        String ad = alan[11];
        int w = g.getFontMetrics().stringWidth(ad);
        g.setColor(new Color(0, 0, 0, 120));
        g.drawString(ad, x - w / 2 + 1, y - 83);
        g.setColor(ben ? new Color(255, 235, 120) : Color.WHITE);
        g.drawString(ad, x - w / 2, y - 84);
    }

    private void skorTablosuCiz(Graphics2D g, ArrayList<Oyuncu> kopya) {
        ArrayList<Oyuncu> sirali = new ArrayList<Oyuncu>(kopya);
        Collections.sort(sirali, new Comparator<Oyuncu>() {
            public int compare(Oyuncu a, Oyuncu b) {
                return b.skor - a.skor;
            }
        });
        ArrayList<String[]> satirlar = new ArrayList<String[]>();
        for (Oyuncu o : sirali) {
            satirlar.add(new String[]{o.isim, String.valueOf(o.skor)});
        }
        skorKutusuCiz(g, satirlar, kopya.size());
    }

    static void skorKutusuCiz(Graphics2D g, ArrayList<String[]> satirlar, int toplam) {
        int n = Math.min(8, satirlar.size());
        int h = 34 + n * 20;
        g.setColor(new Color(20, 30, 40, 200));
        g.fillRoundRect(800, 82, 188, h, 12, 12);
        g.setColor(new Color(245, 210, 90));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        g.drawString("SKOR  (" + toplam + " savaşçı)", 812, 102);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        for (int i = 0; i < n; i++) {
            g.setColor(Color.WHITE);
            g.drawString((i + 1) + ". " + satirlar.get(i)[0], 812, 124 + i * 20);
            g.drawString(satirlar.get(i)[1], 960, 124 + i * 20);
        }
    }

    static void olaylariCiz(Graphics2D g, ArrayList<String> olaylar) {
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        int y = 630;
        for (int i = 0; i < Math.min(4, olaylar.size()); i++) {
            String t = olaylar.get(i);
            int w = g.getFontMetrics().stringWidth(t) + 16;
            g.setColor(new Color(20, 30, 40, 170 - i * 30));
            g.fillRoundRect(14, y - 15, w, 20, 8, 8);
            g.setColor(new Color(255, 255, 255, 255 - i * 45));
            g.drawString(t, 22, y);
            y -= 24;
        }
    }
}
