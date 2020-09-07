package ba.unsa.etf.rpr;

public class Znamenitost {
    private int id;
    private String naziv;
    private String putanjaSlike;
    private Grad grad;

    public Znamenitost() {
    }

    public Znamenitost(int id, String naziv, String putanjaSlike, Grad grad) {
        this.id = id;
        this.naziv = naziv;
        this.putanjaSlike = putanjaSlike;
        this.grad = grad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getPutanjaSlike() {
        return putanjaSlike;
    }

    public void setPutanjaSlike(String putanjaSlike) {
        this.putanjaSlike = putanjaSlike;
    }

    public Grad getGrad() {
        return grad;
    }

    public void setGrad(Grad grad) {
        this.grad = grad;
    }

    @Override
    public String toString() {
        return this.naziv;
    }
}