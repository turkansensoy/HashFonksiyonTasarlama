/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hashfonksiyonlama;

import java.util.Scanner;

/**
 *
 * @author türkan şensoy
 */
public class HashFonksiyonlama {
 // Bir verinin özeti her özet alma işleminde aynı çıkar ve
    //çıkan özet değeri başka verilerden elde edilmez.
    public static int mesajuzunlugu= 0;
    public static void main(String[] args) {
         System.out.println("Hash'ini alacağınız kelime yada cümleyi giriniz...");
         Scanner sc = new Scanner(System.in); // Kullanıcıdan string değer alabilmek için Scanner sınıfı eklendi
         String kelime= sc.nextLine();
         System.out.println("Metin/kelime: " + kelime);
         
         //Kelimeyi binary degerini alma
           String binary = binaryTabanaDönüştürme(kelime);
           mesajuzunlugu = binary.length();
           mesajHesaplama(kelime,binary);     
    }
    
    
     public static String binaryTabanaDönüştürme(String kelime) {

        byte[] bytes = kelime.getBytes();
        StringBuilder binary = new StringBuilder();

        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {    //Binary kodlar 1 byte(8 bit)  halide tutulur.
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;  //bir karakter kaydırma işlemi 
            }
            binary.append(' ');
        }
        return binary.toString();
    }
      public static void mesajHesaplama(String kelime, String binary) {

        int binaryMesajUzunlugu = kelime.length() * 8 - 8; 
        String sonbitUzunlugu = mesajUzunlugunuHesaplama(binaryMesajUzunlugu+8);
        int sonbit = sonbitUzunlugu.length();
        int temp = (binaryMesajUzunlugu) % 512; //burada mesaj uzunlugunun 512'ye göre modunu alıyoruz çünkü 512'den fazla ise parçalara ayırıyor

        if (432 - temp < 0) {
            int x = 512 - temp;
            temp = x + 440 + temp + 64;
        } else {
            temp = 432 - temp;
        }

        int binaryZeros = temp;
        String onePadded = "10000000"; 
        binary = binary.replaceAll("\\s+", "");
        mesajUzunluguOlustur(binary, onePadded, binaryZeros, sonbitUzunlugu);
        
    }
     public static String mesajUzunlugunuHesaplama(int bitUzunluk) {

        String tempBitsUzunluk= Integer.toBinaryString(bitUzunluk);
        StringBuilder sb = new StringBuilder(tempBitsUzunluk);
        int temp = 64 - tempBitsUzunluk.length();

        while (temp > 0) {
            sb.insert(0, 0);
            temp--;
        }

        return sb.toString();
    }
      public static String mesajUzunluguOlustur(String message, String paddedOne, int sıfır, String sonbitUzunlugu) {

        StringBuilder mesajBinary = new StringBuilder(message);
        mesajBinary.insert(mesajBinary.toString().length(), paddedOne);

        while (sıfır > 0) {
            mesajBinary.insert(mesajBinary.toString().length(), 0);
            sıfır--;
        }

        mesajBinary.insert(mesajBinary.toString().length(), sonbitUzunlugu);
        String m = mesajYazdır(mesajBinary.toString());
        m = m.replaceAll("\\s+", "");
        int[] mArray = new int[m.toString().length()/32];

        for (int i = 0; i < m.toString().length(); i+=32) {
            mArray[i/32] = Integer.valueOf(m.substring(i+1, i+32),2);
            if(m.charAt(i) == '1'){
                mArray[i/32] |= 0X80000000;
            }
        }

        hash(mArray);
        return mesajBinary.toString();
    }
    public static String mesajYazdır(String mesaj) {

        StringBuilder sb = new StringBuilder(mesaj);
        int sayı = mesaj.length();

        while (sayı > 0) {
            if (sayı % 32 == 0) {
                sb.insert(sayı, " ");
            }
            sayı--;
        }
        return sb.toString();
    }
     private static int solaDöndürme(int x, int kaydır) { 

        return ((x << kaydır) | (x >>> (32 - kaydır)));
    }
     //Başlangıç degişkeni rastgele seçilmiş degerlerdir.32 bit uzunluktadır
    private static int h0 = 0x67452301;
    private static int h1 = 0xEFCDAB89;
    private static int h2 = 0x98BADCFE;
    private static int h3 = 0x10325476;
    private static int h4 = 0xC3D2E1F0;
    private static int k0 = 0x5A827999;
    private static int k1 = 0x6ED9EBA1;
    private static int k2 = 0x8F1BBCDC;
    private static int k3 = 0xCA62C1D6;
    
    
     private static String hash(int[] z) {

        int[] intArray = new int[80];
        int j;
        for(int i = 0; i < z.length; i += 16) {
            for(j = 0; j <= 15; j++)
                intArray[j] = z[j+i];
            for ( j = 16; j <= 79; j++ ) {
                //w[i] = (w[i-3] xor w[i-8] xor w[i-14] xor w[i-16]) solaDöndürme 1
                intArray[j] = solaDöndürme(intArray[j - 3] ^ intArray[j - 8] ^ intArray[j - 14] ^ intArray[j - 16], 1);
            }

            int A = h0;
            int B = h1;
            int C = h2;
            int D = h3;
            int E = h4;
            int temp = 0;

            for ( int x = 0; x <= 19; x++ ) {
               temp = solaDöndürme(A,5)+((B&C)|((~B)&D))+E+intArray[x]+k0;
                E=D; D=C; C=solaDöndürme(B,30); B=A; A=temp;
            }
            for ( int b = 20; b <= 39; b++ ) {
                temp = solaDöndürme(A,5)+(B^C^D)+E+intArray[b]+k1;
                E=D; D=C; C=solaDöndürme(B,30); B=A; A=temp;
            }
            for (int c = 40; c <= 59; c++ ) {
                temp = solaDöndürme(A,5)+((B&C)|(B&D)|(C&D))+E+intArray[c]+k2;
                E=D; D=C; C=solaDöndürme(B,30); B=A; A=temp;
            }
            for ( int d = 60; d <= 79; d++ ) {
                temp = solaDöndürme(A,5)+(B^C^D)+E+intArray[d]+k3;
                E=D; D=C; C=solaDöndürme(B,30); B=A; A=temp;
            }

            h0+=A; h1+=B; h2+=C; h3+=D; h4+=E;

        }

        String  h0uzunluk = Integer.toHexString(h0);
        String h1uzunluk = Integer.toHexString(h1);
        String h2uzunluk= Integer.toHexString(h2);
        String h3uzunluk= Integer.toHexString(h3);
        String h4uzunluk = Integer.toHexString(h4);

        
        if(h1uzunluk.length() < 8) {
            StringBuilder h0U = new StringBuilder(h0uzunluk); //metinsel ifadeleri birleştirmek için
            h0U.insert(0,0);
            h0uzunluk = h0U.toString();
        } else if(h1uzunluk.length() < 8) {
            StringBuilder h1U = new StringBuilder(h1uzunluk);
            h1U.insert(0,0);
            h1uzunluk = h1U.toString();
        } else if(h2uzunluk.length() < 8) {
            StringBuilder h2U = new StringBuilder(h2uzunluk);
            h2U.insert(0,0);
          h2uzunluk = h2U.toString();
        } else if(h3uzunluk.length() < 8) {
            StringBuilder h3U = new StringBuilder(h3uzunluk);
            h3U.insert(0,0);
            h3uzunluk = h3U.toString();
        } else if(h4uzunluk.length() < 8) {
            StringBuilder h4U = new StringBuilder(h4uzunluk);
            h4U.insert(0,0);
            h4uzunluk = h4U.toString();
        }

       String hh = h0uzunluk + h1uzunluk + h2uzunluk + h3uzunluk + h4uzunluk;
        System.out.println("Sonuç: " + hh);

        return null;
    }
    
}
