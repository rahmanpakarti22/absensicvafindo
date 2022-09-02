package com.afindoinf;

public class ModelAbsen
{
    private String KodeAbsen, UserName, Tanggal, JamMasuk, Keterlambatan, Kategori;

    public ModelAbsen(String kodeAbsen, String userName, String tanggal, String jamMasuk, String keterlambatan, String kategori)
    {
        this.KodeAbsen = kodeAbsen;
        this.UserName = userName;
        this.Tanggal = tanggal;
        this.JamMasuk = jamMasuk;
        this.Keterlambatan = keterlambatan;
        this.Kategori = kategori;
    }

    public String getKodeAbsen()
    {
        return KodeAbsen;
    }

    public void setKodeAbsen(String kodeAbsen)
    {
        KodeAbsen = kodeAbsen;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName)
    {
        UserName = userName;
    }

    public String getTanggal()
    {
        return Tanggal;
    }

    public void setTanggal(String tanggal)
    {
        Tanggal = tanggal;
    }

    public String getJamMasuk()
    {
        return JamMasuk;
    }

    public void setJamMasuk(String jamMasuk)
    {
        JamMasuk = jamMasuk;
    }

    public String getKeterlambatan()
    {
        return Keterlambatan;
    }

    public void setKeterlambatan(String keterlambatan)
    {
        Keterlambatan = keterlambatan;
    }

    public String getKategori()
    {
        return Kategori;
    }

    public void setKategori(String kategori)
    {
        Kategori = kategori;
    }
}
