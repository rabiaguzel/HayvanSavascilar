/**
 * KARTAL — Savasci sınıfının bir ALT SINIFI
 * Kale 2 ve Kale 4 bu dosyayı bekliyor.
 * Yalnızca oyunda seçtiğiniz hayvanın dosyasını doldurmanız yeterlidir.
 */

// GÖREV 2a: Kartal sınıfı Savasci sınıfından kalıtım alsın.
//   class satırına "extends Savasci" ekleyin:   public class Kartal extends Savasci {
//   DİKKAT: 2a ve 2b'yi birlikte yapın. Yalnızca 2a yapılırsa program derlenmez.
public class Kartal {

    public Kartal(String isim) {
        // GÖREV 2b: super(...) ile üst sınıfın (Savasci) yapıcısını çağırın.
        //   Kartal'ın canı 90, gücü 16:   super(isim, 90, 16);
        //   super(...) yapıcının İLK satırı olmalıdır.
        // ... buraya yazın ...
    }

    // GÖREV 4a: Bu metot, Savasci'daki tanit() metodunu geçersiz kılar (override).
    //   "" yerine super.tanit() yazın. super.tanit(), üst sınıftaki tanit() metodunu çalıştırır.
    //   (Bu görev için önce 2a ve 2b yapılmış olmalı.)
    public String tanit() {
        return "" + ", göklerin hükümdarı!";
    }

    // GÖREV 4b: Bu metot, Savasci'daki saldir() metodunu geçersiz kılar.
    //   Kartal'ın saldırısı normal saldırıdan 15 fazladır.
    //   0 yerine super.saldir() + 15 yazın.
    public int saldir() {
        return 0;
    }
}
