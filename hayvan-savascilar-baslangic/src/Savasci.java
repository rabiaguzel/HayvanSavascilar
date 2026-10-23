/**
 * SAVAŞÇI — bütün hayvan savaşçıların ÜST SINIFI
 * Kale 1 bu dosyayı bekliyor.
 *
 * Aslan, Kurt, Ayi ve Kartal sınıfları bu sınıftan kalıtım alacak (extends Savasci).
 * Burada yazılan alanlar ve metotlar o sınıflara da geçer.
 * "protected" alanlara alt sınıflar da erişebilir.
 */
public class Savasci {

    protected String isim;
    protected int can;
    protected int maxCan;
    protected int guc;

    public Savasci(String isim, int can, int guc) {
        // GÖREV 1a: Parametreleri alanlara atayın. maxCan da can ile aynı değeri alsın.
        //   İpucu: this.isim = isim;   this.can = can;   this.maxCan = can;   this.guc = guc;
        // ... buraya yazın ...
    }

    // Savaşçı kendini tanıtır. Alt sınıflar bu metodu geçersiz kılıp (override) genişletecek.
    public String tanit() {
        return "Ben " + isim;
    }

    // Normal saldırının gücü. Alt sınıflar bu metodu geçersiz kılıp güçlendirecek.
    public int saldir() {
        return guc;
    }

    public void hasarAl(int miktar) {
        // GÖREV 1b: can değerini miktar kadar azaltın. can 0'ın altına düşerse 0 yapın.
        //   İpucu: can = can - miktar;   ardından bir if ile 0 kontrolü
        // ... buraya yazın ...
    }

    // Canı miktar kadar artırır, maxCan'ı geçmez. (hazır)
    public void iyiles(int miktar) {
        can = can + miktar;
        if (can > maxCan) {
            can = maxCan;
        }
    }

    public String getIsim() { return isim; }
    public int getCan() { return can; }
    public int getMaxCan() { return maxCan; }
    public int getGuc() { return guc; }
}
