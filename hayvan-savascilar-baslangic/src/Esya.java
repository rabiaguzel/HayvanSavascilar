/**
 * EŞYA — oyundaki eşyaların ÜST SINIFI (hazır, değiştirmeyin)
 * Iksir sınıfı bu sınıftan kalıtım alacak.
 */
public class Esya {

    protected String ad;

    public Esya(String ad) {
        this.ad = ad;
    }

    public String getAd() {
        return ad;
    }

    // Genel bir eşya kullanılınca hiçbir şey olmaz.
    // Alt sınıflar bu metodu geçersiz kılarak (override) kendi işini yapar.
    public void kullan(Savasci s) {
    }
}
