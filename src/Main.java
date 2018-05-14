public class Main {
    public static void main(String[] args) {
        Compiler c = new Compiler("ba*(a+b)b");
        try {
            c.parse();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
