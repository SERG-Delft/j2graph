class A {

    public void m1() {
        int a = 10;
        int b = 20;

        if(a>20) {
            String foo = get_foo(a, a + 1);
        }

        if(b < 20) {
            String baz = superCoolMethod(a, b);
            System.out.println("chocolate");
        }
    }
}