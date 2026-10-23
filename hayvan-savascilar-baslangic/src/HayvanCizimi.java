import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Hayvanların çizimleri. Oyun ve arena sunucusu bu sınıfı kullanır. (Hazır, değiştirmeyin.)
 */
public class HayvanCizimi {

    // Hayvan çizimi: (x, y) gövdenin ortası
    public static void ciz(Graphics2D g, int tur, int x, int y, double o, boolean sagaBak, int kare) {
        int yon = sagaBak ? 1 : -1;
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(x - (int) (26 * o), y + (int) (24 * o), (int) (52 * o), (int) (10 * o));
        if (tur == 0) {            // Aslan
            Color tuy = new Color(235, 180, 70), yele = new Color(170, 95, 35);
            g.setColor(tuy);
            g.fillOval(x - (int) (24 * o), y - (int) (4 * o), (int) (48 * o), (int) (28 * o));
            ayaklar(g, x, y, o, tuy.darker());
            g.setColor(yele);
            g.fillOval(x + yon * (int) (10 * o) - (int) (22 * o), y - (int) (40 * o), (int) (44 * o), (int) (44 * o));
            g.setColor(tuy);
            g.fillOval(x + yon * (int) (12 * o) - (int) (14 * o), y - (int) (31 * o), (int) (28 * o), (int) (28 * o));
            yuz(g, x + yon * (int) (12 * o), y - (int) (17 * o), o, yon, new Color(120, 70, 40));
        } else if (tur == 1) {     // Kurt
            Color tuy = new Color(140, 145, 155);
            g.setColor(tuy);
            g.fillOval(x - (int) (24 * o), y - (int) (4 * o), (int) (48 * o), (int) (26 * o));
            ayaklar(g, x, y, o, tuy.darker());
            int hx = x + yon * (int) (14 * o);
            g.fillPolygon(new int[]{hx - (int) (13 * o), hx - (int) (6 * o), hx}, new int[]{y - (int) (22 * o), y - (int) (42 * o), y - (int) (24 * o)}, 3);
            g.fillPolygon(new int[]{hx, hx + (int) (6 * o), hx + (int) (13 * o)}, new int[]{y - (int) (24 * o), y - (int) (42 * o), y - (int) (22 * o)}, 3);
            g.fillOval(hx - (int) (15 * o), y - (int) (32 * o), (int) (30 * o), (int) (28 * o));
            g.setColor(new Color(210, 210, 215));
            g.fillOval(hx + yon * (int) (6 * o) - (int) (9 * o), y - (int) (20 * o), (int) (18 * o), (int) (12 * o));
            g.setColor(new Color(230, 200, 60));
            g.fillOval(hx - (int) (8 * o), y - (int) (25 * o), (int) (6 * o), (int) (6 * o));
            g.fillOval(hx + (int) (3 * o), y - (int) (25 * o), (int) (6 * o), (int) (6 * o));
            g.setColor(Color.BLACK);
            g.fillOval(hx + yon * (int) (13 * o) - (int) (3 * o), y - (int) (18 * o), (int) (6 * o), (int) (5 * o));
        } else if (tur == 2) {     // Ayı
            Color tuy = new Color(120, 80, 50);
            g.setColor(tuy);
            g.fillOval(x - (int) (28 * o), y - (int) (10 * o), (int) (56 * o), (int) (36 * o));
            ayaklar(g, x, y, o, tuy.darker());
            int hx = x + yon * (int) (12 * o);
            g.fillOval(hx - (int) (17 * o), y - (int) (40 * o), (int) (12 * o), (int) (12 * o));
            g.fillOval(hx + (int) (5 * o), y - (int) (40 * o), (int) (12 * o), (int) (12 * o));
            g.fillOval(hx - (int) (16 * o), y - (int) (35 * o), (int) (32 * o), (int) (30 * o));
            g.setColor(new Color(200, 160, 110));
            g.fillOval(hx + yon * (int) (4 * o) - (int) (8 * o), y - (int) (22 * o), (int) (16 * o), (int) (12 * o));
            g.setColor(Color.BLACK);
            g.fillOval(hx - (int) (8 * o), y - (int) (27 * o), (int) (5 * o), (int) (5 * o));
            g.fillOval(hx + (int) (3 * o), y - (int) (27 * o), (int) (5 * o), (int) (5 * o));
            g.fillOval(hx + yon * (int) (5 * o) - (int) (3 * o), y - (int) (20 * o), (int) (6 * o), (int) (4 * o));
        } else if (tur == 4) {     // Tavşan
            Color tuy = new Color(235, 232, 228), pembe = new Color(240, 170, 185);
            g.setColor(tuy);
            g.fillOval(x - (int) (22 * o), y - (int) (4 * o), (int) (44 * o), (int) (28 * o));
            g.fillOval(x - yon * (int) (24 * o) - (int) (7 * o), y - (int) (2 * o), (int) (14 * o), (int) (14 * o));
            ayaklar(g, x, y, o, new Color(215, 210, 205));
            int hx = x + yon * (int) (12 * o);
            g.fillOval(hx - (int) (11 * o), y - (int) (58 * o), (int) (9 * o), (int) (30 * o));
            g.fillOval(hx + (int) (2 * o), y - (int) (58 * o), (int) (9 * o), (int) (30 * o));
            g.setColor(pembe);
            g.fillOval(hx - (int) (9 * o), y - (int) (54 * o), (int) (5 * o), (int) (22 * o));
            g.fillOval(hx + (int) (4 * o), y - (int) (54 * o), (int) (5 * o), (int) (22 * o));
            g.setColor(tuy);
            g.fillOval(hx - (int) (14 * o), y - (int) (32 * o), (int) (28 * o), (int) (26 * o));
            g.setColor(Color.BLACK);
            g.fillOval(hx - (int) (7 * o), y - (int) (24 * o), (int) (5 * o), (int) (5 * o));
            g.fillOval(hx + (int) (3 * o), y - (int) (24 * o), (int) (5 * o), (int) (5 * o));
            g.setColor(pembe);
            g.fillOval(hx + yon * (int) (6 * o) - (int) (3 * o), y - (int) (17 * o), (int) (6 * o), (int) (5 * o));
        } else if (tur == 5) {     // Kedi
            Color tuy = new Color(235, 150, 70), koyu = new Color(200, 115, 45);
            g.setColor(tuy);
            g.fillOval(x - (int) (22 * o), y - (int) (4 * o), (int) (44 * o), (int) (26 * o));
            ayaklar(g, x, y, o, koyu);
            g.setColor(koyu);
            g.setStroke(new BasicStroke((float) (5 * o)));
            g.drawArc(x - yon * (int) (30 * o) - (int) (12 * o), y - (int) (26 * o), (int) (24 * o), (int) (34 * o), sagaBak ? 90 : -90, 180);
            g.setStroke(new BasicStroke(1));
            int hx = x + yon * (int) (13 * o);
            g.setColor(tuy);
            g.fillPolygon(new int[]{hx - (int) (13 * o), hx - (int) (9 * o), hx - (int) (2 * o)}, new int[]{y - (int) (20 * o), y - (int) (40 * o), y - (int) (26 * o)}, 3);
            g.fillPolygon(new int[]{hx + (int) (2 * o), hx + (int) (9 * o), hx + (int) (13 * o)}, new int[]{y - (int) (26 * o), y - (int) (40 * o), y - (int) (20 * o)}, 3);
            g.fillOval(hx - (int) (14 * o), y - (int) (32 * o), (int) (28 * o), (int) (26 * o));
            g.setColor(new Color(90, 160, 80));
            g.fillOval(hx - (int) (8 * o), y - (int) (25 * o), (int) (6 * o), (int) (6 * o));
            g.fillOval(hx + (int) (3 * o), y - (int) (25 * o), (int) (6 * o), (int) (6 * o));
            g.setColor(new Color(240, 140, 150));
            g.fillOval(hx + yon * (int) (2 * o) - (int) (3 * o), y - (int) (18 * o), (int) (6 * o), (int) (4 * o));
            g.setColor(new Color(90, 60, 40));
            g.drawLine(hx - (int) (4 * o), y - (int) (15 * o), hx - (int) (18 * o), y - (int) (17 * o));
            g.drawLine(hx + (int) (4 * o), y - (int) (15 * o), hx + (int) (18 * o), y - (int) (17 * o));
        } else if (tur == 6) {     // Panda
            Color beyaz = new Color(245, 245, 240), siyah = new Color(35, 35, 40);
            g.setColor(beyaz);
            g.fillOval(x - (int) (26 * o), y - (int) (8 * o), (int) (52 * o), (int) (34 * o));
            ayaklar(g, x, y, o, siyah);
            g.setColor(siyah);
            g.fillRect(x - (int) (8 * o), y - (int) (8 * o), (int) (16 * o), (int) (30 * o));
            int hx = x + yon * (int) (12 * o);
            g.fillOval(hx - (int) (17 * o), y - (int) (40 * o), (int) (12 * o), (int) (12 * o));
            g.fillOval(hx + (int) (5 * o), y - (int) (40 * o), (int) (12 * o), (int) (12 * o));
            g.setColor(beyaz);
            g.fillOval(hx - (int) (16 * o), y - (int) (35 * o), (int) (32 * o), (int) (30 * o));
            g.setColor(siyah);
            g.fillOval(hx - (int) (11 * o), y - (int) (28 * o), (int) (9 * o), (int) (11 * o));
            g.fillOval(hx + (int) (2 * o), y - (int) (28 * o), (int) (9 * o), (int) (11 * o));
            g.setColor(Color.WHITE);
            g.fillOval(hx - (int) (8 * o), y - (int) (25 * o), (int) (3 * o), (int) (3 * o));
            g.fillOval(hx + (int) (5 * o), y - (int) (25 * o), (int) (3 * o), (int) (3 * o));
            g.setColor(siyah);
            g.fillOval(hx + yon * (int) (2 * o) - (int) (3 * o), y - (int) (15 * o), (int) (6 * o), (int) (4 * o));
        } else if (tur == 7) {     // Tilki
            Color tuy = new Color(225, 110, 40), beyaz = new Color(250, 245, 235);
            g.setColor(tuy);
            g.fillOval(x - yon * (int) (40 * o) - (int) (16 * o), y - (int) (14 * o), (int) (32 * o), (int) (18 * o));
            g.setColor(beyaz);
            g.fillOval(x - yon * (int) (50 * o) - (int) (7 * o), y - (int) (11 * o), (int) (14 * o), (int) (12 * o));
            g.setColor(tuy);
            g.fillOval(x - (int) (22 * o), y - (int) (4 * o), (int) (44 * o), (int) (24 * o));
            ayaklar(g, x, y, o, new Color(60, 40, 30));
            g.setColor(tuy);
            int hx = x + yon * (int) (14 * o);
            g.fillPolygon(new int[]{hx - (int) (13 * o), hx - (int) (8 * o), hx - (int) (1 * o)}, new int[]{y - (int) (22 * o), y - (int) (44 * o), y - (int) (26 * o)}, 3);
            g.fillPolygon(new int[]{hx + (int) (1 * o), hx + (int) (8 * o), hx + (int) (13 * o)}, new int[]{y - (int) (26 * o), y - (int) (44 * o), y - (int) (22 * o)}, 3);
            g.fillOval(hx - (int) (14 * o), y - (int) (32 * o), (int) (28 * o), (int) (26 * o));
            g.setColor(beyaz);
            g.fillOval(hx + yon * (int) (7 * o) - (int) (9 * o), y - (int) (20 * o), (int) (18 * o), (int) (12 * o));
            g.setColor(Color.BLACK);
            g.fillOval(hx - (int) (7 * o), y - (int) (25 * o), (int) (5 * o), (int) (5 * o));
            g.fillOval(hx + (int) (3 * o), y - (int) (25 * o), (int) (5 * o), (int) (5 * o));
            g.fillOval(hx + yon * (int) (15 * o) - (int) (3 * o), y - (int) (17 * o), (int) (6 * o), (int) (5 * o));
        } else {                   // Kartal
            Color tuy = new Color(110, 70, 40);
            g.setColor(tuy.darker());
            int kanat = (int) (Math.sin(kare * 0.25) * 6 * o);
            g.fillPolygon(new int[]{x - (int) (8 * o), x - (int) (40 * o), x + (int) (4 * o)},
                    new int[]{y - (int) (2 * o), y - (int) (26 * o) - kanat, y - (int) (12 * o)}, 3);
            g.fillPolygon(new int[]{x + (int) (8 * o), x + (int) (40 * o), x - (int) (4 * o)},
                    new int[]{y - (int) (2 * o), y - (int) (26 * o) - kanat, y - (int) (12 * o)}, 3);
            g.setColor(tuy);
            g.fillOval(x - (int) (16 * o), y - (int) (14 * o), (int) (32 * o), (int) (38 * o));
            g.setColor(new Color(240, 200, 60));
            g.fillRect(x - (int) (8 * o), y + (int) (20 * o), (int) (4 * o), (int) (8 * o));
            g.fillRect(x + (int) (4 * o), y + (int) (20 * o), (int) (4 * o), (int) (8 * o));
            int hx = x + yon * (int) (6 * o);
            g.setColor(Color.WHITE);
            g.fillOval(hx - (int) (13 * o), y - (int) (36 * o), (int) (26 * o), (int) (26 * o));
            g.setColor(new Color(245, 190, 40));
            g.fillPolygon(new int[]{hx + yon * (int) (10 * o), hx + yon * (int) (24 * o), hx + yon * (int) (10 * o)},
                    new int[]{y - (int) (28 * o), y - (int) (22 * o), y - (int) (18 * o)}, 3);
            g.setColor(Color.BLACK);
            g.fillOval(hx + yon * (int) (3 * o) - (int) (3 * o), y - (int) (29 * o), (int) (6 * o), (int) (6 * o));
        }
    }

    private static void ayaklar(Graphics2D g, int x, int y, double o, Color c) {
        g.setColor(c);
        int[] ox = {-18, -8, 6, 16};
        for (int dx : ox) {
            g.fillRoundRect(x + (int) (dx * o), y + (int) (14 * o), (int) (7 * o), (int) (14 * o), 4, 4);
        }
    }

    private static void yuz(Graphics2D g, int x, int y, double o, int yon, Color burun) {
        g.setColor(Color.BLACK);
        g.fillOval(x - (int) (7 * o), y - (int) (6 * o), (int) (5 * o), (int) (5 * o));
        g.fillOval(x + (int) (3 * o), y - (int) (6 * o), (int) (5 * o), (int) (5 * o));
        g.setColor(burun);
        g.fillOval(x + yon * (int) (2 * o) - (int) (4 * o), y + (int) (1 * o), (int) (8 * o), (int) (6 * o));
    }
}
