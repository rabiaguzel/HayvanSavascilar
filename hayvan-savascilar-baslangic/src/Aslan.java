/**
 * ASLAN — Savasci sınıfının bir ALT SINIFI
 * Kale 2 ve Kale 4 bu dosyayı bekliyor.
 * Yalnızca oyunda seçtiğiniz hayvanın dosyasını doldurmanız yeterlidir.
 */

// GÖREV 2a: Aslan sınıfı Savasci sınıfından kalıtım alsın.
//   class satırına "extends Savasci" ekleyin:   public class Aslan extends Savasci {
//   DİKKAT: 2a ve 2b'yi birlikte yapın. Yalnızca 2a yapılırsa program derlenmez.
public class Aslan {

    public Aslan(String isim) {
        // GÖREV 2b: super(...) ile üst sınıfın (Savasci) yapıcısını çağırın.
        //   Aslan'ın canı 120, gücü 14:   super(isim, 120, 14);
        //   super(...) yapıcının İLK satırı olmalıdır.
        // ... buraya yazın ...
    }

    // GÖREV 4a: Bu metot, Savasci'daki tanit() metodunu geçersiz kılar (override).
    //   "" yerine super.tanit() yazın. super.tanit(), üst sınıftaki tanit() metodunu çalıştırır.
    //   (Bu görev için önce 2a ve 2b yapılmış olmalı.)
    public String tanit() {
        return "" + ", ormanın kralı!";
    }

    // GÖREV 4b: Bu metot, Savasci'daki saldir() metodunu geçersiz kılar.
    //   Aslan'ın saldırısı normal saldırının 2 katıdır.
    //   0 yerine super.saldir() * 2 yazın.
    public int saldir() {
        return 0;
    }
}
