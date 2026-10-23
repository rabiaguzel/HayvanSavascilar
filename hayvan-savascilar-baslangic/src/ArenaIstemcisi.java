import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Arenaya bağlanan istemci: sunucuya hareket ve saldırı gönderir, diğer oyuncuların durumunu alır.
 * (Hazır, değiştirmeyin.)
 *
 * Mesajlar düz metin satırlarıdır, alanlar | ile ayrılır:
 *   istemci -> sunucu:  KATIL|isim|tur|maxCan|saldiri    HAREKET|x|y|yon    SALDIR
 *   sunucu -> istemci:  SEN|id|x|y    KONUM|x|y    OYUNCU|id|tur|maxCan|isim    CIKTI|id
 *                       DURUM|id,x,y,yon,can,skor,bayrak;...    OLAY|metin
 */
public class ArenaIstemcisi {

    public static final int PORT = 5555;

    private Socket soket;
    private PrintWriter yazici;
    private volatile boolean bagli = false;
    private volatile int benimId = -1;
    private volatile ArrayList<String[]> oyuncular = new ArrayList<String[]>();
    private final HashMap<Integer, String[]> bilgiler = new HashMap<Integer, String[]>();
    private final LinkedList<String> olaylar = new LinkedList<String>();
    private volatile int[] yeniKonum = null;
    private volatile String hata = "";

    // Sunucuya bağlanır ve arenaya katılır. Başarılıysa true döner.
    public boolean baglan(String adres, String isim, int tur, int maxCan, int saldiri) {
        try {
            soket = new Socket();
            soket.connect(new InetSocketAddress(adres, PORT), 3000);
            soket.setTcpNoDelay(true);
            yazici = new PrintWriter(new OutputStreamWriter(soket.getOutputStream(), "UTF-8"), true);
            final BufferedReader okuyucu = new BufferedReader(new InputStreamReader(soket.getInputStream(), "UTF-8"));
            bagli = true;
            gonder("KATIL|" + temizle(isim) + "|" + tur + "|" + maxCan + "|" + saldiri);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        String satir;
                        while ((satir = okuyucu.readLine()) != null) {
                            try {
                                isle(satir);
                            } catch (RuntimeException e) {
                                // bozuk mesaj, yok say
                            }
                        }
                    } catch (Exception e) {
                        // bağlantı koptu
                    }
                    bagli = false;
                    if (hata.isEmpty()) {
                        hata = "Sunucuyla bağlantı koptu.";
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            return true;
        } catch (Exception e) {
            hata = "Bağlanılamadı: " + adres + " (" + e.getClass().getSimpleName() + ")";
            bagli = false;
            return false;
        }
    }

    private void isle(String satir) {
        String[] p = satir.split("\\|", -1);
        if (p[0].equals("SEN") && p.length >= 4) {
            benimId = Integer.parseInt(p[1]);
            yeniKonum = new int[]{Integer.parseInt(p[2]), Integer.parseInt(p[3])};
        } else if (p[0].equals("KONUM") && p.length >= 3) {
            yeniKonum = new int[]{Integer.parseInt(p[1]), Integer.parseInt(p[2])};
        } else if (p[0].equals("OYUNCU") && p.length >= 5) {
            synchronized (bilgiler) {
                bilgiler.put(Integer.parseInt(p[1]), new String[]{p[2], p[3], p[4]});
            }
        } else if (p[0].equals("CIKTI") && p.length >= 2) {
            synchronized (bilgiler) {
                bilgiler.remove(Integer.parseInt(p[1]));
            }
        } else if (p[0].equals("DURUM")) {
            // Her oyuncu, çizimin beklediği 12 alana dönüştürülür:
            // id, tur, x, y, yon, can, maxCan, skor, saldiriyor, vuruldu, korumali, isim
            ArrayList<String[]> liste = new ArrayList<String[]>();
            if (p.length > 1 && !p[1].isEmpty()) {
                synchronized (bilgiler) {
                    for (String o : p[1].split(";")) {
                        String[] a = o.split(",", -1);
                        if (a.length < 7) {
                            continue;
                        }
                        String[] b = bilgiler.get(Integer.parseInt(a[0]));
                        if (b == null) {
                            continue;
                        }
                        int bayrak = Integer.parseInt(a[6]);
                        liste.add(new String[]{a[0], b[0], a[1], a[2], a[3], a[4], b[1], a[5],
                            (bayrak & 1) != 0 ? "1" : "0", (bayrak & 2) != 0 ? "1" : "0",
                            (bayrak & 4) != 0 ? "1" : "0", b[2]});
                    }
                }
            }
            oyuncular = liste;
        } else if (p[0].equals("OLAY") && p.length >= 2) {
            synchronized (olaylar) {
                olaylar.addFirst(p[1]);
                while (olaylar.size() > 5) {
                    olaylar.removeLast();
                }
            }
            if (p[1].startsWith("Arena dolu")) {
                hata = p[1];
            }
        }
    }

    public synchronized void gonder(String mesaj) {
        if (yazici != null && bagli) {
            yazici.println(mesaj);
        }
    }

    public void hareket(int x, int y, boolean sagaBakiyor) {
        gonder("HAREKET|" + x + "|" + y + "|" + (sagaBakiyor ? 1 : -1));
    }

    public void saldir() {
        gonder("SALDIR");
    }

    public void kapat() {
        bagli = false;
        try {
            if (soket != null) {
                soket.close();
            }
        } catch (Exception e) {
            // önemsiz
        }
    }

    // Sunucu oyuncuyu yeni bir yere aldıysa (doğma, yenilme) o konumu bir kez verir.
    public int[] yeniKonumuAl() {
        int[] k = yeniKonum;
        yeniKonum = null;
        return k;
    }

    public boolean isBagli() { return bagli; }
    public int getBenimId() { return benimId; }
    public ArrayList<String[]> getOyuncular() { return oyuncular; }
    public String getHata() { return hata; }

    public ArrayList<String> getOlaylar() {
        synchronized (olaylar) {
            return new ArrayList<String>(olaylar);
        }
    }

    public static String temizle(String s) {
        String t = s.replace("|", "").replace(",", "").replace(";", "").trim();
        if (t.length() > 14) {
            t = t.substring(0, 14);
        }
        return t.isEmpty() ? "Savaşçı" : t;
    }
}
