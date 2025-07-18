package com.reg.regis.dto.response;

public class RegistrationResponse {
    private String jenisKartu;
    private String namaLengkap;
    private String kodeRekening;
    private String jenisTabungan;
    private String nomorKartuDebitVirtual;

    // Constructor
    public RegistrationResponse(String jenisKartu, String namaLengkap, 
                                 String kodeRekening, String jenisTabungan, 
                                 String nomorKartuDebitVirtual) {
        this.jenisKartu = jenisKartu;
        this.namaLengkap = namaLengkap;
        this.kodeRekening = kodeRekening;
        this.jenisTabungan = jenisTabungan;
        this.nomorKartuDebitVirtual = nomorKartuDebitVirtual;
    }

    // Getters dan Setters
    public String getJenisKartu() {
        return jenisKartu;
    }

    public void setJenisKartu(String jenisKartu) {
        this.jenisKartu = jenisKartu;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getKodeRekening() {
        return kodeRekening;
    }

    public void setKodeRekening(String kodeRekening) {
        this.kodeRekening = kodeRekening;
    }

    public String getJenisTabungan() {
        return jenisTabungan;
    }

    public void setJenisTabungan(String jenisTabungan) {
        this.jenisTabungan = jenisTabungan;
    }

    public String getNomorKartuDebitVirtual() {
        return nomorKartuDebitVirtual;
    }

    public void setNomorKartuDebitVirtual(String nomorKartuDebitVirtual) {
        this.nomorKartuDebitVirtual = nomorKartuDebitVirtual;
    }
}