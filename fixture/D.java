class D {

    public void m1() {
        int total = 0;
        for(int i = 0; i < 10; i++) {
            total += i;
        }
        return total;
    }

    public void m2() {
        int total = 0;
        int i = 0;
        while(i > 10) {
            total += i;
        }
        return total;
    }
}