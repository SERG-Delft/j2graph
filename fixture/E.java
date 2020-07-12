class E {

    void m1() {
        int a = m2();
        return a * 2;
    }

    int m2() {
        return 42;
    }
}