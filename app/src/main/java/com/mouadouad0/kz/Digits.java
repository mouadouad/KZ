package com.mouadouad0.kz;

public class Digits {
    final private int[] number = new int[4];
    
    public Digits(int a, int b, int c, int d) {
        number[0] = a;
        number[1] = b;
        number[2] = c;
        number[3] = d;
    }

    public Digits(String entry){
        final int entryInteger = Integer.parseInt(entry);
        int a, b, c, d;

        a = entryInteger / 1000;
        b = (entryInteger % 1000) / 100;
        c = (entryInteger / 10) - 100 * a - 10 * b;
        d = entryInteger - 10 * (100 * a + 10 * b + c);

        number[0] = a;
        number[1] = b;
        number[2] = c;
        number[3] = d;
    }
    
    public int getDigit(int index) {
        return number[index];
    }

    public boolean isNotValid() {
        return number[0] == number[1] || number[0] == number[2] ||
                number[0] == number[3] || number[1] == number[2] ||
                number[1] == number[3] || number[2] == number[3];
    }

    @Override
    public boolean equals(Object o) {
        if( o == this ) {
            return true;
        }

        if( !(o instanceof Digits) ) {
            return false;
        }

        for(int i = 0; i < 4; i++) {
            if (number[i] != ((Digits) o).number[i]) {
                return false;
            }
        }
        return true;
    }
}
