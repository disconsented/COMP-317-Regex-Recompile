public class Main {
    public static void main(String[] args) {
        String[] patterns = {"(a*b+ac)d", "ba*(a+b)b"};
        for (String pattern : patterns) {
            Compiler c = new Compiler(pattern);
            try {
                System.out.println("\ns  ch 1 2");
                System.out.println("--+--+-+-+");
                c.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
