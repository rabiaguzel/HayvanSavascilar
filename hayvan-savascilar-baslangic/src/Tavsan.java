/**
 * TAVŞAN — Savasci sınıfının bir ALT SINIFI
 * Kale 2 ve Kale 4 bu dosyayı bekliyor.
 * Yalnızca oyunda seçtiğiniz hayvanın dosyasını doldurmanız yeterlidir.
 */

// GÖREV 2a: Tavsan sınıfı Savasci sınıfından kalıtım alsın.
//   class satırına "extends Savasci" ekleyin:   public class Tavsan extends Savasci {
//   DİKKAT: 2a ve 2b'yi birlikte yapın. Yalnızca 2a yapılırsa program derlenmez.
public class Tavsan {

    public Tavsan(String isim) {
        // GÖREV 2b: super(...) ile üst sınıfın (Savasci) yapıcısını çağırın.
        //   Tavşan'ın canı 80, gücü 12:   super(isim, 80, 12);
        //   super(...) yapıcının İLK satırı olmalıdır.
        // ... buraya yazın ...
    }

    // GÖREV 4a: Bu metot, Savasci'daki tanit() metodunu geçersiz kılar (override).
    //   "" yerine super.tanit() yazın. super.tanit(), üst sınıftaki tanit() metodunu çalıştırır.
    //   (Bu görev için önce 2a ve 2b yapılmış olmalı.)
    public String tanit() {
        return "" + ", ormanın en hızlısı!";
    }

    // GÖREV 4b: Bu metot, Savasci'daki saldir() metodunu geçersiz kılar.
    //   Tavşan'ın saldırısı normal saldırının 2 katıdır.
    //   0 yerine super.saldir() * 2 yazın.
    public int saldir() {
        return 0;
    }
}
