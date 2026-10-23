/**
 * İKSİR — Esya sınıfının bir ALT SINIFI
 * Kale 3 bu dosyayı bekliyor.
 * Esya.java dosyasını açıp okuyun: Iksir, Esya'nın alanlarını ve metotlarını kalıtımla alacak.
 */

// GÖREV 3a: Iksir sınıfı Esya sınıfından kalıtım alsın.
//   class satırına "extends Esya" ekleyin:   public class Iksir extends Esya {
//   DİKKAT: 3a ve 3b'yi birlikte yapın. Yalnızca 3a yapılırsa program derlenmez.
public class Iksir {

    private int iyilestirme;

    public Iksir(int iyilestirme) {
        // GÖREV 3b: super(...) ile Esya'nın yapıcısını çağırın. Eşyanın adı "Can İksiri" olsun.
        //   İpucu: super("Can İksiri");     (yapıcının İLK satırı olmalı)
        // ... buraya yazın ...
        this.iyilestirme = iyilestirme;
    }

    // GÖREV 3c: Bu metot, Esya'daki kullan() metodunu geçersiz kılar (override).
    //   İksir kullanılınca savaşçı iyileşsin:   s.iyiles(iyilestirme);
    public void kullan(Savasci s) {
        // ... buraya yazın ...
    }

    public int getIyilestirme() {
        return iyilestirme;
    }
}
