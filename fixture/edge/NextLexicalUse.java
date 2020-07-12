class NextLexicalUse {

    public void m1() {
        int a = 10;
        int b = 20;

        if(a>20) {
            a = a + 1;
        }

        if(b < 20) {
            b = 30;
            a = 10;
        }
    }
}