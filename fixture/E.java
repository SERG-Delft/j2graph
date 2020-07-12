class E {

    void m1() {
        int a = m2();
        return a * 2;
    }

    int m2() {
        if(10 > 20)
            return 42;
        else
            return 30;
    }
}