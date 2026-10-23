/**
 * TİLKİ — Savasci sınıfının bir ALT SINIFI
 * Kale 2 ve Kale 4 bu dosyayı bekliyor.
 * Yalnızca oyunda seçtiğiniz hayvanın dosyasını doldurmanız yeterlidir.
 */

// GÖREV 2a: Tilki sınıfı Savasci sınıfından kalıtım alsın.
//   class satırına "extends Savasci" ekleyin:   public class Tilki extends Savasci {
//   DİKKAT: 2a ve 2b'yi birlikte yapın. Yalnızca 2a yapılırsa program derlenmez.
public class Tilki {

    public Tilki(String isim) {
        // GÖREV 2b: super(...) ile üst sınıfın (Savasci) yapıcısını çağırın.
        //   Tilki'nin canı 95, gücü 15:   super(isim, 95, 15);
        //   super(...) yapıcının İLK satırı olmalıdır.
        // ... buraya yazın ...
    }

    // GÖREV 4a: Bu metot, Savasci'daki tanit() metodunu geçersiz kılar (override).
    //   "" yerine super.tanit() yazın. super.tanit(), üst sınıftaki tanit() metodunu çalıştırır.
    //   (Bu görev için önce 2a ve 2b yapılmış olmalı.)
    public String tanit() {
        return "" + ", ormanın en kurnazı!";
    }

    // GÖREV 4b: Bu metot, Savasci'daki saldir() metodunu geçersiz kılar.
    //   Tilki'nin saldırısı normal saldırıdan 10 fazladır.
    //   0 yerine super.saldir() + 10 yazın.
    public int saldir() {
        return 0;
    }
}
