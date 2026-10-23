/**
 * KEDİ — Savasci sınıfının bir ALT SINIFI
 * Kale 2 ve Kale 4 bu dosyayı bekliyor.
 * Yalnızca oyunda seçtiğiniz hayvanın dosyasını doldurmanız yeterlidir.
 */

// GÖREV 2a: Kedi sınıfı Savasci sınıfından kalıtım alsın.
//   class satırına "extends Savasci" ekleyin:   public class Kedi extends Savasci {
//   DİKKAT: 2a ve 2b'yi birlikte yapın. Yalnızca 2a yapılırsa program derlenmez.
public class Kedi {

    public Kedi(String isim) {
        // GÖREV 2b: super(...) ile üst sınıfın (Savasci) yapıcısını çağırın.
        //   Kedi'nin canı 95, gücü 13:   super(isim, 95, 13);
        //   super(...) yapıcının İLK satırı olmalıdır.
        // ... buraya yazın ...
    }

    // GÖREV 4a: Bu metot, Savasci'daki tanit() metodunu geçersiz kılar (override).
    //   "" yerine super.tanit() yazın. super.tanit(), üst sınıftaki tanit() metodunu çalıştırır.
    //   (Bu görev için önce 2a ve 2b yapılmış olmalı.)
    public String tanit() {
        return "" + ", gecenin avcısı!";
    }

    // GÖREV 4b: Bu metot, Savasci'daki saldir() metodunu geçersiz kılar.
    //   Kedi'nin saldırısı normal saldırıdan 12 fazladır.
    //   0 yerine super.saldir() + 12 yazın.
    public int saldir() {
        return 0;
    }
}
